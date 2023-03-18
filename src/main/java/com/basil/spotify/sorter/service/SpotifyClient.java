package com.basil.spotify.sorter.service;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class SpotifyClient {
private RestTemplate restTemplate;
private String accessToken;


    public void setAccessToken(String accessToken) {
        System.out.println("client:" + accessToken);
        this.accessToken = accessToken;
    }

    public <T> T get(String uri, Class<T> responseClass) throws HttpClientErrorException {
        ResponseEntity<T> response = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Bearer " + accessToken); //accessToken can be the secret key you generate.
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity <> (headers);

        ResponseEntity<T> test = restTemplate.exchange(uri, HttpMethod.GET, entity, responseClass);
        System.out.println(test.getStatusCode());
        return test.getBody();
    }

    public <T> T post(String uri, Class<T> responseClass, List<String> uris) throws HttpClientErrorException {
        ResponseEntity<T> response = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        MultiValueMap<String, List<String>> body = new LinkedMultiValueMap<String, List<String>>();

        body.add("uris", uris);

        headers.set("Authorization", "Bearer " + accessToken); //accessToken can be the secret key you generate.
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<?> entity = new HttpEntity <>(body, headers);

        ResponseEntity<T> test = restTemplate.exchange(uri, HttpMethod.POST, entity, responseClass);
        System.out.println(test.getStatusCode());
        return test.getBody();
    }


}
