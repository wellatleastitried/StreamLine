package com.walit.streamline.Utilities;

import com.walit.streamline.AudioHandle.Song;
import java.util.HashMap;

/**
 * One-to-one map for the songs and their indices to make listing them in the app much easier and convenient.
 */
public class RetrievedStorage {

    private final HashMap<Integer, Song> indexToSong;
    private final HashMap<Song, Integer> songToIndex;

    public RetrievedStorage() {
        indexToSong = new HashMap<Integer, Song>();
        songToIndex = new HashMap<Song, Integer>();
    }

    public void add(int index, Song song) {
        indexToSong.put(index, song);
        songToIndex.put(song, index);
        assert(indexToSong.size() == songToIndex.size());
    }

    public void add(Song song, int index) {
        indexToSong.put(index, song);
        songToIndex.put(song, index);
        assert(indexToSong.size() == songToIndex.size());
    }

    public boolean remove(int index) {
        Song song = indexToSong.get(index);
        boolean result = songToIndex.remove(song, index) && indexToSong.remove(index, song);
        assert(indexToSong.size() == songToIndex.size());
        return result;
    }

    public boolean remove (Song song) {
        int index = songToIndex.get(song);
        boolean result = indexToSong.remove(index, song) && songToIndex.remove(song, index);
        assert(indexToSong.size() == songToIndex.size());
        return result;
    }

    public int getIndexFromSong(Song song) {
        return songToIndex.get(song);
    }

    public Song getSongFromIndex(int index) {
        return indexToSong.get(index);
    }

    public int size() {
        if (indexToSong.size() == songToIndex.size()) {
            return indexToSong.size();
        }
        return -1;
    }

    public boolean contains(Song song) {
        return indexToSong.containsValue(song) && songToIndex.containsKey(song);
    }

    public boolean contains(int index) {
        return indexToSong.containsKey(index) && songToIndex.containsValue(index);
    }
}