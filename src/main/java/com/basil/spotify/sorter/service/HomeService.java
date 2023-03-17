package com.basil.spotify.sorter.service;

import com.basil.spotify.sorter.client.SpotifyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeService {
    @Autowired
    private SpotifyClient spotifyClient;

    public String getHomeOrLoginPage() {
        if(spotifyClient.hasAccessToken()) {
            return "home";
        }
        else {
            return "login";
        }
    }

    public String getGenreSortHomeOrLoginPage() {
        if(spotifyClient.hasAccessToken()) {
            return "genreSortHome";
        }
        else {
            return "genreSortHomeAuthentication";
        }
    }

    public String getArtistSearchHomeOrLoginPage() {
        if(spotifyClient.hasAccessToken()) {
            return "artistSearch";
        }
        else {
            return "artistSearchHomeAuthentication";
        }
    }
}
