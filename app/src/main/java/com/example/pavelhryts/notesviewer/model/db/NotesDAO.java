package com.example.pavelhryts.notesviewer.model.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.pavelhryts.notesviewer.model.notes.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel.Hryts on 30.03.2018.
 */

public class NotesDAO extends BaseDAO<Note> {
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_BODY = "body";
    private static final String TABLE_NAME = "notes";

    private static volatile NotesDAO instance;

    private NotesDAO(){
        super(TABLE_NAME);
    }

    @Override
    ContentValues generateValues(Note note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_BODY, note.getBody());
        return values;
    }

    @Override
    List<Note> getObjectsFromCursor(Cursor cursor) {
        List<Note> result = new ArrayList<>();
        if(cursor != null && cursor.moveToFirst()) {
            do {
                result.add(new Note(cursor.getString(0), cursor.getString(1), cursor.getLong(2)));
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
        return new String[]{COLUMN_TITLE, COLUMN_BODY, COLUMN_ID};
    }

    public synchronized static NotesDAO getInstance(){
        if (instance == null)
            instance = new NotesDAO();
        return instance;
    }

    void createTable(){
        super.createTable(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT)",
                TABLE_NAME, COLUMN_ID, COLUMN_TITLE, COLUMN_BODY));
    }

    void upgrade(int oldVersion, int newVersion){
        if ((oldVersion == 1) && (newVersion == 2)) {
            String upgradeQuery = String.format("ALTER TABLE %s ADD COLUMN %s TEXT DEFAULT Title", TABLE_NAME, COLUMN_TITLE);
            if(getDatabase() != null)
                getDatabase().execSQL(upgradeQuery);
        }

    }

}
