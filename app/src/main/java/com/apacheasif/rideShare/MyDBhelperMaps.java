package com.apacheasif.rideShare;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by apache_asif on 02-09-2016.
 */
public class MyDBhelperMaps extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "myBD";
    // table name
    private static final String TABLE_NAME = "location";


    private static final String KEY_DATE = "firstName";
    private static final String KEY_LOCATION = "lastName";


    private ArrayList<String> allProfiles = new ArrayList<String>();


    public MyDBhelperMaps(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_PROFILE_TABLE = "CREATE TABLE location ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date TEXT , " +
                "location TEXT)";

        db.execSQL(CREATE_PROFILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS profiles");

        this.onCreate(db);
    }


    public void saveLocation(String date, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, date);
        values.put(KEY_LOCATION, location);

        db.close();
        Log.d("data entered: ", date + " " + location);

    }


}