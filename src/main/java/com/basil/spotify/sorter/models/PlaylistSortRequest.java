package com.basil.spotify.sorter.models;

public class PlaylistSortRequest {
    private String playlistUrlSource;
    private String genres;
    private String playlistUrlDestination;
    private String genresExactMatch;
    private String genresExactExclude;

    public String getPlaylistUrlSource() {
        return playlistUrlSource;
    }

    public void setPlaylistUrlSource(String playlistUrlSource) {
        this.playlistUrlSource = playlistUrlSource;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getPlaylistUrlDestination() {
        return playlistUrlDestination;
    }

    public void setPlaylistUrlDestination(String playlistUrlDestination) {
        this.playlistUrlDestination = playlistUrlDestination;
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
