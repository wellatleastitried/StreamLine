package com.streamline.utilities;

import com.streamline.audio.Song;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * One-to-one map for the songs and their indices to make listing them in the app much easier and convenient.
 * @author wellatleastitried
 */
public class RetrievedStorage implements Iterable<Song>{

    private final HashMap<Integer, Song> indexToSong;
    private final HashMap<Song, Integer> songToIndex;

    public RetrievedStorage() {
        indexToSong = new HashMap<Integer, Song>();
        songToIndex = new HashMap<Song, Integer>();
    }

    /*
     * In just about every use case, the first index that will be passed into the
     * two add() methods will be '1'. This is so that the index will be easier to
     * refer to from within the UI.
     */
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

    public boolean remove(Song song) {
        int index = songToIndex.get(song);
        boolean result = indexToSong.remove(index, song) && songToIndex.remove(song, index);
        assert(indexToSong.size() == songToIndex.size());
        return result;
    }

    public int getIndexFromSong(Song song) {
        return songToIndex.getOrDefault(song, -1);
    }

    public Song getSongFromIndex(int index) {
        return indexToSong.getOrDefault(index, null);
    }

    public void clear() {
        indexToSong.clear();
        songToIndex.clear();
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

    public Song[] getArrayOfSongs() {
        return indexToSong.values().toArray(new Song[0]);
    }

    @Override
    public Iterator<Song> iterator() {
        return indexToSong.values().iterator();
    }

    /**
     * Iterates over the songs in the storage, removing them after they have been iterated over.
     * @return {@see Iterator} of the songs in the storage.
     */
    public Iterable<Song> drain() {
        return () -> new Iterator<>() {
            private final Iterator<Map.Entry<Integer, Song>> entryIterator = indexToSong.entrySet().iterator();

            @Override
            public boolean hasNext() {
                return entryIterator.hasNext();
            }

            @Override
            public Song next() {
                Map.Entry<Integer, Song> entry = entryIterator.next();
                entryIterator.remove();
                songToIndex.remove(entry.getValue());
                return entry.getValue();
            }
        };
    }
}
