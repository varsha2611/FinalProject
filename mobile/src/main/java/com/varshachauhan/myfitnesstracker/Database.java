package com.varshachauhan.myfitnesstracker;

/**
 * Created by varshac on 4/15/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alex on 4/2/2017.
 */


public class Database extends SQLiteOpenHelper{
    private static SQLiteDatabase db;
    public static String getData = "asdf"; //there has to be a better way of getting things out of getData
    Database(Context context){
        super(context, "mobileDatabase", null, 2);
        db = this.getWritableDatabase();
    }
    public static Cursor select(String querry){ return db.rawQuery(querry, null); }
    public void exec(String querry){ db.execSQL(querry); }

    public void setLogin(String userName, String passWord){
        db.execSQL("update login set username = \"" + userName + "\" where asdf = '1'");
        db.execSQL("update login set password = \"" + passWord + "\" where asdf = '1'");
        if(isLoggedIn())
            Log.i("login test", "you are logged in");
    }

    public static boolean isLoggedIn(){
        String Query = "select username, password from login where asdf = '1'" ;
        Cursor res = db.rawQuery(Query, null);
        if (res != null) {
            if (res.moveToFirst() && res.getCount() > 0) {
                //Log.i("login", "username: " + res.getString(0));
                //Log.i("login", "password: " + res.getString(1));
                if(!res.getString(0).equals("") && !res.getString(1).equals(""))
                    return true;
            } else {
                Log.i("login", "there's some weird kind of error going on");
            }
        } else {
            Log.i("test", "querry is null");
        }
        /*Log.i("test", "reached");
        Cursor c = select(Query);
        Log.i("test", "querry made");
        if (c.getCount() <= 0) {
            c.close();
            Log.i("login", "something weird went wrong");
            return false;
        } else {
            Log.i("test", Integer.toString(c.getCount()));
            try {
                Log.i("test", c.getString(0));
            } catch (Exception e){
                Log.i("test", "empty querry maybe?");
            }
            Log.i("asdf", c.getColumnName(0));
            //Log.i("asdf", c.getString(0));
            if(!c.getString(0).equals("") && !c.getString(1).equals("")){
                Log.i("test", "couldn't be here");
                c.close();
                Log.i("test", "definitely not this");
                return true;
            } else {
                Log.i("test", "another weird crash");
                c.close();
                Log.i("test", "no way");
                return false;
            }
        }*/
        return false;
    }

    public void logOut(){
        db.execSQL("update login set username = \"\" where asdf = '1'");
        db.execSQL("update login set password = \"\" where asdf = '1'");
    }

    /*public void pullUserDevices(){
        new getData(user, pass);
        try{
            JSONArray array = new JSONArray(getData);
            for(int i = 0; i < array.length(); i++){
                JSONObject object = array.getJSONObject(i);
                String watch = object.getString("DeviceId");
                if(!checkRow("watches", "deviceId", watch)){
                    db.execSQL("insert into watches (deviceId, nickname) values (" + watch + ", \"Watch #" + watch + "\")");
                }
            }
            //create table watches(deleted bool default false, deviceId text not null, nickname text not null, lastsynch timestamp)
            //create table datagrams(deleted bool default false, watch_id text not null, sent_time timestamp, hbpm integer, steps integer, calories integer, sleep integer)");//, foreign key(WatchId) references Watches(WatchId))
        } catch (Exception e){}
    } */

    //check if there is a row with a certain value
    public boolean checkRow(String table, String column, String value){
        String Query = "select * from " + table + " where " + column + "=\""+ value +"\" AND deleted = 'false'" ;
        Log.i("Query",Query);
        Cursor c = db.rawQuery(Query, null);
        if (c.getCount() <= 0) {
            c.close();
            return false;
        } else {
            c.close();
            return true;
        }
    }



    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table watches( deleted bool default false, deviceId text not null, nickname text not null, lastsynch timestamp)");
        db.execSQL("create table datagrams(deleted bool default false, deviceId text not null, sent_time timestamp, hbpm integer, steps integer, calories integer, sleep integer)");
        db.execSQL("create table login(asdf integer, username text, password text)");
        db.execSQL("insert into login (asdf, username, password) values (1, \"\", \"\")");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS watches");
        db.execSQL("DROP TABLE IF EXISTS datagrams");
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean InsertSensorDataIntoTable(float HeartRate, float StepCount, long time, String DeviceId)
    {
        if (true == EntryAlreadyExist(DeviceId)) {
            UpdateTableWithNewValues( HeartRate,StepCount,time,DeviceId);
            //UpdateExternalDatabase(iHeartRate,iStepCount,timestamp);
            return true;
        } else {
            //Add new row in the table
            // Gets the data repository in write mode

            //SQLiteDatabase db = this.getWritableDatabase();
            ContentValues con = new ContentValues();
            con.put("deviceId", DeviceId);
            con.put("hbpm", HeartRate);
            con.put("steps", StepCount);
            con.put("calories",0.0);
            con.put("sleep", 0.0);
            con.put("sent_time", time);
            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    "datagrams",
                    null,
                    con);
            if (newRowId == -1)
                return false;
            else
                return true;
        }
    }
    public boolean UpdateTableWithNewValues(float HeartRate, float StepCount, long time, String DeviceId) {
        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        long millisecond = today.getTime();
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues con = new ContentValues();
        con.put("deviceId", DeviceId);
        con.put("hbpm", HeartRate);
        con.put("steps", StepCount);
        con.put("calories",0.0);
        con.put("sleep", 0.0);
        con.put("sent_time", time);
        db.update("datagrams", con,
                 "DeviceId  =? AND sent_time > ?",
                new String[]{DeviceId, Long.toString(millisecond)});

        return true;
    }
    public boolean EntryAlreadyExist(String DeviceId) {
        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        //String date = (DateFormat.format("dd-MM-yyyy",today.getTime())).toString();
        long millisecond = today.getTime();
        SQLiteDatabase db = this.getWritableDatabase();
        //if an entry exists that has value greater 00:00 today means an entry for today exist
        String Query = "Select * from datagrams where sent_time >  \"" + millisecond + "\" AND deviceId = \""
                + DeviceId + "\"" ;
        Cursor res = db.rawQuery(Query, null);
        if (res != null) {
            if (res.moveToFirst() && res.getCount() > 0) {
                return true;
            }
        }
        return false;
    }
   public boolean ValueInInternalDatabase(String deviceId, Long TimeStamp)
   {
       SQLiteDatabase db = this.getWritableDatabase();
       //if an entry exists that has value greater 00:00 today means an entry for today exist
       String Query = "Select * from datagrams where sent_time >  \"" + TimeStamp + "\" AND deviceId = \""
               + deviceId + "\"" ;
       Cursor res = db.rawQuery(Query, null);
       if (res != null) {
           if (res.moveToFirst() && res.getCount() > 0) {
               return true;
           }
       }
       return false;
   }
   public String[] getRecentSensorData(String deviceId)
   {
       SQLiteDatabase db = this.getWritableDatabase();
       String [] SensorData = {"","",""};
       //if an entry exists that has value greater 00:00 today means an entry for today exist
       String Query = "Select * from datagrams where deviceId = \"" + deviceId + "\" ORDER BY sent_time DESC LIMIT 1";;
       Cursor res = db.rawQuery(Query, null);
       if (res != null) {
           if (res.moveToFirst() && res.getCount() > 0)
           {
               SensorData[0] = Float.toString(res.getFloat(res.getColumnIndex("steps")));
               SensorData[1] = Float.toString(res.getFloat(res.getColumnIndex("hbpm")));
               SensorData[2] = Float.toString(res.getFloat(res.getColumnIndex("calories")));
           }
       }
       return SensorData;
   }
}
