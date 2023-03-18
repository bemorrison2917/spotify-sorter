package com.basil.spotify.sorter.models;

import java.util.List;

public class ArtistsResponse {
    private List<Artist> artists;

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }
}
