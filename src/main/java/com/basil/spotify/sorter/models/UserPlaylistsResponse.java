package com.basil.spotify.sorter.models;

import java.util.List;

public class UserPlaylistsResponse {
    private int total;
    private List<Item> items;


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
