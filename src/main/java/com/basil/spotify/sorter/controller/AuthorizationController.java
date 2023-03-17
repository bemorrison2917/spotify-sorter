package com.basil.spotify.sorter.controller;

import com.basil.spotify.sorter.client.SpotifyClient;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthorizationController {

    @Autowired
    private SpotifyClient spotifyClient;

    @RequestMapping(value = "/authenticate", method = RequestMethod.GET)
    public void method(HttpServletResponse httpServletResponse, @RequestParam String redirectPath) {
        httpServletResponse.setHeader("Location", "https://accounts.spotify.com/authorize?" +
                "client_id=11a3f71e96a8452aa096c72690c25f96&response_type=code" +
                "&redirect_uri=http://localhost:8080/authorize/" + redirectPath + "&show_dialog=true" +
                "&scope=user-read-private playlist-read-private playlist-modify-public playlist-modify-private user-library-read");
        httpServletResponse.setStatus(302);

    }

    @GetMapping(value = "/authorize/home")
    public ModelAndView authorizeHome(@RequestParam String code) {
        spotifyClient.setAccessToken(code, "home");
        return new ModelAndView("redirect:/home");
    }

    @GetMapping(value = "/authorize/genre-sort-home")
    public ModelAndView authorizeGenreSortHome(@RequestParam String code) {
        spotifyClient.setAccessToken(code, "genre-sort-home");
        return new ModelAndView("redirect:/genre-sort-home");
    }

    @GetMapping(value = "/authorize/artist-search-home")
    public ModelAndView authorizeArtistSearchHome(@RequestParam String code) {
        spotifyClient.setAccessToken(code, "artist-search-home");
        return new ModelAndView("redirect:/artist-search-home");
    }

}
