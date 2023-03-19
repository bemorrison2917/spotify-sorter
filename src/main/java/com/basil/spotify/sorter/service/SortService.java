package com.basil.spotify.sorter.service;

import com.basil.spotify.sorter.client.SpotifyClient;
import com.basil.spotify.sorter.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

//TODO clean this shit up
@Service
public class SortService {
    @Autowired
    private SpotifyClient spotifyClient;

    private Map<String, List<String>> artistGenreMap;
    private String playlistIdDestination;
    List<String> trackUris;

    public void sort(PlaylistSortRequest playlistSortRequest) {
        String playlistIdSource = parseIdFromPlaylistUrl(playlistSortRequest.getPlaylistUrlSource());
        List<String> genres;
        if(playlistSortRequest.getGenres() == null || playlistSortRequest.getGenres().isEmpty()) {
            genres = null;
        }
        else {
            genres = Arrays.stream(playlistSortRequest.getGenres().split(",")).toList();
        }
        System.out.println(genres);
        List<String> genresExactMatch = Arrays.stream(playlistSortRequest.getGenresExactMatch().split(",")).toList();
        List<String> genresExactExclude = Arrays.stream(playlistSortRequest.getGenresExactExclude().split(",")).toList();
        System.out.println(genresExactMatch);
        System.out.println("test");

        playlistIdDestination = parseIdFromPlaylistUrl(playlistSortRequest.getPlaylistUrlDestination());
        trackUris = new ArrayList<>();


        PlaylistTracksResponse playlistTracksResponse = spotifyClient.get("https://api.spotify.com/v1/playlists/" + playlistIdSource + "/tracks", PlaylistTracksResponse.class);
        int total = playlistTracksResponse.getTotal();
        artistGenreMap = new HashMap<>();

        for(int i = 0; i < Math.ceil(total/100.0) + 1; i++) {


                playlistTracksResponse = spotifyClient.get("https://api.spotify.com/v1/playlists/" +
                        playlistIdSource + "/tracks?limit=100&offset=" + (i * 100), PlaylistTracksResponse.class);

            List<Item> items = playlistTracksResponse.getItems();
            getMatchingGenreTrackUrisAddToPlaylistAt50(items, genres, genresExactMatch, genresExactExclude);

        }
        if(trackUris.size() > 0) {
            addTracksToDestinationPlaylist();
        }
    }

    private String parseIdFromPlaylistUrl(String playlistUrl) {
        System.out.println(playlistUrl.substring(playlistUrl.indexOf("playlist/") + 9, playlistUrl.indexOf("?si=")));
        return playlistUrl.substring(playlistUrl.indexOf("playlist/") + 9, playlistUrl.indexOf("?si="));
    }

    private void getMatchingGenreTrackUrisAddToPlaylistAt50(List<Item> items, List<String> genres, List<String> genresExactMatch, List<String> genresExactExclude) {
        ArtistsResponse artistsResponse = null;
        Set<String> artistsList = new HashSet<>();
        for(Item item: items) {
            if(item.getTrack() == null) {
                continue;
            }
            String artistId = item.getTrack().getArtists().get(0).getId();
            List<String> artistGenres = artistGenreMap.get(artistId);
            if (artistGenres == null) {

                    if(artistId != null && !artistId.isBlank() ) {
                        artistsList.add(artistId);
                    }
                    if(artistsList.size() == 50) {
                        ///////////////////////////////////////////////////////////////////////////////


                            artistsResponse = spotifyClient.get("https://api.spotify.com/v1/artists?ids=" + generateArtistList(artistsList),
                                    ArtistsResponse.class);

                        for(Artist artist: artistsResponse.getArtists()) {
                            artistGenreMap.put(artist.getId(), artist.getGenres());
                        }
                        //////////////////////////////////////////////////////////////////////////////////
                        artistsList = new HashSet<>();
                    }


            }
        }
        /////////////////////////////////////////////////////////////////////////////
        if(artistsList.size() > 0) {
            //System.out.println(generateJsonWithSingleList(artistsList, "ids"));

                artistsResponse = spotifyClient.get("https://api.spotify.com/v1/artists?ids=" + generateArtistList(artistsList),
                        ArtistsResponse.class);


                for (Artist artist : artistsResponse.getArtists()) {
                    artistGenreMap.put(artist.getId(), artist.getGenres());
                }

        }
        ////////////////////////////////////////////////////////////////////////////////
        for(Item item: items) {

                boolean hasGenre = false;
                if(item.getTrack() == null) {
                    continue;
                }
                List<String> artistGenres = artistGenreMap.get(item.getTrack().getArtists().get(0).getId());

                if(artistGenres == null) {
                    continue;
                }

                if(genres != null) {
                    for (String genre : genres) {
                        if(genre.isEmpty()) {
                            break;
                        }
                        for (String artistGenre : artistGenres) {
                            if(genresExactExclude.contains(artistGenre)) {
                                break;
                            }
                            if (artistGenre.contains(genre)) {
                                hasGenre = true;
                                break;
                            }
                        }
                        if (hasGenre) {
                            trackUris.add(item.getTrack().getUri());
                            if (trackUris.size() == 50) {
                                addTracksToDestinationPlaylist();
                                trackUris = new ArrayList<>();
                            }
                            break;
                        }
                    }
                }
                if(hasGenre == false && genresExactMatch != null) {
                    for(String genre: genresExactMatch) {
                        if(artistGenres.contains(genre)) {
                            trackUris.add(item.getTrack().getUri());
                            if(trackUris.size() == 50) {
                                addTracksToDestinationPlaylist();
                                trackUris = new ArrayList<>();
                            }
                            break;
                        }
                    }
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

    private void addTracksToDestinationPlaylist() {
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
