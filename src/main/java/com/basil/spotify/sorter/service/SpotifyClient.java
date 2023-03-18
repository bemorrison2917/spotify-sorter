package com.basil.spotify.sorter.service;

import org.slf4j.Logger;
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

    public <T> T get(String uri, Class<T> responseClass) throws HttpClientErrorException, InterruptedException {
      boolean retry = true;
        ResponseEntity<T> test = null;
        while(retry) {
           try {
               ResponseEntity<T> response = null;
               RestTemplate restTemplate = new RestTemplate();
               HttpHeaders headers = new HttpHeaders();

               headers.set("Authorization", "Bearer " + accessToken); //accessToken can be the secret key you generate.
               headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
               HttpEntity<String> entity = new HttpEntity<>(headers);

               test = restTemplate.exchange(uri, HttpMethod.GET, entity, responseClass);
               retry = false;
           }
           catch(HttpClientErrorException.TooManyRequests e) {
               int retryAfter = Integer.parseInt(e.getResponseHeaders().get(HttpHeaders.RETRY_AFTER).get(0));
               System.out.println("Too many requests, waiting back up period then trying again");
               Thread.sleep(1000 * retryAfter);
           }

       }
        return test.getBody();
    }

    public <T> T post(String uri, Class<T> responseClass, String requestBody) throws HttpClientErrorException, InterruptedException {
        boolean retry = true;
        ResponseEntity<T> test = null;
        while(retry) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();

                headers.set("Authorization", "Bearer " + accessToken); //accessToken can be the secret key you generate.
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);

                restTemplate.exchange(uri, HttpMethod.POST, entity, responseClass);
                System.out.println(test.getStatusCode());
            }
            catch(HttpClientErrorException.TooManyRequests e) {
                int retryAfter = Integer.parseInt(e.getResponseHeaders().get(HttpHeaders.RETRY_AFTER).get(0));
                System.out.println("Too many requests, waiting back up period then trying again");
                Thread.sleep(1000 * retryAfter);
            }
            retry = false;
        }
        return test.getBody();
    }


}
