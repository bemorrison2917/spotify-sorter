package com.basil.spotify.sorter.util;

import com.basil.spotify.sorter.models.ArtistSearchResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EncodeUrl {
    public static String encodeUrlString(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is unknown");
        }
    }
}
