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
        if(trackUris.size() > 0) {
            addTracksToDestinationPlaylist();
        }
    }

    private void getMatchingGenreTrackUrisAddToPlaylistAt100(List<Item> items, List<String> genres) {
        ArtistsResponse artistsResponse = null;
        Set<String> artistsList = new HashSet<>();
        for(Item item: items) {
            if(item.getTrack() == null) {
                continue;
            }
            String artistId = item.getTrack().getArtists().get(0).getId();
            List<String> artistGenres = artistGenreMap.get(artistId);
            if (artistGenres == null) {
                try {
                    if(!artistId.isBlank() && artistId != null) {
                        artistsList.add(artistId);
                    }
                    if(artistsList.size() == 50) {
                        ///////////////////////////////////////////////////////////////////////////////

                        try {
                            artistsResponse = spotifyClient.get("https://api.spotify.com/v1/artists?ids=" + generateArtistList(artistsList),
                                    ArtistsResponse.class);
                        }
                        catch(Exception e) {
                            System.out.println("ERROR");
                            continue;
                        }
                        for(Artist artist: artistsResponse.getArtists()) {
                            artistGenreMap.put(artist.getId(), artist.getGenres());
                        }
                        //////////////////////////////////////////////////////////////////////////////////
                        artistsList = new HashSet<>();
                    }

                } catch (Exception e) {
                    continue;
                }
            }
        }
        /////////////////////////////////////////////////////////////////////////////
        if(artistsList.size() > 0) {
            //System.out.println(generateJsonWithSingleList(artistsList, "ids"));
            try {
                artistsResponse = spotifyClient.get("https://api.spotify.com/v1/artists?ids=" + generateArtistList(artistsList),
                        ArtistsResponse.class);


                for (Artist artist : artistsResponse.getArtists()) {
                    artistGenreMap.put(artist.getId(), artist.getGenres());
                }
            }
             catch(Exception e) {
                 System.out.println("ERROR");
            }
        }
        ////////////////////////////////////////////////////////////////////////////////
        for(Item item: items) {
            try {
                for(String genre: genres) {
                    if (artistGenreMap.get(item.getTrack().getArtists().get(0).getId()).contains(genre)) {
                        trackUris.add(item.getTrack().getUri());
                        if(trackUris.size() == 100) {
                            addTracksToDestinationPlaylist();
                            trackUris = new ArrayList<>();
                        }
                        break;
                    }
                }
            }
            catch(Exception e) {
                continue;
            }
            //Wait.waitInMillis(350);
        }
    }

    private String generateArtistList(Set<String> artistList) {
        String artistString = "";
        Iterator artistIter = artistList.iterator();
        while(artistIter.hasNext()) {
            artistString += artistIter.next();
            if(artistIter.hasNext()) {
                artistString += ",";
            }

        }
        System.out.println(artistString);
        return artistString;
    }

    private void addTracksToDestinationPlaylist() throws InterruptedException {
        //System.out.println(trackUris.size());
        //System.out.println(generateJsonWithSingleList(trackUris, "uris"));
        spotifyClient.post("https://api.spotify.com/v1/playlists/" + playlistIdDestination + "/tracks",
                null, generateJsonWithSingleList(trackUris, "uris"));
    }

    private String generateJsonWithSingleList(List<String> uris, String fieldName) {
        String json = "{\"" + fieldName + "\":[";
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
