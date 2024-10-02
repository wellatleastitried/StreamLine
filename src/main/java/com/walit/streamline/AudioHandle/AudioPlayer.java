package com.walit.streamline.AudioHandle;

import java.util.HashMap;
import java.util.Queue;
import java.util.PriorityQueue;

public class AudioPlayer implements Runnable {

    public Queue<Song> songsToPlay;

    public AudioPlayer() {
        songsToPlay = new PriorityQueue<Song>();
    }

    public AudioPlayer(HashMap<Integer, Song> queriedSongs) {
        songsToPlay = new PriorityQueue<Song>();
        for (int i = 1; i <= queriedSongs.size(); i++) {
            songsToPlay.add(queriedSongs.get(i));
        }
    }

    @Override
    public void run() {
        // Start playing songs
        System.err.println("Playing songs");
    }
}
