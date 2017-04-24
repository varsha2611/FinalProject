package com.varshachauhan.myfitnesstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.format.DateFormat;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Varsha on 4/1/2017.
 */

public class DatabaseHandler {
    //Constructor
    public DatabaseHandler() {
    }

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME_WATCH_DATABASE = "WATCH_DATABASE";
        public static final String TABLE_NAME_WATCH_DEVICEID = "WATCH_DEVICE_ID";
        public static final String COLUMN_NAME_HBPM= "HBPM";
        public static final String COLUMN_NAME_STEPS= "STEPS";
        public static final String COLUMN_NAME_CALORIES= "CALORIES";
        public static final String COLUMN_NAME_SLEEP= "SLEEP";
        public static final String COLUMN_NAME_TIMESTAMP= "TIMESTAMP";
        public static final String COLUMN_NAME_DEVICEID= "DEVICE_ID";



        public static final String SQL_CREATE_WATCH_DATABASE_TABLE =
                "CREATE TABLE " + FeedEntry.TABLE_NAME_WATCH_DATABASE+ " (" +
                        FeedEntry.COLUMN_NAME_HBPM + " INTEGER," +
                        FeedEntry.COLUMN_NAME_STEPS+ " INTEGER," +
                        FeedEntry.COLUMN_NAME_CALORIES + " INTEGER,"+
                        FeedEntry.COLUMN_NAME_SLEEP+ " INTEGER," +
                        FeedEntry.COLUMN_NAME_TIMESTAMP+ " TIMESTAMP" +
                        ")";
        public static final String SQL_CREATE_WATCH_DEVICEID_TABLE =
                "CREATE TABLE " + FeedEntry.TABLE_NAME_WATCH_DEVICEID+ " (" +
                        FeedEntry.COLUMN_NAME_DEVICEID + " TEXT )";

        private static final String SQL_DELETE_ENTRIES_WATCH_DATABASE =
                "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME_WATCH_DATABASE;
        private static final String SQL_DELETE_ENTRIES_WATCH_DEVICE_ID =
                "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME_WATCH_DEVICEID;

         /**
         * FeedReaderHelper Class to create Database
         */
        public static class FeedReaderDbHelper extends SQLiteOpenHelper {

             // If you change the database schema, you must increment the database version.
             public static final int DATABASE_VERSION = 1;
             public static final String DATABASE_NAME = "WatchDatabase.db";

             public FeedReaderDbHelper(Context context) {
                 super(context, DATABASE_NAME, null, DATABASE_VERSION);
                 this.getWritableDatabase();
             }

             public void onCreate(SQLiteDatabase db) {
                 db.execSQL(SQL_CREATE_WATCH_DATABASE_TABLE);
                 db.execSQL(SQL_CREATE_WATCH_DEVICEID_TABLE);
             }

             public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                 // This database is only a cache for online data, so its upgrade policy is
                 // to simply to discard the data and start over
                 db.execSQL(SQL_DELETE_ENTRIES_WATCH_DATABASE);
                 db.execSQL(SQL_DELETE_ENTRIES_WATCH_DEVICE_ID);
                 onCreate(db);
             }

             public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                 onUpgrade(db, oldVersion, newVersion);
             }

             public boolean WriteValuesToDatabase(String HeartRate, String StepCount, long timestamp) {
                 float iHeartRate = Float.parseFloat(HeartRate);
                 float iStepCount = Float.parseFloat(StepCount);
                 //Check if the date entry for the device is already in the table
                 if (true == EntryAlreadyExist(timestamp)) {
                     UpdateTableWithNewValues(iHeartRate, iStepCount, timestamp);
                     //UpdateExternalDatabase(iHeartRate,iStepCount,timestamp);
                     return true;
                 } else {
                     //Add new row in the table
                     // Gets the data repository in write mode

                     SQLiteDatabase db = this.getWritableDatabase();
                     double Calories = CalculateCaloriesFromHeartRateAndTime(iHeartRate);
                     ContentValues values = new ContentValues();
                     values.put(COLUMN_NAME_HBPM, HeartRate);
                     values.put(COLUMN_NAME_STEPS, StepCount);
                     values.put(COLUMN_NAME_CALORIES, Calories);
                     values.put(COLUMN_NAME_TIMESTAMP, timestamp);
                     // Insert the new row, returning the primary key value of the new row
                     long newRowId;
                     newRowId = db.insert(
                             TABLE_NAME_WATCH_DATABASE,
                             null,
                             values);
                     if (newRowId == -1)
                         return false;
                     else
                         return true;
                 }
             }

             public boolean EntryAlreadyExist(long timestamp) {
                 Date today = new Date();
                 today.setHours(0);
                 today.setMinutes(0);
                 today.setSeconds(0);
                 //String date = (DateFormat.format("dd-MM-yyyy",today.getTime())).toString();
                 long millisecond = today.getTime();
                 // String currentDate  = (DateFormat.format("yyyy-MM-dd HH:mm:ss", todayDate.getTime())).toString();
                 SQLiteDatabase db = this.getWritableDatabase();
                 //if an entry exists that has value greater 00:00 today means an entry for today exist
                 String Query = "Select * from " + TABLE_NAME_WATCH_DATABASE + " where " + COLUMN_NAME_TIMESTAMP + " > '" + millisecond + "'";
                 Cursor res = db.rawQuery(Query, null);
                 if (res != null) {
                     if (res.moveToFirst() && res.getCount() > 0) {
                         return true;
                     }
                 }
                 return false;
             }

             public boolean UpdateTableWithNewValues(float HeartRate, float StepCount, long time) {
                 Date today = new Date();
                 today.setHours(0);
                 today.setMinutes(0);
                 today.setSeconds(0);
                 //String date = (DateFormat.format("dd-MM-yyyy",today.getTime())).toString();
                 long millisecond = today.getTime();
                 //String sTodayDate = df.format(todayDate);
                 SQLiteDatabase db = this.getWritableDatabase();
                 ContentValues con = new ContentValues();
                 con.put(FeedEntry.COLUMN_NAME_STEPS, StepCount);
                 con.put(FeedEntry.COLUMN_NAME_HBPM, HeartRate);
                 con.put(FeedEntry.COLUMN_NAME_TIMESTAMP, time);
                 db.update(FeedEntry.TABLE_NAME_WATCH_DATABASE, con,
                         FeedEntry.COLUMN_NAME_TIMESTAMP + ">?",
                         new String[]{Long.toString(millisecond)});

                 return true;
             }

             public double CalculateCaloriesFromHeartRateAndTime(float HeartRate) {
                 double calories = 0.0f;
                 return calories;
             }

             public Cursor getValuesFromWatchDatabase() {
                 Cursor res = null;
                 return res;
             }

             public String getDeviceId() {
                 SQLiteDatabase db = this.getWritableDatabase();
                 String DeviceId = "";
                 //if an entry exists that has value greater 00:00 today means an entry for today exist
                 String Query = "Select * from " + TABLE_NAME_WATCH_DEVICEID;
                 Cursor res = db.rawQuery(Query, null);
                 if (res != null) {
                     if (res.moveToFirst() && res.getCount() > 0) {
                         DeviceId = res.getString(0);
                     }
                 }
                 return DeviceId;
             }

             public boolean StoreDeviceIdToDatabase(String devideId) {
                 SQLiteDatabase db = this.getWritableDatabase();
                 ContentValues values = new ContentValues();
                 values.put(COLUMN_NAME_DEVICEID, devideId);
                 // Insert the new row, returning the primary key value of the new row
                 long newRowId;
                 newRowId = db.insert(
                         TABLE_NAME_WATCH_DEVICEID,
                         null,
                         values);
                 if (newRowId == -1)
                     return false;
                 else
                     return true;
             }

             public String[] getMaxValueOfColumn(String columnName) {
                 SQLiteDatabase db = this.getWritableDatabase();
                 String [] TopThreeValues = new String [7];
                 TopThreeValues[0]="0.0";
                 TopThreeValues[1]="0.0";
                 TopThreeValues[2]="0.0";
                 String Query = "Select * from " + TABLE_NAME_WATCH_DATABASE + " ORDER BY  " + columnName;
                 Cursor res = db.rawQuery(Query, null);
                 if (res != null) {
                     if (res.moveToFirst() && res.getCount() > 0) {
                         Log.i("row count", Integer.toString(res.getCount()));
                         int count = 0;
                         do {
                             Float data = res.getFloat(res.getColumnIndex(columnName));
                             if (count == 0)
                                 TopThreeValues[0] = Float.toString(data);
                             if (count == 1)
                                 TopThreeValues[1] = Float.toString(data);
                             if (count == 2)
                                 TopThreeValues[2] = Float.toString(data);
                             count++;
                             // do what ever you want here
                         } while (res.moveToNext());
                     }
                 }
                 return TopThreeValues;
             }
         }
    }
}

