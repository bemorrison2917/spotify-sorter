package com.basil.spotify.sorter.controller;

import com.basil.spotify.sorter.models.ArtistSearchResponse;
import com.basil.spotify.sorter.models.Item;
import com.basil.spotify.sorter.models.api.ArtistGenre;
import com.basil.spotify.sorter.models.api.ArtistSearchRequest;
import com.basil.spotify.sorter.service.ArtistSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {
    @Autowired
    private ArtistSearchService artistSearchService;

    @GetMapping("/artist-search")
    public String artistSearch(@ModelAttribute("artistSearchRequest") ArtistSearchRequest artistSearchRequest, Model model) {
        if(artistSearchRequest.getArtistName().length() > 100) {
            return "artistLengthError";
        }
        try {
            ArtistSearchResponse searchResponse = artistSearchService.search(artistSearchRequest.getArtistName());
            List<ArtistGenre> artistGenres = new ArrayList<>();
            for (Item item : searchResponse.getArtists().getItems()) {
                ArtistGenre artistGenre = new ArtistGenre();
                artistGenre.setName(item.getName());
                if (item.getGenres() == null || item.getGenres().isEmpty()) {
                    artistGenre.setGenres("no genres listed");
                } else {
                    artistGenre.setGenres(String.join(", ", item.getGenres()));
                }
                artistGenre.setId(item.getId());
                if (item.getImages().size() > 0) {
                    artistGenre.setImageUrl(item.getImages().get(0).getUrl());
                }
                artistGenres.add(artistGenre);
            }
            model.addAttribute("artistGenres", artistGenres);
            return "artistSearchResults";
        }
        catch(HttpClientErrorException.Unauthorized e) {
            return "authentication";
        }
    }
}
