package com.client.itrack.activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.client.itrack.model.CountryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 4/22/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Address";

    //table name
    private static final String TABLE_COUNTRY = "country";
    private static final String TABLE_STATE = "state";
    private static final String TABLE_CITY = "city";

    //Countrykey
    private static final String COUNTRY_ID = "countryid";
    private static final String COUNTRY_NAME = "countryname";

    //State Key
    private static final String STATE_COUNTRY_ID = "countryid";
    private static final String STATE_ID = "stateid";
    private static final String STATE_NAME = "statename";

    //citykey
    private static final String CITY_COUNTRY_ID = "countryid";
    private static final String CITY_STATE_ID = "stateid";
    private static final String CITY_ID = "cityid";
    private static final String CITY_NAME = "cityname";





    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_COUNTRY_TABLE = "CREATE TABLE " + TABLE_COUNTRY + "("
                +  COUNTRY_ID + " INTEGER PRIMARY KEY,"
                + COUNTRY_NAME + " TEXT" + ")";

        String CREATE_STATE_TABLE = "CREATE TABLE " + TABLE_STATE + "("
                +  STATE_COUNTRY_ID + " TEXT,"+ STATE_ID + " INTEGER PRIMARY KEY,"
                + STATE_NAME + " TEXT" + ")";

        String CREATE_CITY_TABLE = "CREATE TABLE " + TABLE_CITY + "("
                +  CITY_COUNTRY_ID + " TEXT,"+ CITY_STATE_ID + " TEXT,"
                + CITY_ID + " INTEGER PRIMARY KEY," + CITY_NAME + " TEXT" + ")";


        db.execSQL(CREATE_COUNTRY_TABLE);
        db.execSQL(CREATE_STATE_TABLE);
        db.execSQL(CREATE_CITY_TABLE);
    }

    public void addCountry(ArrayList<CountryModel> allcountry) {
        SQLiteDatabase db = this.getWritableDatabase();

        for(int i=0;i<allcountry.size();i++) {
            ContentValues values = new ContentValues();
            CountryModel countrymodel=allcountry.get(i);

            values.put(COUNTRY_ID, countrymodel.id); // Contact Phone Number
            values.put(COUNTRY_NAME, countrymodel.name); // Contact Name


            // Inserting Row
            db.insert(TABLE_COUNTRY, null, values);
        }
        db.close(); // Closing database connection
    }

    public String getCountryById(String Id) {

        String selectQuery = "SELECT " + COUNTRY_NAME + " FROM " + TABLE_COUNTRY + " WHERE " + COUNTRY_ID + "='" + Id + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                return cursor.getString(cursor.getColumnIndex(COUNTRY_NAME));
            } while (cursor.moveToNext());
        }

        // return contact list
        return "";
    }

    public String getStateById(String countryId, String stateId){

        String selectQuery = "SELECT "+STATE_NAME+" FROM " + TABLE_STATE+ " WHERE "+STATE_COUNTRY_ID+"='"+countryId+"' AND "+STATE_ID+"='"+stateId+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                return cursor.getString(cursor.getColumnIndex(STATE_NAME));
            } while (cursor.moveToNext());
        }

        // return contact list
        return "";

    }

    public List<CountryModel> getAllContacts() {
        List<CountryModel> countryList = new ArrayList<CountryModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_COUNTRY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CountryModel contact = new CountryModel();
                contact.id=String.valueOf(cursor.getString(0));
                contact.name=cursor.getString(1);

                countryList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return countryList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY);

        // Create tables again
        onCreate(db);
    }
}
