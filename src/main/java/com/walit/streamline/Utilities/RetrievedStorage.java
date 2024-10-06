package com.walit.streamline.Utilities;

import com.walit.streamline.AudioHandle.Song;
import java.util.HashMap;

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

    public void remove(int index) {
        songToIndex.remove(indexToSong.get(index));
        indexToSong.remove(index);
    }

    public void remove (Song song) {
        indexToSong.remove(songToIndex.get(song));
        songToIndex.remove(song);
    }

    public int getIndexFromSong(Song song) {
        return songToIndex.get(song);
    }

    public Song getSongFromIndex(int index) {
        return indexToSong.get(index);
    }
}
