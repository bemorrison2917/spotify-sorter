package com.basil.spotify.sorter.service;

import com.basil.spotify.sorter.client.SpotifyClient;
import com.basil.spotify.sorter.models.ArtistSearchResponse;
import com.basil.spotify.sorter.util.EncodeUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArtistSearchService {
    @Autowired
    private SpotifyClient spotifyClient;

    public ArtistSearchResponse search(String artistName) {
        String url = "https://api.spotify.com/v1/search?q=artist:" + EncodeUrl.encodeUrlString(artistName)
                + "&type=artist";
        return spotifyClient.get(url, ArtistSearchResponse.class);
    }
}
