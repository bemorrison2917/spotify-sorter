package com.basil.spotify.sorter.client;

import com.basil.spotify.sorter.models.SpotifyToken;
import com.basil.spotify.sorter.util.Wait;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class SpotifyClient {
private RestTemplate restTemplate;
private static String accessToken;


    public void setAccessToken(String code, String redirectPath) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.set("grant_type", "authorization_code");
        map.set("code", code);
        map.set("redirect_uri", "http://localhost:8080/authorize/" + redirectPath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic MTFhM2Y3MWU5NmE4NDUyYWEwOTZjNzI2OTBjMjVmOTY6ODllMDM4ZmFhMmQ3NDViZGE4ZmI3NDBkNTNlNWRlZDU=");
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(map, headers);
        accessToken = restTemplate.postForEntity("https://accounts.spotify.com/api/token", request, SpotifyToken.class).getBody().getAccessToken();
    }

    public boolean hasAccessToken() {
        return accessToken != null;
    }

    public <T> T get(String uri, Class<T> responseClass) throws HttpClientErrorException {
        URI testUri = URI.create(uri);
      boolean retry = true;
        ResponseEntity<T> response = null;
        while(retry) {
           try {
               RestTemplate restTemplate = new RestTemplate();
               HttpHeaders headers = new HttpHeaders();

               headers.set("Authorization", "Bearer " + accessToken); //accessToken can be the secret key you generate.
               headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
               HttpEntity<String> entity = new HttpEntity<>(headers);

               response = restTemplate.exchange(testUri, HttpMethod.GET, entity, responseClass);
               retry = false;
           }
           catch(HttpClientErrorException.TooManyRequests e) {
               int retryAfter = Integer.parseInt(e.getResponseHeaders().get(HttpHeaders.RETRY_AFTER).get(0));
               System.out.println("Too many requests, waiting back up period then trying again");
               System.out.println(retryAfter);
               Wait.waitInMillis(1000 * retryAfter);
           }

       }
        return response.getBody();
    }

    public <T> T get(String uri, Class<T> responseClass, String requestBody) throws HttpClientErrorException {
        boolean retry = true;
        ResponseEntity<T> test = null;
        while(retry) {
            try {
                ResponseEntity<T> response = null;
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();

                headers.set("Authorization", "Bearer " + accessToken); //accessToken can be the secret key you generate.
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

                test = restTemplate.exchange(uri, HttpMethod.GET, entity, responseClass);
                retry = false;
            }
            catch(HttpClientErrorException.TooManyRequests e) {
                int retryAfter = Integer.parseInt(e.getResponseHeaders().get(HttpHeaders.RETRY_AFTER).get(0));
                System.out.println("Too many requests, waiting back up period then trying again");
                System.out.println(retryAfter);
                Wait.waitInMillis(1000 * retryAfter);
            }

        }
        return test.getBody();
    }

    public <T> T put(String uri, Class<T> responseClass, String requestBody) throws HttpClientErrorException {
        boolean retry = true;
        ResponseEntity<T> response = null;
        while(retry) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();

                headers.set("Authorization", "Bearer " + accessToken); //accessToken can be the secret key you generate.
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

                response = restTemplate.exchange(uri, HttpMethod.PUT, entity, responseClass);
                retry = false;
            }
            catch(HttpClientErrorException.TooManyRequests e) {
                int retryAfter = Integer.parseInt(e.getResponseHeaders().get(HttpHeaders.RETRY_AFTER).get(0));
                System.out.println("Too many requests, waiting back up period then trying again");
                System.out.println(retryAfter);
                Wait.waitInMillis(1000 * retryAfter);
            }

        }
        return response.getBody();
    }

    public <T> T post(String uri, Class<T> responseClass, String requestBody) throws HttpClientErrorException {
        boolean retry = true;
        ResponseEntity<T> response = null;
        while(retry) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();

                headers.set("Authorization", "Bearer " + accessToken); //accessToken can be the secret key you generate.
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);

                response = restTemplate.exchange(uri, HttpMethod.POST, entity, responseClass);

            }
            catch(HttpClientErrorException.TooManyRequests e) {
                int retryAfter = Integer.parseInt(e.getResponseHeaders().get(HttpHeaders.RETRY_AFTER).get(0));
                System.out.println("Too many requests, waiting back up period then trying again");
                System.out.println(retryAfter);
                Wait.waitInMillis(1000 * retryAfter);
            }
            retry = false;
        }
        return response.getBody();
    }


}
