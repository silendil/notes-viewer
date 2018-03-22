package com.example.pavelhryts.notesviewer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pavelhryts.notesviewer.R;
import com.example.pavelhryts.notesviewer.model.map.MapHolder;
import com.example.pavelhryts.notesviewer.model.map.Marker;

import java.util.Locale;

import static com.example.pavelhryts.notesviewer.util.Consts.LATITUDE;
import static com.example.pavelhryts.notesviewer.util.Consts.LONGITUDE;

public class MapEditorActivity extends AppCompatActivity implements View.OnClickListener{

    private MapHolder holder = MapHolder.getInstance();
    private EditText title;
    private double latitudeValue, longitudeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_editor);
        title = findViewById(R.id.marker_title);
        TextView latitude = findViewById(R.id.marker_latitude);
        TextView longitude = findViewById(R.id.marker_longitude);
        Intent intent = getIntent();
        latitudeValue = intent.getDoubleExtra(LATITUDE,0d);
        longitudeValue = intent.getDoubleExtra(LONGITUDE,0d);
        latitude.setText(String.format(Locale.ENGLISH,"%f",latitudeValue));
        longitude.setText(String.format(Locale.ENGLISH,"%f",longitudeValue));
        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                holder.addMarker(new Marker(latitudeValue,longitudeValue,title.getText().toString()));
                setResult(RESULT_OK);
                finish();
                break;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }

    }
}
