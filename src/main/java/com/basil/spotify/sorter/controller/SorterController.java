package com.basil.spotify.sorter.controller;

import com.basil.spotify.sorter.models.*;
import com.basil.spotify.sorter.client.SpotifyClient;
import com.basil.spotify.sorter.util.Wait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

@RestController
public class SorterController {
    @Autowired
    private SpotifyClient spotifyClient;

    private Map<String, List<String>> artistGenreMap;
    private String playlistIdDestination;
    List<String> trackUris;

    @GetMapping("/sort")
    public void sort(@ModelAttribute("playlistSortRequest") PlaylistSortRequest playlistSortRequest) throws InterruptedException {
        String playlistIdSource = playlistSortRequest.getPlaylistIdSource();
        List<String> genres = Arrays.stream(playlistSortRequest.getGenres().split(",")).toList();

        playlistIdDestination = playlistSortRequest.getPlaylistIdDestination();
        trackUris = new ArrayList<>();


        PlaylistTracksResponse playlistTracksResponse = spotifyClient.get("https://api.spotify.com/v1/playlists/" + playlistIdSource + "/tracks", PlaylistTracksResponse.class);
        int total = playlistTracksResponse.getTotal();
        artistGenreMap = new HashMap<>();

        for(int i = 0; i < Math.ceil(total/100) + 1; i++) {

            try {
                playlistTracksResponse = spotifyClient.get("https://api.spotify.com/v1/playlists/" +
                        playlistIdSource + "/tracks?limit=100&offset=" + (i * 100), PlaylistTracksResponse.class);
            }
            catch(HttpClientErrorException e) {
                throw e;
            }
            List<Item> items = playlistTracksResponse.getItems();
            getMatchingGenreTrackUrisAddToPlaylistAt100(items, genres);

        }
        addTracksToDestinationPlaylist();
    }

    private void getMatchingGenreTrackUrisAddToPlaylistAt100(List<Item> items, List<String> genres) {
        ArtistResponse artistResponse = null;
        List<String> trackUris = new ArrayList<>();
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
            }
            try {
                for(String genre: genres) {
                    if (artistResponse.getGenres().contains(genre)) {
                        trackUris.add(item.getTrack().getUri());
                        if(trackUris.size() == 100) {
                            addTracksToDestinationPlaylist();
                            trackUris = new ArrayList<>();
                        }
                        continue;
                    }
                }

            }
            catch(Exception e) {
                continue;
            }
            Wait.waitInMillis(350);
        }
    }

    private void addTracksToDestinationPlaylist() throws InterruptedException {
        System.out.println(trackUris.size());
        //spotifyClient.post("https://api.spotify.com/v1/playlists/" + playlistIdDestination + "/tracks",
           //     null, generateUrisJson(trackUris));
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
