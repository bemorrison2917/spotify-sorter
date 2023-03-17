package com.basil.spotify.sorter.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class RedirectController {

    @RequestMapping(value = "/authenticate", method = RequestMethod.GET)
    public void method(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Location", "https://accounts.spotify.com/authorize?" +
                "client_id=11a3f71e96a8452aa096c72690c25f96&response_type=code" +
                "&redirect_uri=http://localhost:8080/test&show_dialog=true" +
                "&scope=user-read-private playlist-read-private playlist-modify-public playlist-modify-private");
        httpServletResponse.setStatus(302);

    }
}
