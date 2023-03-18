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

import java.util.*;

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
        List<String> trackUris = new ArrayList<>();


        PlaylistTracksResponse playlistTracksResponse = spotifyClient.get("https://api.spotify.com/v1/playlists/" + playlistIdSource + "/tracks", PlaylistTracksResponse.class);
        System.out.println(spotifyClient.get("https://api.spotify.com/v1/playlists/" + playlistIdSource + "/tracks", String.class));
        int total = playlistTracksResponse.getTotal();

        Map<String, List<String>> artistGenreMap = new HashMap<>();
        ArtistResponse artistResponse = null;
        for(int i = 0; i < Math.ceil(total/100) + 1; i++) {
            if(trackUris.size() == 100) {
                spotifyClient.post("https://api.spotify.com/v1/playlists/" + playlistIdDestination + "/tracks",
                        null, generateUrisJson(trackUris));
                trackUris = new ArrayList<>();
            }
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
                List<String> artistGenres = artistGenreMap.get(artistId);
                if(artistGenres == null) {
                    try {
                        artistResponse = spotifyClient.get("https://api.spotify.com/v1/artists/" + artistId, ArtistResponse.class);
                        artistGenres = artistResponse.getGenres();
                        artistGenreMap.put(artistId, artistGenres);
                    } catch (Exception e) {
                        continue;
                    }
                    System.out.println(artistResponse.getGenres());
                }
                try {
                    for(String genre: genres) {
                        if (artistResponse.getGenres().contains(genre)) {
                            System.out.println("HIT");
                            trackUris.add(item.getTrack().getUri());
//                            spotifyClient.post("https://api.spotify.com/v1/playlists/" + playlistIdDestination + "/tracks?uris=" + trackUri,
//                                    null);

                            continue;
                        }
                    }

                }
                catch(Exception e) {
                    continue;
                }
                Thread.sleep(350);
            }
        }
        spotifyClient.post("https://api.spotify.com/v1/playlists/" + playlistIdDestination + "/tracks",
                null, generateUrisJson(trackUris));
    }

    private String generateUrisJson(List<String> uris) {
        String json = "{\"uris\":[";
        Iterator<String> urisIter = uris.iterator();
        while(urisIter.hasNext()) {
            json += "\""+ urisIter.next()+"\"";
            if(!urisIter.hasNext()) {
                json += "]}";
            }
            else {
                json+=",";
            }
        }
        System.out.println(json);
        return json;
    }

}
