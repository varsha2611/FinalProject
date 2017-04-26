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

             public boolean WriteValuesToDatabase(String HeartRate, String StepCount, long timestamp,String SleepHours) {
                 float iHeartRate = Float.parseFloat(HeartRate);
                 float iStepCount = Float.parseFloat(StepCount);
                 float iSleepHrs = Float.parseFloat(SleepHours);

                 //Check if the date entry for the device is already in the table
                 if (true == EntryAlreadyExist(timestamp)) {
                     UpdateTableWithNewValues(iHeartRate, iStepCount, iSleepHrs, timestamp);
                     //UpdateExternalDatabase(iHeartRate,iStepCount,timestamp);
                     return true;
                 } else {
                     //Add new row in the table
                     // Gets the data repository in write mode
                     float previousSteps = getLastValue(FeedEntry.COLUMN_NAME_STEPS);
                     if(previousSteps < iStepCount)
                         iStepCount = iStepCount - previousSteps;

                     SQLiteDatabase db = this.getWritableDatabase();
                     float Calories = CalculateCaloriesFromHeartRateAndTime(iHeartRate);
                     ContentValues values = new ContentValues();
                     values.put(COLUMN_NAME_HBPM, iHeartRate);
                     values.put(COLUMN_NAME_STEPS, iStepCount);
                     values.put(COLUMN_NAME_CALORIES, Calories);
                     values.put(COLUMN_NAME_SLEEP,iSleepHrs);
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
                 //Log.i("today's date",Long.toString(millisecond));
                 // String currentDate  = (DateFormat.format("yyyy-MM-dd HH:mm:ss", todayDate.getTime())).toString();
                 SQLiteDatabase db = this.getWritableDatabase();
                 //if an entry exists that has value greater 00:00 today means an entry for today exist
                 String Query = "Select * from " + TABLE_NAME_WATCH_DATABASE + " where " + COLUMN_NAME_TIMESTAMP + " > '" + millisecond + "'";
                 Cursor res = db.rawQuery(Query, null);
                 if (res != null) {
                     if (res.moveToFirst() && res.getCount() > 0) {
                        // Log.i("No of rows",Integer.toString(res.getCount()));
                         return true;
                     }
                 }
                 return false;
             }

             public boolean UpdateTableWithNewValues(float HeartRate, float StepCount, float SleepHrs,long time) {
                 Date today = new Date();
                 today.setHours(0);
                 today.setMinutes(0);
                 today.setSeconds(0);
                 long millisecond = today.getTime();
                 float calories = CalculateCaloriesFromHeartRateAndTime(HeartRate);
                 float previousSteps = getLastValue(FeedEntry.COLUMN_NAME_STEPS);
                 if(previousSteps > StepCount)
                     StepCount = StepCount+previousSteps;
                 SQLiteDatabase db = this.getWritableDatabase();
                 ContentValues con = new ContentValues();
                 con.put(FeedEntry.COLUMN_NAME_STEPS, StepCount);
                 con.put(FeedEntry.COLUMN_NAME_HBPM, HeartRate);
                 con.put(FeedEntry.COLUMN_NAME_TIMESTAMP, time);
                 con.put(FeedEntry.COLUMN_NAME_CALORIES, calories);
                 con.put(FeedEntry.COLUMN_NAME_SLEEP,SleepHrs);
                 db.update(FeedEntry.TABLE_NAME_WATCH_DATABASE, con,
                         FeedEntry.COLUMN_NAME_TIMESTAMP + ">?",
                         new String[]{Long.toString(millisecond)});

                 return true;
             }

             public float CalculateCaloriesFromHeartRateAndTime(float HeartRate) {
                 float calories = 0.0f;
                 calories =  getLastValue(FeedEntry.COLUMN_NAME_CALORIES) +
                         ( (( -20.4022f + (0.4472f * HeartRate) - (0.1236f *50) + (0.074f *26))/4.184f)*(1.666668f))/100000;
                 return calories;
             }

             public String[] getSensorDataFromDatabase()
             {
                 String[] SensorData={"","","",""};
                 SQLiteDatabase db = this.getWritableDatabase();
                 String Query = "Select * from " + TABLE_NAME_WATCH_DATABASE + " ORDER BY " + FeedEntry.COLUMN_NAME_TIMESTAMP +" DESC LIMIT 1";
                 Cursor res = db.rawQuery(Query, null);
                 if (res != null)
                 {
                     if (res.moveToFirst() && res.getCount() > 0)
                     {
                         SensorData[0] = Float.toString(res.getFloat(res.getColumnIndex(FeedEntry.COLUMN_NAME_STEPS)));
                         SensorData[1] = Float.toString(res.getFloat(res.getColumnIndex(FeedEntry.COLUMN_NAME_HBPM)));
                         SensorData[2] = Float.toString(res.getFloat(res.getColumnIndex(FeedEntry.COLUMN_NAME_CALORIES)));
                         SensorData[3] = Float.toString(res.getFloat(res.getColumnIndex(FeedEntry.COLUMN_NAME_SLEEP)));
                     }
                 }
                 return SensorData;
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
             public float getLastValue(String ColumnName)
             {
                 Float data =0.0f;
                 SQLiteDatabase db = this.getWritableDatabase();
                 String Query = "Select * from " + TABLE_NAME_WATCH_DATABASE + " ORDER BY " + FeedEntry.COLUMN_NAME_TIMESTAMP +" DESC LIMIT 1";
                 Cursor res = db.rawQuery(Query, null);
                 if (res != null) {
                     if (res.moveToFirst() && res.getCount() > 0)
                         data = res.getFloat(res.getColumnIndex(ColumnName));
                 }
                     return data;
             }

             public String[] getMaxValueOfColumn(String columnName) {
                 SQLiteDatabase db = this.getWritableDatabase();
                 String [] TopThreeValues = new String [7];
                 TopThreeValues[0]="0.0";
                 TopThreeValues[1]="0.0";
                 TopThreeValues[2]="0.0";
                 String Query = "Select * from " + TABLE_NAME_WATCH_DATABASE + " ORDER BY  " + columnName + " DESC LIMIT 3";
                 Cursor res = db.rawQuery(Query, null);
                 if (res != null) {
                     if (res.moveToFirst() && res.getCount() > 0) {
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

