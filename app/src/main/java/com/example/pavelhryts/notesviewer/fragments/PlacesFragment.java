package com.example.pavelhryts.notesviewer.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.pavelhryts.notesviewer.R;
import com.example.pavelhryts.notesviewer.activities.MapEditorActivity;
import com.example.pavelhryts.notesviewer.model.map.MapHolder;
import com.example.pavelhryts.notesviewer.model.map.Marker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static com.example.pavelhryts.notesviewer.util.Consts.LATITUDE;
import static com.example.pavelhryts.notesviewer.util.Consts.LONGITUDE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap = null;
    private LocationManager mLocManager = null;
    private Context context;

    private MapHolder mapHolder = MapHolder.getInstance();

    private static final int REQUEST = 1;

    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_option_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.map_position:
                if(mMap.isMyLocationEnabled()){
                    @SuppressLint("MissingPermission")
                    final Location loc = mLocManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    if(loc != null){
                        CameraUpdate camera =
                                CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(),loc.getLongitude()),15F);
                        mMap.animateCamera(camera);
                    }
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLocManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
        fragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit();
        return inflater.inflate(R.layout.fragment_places, container, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        } else {
            mMap.setMyLocationEnabled(true);

            // Disable my-location button
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            /*mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    return false;
                }
            });*/

            // Enable zoom controls
            mMap.getUiSettings().setZoomControlsEnabled(true);

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Add new marker?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showMarkerEditor(latLng);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
    }

    private void showMarkerEditor(LatLng latLng){
        Intent mapUpdate = new Intent(getActivity(), MapEditorActivity.class);
        mapUpdate.putExtra(LATITUDE,latLng.latitude);
        mapUpdate.putExtra(LONGITUDE,latLng.longitude);
        startActivityForResult(mapUpdate,REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            Marker marker = mapHolder.getLastMarker();
            LatLng latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f);
            mMap.addMarker(new MarkerOptions().position(latLng).title(marker.getTitle()));
            mMap.animateCamera(cameraUpdate);
        }
    }
}
