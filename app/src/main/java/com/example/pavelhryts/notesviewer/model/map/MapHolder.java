package com.example.pavelhryts.notesviewer.model.map;

import com.example.pavelhryts.notesviewer.model.db.MarkerDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Pavel.Hryts on 22.03.2018.
 */

public class MapHolder {

    private List<Marker> markers = new ArrayList<>();
    private static volatile MapHolder instance = null;

    private MarkerDAO markerDAO = MarkerDAO.getInstance();

    public static synchronized MapHolder getInstance(){
        if(instance == null)
            instance = new MapHolder();
        return instance;
    }

    private MapHolder() {
    }

    public void addMarker(Marker marker){
        markers.add(marker);
        markerDAO.add(marker);
    }

    public Marker findMarker(double latitude, double longitude){
        for(Marker marker : markers){
            if(marker.getLatitude() == latitude && marker.getLongitude() == longitude){
                return marker;
            }
        }
        return null;
    }

    public List<Marker> getMarkers(){
        return Collections.unmodifiableList(markers);
    }

    public Marker getLastMarker(){
        if(markers.isEmpty())
            return null;
        return markers.get(markers.size()-1);
    }

    public void initMarkersFromDB(){
        markers = markerDAO.getAll();
    }

    public boolean isEmpty(){
        return markers.isEmpty();
    }

    public void deleteMarker(Marker marker){
        markers.remove(marker);
        markerDAO.delete(marker);
    }
}
