package com.example.pavelhryts.notesviewer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pavelhryts.notesviewer.R;
import com.example.pavelhryts.notesviewer.model.map.MapHolder;
import com.example.pavelhryts.notesviewer.model.map.Marker;
import com.example.pavelhryts.notesviewer.util.handlers.TextChangeHandler;

import java.util.Locale;

import static com.example.pavelhryts.notesviewer.util.Consts.LATITUDE;
import static com.example.pavelhryts.notesviewer.util.Consts.LONGITUDE;

public class MapEditorActivity extends AppCompatActivity {

    private MapHolder holder = MapHolder.getInstance();
    private EditText title;
    private TextInputLayout til;
    private double latitudeValue, longitudeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_editor);
        init();
    }

    private void init(){
        title = findViewById(R.id.marker_title);
        til = findViewById(R.id.map_title_layout);
        if(title.getText().toString().isEmpty())
            til.setError(getString(R.string.required));
        TextView latitude = findViewById(R.id.marker_latitude);
        TextView longitude = findViewById(R.id.marker_longitude);
        Intent intent = getIntent();
        latitudeValue = intent.getDoubleExtra(LATITUDE,0d);
        longitudeValue = intent.getDoubleExtra(LONGITUDE,0d);
        latitude.setText(String.format(Locale.ENGLISH,"%f",latitudeValue));
        longitude.setText(String.format(Locale.ENGLISH,"%f",longitudeValue));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.done){
            holder.addMarker(new Marker(latitudeValue,longitudeValue,title.getText().toString()));
            setResult(RESULT_OK);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_editor_option,menu);
        if(title.getText().toString().isEmpty())
            menu.getItem(0).setVisible(false);
        title.addTextChangedListener(new TextChangeHandler(til, menu, getString(R.string.required)));
        return super.onCreateOptionsMenu(menu);
    }
}
