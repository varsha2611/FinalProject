package com.varshachauhan.myfitnesstracker;

/**
 * Created by varshac on 4/15/2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Alex on 4/2/2017.
 */


public class Database extends SQLiteOpenHelper{
    private SQLiteDatabase db;
    public static String getData = ""; //there has to be a better way of getting things out of getData
    Database(Context context){
        super(context, "mobileDatabase", null, 1);
        db = this.getWritableDatabase();
    }

    public void pullUserDevices(){
        new getData("Alex", "pass");
        try{
            JSONArray array = new JSONArray(getData);
            for(int i = 0; i < array.length(); i++){
                JSONObject object = array.getJSONObject(i);
                String watch = object.getString("DeviceId");
                if(!checkRow("watches", "deviceId", watch)){
                    db.execSQL("insert into watches (deviceId, nickname) values (" + watch + ", \"Watch #" + watch + "\")");
                }
            }
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
        db.execSQL("create table datagrams(deleted bool default false, watch_id text not null, sent_time timestamp, hbpm integer, steps integer, calories integer, sleep integer)");//, foreign key(WatchId) references Watches(WatchId))");
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
}
