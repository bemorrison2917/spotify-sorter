package com.basil.spotify.sorter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    @GetMapping(value = "/welcome")
    public String test() {
        return "welcome";
    }

    @GetMapping(value= "/home")
    public String home(){
        return "home";
    }

    @GetMapping(value = "/genre-sort")
    public String test2() {
        return "test";
    }

    @GetMapping(value = "/artist-search")
    public String test3() {
        return "artistSearch";
    }
}
