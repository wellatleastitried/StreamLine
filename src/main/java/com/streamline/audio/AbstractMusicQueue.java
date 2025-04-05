package com.streamline.audio;

import com.streamline.utilities.RetrievedStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Abstract class that handles the management of the music queue.
 * Responsible for queue operations such as adding songs, navigating between songs,
 * and maintaining the state of the queue.
 * @author wellatleastitried
 */
abstract class AbstractMusicQueue {
    
    protected final Deque<Song> nextSongs;
    protected final Deque<Song> previousSongs;
    
    protected Song currentSong;
    
    protected boolean loopQueue = false;
    protected boolean loopSong = false;

    public AbstractMusicQueue() {
        nextSongs = new LinkedList<>();
        previousSongs = new LinkedList<>();
    }

    public AbstractMusicQueue(Song song) {
        previousSongs = new LinkedList<>();
        nextSongs = new LinkedList<>();
        nextSongs.add(song);
    }

    public AbstractMusicQueue(RetrievedStorage queriedSongs) {
        previousSongs = new LinkedList<>();
        nextSongs = new LinkedList<>();
        for (int i = 1; i <= queriedSongs.size(); i++) {
            nextSongs.addLast(queriedSongs.getSongFromIndex(i));
        }
    }

    /**
     * Add a song to be played immediately after the current song.
     * 
     * @param song The song to add to the front of the queue
     */
    public void addNextSong(Song song) {
        nextSongs.addFirst(song);
    }

    /**
     * Add a song to the end of the queue.
     * 
     * @param song The song to add to the end of the queue
     */
    public void addSongToQueue(Song song) {
        nextSongs.addLast(song);
    }

    /**
     * Clear the current queue and fill it with songs from the provided {@link RetrievedStorage}.
     * 
     * @param queriedSongs Storage containing songs to populate the queue
     */
    public void fillQueue(RetrievedStorage queriedSongs) {
        nextSongs.clear();
        previousSongs.clear();
        for (int i = 1; i <= queriedSongs.size(); i++) {
            nextSongs.addLast(queriedSongs.getSongFromIndex(i));
        }
    }

    /**
     * Move to the next song in the queue.
     */
    public void playNextSong() {
        if (loopSong && currentSong != null) {
            return;
        }

        if (!nextSongs.isEmpty()) {
            if (currentSong != null) {
                previousSongs.addLast(currentSong);
            }
            currentSong = nextSongs.pollFirst();
        } else if (loopQueue && !previousSongs.isEmpty()) {
            nextSongs.clear();
            nextSongs.addAll(previousSongs);
            previousSongs.clear();
            playNextSong();
        }
    }

    public void playPreviousSong() {
        if (!previousSongs.isEmpty()) {
            if (currentSong != null) {
                nextSongs.addFirst(currentSong);
            }
            currentSong = previousSongs.pollLast();
        }
    }

    public void shuffleQueue() {
        if (nextSongs.size() > 1) {
            List<Song> songList = new ArrayList<>(nextSongs);
            Collections.shuffle(songList, new Random());
            nextSongs.clear();
            nextSongs.addAll(songList);
        }
    }

    public boolean toggleLoopQueue() {
        loopQueue = !loopQueue;
        return loopQueue;
    }

    public boolean toggleLoopSong() {
        loopSong = !loopSong;
        return loopSong;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public int getQueueSize() {
        return nextSongs.size();
    }

    public List<Song> getNextSongs() {
        return new ArrayList<>(nextSongs);
    }

    public List<Song> getPreviousSongs() {
        return new ArrayList<>(previousSongs);
    }

    public abstract void playCurrent();
    
    public abstract void pause();
    
    public abstract void resume();
    
    public abstract void shutdown();
}
