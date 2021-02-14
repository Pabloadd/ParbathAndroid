package com.holamundo.pabloxd.practicemaps.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {

    private LatLng position;
    private String title, snippet;
    private int iconPicture;
    private ToiletPlaces toiletPlaces;

    public ClusterMarker(LatLng position, String title, String snippet, int iconPicture, ToiletPlaces toiletPlaces) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.toiletPlaces = toiletPlaces;

    }

    public ClusterMarker() {
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(int iconPicture) {
        this.iconPicture = iconPicture;
    }

    public ToiletPlaces getToiletPlaces() {
        return toiletPlaces;
    }

    public void setToiletPlaces(ToiletPlaces toiletPlaces) {
        this.toiletPlaces = toiletPlaces;
    }
}
