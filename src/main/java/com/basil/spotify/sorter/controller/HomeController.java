package com.basil.spotify.sorter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    @GetMapping(value = "/welcome")
    public String test() {
        return "welcome";
    }

}
