package com.basil.spotify.sorter.controller;

import com.basil.spotify.sorter.models.*;
import com.basil.spotify.sorter.service.SpotifyClient;
import jakarta.servlet.http.HttpServletResponse;
import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RestController
public class SorterController {
    @Autowired
    private SpotifyClient spotifyClient;

    @GetMapping("/sort")
    public void test2(@ModelAttribute("playlistSortRequest") PlaylistSortRequest playlistSortRequest) throws InterruptedException {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//
//        headers.set("Authorization", "Bearer " + accessToken); //accessToken can be the secret key you generate.
//        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//        HttpEntity <String> entity = new HttpEntity <> (headers);
//        ResponseEntity <String> response = restTemplate.exchange("https://api.spotify.com/v1/playlists/" + playlistIdSource + "/tracks", HttpMethod.GET, entity, String.class);
       String playlistIdSource = playlistSortRequest.getPlaylistIdSource();
       List<String> genres = Arrays.stream(playlistSortRequest.getGenres().split(",")).toList();

       String playlistIdDestination = playlistSortRequest.getPlaylistIdDestination();
        System.out.println(genres);
        System.out.println(playlistIdSource);
        System.out.println(playlistIdDestination);

        PlaylistTracksResponse playlistTracksResponse = spotifyClient.get("https://api.spotify.com/v1/playlists/" + playlistIdSource + "/tracks", PlaylistTracksResponse.class);
        System.out.println(spotifyClient.get("https://api.spotify.com/v1/playlists/" + playlistIdSource + "/tracks", String.class));
        int total = playlistTracksResponse.getTotal();
        System.out.println(total/100);
//total/100
        ArtistResponse artistResponse;
        System.out.println(Math.ceil(total/100));
        for(int i = 0; i < Math.ceil(total/100) + 1; i++) {
            System.out.println("i:" + i);
            try {
                playlistTracksResponse = spotifyClient.get("https://api.spotify.com/v1/playlists/" +
                        playlistIdSource + "/tracks?limit=100&offset=" + (i * 100), PlaylistTracksResponse.class);
            }
            catch(HttpClientErrorException e) {
                throw e;
            }
            List<Item> items = playlistTracksResponse.getItems();
            List<String> trackIds = new ArrayList<>();
            for(Item item: items) {
                String artistId = item.getTrack().getArtists().get(0).getId();
                try {
                    artistResponse = spotifyClient.get("https://api.spotify.com/v1/artists/" + artistId, ArtistResponse.class);
                }catch(Exception e) {
                    continue;
                }
                System.out.println(artistResponse.getGenres());
                try {
                    for(String genre: genres) {
                        if (artistResponse.getGenres().contains(genre)) {
                            System.out.println("HIT");
                            String trackUri = item.getTrack().getUri();
                            spotifyClient.post("https://api.spotify.com/v1/playlists/" + playlistIdDestination + "/tracks?uris=" + trackUri,
                                    null);

                            continue;
                        }
                    }

                }
                catch(Exception e) {
                    continue;
                }
                Thread.sleep(1000);
            }
        }


    }

}
