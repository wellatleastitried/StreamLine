package com.streamline.audio;

import com.streamline.utilities.RetrievedStorage;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;

import java.net.URL;
import java.net.URLConnection;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tinylog.Logger;

/**
 * Implementation of AbstractMusicQueue that handles audio playback functionality.
 * This class is responsible for actually playing the audio from Song objects.
 * @author wellatleastitried
 */
public final class AudioPlayer extends AbstractMusicQueue implements Runnable {

    private SourceDataLine currentAudioLine;
    private boolean isPlaying = false;
    private boolean isPaused = false;

    public AudioPlayer() {
        super();
    }

    public AudioPlayer(Song song) {
        super(song);
    }

    public AudioPlayer(RetrievedStorage queriedSongs) {
        super(queriedSongs);
    }

    /**
     * Main execution method that processes and plays songs in the queue.
     */
    @Override
    public void run() {
        try {
            while (!nextSongs.isEmpty()) {
                currentSong = nextSongs.pollFirst();
                // TODO: Display info on currently playing song
                // Play the song from file or URL as appropriate
                playCurrent();
                if (currentSong != null) {
                    previousSongs.addLast(currentSong);
                }
            }
        } catch (Exception e) {
            Logger.warn("[!] An unknown error has occurred during audio playback: {}", e.getMessage());
        }
    }

    /**
     * Play the current song from either local file or URL.
     */
    @Override
    public void playCurrent() {
        if (currentSong == null) {
            return;
        }
        
        try {
            isPlaying = true;
            isPaused = false;
            
            if (currentSong.isSongDownloaded() && checkDownloadIntegrity(currentSong)) {
                playSongFromFile(currentSong.getDownloadPath());
            } else {
                playSongFromUrl(currentSong.getSongLink());
            }
        } catch (UnsupportedAudioFileException uAFE) {
            System.err.println("[!] Error resolving file format, please try again.");
        } catch (LineUnavailableException lUE) {
            System.err.println("[!] Error while fetching audio, please try again.");
        } catch (IOException iE) {
            System.err.println("[!] IOException encountered during song playback, please try again.");
        } catch (Exception e) {
            Logger.warn("[!] An error occurred during playback: {}", e.getMessage());
        } finally {
            isPlaying = false;
        }
    }

    /**
     * Verifies file hash of the queued song with the hash that was generated when the file
     * was created. This ensures that files that have been tampered with cannot be used.
     * 
     * @param song The song to verify
     * @return True if the file hash matches the stored hash
     */
    public boolean checkDownloadIntegrity(Song song) {
        // TODO: Implementation placeholder
        return true;
    }

    /**
     * Stream the audio from the given file path.
     * 
     * @param pathToAudio The file path of the audio file
     * @throws UnsupportedAudioFileException If the file is not a supported audio format
     * @throws IOException If an I/O error occurs
     * @throws LineUnavailableException If the audio line cannot be opened
     */
    public void playSongFromFile(String pathToAudio) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(pathToAudio));
        AudioFormat format = audioInputStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        
        currentAudioLine = (SourceDataLine) AudioSystem.getLine(info);
        currentAudioLine.open(format);
        currentAudioLine.start();
        
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        
        while ((bytesRead = audioInputStream.read(buffer)) != -1 && isPlaying) {
            if (!isPaused) {
                currentAudioLine.write(buffer, 0, bytesRead);
            } else {
                // If paused, sleep to reduce CPU usage
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        currentAudioLine.drain();
        currentAudioLine.close();
        audioInputStream.close();
    }

    /**
     * Stream the audio from the given URL.
     * 
     * @param pathToAudio The URL that points to the audio stream
     * @throws Exception If any error occurs during playback
     */
    public void playSongFromUrl(String pathToAudio) throws Exception {
        URL audioUrl = new URL(pathToAudio);

        URLConnection connection = audioUrl.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);

        Logger.debug("Content-Type: {}", connection.getContentType());

        InputStream inputStream = connection.getInputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        // Try to get audio input stream, with fallback for MP3
        AudioInputStream audioInputStream;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
        } catch (UnsupportedAudioFileException e) {
            Logger.info("[*] Standard format not recognized, trying MP3 conversion...");

            // Reopen stream as it might have been partially consumed
            inputStream = connection.getInputStream();
            bufferedInputStream = new BufferedInputStream(inputStream);
            audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
        }
        AudioFormat baseFormat = audioInputStream.getFormat();
        Logger.debug("Original format: {}", baseFormat);

        // Convert to PCM if needed
        AudioFormat targetFormat = getOutFormat(baseFormat);
        AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, targetFormat);
        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("Line not supported for format: " + targetFormat);
        }

        currentAudioLine = (SourceDataLine) AudioSystem.getLine(info);
        currentAudioLine.open(targetFormat);
        currentAudioLine.start();

        byte[] buffer = new byte[4096];
        int bytesRead;
        try {
            while ((bytesRead = convertedStream.read(buffer, 0, buffer.length)) != -1 && isPlaying) {
                if (!isPaused) {
                    currentAudioLine.write(buffer, 0, bytesRead);
                } else {
                    // If paused, sleep to reduce CPU usage
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        } finally {
            currentAudioLine.drain();
            currentAudioLine.stop();
            currentAudioLine.close();
            convertedStream.close();
            audioInputStream.close();
            bufferedInputStream.close();
            inputStream.close();
        }
    }

    /**
     * Helper method to convert audio format to PCM which is widely supported.
     * 
     * @param inFormat The original audio format
     * @return The new audio format converted to PCM
     */
    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int sampleSizeInBits = 16;
        final int channels = inFormat.getChannels();
        final boolean signed = true;
        final boolean bigEndian = false;
        return new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                inFormat.getSampleRate(),
                sampleSizeInBits,
                channels,
                channels * (sampleSizeInBits / 8),
                inFormat.getSampleRate(),
                bigEndian
        );
    }

    @Override
    public void pause() {
        if (isPlaying && !isPaused && currentAudioLine != null) {
            isPaused = true;
            // We don't stop the audio line here, just set the flag. Actual pause behavior is handled in the playback loops
        }
    }

    @Override
    public void resume() {
        if (isPlaying && isPaused && currentAudioLine != null) {
            isPaused = false;
        }
    }

    /**
     * Stop playback and release resources.
     */
    @Override
    public void shutdown() {
        isPlaying = false;
        isPaused = false;
        
        if (currentAudioLine != null) {
            currentAudioLine.stop();
            currentAudioLine.close();
            currentAudioLine = null;
        }
        
        Thread.currentThread().interrupt();
    }
}
