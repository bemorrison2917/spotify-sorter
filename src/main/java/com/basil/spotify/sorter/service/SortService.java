package com.basil.spotify.sorter.service;

import com.basil.spotify.sorter.client.SpotifyClient;
import com.basil.spotify.sorter.models.*;
import com.basil.spotify.sorter.models.myModels.PlaylistSortRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SortService {
    @Autowired
    private SpotifyClient spotifyClient;

    private List<String> playlistDestinationTrackList;
    private Map<String, List<String>> artistGenreMap;
    private String playlistIdDestination;
    private List<String> trackUris;
    private boolean duplicatesAllowed;
    private Set<String> tracksProcessed;
    private Set<String> artistsToAddSongsBy;

    public String sort(Model model, PlaylistSortRequest playlistSortRequest, String duplicatesAllowedCheckboxValue,
                       String addGenreFilterFromGenreInfoCheckboxValue,
                       String addCurrentUsersPlaylistsCheckboxValue, String addLikedSongsFromCurrentUserCheckboxValue) {

        try {
            playlistIdDestination = parseIdFromPlaylistUrl(playlistSortRequest.getPlaylistUrlDestination());
        } catch (Exception e) {
            playlistSortRequest.setDuplicatesAllowedBoolean(playlistSortRequest.getDuplicatesAllowed() != null);
            playlistSortRequest.setAddLikedSongsFromCurrentUserBoolean(playlistSortRequest.getAddLikedSongsFromCurrentUser() != null);
            playlistSortRequest.setAddGenreFilterFromGenreInfoBoolean(playlistSortRequest.getAddGenreFilterFromGenreInfo() != null);
            playlistSortRequest.setAddPlaylistsFromCurrentUserBoolean(playlistSortRequest.getAddPlaylistsFromCurrentUser() != null);
            model.addAttribute("playlistSortRequest", playlistSortRequest);
            return "invalidDestinationPlaylist";
        }

        artistsToAddSongsBy = new HashSet<>();
        artistsToAddSongsBy.addAll((trimAndToLowercaseAll(playlistSortRequest.getArtistIdToIncludeSongsBy().split("\n"))));

        tracksProcessed = new HashSet<>();

        duplicatesAllowed = duplicatesAllowedCheckboxValue != null;


        Set<String> playlistSourceSet = new HashSet<>();
        if (playlistSortRequest.getPlaylistUrlSource() != null && !playlistSortRequest.getPlaylistUrlSource().isEmpty()) {
            for (String playlistUrl : playlistSortRequest.getPlaylistUrlSource().split("\n")) {
                playlistSourceSet.add(parseIdFromPlaylistUrl(playlistUrl));
            }
        }

        if (playlistSortRequest.getUserPlaylistSource() != null && !playlistSortRequest.getUserPlaylistSource().isEmpty()) {
            playlistSourceSet.addAll(getPlaylistIdsFromUser(parseIdFromUserUrl(playlistSortRequest.getUserPlaylistSource())));
        }
        if (addCurrentUsersPlaylistsCheckboxValue != null) {
            playlistSourceSet.addAll(getPlaylistIdsFromCurrentUser());
        }

        Set<String> genres = new HashSet<>();
        Set<String> genresExactMatch = new HashSet<>();
        Set<String> genresExactExclude = new HashSet<>();
        if (addGenreFilterFromGenreInfoCheckboxValue != null) {
            addGenreFilterFromGenreInfo(playlistIdDestination, genres, genresExactMatch, genresExactExclude);
        }

        String genresString = playlistSortRequest.getGenres();
        String genresExactMatchString = playlistSortRequest.getGenresExactMatch();
        String genresExactExcludeString = playlistSortRequest.getGenresExactExclude();
        if (genresString != null && !genresString.isEmpty()) {
            genres.addAll(trimAndToLowercaseAll(genresString.split(",")));
        }
        if (genresExactMatchString != null && !genresExactMatchString.isEmpty()) {
            genresExactMatch.addAll(trimAndToLowercaseAll(genresExactMatchString.split(",")));
        }
        if (genresExactExcludeString != null && !genresExactExcludeString.isEmpty()) {
            genresExactExclude.addAll(trimAndToLowercaseAll(genresExactExcludeString.split(",")));
        }


        addGenreInformationToPlaylist(playlistIdDestination, genres, genresExactMatch, genresExactExclude);

        trackUris = new ArrayList<>();
        artistGenreMap = new HashMap<>();

        if (!duplicatesAllowed) {
            playlistDestinationTrackList = new ArrayList<>();
            PlaylistTracksResponse playlistTracksDestinationResponse = spotifyClient.get("https://api.spotify.com/v1/playlists/" + playlistIdDestination + "/tracks?limit=1", PlaylistTracksResponse.class);
            int destinationTotal = playlistTracksDestinationResponse.getTotal();
            for (int i = 0; i < Math.ceil(destinationTotal / 100.0); i++) {
                playlistTracksDestinationResponse = spotifyClient.get("https://api.spotify.com/v1/playlists/" +
                        playlistIdDestination + "/tracks?limit=100&offset=" + (i * 100), PlaylistTracksResponse.class);
                for (Item item : playlistTracksDestinationResponse.getItems()) {
                    playlistDestinationTrackList.add(item.getTrack().getId());
                }
            }
        }

        List<Item> items = new ArrayList<>();

        if (addLikedSongsFromCurrentUserCheckboxValue != null) {
            items.addAll(getTracksFromCurrentUsersLikes());
        }

        for (String playlistSourceId : playlistSourceSet) {
            PlaylistTracksResponse playlistTracksSourceResponse = spotifyClient.get("https://api.spotify.com/v1/playlists/" + playlistSourceId + "/tracks?limit=1", PlaylistTracksResponse.class);
            int total = playlistTracksSourceResponse.getTotal();
            for (int j = 0; j < Math.ceil(total / 100.0); j++) {
                playlistTracksSourceResponse = spotifyClient.get("https://api.spotify.com/v1/playlists/" +
                        playlistSourceId + "/tracks?limit=100&offset=" + (j * 100), PlaylistTracksResponse.class);
                items.addAll(playlistTracksSourceResponse.getItems());
            }
        }
        getMatchingGenreTrackUrisAddToPlaylistAt50(items, genres, genresExactMatch, genresExactExclude);

        if (trackUris.size() > 0) {
            addTracksToDestinationPlaylist();
        }
        return "sortComplete";
    }

    private List<String> trimAndToLowercaseAll(String[] stringArray) {
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = stringArray[i].trim().toLowerCase();
        }
        return Arrays.asList(stringArray);
    }

    private List<Item> getTracksFromCurrentUsersLikes() {
        List<Item> items = new ArrayList<>();
        PlaylistTracksResponse playlistTracksSourceResponse = spotifyClient.get("https://api.spotify.com/v1/me/tracks?limit=1", PlaylistTracksResponse.class);
        int total = playlistTracksSourceResponse.getTotal();
        for (int i = 0; i < Math.ceil(total / 50.0); i++) {


            playlistTracksSourceResponse = spotifyClient.get("https://api.spotify.com/v1/me/tracks?limit=50&offset=" + (i * 50), PlaylistTracksResponse.class);

            items.addAll(playlistTracksSourceResponse.getItems());
        }
        return items;
    }

    private List<String> getPlaylistIdsFromUser(String userId) {
        List<String> playlistIds = new ArrayList<>();
        UserPlaylistsResponse playlists = spotifyClient.get("https://api.spotify.com/v1/users/" + userId + "/playlists?limit=1", UserPlaylistsResponse.class);

        int total = playlists.getTotal();
        for (int i = 0; i < Math.ceil(total / 50.0); i++) {
            playlists = spotifyClient.get("https://api.spotify.com/v1/users/" + userId + "/playlists?limit=50", UserPlaylistsResponse.class);
            for (Item item : playlists.getItems()) {
                if (item.getOwner().getId().equals(userId)) {
                    playlistIds.add(item.getId());
                }
            }
        }
        return playlistIds;
    }

    private List<String> getPlaylistIdsFromCurrentUser() {
        String userId = spotifyClient.get("https://api.spotify.com/v1/me", UserResponse.class).getId();
        List<String> playlistIds = new ArrayList<>();
        UserPlaylistsResponse playlists = spotifyClient.get("https://api.spotify.com/v1/users/" + userId + "/playlists?limit=1", UserPlaylistsResponse.class);
        int total = playlists.getTotal();

        for (int i = 0; i < Math.ceil(total / 50.0); i++) {
            playlists = spotifyClient.get("https://api.spotify.com/v1/users/" + userId + "/playlists?limit=50&offset=" + (i * 50), UserPlaylistsResponse.class);
            for (Item item : playlists.getItems()) {
                if (item.getOwner().getId().equals(userId)) {
                    playlistIds.add(item.getId());
                }
            }
        }
        return playlistIds;
    }

    private void addGenreFilterFromGenreInfo(String playlistIdDestination, Set<String> genres, Set<String> genresExactMatch, Set<String> genresExactExclude) {
        PlaylistResponse playlistResponse = spotifyClient.get("https://api.spotify.com/v1/playlists/" + playlistIdDestination, PlaylistResponse.class);
        String currentDescription = playlistResponse.getDescription();

        boolean hasGenreInfo = Pattern.compile("\\(genres\\)(.*?)\\(_exactGenresExcluded\\)").matcher(currentDescription).find();

        if (hasGenreInfo) {
            genres.addAll(trimAndToLowercaseAll(test("genres", currentDescription).split(",")));
            genresExactMatch.addAll(trimAndToLowercaseAll(test("exactGenres", currentDescription).split(",")));
            genresExactExclude.addAll(trimAndToLowercaseAll(test("exactGenresExcluded", currentDescription).split(",")));
        }
        if (hasGenreInfo) {
            String genresString = test("genres", currentDescription);
            String genresExactMatchString = test("exactGenres", currentDescription);
            String genresExactExcludeString = test("exactGenresExcluded", currentDescription);
            if (genresString != null && !genresString.isEmpty()) {
                genres.addAll(trimAndToLowercaseAll(genresString.split(",")));
            }
            if (!genresExactMatch.isEmpty()) {
                genresExactMatch.addAll(trimAndToLowercaseAll(genresExactMatchString.split(",")));
            }
            if (genresExactExcludeString != null && !genresExactExcludeString.isEmpty()) {
                genresExactExclude.addAll(trimAndToLowercaseAll(genresExactExcludeString.split(",")));
            }
        }
    }

    private void addGenreInformationToPlaylist(String playlistIdDestination, Set<String> genres, Set<String> exactGenres,
                                               Set<String> exactGenresExcluded) {
        PlaylistResponse playlistResponse = spotifyClient.get("https://api.spotify.com/v1/playlists/" + playlistIdDestination, PlaylistResponse.class);

        String currentDescription = playlistResponse.getDescription();

        Set<String> currentGenres = new HashSet<>(genres);
        Set<String> currentExactGenres = new HashSet<>(exactGenres);
        Set<String> currentExactGenresExcluded = new HashSet<>(exactGenresExcluded);

        boolean hasGenreInfo = Pattern.compile("\\(genres\\)(.*?)\\(_exactGenresExcluded\\)").matcher(currentDescription).find();

        if (hasGenreInfo) {
            String currentGenresString = test("genres", currentDescription);
            String currentExactGenresString = test("exactGenres", currentDescription);
            String currentExactGenresExcludedString = test("exactGenresExcluded", currentDescription);
            if (currentGenresString != null && !currentGenresString.isEmpty()) {
                currentGenres.addAll(Set.of(currentGenresString.split(",")));
            }
            if (currentExactGenresString != null && !currentExactGenresString.isEmpty()) {
                currentExactGenres.addAll(Set.of(currentExactGenresString.split(",")));
            }
            if (currentExactGenresExcludedString != null && !currentExactGenresExcludedString.isEmpty()) {
                currentExactGenresExcluded.addAll(Set.of(currentExactGenresExcludedString.split(",")));
            }
        }

        String genreInformationString = generateGenreInformationString(currentGenres, currentExactGenres, currentExactGenresExcluded);
        String newDescription;
        if (!hasGenreInfo) {
            newDescription = currentDescription + " " + genreInformationString;
        } else {
            newDescription = currentDescription.replaceAll("\\(genres\\)(.*?)\\(_exactGenresExcluded\\)", genreInformationString);
        }
        String requestBody = "{\"description\":\"" + newDescription + "\"}";
        spotifyClient.put("https://api.spotify.com/v1/playlists/" + playlistIdDestination, String.class, requestBody);
    }

    private String generateGenreInformationString(Set<String> currentGenres, Set<String> currentExactGenres, Set<String> currentExactGenresExcluded) {
        String genreInformationString = "(genres)";
        genreInformationString += String.join(",", currentGenres) + "(_genres)";
        genreInformationString += "(exactGenres)";
        genreInformationString += String.join(",", currentExactGenres) + "(_exactGenres)";
        genreInformationString += "(exactGenresExcluded)";
        genreInformationString += String.join(",", currentExactGenresExcluded) + "(_exactGenresExcluded)";

        return genreInformationString;
    }

    private String test(String fieldName, String currentDescription) {
        Pattern pattern = Pattern.compile("\\(" + fieldName + "\\)(.*?)\\(_" + fieldName + "\\)");
        Matcher matcher = pattern.matcher(currentDescription);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    private String parseIdFromPlaylistUrl(String playlistUrl) {
        return playlistUrl.substring(playlistUrl.indexOf("playlist/") + 9, playlistUrl.indexOf("?si="));
    }

    private String parseIdFromUserUrl(String userPlaylistSource) {
        return userPlaylistSource.substring(userPlaylistSource.indexOf("user/") + 5, userPlaylistSource.indexOf("?si="));
    }

    private void getMatchingGenreTrackUrisAddToPlaylistAt50(List<Item> items, Set<String> genres, Set<String> genresExactMatch, Set<String> genresExactExclude) {
        ArtistsResponse artistsResponse;
        Set<String> artistsList = new HashSet<>();
        for (Item item : items) {
            if (item.getTrack() == null) {
                continue;
            }

            if (!duplicatesAllowed) {
                if (playlistDestinationTrackList.contains(item.getTrack().getId())) {
                    continue;
                }
            }

            String artistId = item.getTrack().getArtists().get(0).getId();
            List<String> artistGenres = artistGenreMap.get(artistId);
            if (artistGenres == null) {

                if (artistId != null && !artistId.isBlank()) {
                    artistsList.add(artistId);
                }
                if (artistsList.size() == 50) {
                    ///////////////////////////////////////////////////////////////////////////////


                    artistsResponse = spotifyClient.get("https://api.spotify.com/v1/artists?ids=" + generateArtistList(artistsList),
                            ArtistsResponse.class);

                    for (Artist artist : artistsResponse.getArtists()) {
                        if (artist == null) {
                            continue;
                        }
                        artistGenreMap.put(artist.getId(), artist.getGenres());
                    }

                    artistsList = new HashSet<>();
                }


            }
        }
        if (artistsList.size() > 0) {
            artistsResponse = spotifyClient.get("https://api.spotify.com/v1/artists?ids=" + generateArtistList(artistsList),
                    ArtistsResponse.class);


            for (Artist artist : artistsResponse.getArtists()) {
                if (artist != null) {
                    artistGenreMap.put(artist.getId(), artist.getGenres());
                }
            }

        }

        for (Item item : items) {
            boolean hasGenre = false;
            if (item.getTrack() == null || item.getTrack().getId() == null || tracksProcessed.contains(item.getTrack().getId())) {
                continue;
            }
            tracksProcessed.add(item.getTrack().getId());
            for (Artist trackArtist : item.getTrack().getArtists()) {
                if (artistsToAddSongsBy.contains(trackArtist.getId())) {
                    addItemToTrackUrisAndUploadInBatchOf50(item);
                    break;
                }
            }
            List<String> artistGenres = artistGenreMap.get(item.getTrack().getArtists().get(0).getId());

            if (artistGenres == null) {
                continue;
            }

            if (genres != null) {
                for (String genre : genres) {
                    if (genre.isEmpty()) {
                        break;
                    }
                    for (String artistGenre : artistGenres) {
                        if (genresExactExclude.contains(artistGenre)) {
                            break;
                        }
                        if (artistGenre.contains(genre)) {
                            hasGenre = true;
                            break;
                        }
                    }
                    if (hasGenre) {
                        addItemToTrackUrisAndUploadInBatchOf50(item);
                        break;
                    }
                }
            }

            if (!hasGenre && genresExactMatch != null) {
                for (String genre : genresExactMatch) {
                    if (artistGenres.contains(genre)) {
                        addItemToTrackUrisAndUploadInBatchOf50(item);
                        break;
                    }
                }
            }
        }
    }

    private void addItemToTrackUrisAndUploadInBatchOf50(Item item) {
        trackUris.add(item.getTrack().getUri());
        if (trackUris.size() == 50) {
            addTracksToDestinationPlaylist();
            trackUris = new ArrayList<>();
        }
    }

    private String generateArtistList(Set<String> artistList) {
        StringBuilder artistString = new StringBuilder();
        Iterator<String> artistIter = artistList.iterator();
        while (artistIter.hasNext()) {
            artistString.append(artistIter.next());
            if (artistIter.hasNext()) {
                artistString.append(",");
            }
        }
        return artistString.toString();
    }

    private void addTracksToDestinationPlaylist() {
        spotifyClient.post("https://api.spotify.com/v1/playlists/" + playlistIdDestination + "/tracks",
                null, generateJsonWithSingleList(trackUris, "uris"));
    }

    private String generateJsonWithSingleList(List<String> uris, String fieldName) {
        StringBuilder json = new StringBuilder("{\"" + fieldName + "\":[");
        Iterator<String> urisIter = uris.iterator();
        while (urisIter.hasNext()) {
            json.append("\"").append(urisIter.next()).append("\"");
            if (!urisIter.hasNext()) {
                json.append("]}");
            } else {
                json.append(",");
            }
        }

        return json.toString();
    }
}
