package com.basil.spotify.sorter.models.myModels;

public class PlaylistSortRequest {
    private String playlistUrlSource;
    private String genres;
    private String playlistUrlDestination;
    private String genresExactMatch;
    private String genresExactExclude;
    private String userPlaylistSource;
    private String addLikedSongsFromCurrentUser;
    private String artistIdToIncludeSongsBy;
    private String duplicatesAllowed;
    private String addGenreInformation;
    private String addGenreFilterFromGenreInfo;
    private String addPlaylistsFromCurrentUser;

    private boolean addGenreInformationBoolean;
    private boolean addLikedSongsFromCurrentUserBoolean;
    private boolean duplicatesAllowedBoolean;
    private boolean addGenreFilterFromGenreInfoBoolean;
    private boolean addPlaylistsFromCurrentUserBoolean;

    public String getPlaylistUrlSource() {
        return playlistUrlSource;
    }

    public void setPlaylistUrlSource(String playlistUrlSource) {
        this.playlistUrlSource = playlistUrlSource;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getPlaylistUrlDestination() {
        return playlistUrlDestination;
    }

    public void setPlaylistUrlDestination(String playlistUrlDestination) {
        this.playlistUrlDestination = playlistUrlDestination;
    }

    public String getGenresExactMatch() {
        return genresExactMatch;
    }

    public void setGenresExactMatch(String genresExactMatch) {
        this.genresExactMatch = genresExactMatch;
    }

    public String getGenresExactExclude() {
        return genresExactExclude;
    }

    public void setGenresExactExclude(String genresExactExclude) {
        this.genresExactExclude = genresExactExclude;
    }

    public String getUserPlaylistSource() {
        return userPlaylistSource;
    }

    public void setUserPlaylistSource(String userPlaylistSource) {
        this.userPlaylistSource = userPlaylistSource;
    }

    public String getAddLikedSongsFromCurrentUser() {
        return addLikedSongsFromCurrentUser;
    }

    public void setAddLikedSongsFromCurrentUser(String addLikedSongsFromCurrentUser) {
        this.addLikedSongsFromCurrentUser = addLikedSongsFromCurrentUser;
    }

    public String getArtistIdToIncludeSongsBy() {
        return artistIdToIncludeSongsBy;
    }

    public void setArtistIdToIncludeSongsBy(String artistIdToIncludeSongsBy) {
        this.artistIdToIncludeSongsBy = artistIdToIncludeSongsBy;
    }

    public String getDuplicatesAllowed() {
        return duplicatesAllowed;
    }

    public void setDuplicatesAllowed(String duplicatesAllowed) {
        this.duplicatesAllowed = duplicatesAllowed;
    }

    public String getAddGenreInformation() {
        return addGenreInformation;
    }

    public void setAddGenreInformation(String addGenreInformation) {
        this.addGenreInformation = addGenreInformation;
    }

    public String getAddGenreFilterFromGenreInfo() {
        return addGenreFilterFromGenreInfo;
    }

    public void setAddGenreFilterFromGenreInfo(String addGenreFilterFromGenreInfo) {
        this.addGenreFilterFromGenreInfo = addGenreFilterFromGenreInfo;
    }

    public String getAddPlaylistsFromCurrentUser() {
        return addPlaylistsFromCurrentUser;
    }

    public void setAddPlaylistsFromCurrentUser(String addPlaylistsFromCurrentUser) {
        this.addPlaylistsFromCurrentUser = addPlaylistsFromCurrentUser;
    }

    public boolean isAddGenreInformationBoolean() {
        return addGenreInformationBoolean;
    }

    public void setAddGenreInformationBoolean(boolean addGenreInformationBoolean) {
        this.addGenreInformationBoolean = addGenreInformationBoolean;
    }

    public boolean isAddLikedSongsFromCurrentUserBoolean() {
        return addLikedSongsFromCurrentUserBoolean;
    }

    public void setAddLikedSongsFromCurrentUserBoolean(boolean addLikedSongsFromCurrentUserBoolean) {
        this.addLikedSongsFromCurrentUserBoolean = addLikedSongsFromCurrentUserBoolean;
    }

    public boolean isDuplicatesAllowedBoolean() {
        return duplicatesAllowedBoolean;
    }

    public void setDuplicatesAllowedBoolean(boolean duplicatesAllowedBoolean) {
        this.duplicatesAllowedBoolean = duplicatesAllowedBoolean;
    }

    public boolean isAddGenreFilterFromGenreInfoBoolean() {
        return addGenreFilterFromGenreInfoBoolean;
    }

    public void setAddGenreFilterFromGenreInfoBoolean(boolean addGenreFilterFromGenreInfoBoolean) {
        this.addGenreFilterFromGenreInfoBoolean = addGenreFilterFromGenreInfoBoolean;
    }

    public boolean isAddPlaylistsFromCurrentUserBoolean() {
        return addPlaylistsFromCurrentUserBoolean;
    }

    public void setAddPlaylistsFromCurrentUserBoolean(boolean addPlaylistsFromCurrentUserBoolean) {
        this.addPlaylistsFromCurrentUserBoolean = addPlaylistsFromCurrentUserBoolean;
    }
}
