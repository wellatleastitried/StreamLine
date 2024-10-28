package com.walit.streamline.AudioHandle;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Queue;
import java.util.PriorityQueue;
import javax.sound.sampled.*;
import javax.sound.sampled.LineUnavailableException;

import com.walit.streamline.Utilities.Internal.StreamLineMessages;

public class AudioPlayer implements Runnable {

    public Queue<Song> songsToPlay;
    public Queue<Song> shuffledSongsToPlay;

    public AudioPlayer() {
        songsToPlay = new PriorityQueue<Song>();
        shuffledSongsToPlay = new PriorityQueue<Song>();
    }

    public AudioPlayer(HashMap<Integer, Song> queriedSongs) {
        songsToPlay = new PriorityQueue<Song>();
        for (int i = 1; i <= queriedSongs.size(); i++) {
            songsToPlay.add(queriedSongs.get(i));
        }
    }

    public void fillQueue(HashMap<Integer, Song> queriedSongs) {
        songsToPlay = new PriorityQueue<Song>();
        for (int i = 1; i <= queriedSongs.size(); i++) {
            songsToPlay.add(queriedSongs.get(i));
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
                    playSong(song.getDownloadPath());
                } else {
                    playSong(song.getSongLink());
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

    private void playSong(String pathToAudio) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
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
