package com.example.pavelhryts.notesviewer.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Pavel.Hryts on 30.03.2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "viewer.db";
    private static final int DB_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if(!NotesTable.getInstance().isDatabaseExists())
            NotesTable.getInstance().initDatabase(sqLiteDatabase);
        if(!MarkerTable.getInstance().isDatabaseExists())
            MarkerTable.getInstance().initDatabase(sqLiteDatabase);
        NotesTable.getInstance().createTable();
        MarkerTable.getInstance().createTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(!NotesTable.getInstance().isDatabaseExists())
            NotesTable.getInstance().initDatabase(sqLiteDatabase);
        if(!MarkerTable.getInstance().isDatabaseExists())
            MarkerTable.getInstance().initDatabase(sqLiteDatabase);
        NotesTable.getInstance().upgrade(i, i1);
        MarkerTable.getInstance().upgrade(i, i1);
    }
}
