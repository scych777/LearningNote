package com.mattyang.demos.MusicPlayer.model;

import android.support.v4.media.MediaMetadataCompat;

import java.util.List;

public class MusicProvider {
    public List<MediaMetadataCompat> getMusicsByGenre(String categoryValue) {
        return null;
    }

    public List<MediaMetadataCompat> searchMusicBySongTitle(String categoryValue) {
        return  null;
    }

    public List<MediaMetadataCompat> searchMusicByAlbum(String album) {
        return null;
    }

    public List<MediaMetadataCompat> searchMusicByArtist(String artist) {
        return null;
    }

    public List<MediaMetadataCompat> searchMusicByGenre(String query) {
        return null;
    }

    public Iterable<MediaMetadataCompat> getShuffledMusic() {
        return null;
    }
}
