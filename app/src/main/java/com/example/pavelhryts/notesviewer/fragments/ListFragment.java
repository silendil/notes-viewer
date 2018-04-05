package com.example.pavelhryts.notesviewer.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pavelhryts.notesviewer.App;
import com.example.pavelhryts.notesviewer.R;
import com.example.pavelhryts.notesviewer.activities.MessageActivity;
import com.example.pavelhryts.notesviewer.adapters.ListAdapter;
import com.example.pavelhryts.notesviewer.model.notes.Note;
import com.example.pavelhryts.notesviewer.model.notes.NoteHolder;
import com.example.pavelhryts.notesviewer.model.weather.WeatherModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.pavelhryts.notesviewer.util.Consts.MESSAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements View.OnClickListener {

    private static final int CREATE_MESSAGE = 1;
    private RecyclerView list;
    private NoteHolder noteHolder = NoteHolder.getInstance();
    private TextView emptyMessage;
    private int pushedItemId;
    private Menu optionMenu;
    private String city = "";
    private final String SEPARATOR = "; ";
    private final String FILENAME = "Notes.sav";
    private final String DOCUMENTS = "/Documents";
    private boolean permissions = false;
    private ListAdapter listAdapter;
    private LinearLayoutManager linearManager;

    private final static String SHARED_NAME = "LIST_FRAGMENT";
    private final static String PERMISSIONS = "PERMISSIONS";

    private LocationListener locationListener;
    private LocationManager locationManager;

    public ListFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sf = getContext().getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        permissions = sf.getBoolean(PERMISSIONS, false);
        if (locationListener == null)
            locationListener = new LocListener();
        if (locationManager == null)
            initLocation();
        if (permissions) {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
                    3000L, 1.0F, locationListener);
            weatherInit(getView());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        noteHolder.initNotesFromDB();
        init(view);
        checkPermissions();
        getActivity().setTitle(getString(R.string.notes));
        return view;
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && !permissions) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        this.permissions = permissions.length != 0;
        if (requestCode == 100) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED)
                    this.permissions = false;
            }
        }
        initLocation();
        weatherInit(getView());
        SharedPreferences sp = getContext().getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(PERMISSIONS, this.permissions).apply();
    }

    private void init(View view) {
        listAdapter = new ListAdapter(getContext()) {
            @Override
            public void getPushedItem(Note note) {
                pushedItemId = noteHolder.indexOf(note);
            }

            @Override
            public void onSelect(Note note) {
                updateMenuView();
            }
        };
        linearManager = new LinearLayoutManager(getContext(), 1, false);
        list = view.findViewById(R.id.list_view);
        emptyMessage = view.findViewById(R.id.empty_message);
        list.setAdapter(listAdapter);
        list.setLayoutManager(linearManager);
        registerForContextMenu(list);
        checkListVisibility();
    }

    private void initLocation() {
        if (permissions) {
            if (locationManager == null)
                locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        }
    }

    private void updateCity(Location loc) {
        if (permissions) {
            Geocoder geocoder = new Geocoder(getContext());
            List<Address> list = null;
            try {
                if (loc != null)
                    list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (list != null && !list.isEmpty()) {
                Address address = list.get(0);
                city = String.format("%s, %s", address.getLocality(), address.getCountryName());
            }
        }
    }

    private void weatherInit(View view) {
        final TextView weatherView = view.findViewById(R.id.weather_view);
        weatherView.setSelected(true);
        if (locationManager == null)
            initLocation();
        if (permissions) {
            @SuppressLint("MissingPermission") Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            updateCity(loc);
            if (loc != null) {
                App.getApi().getData(getString(R.string.weather_key), loc.getLatitude(), loc.getLongitude())
                        .enqueue(new Callback<WeatherModel>() {
                            @Override
                            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                                String result = getString(R.string.weather_not_found);
                                if (response.isSuccessful()) {
                                    result = getWeatherString(response.body());
                                }
                                weatherView.setText(result);
                            }

                            @Override
                            public void onFailure(Call<WeatherModel> call, Throwable t) {
                                weatherView.setText(R.string.weather_not_found);
                            }
                        });
            } else
                weatherView.setText(R.string.weather_not_found);
        } else {
            weatherView.setText(R.string.weather_not_found);
        }
    }

    private String getWeatherString(WeatherModel model) {
        StringBuilder builder = new StringBuilder();
        if (model != null) {
            if (city.isEmpty())
                city = model.getName();
            builder.append(getString(R.string.location)).append(city).append(SEPARATOR)
                    .append(getString(R.string.weather_string)).append(model.getWeather().get(0).getDescription())
                    .append(SEPARATOR)
                    .append(getString(R.string.temp)).append(model.getMain().getIntTemp()).append("\u2103")
                    .append(SEPARATOR)
                    .append(getString(R.string.press)).append(model.getMain().getPressure())
                    .append(getString(R.string.press_units));
        } else {
            builder.append(getString(R.string.weather_not_found));
        }
        return builder.toString();
    }

    private void checkListVisibility() {
        if (noteHolder.isEmpty()) {
            list.setVisibility(View.INVISIBLE);
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            list.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.INVISIBLE);
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu menu = new PopupMenu(getContext(), view);
        menu.inflate(R.menu.popup_menu);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_message_popup:
                        showMessageEditor(-1);
                        break;
                    case R.id.clear_notes_popup:
                        noteHolder.clearNotes();
                        updateView();
                        break;
                    case R.id.clear_selection_popup:
                        noteHolder.selectNone();
                        updateView();
                        break;
                }
                return false;
            }
        });
        menu.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public void onStop() {
        if (locationListener != null && locationManager != null)
            locationManager.removeUpdates(locationListener);
        super.onStop();
    }

    public void updateView() {
        list.getAdapter().notifyDataSetChanged();
        updateMenuView();
        checkListVisibility();
    }

    private void showMessageEditor(int index) {
        Intent messageIntent = new Intent(getActivity(), MessageActivity.class);
        messageIntent.putExtra(MESSAGE, index);
        startActivityForResult(messageIntent, CREATE_MESSAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        noteHolder.selectNone();
        list.setAdapter(null);
        list.setLayoutManager(null);
        list.setAdapter(listAdapter);
        list.setLayoutManager(linearManager);
        updateView();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.edit_message_context:
                showMessageEditor(pushedItemId);
                break;
            case R.id.delete_message_context:
                deletePushedNote(pushedItemId);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        showPopupMenu(view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        optionMenu = menu;
        updateMenuView();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateMenuView() {
        if (optionMenu != null) {
            if (noteHolder.getSelectedNotesCount() > 0) {
                optionMenu.getItem(2).setVisible(true);
                optionMenu.getItem(4).setVisible(true);
                if (noteHolder.getSelectedNotesCount() == 1) {
                    optionMenu.getItem(1).setVisible(true);
                } else {
                    optionMenu.getItem(1).setVisible(false);
                }
            }
            if (noteHolder.getSelectedNotesCount() == 0) {
                optionMenu.getItem(1).setVisible(false);
                optionMenu.getItem(2).setVisible(false);
                optionMenu.getItem(4).setVisible(false);
            }
            if (noteHolder.isEmpty())
                optionMenu.getItem(3).setVisible(false);
            else
                optionMenu.getItem(3).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.add_message:
                showMessageEditor(-1);
                break;
            case R.id.clear_notes:
                clearNotes();
                break;
            case R.id.delete_message:
                deleteNotes();
                break;
            case R.id.edit_message:
                showMessageEditor(noteHolder.indexOf(noteHolder.getSelectedNotes().get(0)));
                break;
            case R.id.clear_selection:
                clearSelection();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearNotes() {
        noteHolder.clearNotes();
        updateView();
    }

    private void deleteNotes() {
        noteHolder.removeNotes(noteHolder.getSelectedNotes());
        noteHolder.selectNone();
        updateView();
    }

    private void clearSelection() {
        noteHolder.selectNone();
        updateView();
    }

    private void deletePushedNote(int id) {
        noteHolder.removeNote(id);
        noteHolder.selectNone();
        updateView();
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private void saveNotes() {
        String filename = getPath();
        File file;
        try {
            file = new File(filename);
            FileOutputStream fos;
            ObjectOutputStream oos;
            if (!file.exists())
                file.createNewFile();
            fos = new FileOutputStream(file, false);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(noteHolder.getNotes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadNotes() {
        String filename = getPath();
        File file;
        try {
            file = new File(filename);
            FileInputStream fis;
            ObjectInputStream ois;
            if (file.exists()) {
                fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);
                Object obj = ois.readObject();
                if (obj instanceof List && !((List) obj).isEmpty() && ((List) obj).get(0) instanceof Note) {
                    noteHolder.setNotes((List<Note>) obj);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getPath() {
        String path;
        if (isExternalStorageWritable()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                path = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                        + "/" + FILENAME;
            } else {
                File docsFolder = new File(Environment.getExternalStorageDirectory() + DOCUMENTS);
                if (!docsFolder.exists())
                    docsFolder.mkdir();
                path = docsFolder.getPath() + "/" + FILENAME;
            }
        } else {
            path = getActivity().getFilesDir() + "/" + FILENAME;
        }
        return path;
    }

    private class LocListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            updateCity(location);
            weatherInit(getView());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
