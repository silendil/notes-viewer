package com.example.pavelhryts.notesviewer.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
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
import com.example.pavelhryts.notesviewer.services.WeatherService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import static com.example.pavelhryts.notesviewer.util.Consts.MESSAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements View.OnClickListener, WeatherService.WeatherCallback {

    private static final int CREATE_MESSAGE = 1;
    private RecyclerView list;
    private NoteHolder noteHolder = NoteHolder.getInstance();
    private TextView emptyMessage;
    private int pushedItemId;
    private Menu optionMenu;
    private final String FILENAME = "Notes.sav";
    private final String DOCUMENTS = "/Documents";
    private ListAdapter listAdapter;
    private LinearLayoutManager linearManager;

    private Typeface weatherFont;

    private final static String SHARED_NAME = "LIST_FRAGMENT";
    private final static String PERMISSIONS = "PERMISSIONS";

    private WeatherService weatherService;
    private boolean bound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WeatherService.ServiceBinder binder = (WeatherService.ServiceBinder) service;
            weatherService = binder.getService();
            weatherService.registerCallback(ListFragment.this);
            weatherService.requestWeatherInformation();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    private void initFonts(TextView weatherTextView) {
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        weatherTextView.setTypeface(weatherFont);
    }

    public ListFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        Intent serviceIntent = new Intent(getContext(),WeatherService.class);
        getActivity().bindService(serviceIntent,mConnection,Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        noteHolder.initNotesFromDB();
        init(view);
        getActivity().setTitle(getString(R.string.notes));
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean perms = permissions.length != 0;
        if (requestCode == 100) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED)
                    perms = false;
            }
        }
        weatherService.setPermissions(perms);
        if(perms)
            weatherService.requestWeatherInformation();
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
        super.onStop();
        if(bound){
            getActivity().unbindService(mConnection);
            bound = false;
        }
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
        list.setAdapter(null);
        list.setLayoutManager(null);
        list.setAdapter(listAdapter);
        list.setLayoutManager(linearManager);
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

    @Override
    public void updateWeatherState(String weatherState) {
        if(getView() != null) {
            TextView weatherView = getView().findViewById(R.id.weather_view);
            weatherView.setSelected(true);
            initFonts(weatherView);
            weatherView.setText(weatherState);
        }
    }

    @Override
    public void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

}
