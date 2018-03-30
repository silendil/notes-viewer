package com.example.pavelhryts.notesviewer.activities;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.pavelhryts.notesviewer.R;
import com.example.pavelhryts.notesviewer.fragments.AboutFragment;
import com.example.pavelhryts.notesviewer.fragments.FeedbackFragment;
import com.example.pavelhryts.notesviewer.fragments.ListFragment;
import com.example.pavelhryts.notesviewer.fragments.PlacesFragment;
import com.example.pavelhryts.notesviewer.model.db.DatabaseHelper;
import com.example.pavelhryts.notesviewer.model.db.MarkerTable;
import com.example.pavelhryts.notesviewer.model.db.NotesTable;

import static com.example.pavelhryts.notesviewer.util.Consts.FRAGMENT_ID;
import static com.example.pavelhryts.notesviewer.util.Consts.SHARED_NAME;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton fab;

    private SharedPreferences sp;

    private int fragmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences(SHARED_NAME, MODE_PRIVATE);

        initDB();

        initFAB();
        initDrawer();
        initNavigationView();

        fragmentId = sp.getInt(FRAGMENT_ID, R.id.nav_notes);
        selectFragment();
    }

    private void initDB(){
        SQLiteDatabase database = new DatabaseHelper(getApplicationContext()).getWritableDatabase();
        NotesTable.getInstance().initDatabase(database);
        MarkerTable.getInstance().initDatabase(database);
    }

    private void initList(){
        ListFragment listFragment = new ListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,listFragment).commit();
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(listFragment);
    }

    private void initAbout(){
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new AboutFragment()).commit();
        fab.setVisibility(View.INVISIBLE);
    }

    private void initPlaces(){
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new PlacesFragment()).commit();
        fab.setVisibility(View.INVISIBLE);
    }

    private void initNavigationView(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initFAB() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void initDrawer(){
        Toolbar toolbar = initToolbar();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private Toolbar initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        fragmentId = item.getItemId();
        sp.edit().putInt(FRAGMENT_ID, fragmentId).apply();
        selectFragment();

        ((NavigationView) findViewById(R.id.nav_view)).setCheckedItem(fragmentId);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void selectFragment(){
        switch (fragmentId){
            case R.id.nav_notes:
                initList();
                break;
            case R.id.nav_about:
                initAbout();
                break;
            case R.id.nav_places:
                initPlaces();
                break;
            case R.id.nav_feedback:
                initFeedback();
                break;
        }
    }

    private void initFeedback() {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new FeedbackFragment()).commit();
        fab.setVisibility(View.INVISIBLE);
    }
}
