package com.example.pavelhryts.notesviewer.model.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pavelhryts.notesviewer.model.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pavel.Hryts on 30.03.2018.
 */

public abstract class BaseDAO<T extends BaseModel> {

    private static final String COLUMN_ID = "_id";

    private String tableName;

    private SQLiteDatabase database;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    SQLiteDatabase getDatabase(){
        return database;
    }

    BaseDAO(String tableName) {

        this.tableName = tableName;
    }

    void createTable(String sql){
        if(database != null){
            database.execSQL(sql);
        }
    }

    public void initDatabase(SQLiteDatabase database){
        this.database = database;
    }

    public void delete(T t){
        if(database != null){
            database.delete(tableName, getIdFilter(t),null);
        }
    }

    public void deleteAll(){
        if(database != null){
            database.delete(tableName, null,null);
        }
    }

    abstract ContentValues generateValues(T t);

    public void add(T t){
        if(database != null) {
            ContentValues values = generateValues(t);
            t.setId(database.insert(tableName, null, values));
        }
    }

    public void edit(T t){
        if(database != null) {
            ContentValues values = generateValues(t);
            database.update(tableName, values, getIdFilter(t), null );
        }
    }

    private String getIdFilter(T t){
        return String.format(Locale.ENGLISH,"%s=%d",COLUMN_ID, t.getId());
    }

    abstract List<T> getObjectsFromCursor(Cursor cursor);

    public List<T> getAll(){
        if(database != null){
            Cursor cursor = database.query(tableName, getAllColumns(),null, null,
                    null, null, null);
            return getObjectsFromCursor(cursor);
        }
        return new ArrayList<>(0);
    }

    abstract String[] getAllColumns();

    public boolean isDatabaseExists(){
        return database != null;
    }

    public boolean isExists(T t){
        if(database != null){
            Cursor cursor = database.query(tableName,getAllColumns(),getIdFilter(t),
                    null,null,null,null);
            boolean result = cursor.getCount() > 0;
            try{
                cursor.close();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return result;
        }else
            return false;
    }
}
