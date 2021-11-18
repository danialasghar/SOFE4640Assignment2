package com.mobiledev.locationfinder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "Locations.db";
    public static final String TABLE_NAME = "location_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "Address";
    public static final String COL_3 = "Latitude";
    public static final String COL_4 = "Longitude";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    //Creates the initial database table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                 COL_2 + " TEXT, " + COL_3 + " REAL, " + COL_4 + " REAL);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Allows saving to the database
    public long saveLocation(String address, String latitude, String longitude){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_2, address);
        cv.put(COL_3, latitude);
        cv.put(COL_4, longitude);
        return db.insert(TABLE_NAME, null, cv);
    }

    //Reads from the database and returns a Cursor object with all the data
    public Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        if (db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    //Deletes a location from the db using the id
    public void deleteLocation(String id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, "id=?", new String[]{id});
    }

    //Updates a location in the db using the id
    public boolean updateNote(String id, String address, String latitude, String longitude){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1, id);
        cv.put(COL_2, address);
        cv.put(COL_3, latitude);
        cv.put(COL_4, longitude);
        long result = db.update(TABLE_NAME, cv,"id=?", new String[]{id});
        boolean saved = result > 0 ? true : false;
        return saved;
    }

}
