package com.streamline.audio;

import com.streamline.backend.Dispatcher;
import com.streamline.utilities.RetrievedStorage;

import org.tinylog.Logger;

public class Playlist {

    private Dispatcher backend;

    private final int id;
    private final String name;
    private RetrievedStorage songsInPlaylist = null;

    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
        this.backend = null;
    }

    public Playlist(String name, Dispatcher backend) {
        this.name = name;
        this.backend = backend;
        this.id = backend.getPlaylistId(name);
        if (id == -1) {
            Logger.debug("[!] Playlist: Playlist not found, creating a new one.");
            this.backend.createPlaylist(name);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RetrievedStorage getSongs() {
        songsInPlaylist = backend.getPlaylistSongs(id);
        return songsInPlaylist;
    }

    public void setBackend(Dispatcher backend) {
        this.backend = backend;
    }

    public void addTrack(Song song) {
        backend.addSongToPlaylist(name, song);
        songsInPlaylist.add(songsInPlaylist.size(), song);
    }

    public void removeTrack(Song song) {
        backend.removeSongFromPlaylist(name, song);
        songsInPlaylist.remove(song);
    }
}
