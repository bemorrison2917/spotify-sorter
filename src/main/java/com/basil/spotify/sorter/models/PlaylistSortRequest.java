package com.basil.spotify.sorter.models;

public class PlaylistSortRequest {
    private String playlistIdSource;
    private String genres;
    private String playlistIdDestination;
    private String genresExactMatch;
    private String genresExactExclude;

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

    public String getGenresExactMatch() {
        return genresExactMatch;
    }

    public void setGenresExactMatch(String genresExactMatch) {
        this.genresExactMatch = genresExactMatch;
    }

    public String getGenresExactExclude() {
        return genresExactExclude;
    }

    public void setGenresExactExclude(String genresExactExclude) {
        this.genresExactExclude = genresExactExclude;
    }
}
