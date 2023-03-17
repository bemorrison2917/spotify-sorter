package com.basil.spotify.sorter.models;

import java.util.List;

public class ArtistResponse {
    private List<String> genres;

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
