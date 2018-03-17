package com.example.pavelhryts.notesviewer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.pavelhryts.notesviewer.R;
import com.example.pavelhryts.notesviewer.adapters.ListAdapter;
import com.example.pavelhryts.notesviewer.model.Note;
import com.example.pavelhryts.notesviewer.model.NoteHolder;

import static com.example.pavelhryts.notesviewer.util.Consts.MESSAGE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CREATE_MESSAGE = 1;

    private NoteHolder noteHolder = NoteHolder.getInstance();

    private RecyclerView list;

    private Menu optionMenu;

    private int pushedItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(fab);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        list = findViewById(R.id.list_view);
        list.setAdapter(new ListAdapter(this) {
            @Override
            public void onSelect(Note note) {
                updateMenuView();
            }

            @Override
            public void getPushedItem(Note note) {
                pushedItemId = noteHolder.indexOf(note);
            }
        });
        list.setLayoutManager(new LinearLayoutManager(this, 1, false));
        registerForContextMenu(list);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    private void updateMenuView(){
        if(optionMenu != null){
            if (noteHolder.getSelectedNotesCount() > 0) {
                optionMenu.getItem(2).setVisible(true);
                if (noteHolder.getSelectedNotesCount() == 1) {
                    optionMenu.getItem(1).setVisible(true);
                }
                else {
                    optionMenu.getItem(1).setVisible(false);
                }
            }
            if (noteHolder.getSelectedNotesCount() == 0) {
                optionMenu.getItem(1).setVisible(false);
                optionMenu.getItem(2).setVisible(false);
            }
        }
    }

    private void showPopupMenu(View view){
        PopupMenu menu = new PopupMenu(this, view);
        menu.inflate(R.menu.popup_menu);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_message_popup:
                        showMessageEditor(-1);
                        break;
                    case R.id.clear_notes_popup:
                        noteHolder.clearNotes();
                        list.getAdapter().notifyDataSetChanged();
                        break;
                    case R.id.clear_selection_popup:
                        noteHolder.selectNone();
                        list.getAdapter().notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
        menu.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        optionMenu = menu;
        updateMenuView();
        return true;
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.edit_message_context:
                showMessageEditor(pushedItemId);
                break;
            case R.id.delete_message_context:
                deletePushedNote(pushedItemId);
                break;
        }
        return false;
    }

    private void showMessageEditor(int index) {
        Intent messageIntent = new Intent(this, MessageActivity.class);
        messageIntent.putExtra(MESSAGE, index);
        startActivityForResult(messageIntent, CREATE_MESSAGE);
    }

    private void clearNotes() {
        noteHolder.clearNotes();
        list.getAdapter().notifyDataSetChanged();
        updateMenuView();
    }

    private void deleteNotes(){
        noteHolder.removeNotes(noteHolder.getSelectedNotes());
        noteHolder.selectNone();
        list.getAdapter().notifyDataSetChanged();
        updateMenuView();
    }

    private void deletePushedNote(int id){
        noteHolder.removeNote(id);
        noteHolder.selectNone();
        list.getAdapter().notifyDataSetChanged();
        updateMenuView();
    }

    private void clearSelection(){
        noteHolder.selectNone();
        updateMenuView();
        list.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        list.getAdapter().notifyDataSetChanged();
        noteHolder.selectNone();
        updateMenuView();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
