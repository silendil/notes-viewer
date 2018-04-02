package com.example.pavelhryts.notesviewer.model.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.pavelhryts.notesviewer.model.map.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel.Hryts on 30.03.2018.
 */

public class MarkerDAO extends BaseDAO<Marker> {
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_LONG = "longitude";
    private static final String COLUMN_LAT = "latitude";
    private static final String TABLE_NAME = "marker";

    private static final String[] allColumns = {COLUMN_LAT, COLUMN_LONG, COLUMN_TITLE, COLUMN_ID};

    private MarkerDAO(){
        super(TABLE_NAME);
    }

    private volatile static MarkerDAO instance;

    public synchronized static MarkerDAO getInstance(){
        if(instance == null)
            instance = new MarkerDAO();
        return instance;
    }

    void createTable(){
        super.createTable(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, " +
                            "%s REAL, %s REAL)",
                    TABLE_NAME, COLUMN_ID, COLUMN_TITLE, COLUMN_LAT, COLUMN_LONG));
    }

    void upgrade(int oldVersion, int newVersion){
        if ((oldVersion == 1) && (newVersion == 2)) {
            String upgradeQuery = String.format("ALTER TABLE %s ADD COLUMN %s TEXT DEFAULT Title", TABLE_NAME, COLUMN_TITLE);
            if(getDatabase() != null)
                getDatabase().execSQL(upgradeQuery);
        }

    }

    @Override
    ContentValues generateValues(Marker marker) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, marker.getTitle());
        values.put(COLUMN_LAT, marker.getLatitude());
        values.put(COLUMN_LONG, marker.getLongitude());
        return values;
    }

    @Override
    List<Marker> getObjectsFromCursor(Cursor cursor) {
        List<Marker> result = new ArrayList<>();
        if(cursor != null && cursor.moveToFirst()) {
            do {
                result.add(new Marker(cursor.getDouble(0),cursor.getDouble(1), cursor.getString(2), cursor.getLong(3)));
            } while (cursor.moveToNext());

            try {
                cursor.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    @Override
    String[] getAllColumns() {
        return new String[]{COLUMN_LAT, COLUMN_LONG, COLUMN_TITLE, COLUMN_ID};
    }

    private List<Marker> getMarkersFromCursor(Cursor cursor){
        List<Marker> result = new ArrayList<>();
        if(cursor != null && cursor.moveToFirst()) {
            do {
                result.add(new Marker(cursor.getDouble(0),cursor.getDouble(1), cursor.getString(2), cursor.getLong(3)));
            } while (cursor.moveToNext());

            try {
                cursor.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
