package com.basil.spotify.sorter.controller;

import com.basil.spotify.sorter.models.myModels.MyArtistSearchRequest;
import com.basil.spotify.sorter.models.myModels.PlaylistSortRequest;
import com.basil.spotify.sorter.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class MainController {
    @Autowired
    private HomeService homeService;


    @GetMapping(value = "/")
    public String defaultHome(){
        return homeService.getHomeOrLoginPage();
    }

    @GetMapping(value= "/home")
    public String home(){
        return homeService.getHomeOrLoginPage();
    }

    @GetMapping(value = "/genre-sort-home")
    public String genreSortHome(@ModelAttribute("playlistSortRequest") PlaylistSortRequest playlistSortRequest, Model model) {
        model.addAttribute("playlistSortRequest", playlistSortRequest);
        return homeService.getGenreSortHomeOrLoginPage();
    }

    @GetMapping(value = "/artist-search-home")
    public String artistSearchHome(MyArtistSearchRequest artistSearchRequest, Model model) {
        return homeService.getArtistSearchHomeOrLoginPage();
    }
}
