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
        if(!NotesDAO.getInstance().isDatabaseExists())
            NotesDAO.getInstance().initDatabase(sqLiteDatabase);
        if(!MarkerDAO.getInstance().isDatabaseExists())
            MarkerDAO.getInstance().initDatabase(sqLiteDatabase);
        NotesDAO.getInstance().createTable();
        MarkerDAO.getInstance().createTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(!NotesDAO.getInstance().isDatabaseExists())
            NotesDAO.getInstance().initDatabase(sqLiteDatabase);
        if(!MarkerDAO.getInstance().isDatabaseExists())
            MarkerDAO.getInstance().initDatabase(sqLiteDatabase);
        NotesDAO.getInstance().upgrade(i, i1);
        MarkerDAO.getInstance().upgrade(i, i1);
    }
}
