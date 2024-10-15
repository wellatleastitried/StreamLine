package com.walit.streamline.Utilities;

import com.walit.streamline.AudioHandle.Song;
import java.util.HashMap;

// One-to-One map, where the key or value can be used to get its corresponding link

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
    }

    public void add(Song song, int index) {
        indexToSong.put(index, song);
        songToIndex.put(song, index);
    }

    public boolean remove(int index) {
        Song song = indexToSong.get(index);
        return songToIndex.remove(song, index) && indexToSong.remove(index, song);
    }

    public boolean remove (Song song) {
        int index = songToIndex.get(song);
        return indexToSong.remove(index, song) && songToIndex.remove(song, index);
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
