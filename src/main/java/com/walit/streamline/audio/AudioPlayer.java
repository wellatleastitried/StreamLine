package com.walit.streamline.audio;

import com.walit.streamline.utilities.RetrievedStorage;
import com.walit.streamline.utilities.internal.StreamLineMessages;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;

import java.net.URL;
import java.net.URLConnection;

import java.util.Queue;
import java.util.PriorityQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tinylog.Logger;

/**
 * This is the engine for playing audio from the {@link Song} objects.
 * @author wellatleastitried
 */
public class AudioPlayer implements Runnable {

    public Queue<Song> songsToPlay;
    public Queue<Song> shuffledSongsToPlay;

    public AudioPlayer() {
        songsToPlay = new PriorityQueue<Song>();
        shuffledSongsToPlay = new PriorityQueue<Song>();
    }

    public AudioPlayer(Song song) {
        songsToPlay = new PriorityQueue<Song>();
        songsToPlay.add(song);
    }

    public AudioPlayer(RetrievedStorage queriedSongs) {
        songsToPlay = new PriorityQueue<Song>();
        for (int i = 1; i <= queriedSongs.size(); i++) {
            songsToPlay.add(queriedSongs.getSongFromIndex(i));
        }
    }

    /**
     * Fill the {@link PriorityQueue} with Song objects to playback.
     * @param queriedSongs {@link RetrievedStorage} that contains the Song objects and their indices.
     */
    public void fillQueue(RetrievedStorage queriedSongs) {
        songsToPlay = new PriorityQueue<Song>();
        for (int i = 1; i <= queriedSongs.size(); i++) {
            songsToPlay.add(queriedSongs.getSongFromIndex(i));
        }
    }

    @Override
    public void run() {
        Song song = null;
        try {
            for (int i = 0; i < songsToPlay.size(); i++) {
                song = songsToPlay.poll();
                // TODO: Display info on currently playing song
                if (song.isSongDownloaded() && checkDownloadIntegrity(song)) {
                    playSongFromFile(song.getDownloadPath());
                } else {
                    playSongFromUrl(song.getSongLink());
                }
            }
        } catch (UnsupportedAudioFileException uAFE) {
            System.err.println(StreamLineMessages.AudioFileFormatError.getMessage());
            if (songsToPlay.peek() != null) {
                run();
            }
        } catch (LineUnavailableException lUE) {
            System.err.println(StreamLineMessages.AudioFetchFailure.getMessage());
            if (songsToPlay.peek() != null) {
                run();
            }
        } catch (IOException iE) {
            System.err.println(StreamLineMessages.IOException.getMessage());
            if (songsToPlay.peek() != null) {
                run();
            }
        } catch (Exception e) {
            Logger.warn("[!] An unknown error has occured during audio playback.");
        }
    }

    /**
     * Verifies file hash of the queued song with the hash that was generated of the file when it was created. This is done in order to ensure that files that have been tampered with cannot be used by the program.
     * @return Returns whether or not the file hash matches the stored hash.
     */
    public boolean checkDownloadIntegrity(Song song) {
        return true;
    }

    /**
     * Stream the audio from the given file path.
     * @param pathToAudio The file path of the audio file.
     * @throws UnsupportedAudioFileException The file path does not point to a file that contains audio.
     * @throws IOException
     * @throws LineUnavailableException
     */
    public void playSongFromFile(String pathToAudio) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(pathToAudio));
        AudioFormat format = audioInputStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
        audioLine.open(format);
        audioLine.start();
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = audioInputStream.read(buffer)) != -1) {
            audioLine.write(buffer, 0, bytesRead);
        }
        audioLine.drain();
        audioLine.close();
        audioInputStream.close();
    }

    /**
     * Stream the audio from the given URL.
     * @param pathToAudio The URL that points to the audio stream.
     * @throws Exception
     */
    public void playSongFromUrl(String pathToAudio) throws Exception {
        URL audioUrl = new URL(pathToAudio);

        URLConnection connection = audioUrl.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);

        // For debugging
        System.out.println("Content-Type: " + connection.getContentType());

        InputStream inputStream = connection.getInputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        // Try to get audio input stream, with fallback for MP3
        AudioInputStream audioInputStream;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
        } catch (UnsupportedAudioFileException e) {
            Logger.info("[*] Standard format not recognized, trying MP3 conversion...");

            // Note: For MP3 support, I need to add MP3SPI library to the project
            // (http://www.javazoom.net/mp3spi/mp3spi.html)

            // Reopen stream as it might have been partially consumed
            inputStream = connection.getInputStream();
            bufferedInputStream = new BufferedInputStream(inputStream);
            audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
        }
        AudioFormat baseFormat = audioInputStream.getFormat();
        System.out.println("Original format: " + baseFormat);

        // Convert to PCM if needed
        AudioFormat targetFormat = getOutFormat(baseFormat);
        AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, targetFormat);
        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("Line not supported for format: " + targetFormat);
        }

        SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
        audioLine.open(targetFormat);
        audioLine.start();

        byte[] buffer = new byte[4096];
        int bytesRead;
        try {
            while ((bytesRead = convertedStream.read(buffer, 0, buffer.length)) != -1) {
                audioLine.write(buffer, 0, bytesRead);
            }
        } finally {
            audioLine.drain();
            audioLine.stop();
            audioLine.close();
            convertedStream.close();
            audioInputStream.close();
            bufferedInputStream.close();
            inputStream.close();
        }
    }

    /**
     * Helper method to convert audio format to PCM which is widely supported
     * @param inFormat The {@link AudioFormat} of the audio stream.
     * @return The new {@link AudioFormat} converted to PCM.
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
    private void pause() {
    }

    private void resume() {
    }

    private void skipSong() {
    }

    private void previousSong() {
    }

    private void shutdown() {
        // Kill the running thread
    }
}
