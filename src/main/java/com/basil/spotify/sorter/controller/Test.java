package com.basil.spotify.sorter.controller;

import com.basil.spotify.sorter.models.SpotifyToken;
import com.basil.spotify.sorter.service.SpotifyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Controller
public class Test {

    @Autowired
    private SpotifyClient spotifyClient;



    @GetMapping(value = "/test")
    public String test(@RequestParam String code) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.set("grant_type", "authorization_code");
        map.set("code", code);
        map.set("redirect_uri", "http://localhost:8080/test");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic MTFhM2Y3MWU5NmE4NDUyYWEwOTZjNzI2OTBjMjVmOTY6ODllMDM4ZmFhMmQ3NDViZGE4ZmI3NDBkNTNlNWRlZDU=");
        headers.set("Content-Type", "application/x-www-form-urlencoded");


        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(map, headers);

        spotifyClient.setAccessToken(restTemplate.postForEntity("https://accounts.spotify.com/api/token", request, SpotifyToken.class).getBody().getAccessToken());

        return "test";
    }


}
