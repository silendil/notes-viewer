package com.example.pavelhryts.notesviewer.model.map;

/**
 * Created by Pavel.Hryts on 22.03.2018.
 */

public class Marker {
    private double latitude;
    private double longitude;
    private String title;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Marker(double latitude, double longitude, String title) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
    }
}
