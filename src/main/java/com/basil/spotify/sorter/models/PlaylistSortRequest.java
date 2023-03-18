package com.basil.spotify.sorter.models;

import java.util.List;

public class PlaylistSortRequest {
    private String playlistIdSource;
    private String genres;
    private String playlistIdDestination;

    public String getPlaylistIdSource() {
        return playlistIdSource;
    }

    public void setPlaylistIdSource(String playlistIdSource) {
        this.playlistIdSource = playlistIdSource;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getPlaylistIdDestination() {
        return playlistIdDestination;
    }

    public void setPlaylistIdDestination(String playlistIdDestination) {
        this.playlistIdDestination = playlistIdDestination;
    }
}
