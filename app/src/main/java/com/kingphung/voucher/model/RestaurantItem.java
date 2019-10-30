package com.kingphung.voucher.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class RestaurantItem implements ClusterItem {
    private LatLng position;
    private String title;
    private String snippet;
    public RestaurantItem(double lat, double lng, String title, String snippet){
        this.position = new LatLng(lat,lng);
        this.title = title;
        this.snippet = snippet;
    }
    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}
