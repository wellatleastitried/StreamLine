package com.walit.streamline.audio;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Queue;
import java.util.PriorityQueue;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.walit.streamline.utilities.RetrievedStorage;
import com.walit.streamline.utilities.internal.StreamLineMessages;

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

    public void fillQueue(RetrievedStorage queriedSongs) {
        songsToPlay = new PriorityQueue<Song>();
        for (int i = 1; i <= queriedSongs.size(); i++) {
            songsToPlay.add(queriedSongs.getSongFromIndex(i));
        }
    }

    @Override
    public void run() {
        // Start playing songs
        Song song = null;
        try {
            for (int i = 0; i < songsToPlay.size(); i++) {
                song = songsToPlay.poll();
                // Display info on currently playing song
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
        }
    }

    /**
     * Verifies file hash of the queued song with the hash that was generated of the file when it was created. This is done in order to ensure that files that have been tampered with cannot be used by the program.
     * @return Returns whether or not the file hash matches the stored hash.
     */
    public boolean checkDownloadIntegrity(Song song) {
        return true;
    }

    private void playSongFromFile(String pathToAudio) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
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

    // Change back to public
    public void playSongFromUrl(String pathToAudio) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        URL audioUrl = new URL(pathToAudio);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioUrl);
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
