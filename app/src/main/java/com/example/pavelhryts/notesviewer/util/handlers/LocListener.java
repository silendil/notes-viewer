package com.example.pavelhryts.notesviewer.util.handlers;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocListener implements LocationListener {

    private LocationCallback callback;

    public LocListener(LocationCallback callback){
        this.callback = callback;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(callback != null) {
            callback.updateCity(location);
            callback.updateWeather(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public interface LocationCallback{
        void updateCity(Location location);
        void updateWeather(Location location);
    }
}
