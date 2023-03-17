package com.basil.spotify.sorter.controller;

import com.basil.spotify.sorter.models.myModels.PlaylistSortRequest;
import com.basil.spotify.sorter.service.SortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.HttpClientErrorException;

@Controller
public class SorterController {
    @Autowired
    private SortService sortService;

    @GetMapping("/genre-sort")
    public String sort(@ModelAttribute("playlistSortRequest") PlaylistSortRequest playlistSortRequest, Model model) {
        try {
            return sortService.sort(model, playlistSortRequest, playlistSortRequest.getDuplicatesAllowed(),
                    playlistSortRequest.getAddGenreFilterFromGenreInfo(),
                    playlistSortRequest.getAddPlaylistsFromCurrentUser(), playlistSortRequest.getAddLikedSongsFromCurrentUser());
        }
        catch(HttpClientErrorException.Unauthorized e) {
            return "authentication";
        }
    }
}
