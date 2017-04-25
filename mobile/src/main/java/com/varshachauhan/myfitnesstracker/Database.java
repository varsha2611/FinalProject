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
    private SQLiteDatabase db;
    public static String getData = ""; //there has to be a better way of getting things out of getData
    Database(Context context){
        super(context, "mobileDatabase", null, 2);
        db = this.getWritableDatabase();
    }

    private String user = "Alex";
    private String pass = "pass";

    public void setLogin(String userName, String passWord){
        user = userName;
        pass = passWord;
    }


    public void pullUserDevices(){
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
    }

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

    public Cursor select(String querry){
        return db.rawQuery(querry, null);
    }

    public void exec(String querry){
        db.execSQL(querry);
    }

    public void receiveDatagram(Integer watch_id, String timestamp, Integer hbpm, Integer steps, Integer calories, Integer sleep){
        if(checkRow("datagrams", "deviceId", Integer.toString(watch_id))){
            if(!timestamp.equals("")){
                String messageStart = "insert into datagrams (";
                String messageEnd = ") values (";
                if(hbpm > 0) {
                    messageStart += "hbpm";
                    messageEnd += Integer.toString(hbpm);
                }
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table watches( deleted bool default false, deviceId text not null, nickname text not null, lastsynch timestamp)");
        db.execSQL("create table datagrams(deleted bool default false, deviceId text not null, sent_time timestamp, hbpm integer, steps integer, calories integer, sleep integer)");
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
    public void AddUserToExternalDatabase(String url, String DeviceId, String Devicename)
    {
        // Building Parameters
        JSONParser jsonParser = new JSONParser();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("DeviceId", DeviceId ));
            params.add(new BasicNameValuePair("DeviceName", Devicename));
            JSONObject json = jsonParser.makeHttpRequest(url,
                    "POST", params);
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

            SQLiteDatabase db = this.getWritableDatabase();
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
        SQLiteDatabase db = this.getWritableDatabase();
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
        Log.i("today's date",Long.toString(millisecond));
        // String currentDate  = (DateFormat.format("yyyy-MM-dd HH:mm:ss", todayDate.getTime())).toString();
        SQLiteDatabase db = this.getWritableDatabase();
        //if an entry exists that has value greater 00:00 today means an entry for today exist
        String Query = "Select * from datagrams where sent_time >  \"" + millisecond + "\" AND deviceId = \""
                + DeviceId + "\"" ;
        Log.i("Query",Query);
        Cursor res = db.rawQuery(Query, null);
        if (res != null) {
            if (res.moveToFirst() && res.getCount() > 0) {
                Log.i("No of rows",Integer.toString(res.getCount()));
                return true;
            }
        }
        return false;
    }
}
