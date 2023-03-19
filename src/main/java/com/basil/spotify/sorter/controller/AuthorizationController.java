package com.basil.spotify.sorter.controller;

import com.basil.spotify.sorter.models.SpotifyToken;
import com.basil.spotify.sorter.client.SpotifyClient;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthorizationController {

    @Autowired
    private SpotifyClient spotifyClient;

    @RequestMapping(value = "/authenticate", method = RequestMethod.GET)
    public void method(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Location", "https://accounts.spotify.com/authorize?" +
                "client_id=11a3f71e96a8452aa096c72690c25f96&response_type=code" +
                "&redirect_uri=http://localhost:8080/authorize&show_dialog=true" +
                "&scope=user-read-private playlist-read-private playlist-modify-public playlist-modify-private");
        httpServletResponse.setStatus(302);

    }

    @GetMapping(value = "/authorize")
    public ModelAndView authorize(@RequestParam String code) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.set("grant_type", "authorization_code");
        map.set("code", code);
        map.set("redirect_uri", "http://localhost:8080/authorize");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic MTFhM2Y3MWU5NmE4NDUyYWEwOTZjNzI2OTBjMjVmOTY6ODllMDM4ZmFhMmQ3NDViZGE4ZmI3NDBkNTNlNWRlZDU=");
        headers.set("Content-Type", "application/x-www-form-urlencoded");


        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(map, headers);

        spotifyClient.setAccessToken(restTemplate.postForEntity("https://accounts.spotify.com/api/token", request, SpotifyToken.class).getBody().getAccessToken());

        return new ModelAndView("redirect:/home");
    }

}
