package com.basil.spotify.sorter.controller;

import com.basil.spotify.sorter.models.*;
import com.basil.spotify.sorter.client.SpotifyClient;
import com.basil.spotify.sorter.service.SortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

@Controller
public class SorterController {
    @Autowired
    private SortService sortService;

    @GetMapping("/genre-sort")
    public String sort(@ModelAttribute("playlistSortRequest") PlaylistSortRequest playlistSortRequest) {
        sortService.sort(playlistSortRequest);
        return "sortComplete";
    }
}
