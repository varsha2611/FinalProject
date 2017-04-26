package com.varshachauhan.myfitnesstracker;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alex on 4/12/2017.
 */

public class getData extends AsyncTask<String, Void, String> {
    private String userName;
    private String passWord;
    private String requestType;
    private String DeviceID;
    private float Steps;
    private float HBPM;
    private long timestamp;

    public getData (String user, String pass, String type) throws ExecutionException, InterruptedException {
        Log.i("getData", "starting getData");
        userName = user;
        passWord = pass;
        requestType = type;
        Log.i("getData", "\"" + userName + "\"\t\"" + passWord + "\"\t\"" + requestType + "\"");
        String hi = this.execute().get();
        Log.i("getData hi", hi);
    }
    public getData (String DeviceID, float Steps, float HBPM, long timestamp, String type) throws ExecutionException, InterruptedException{
        this.DeviceID = DeviceID;
        this.Steps = Steps;
        this.HBPM = HBPM;
        this.timestamp = timestamp;
        this.requestType = type;
        this.execute().get();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private String getURL(){
        if(requestType.equals("getUserDevices"))
            return "https://people.cs.clemson.edu/~asferre/cpsc4820/Assignment4/getUserDevices.php?user=" + userName + "&pass=" + passWord;
        if(requestType.equals("validateLogin"))
            return "https://people.cs.clemson.edu/~asferre/cpsc4820/Assignment4/validateLogin.php?Username=" + userName + "&password=" + passWord;
        if(requestType.equals("checkUser"))
            return "https://people.cs.clemson.edu/~asferre/cpsc4820/Assignment4/checkUser.php?user=" + userName;
        if(requestType.equals("addUser"))
            return "https://people.cs.clemson.edu/~asferre/cpsc4820/Assignment4/addUser.php?name=" + userName + "&pass=" + passWord;
        if(requestType.equals("leaderBoard"))
            return "https://people.cs.clemson.edu/~asferre/cpsc4820/Assignment4/getLeaders.php";
        return "";
    }

    @Override
    protected String doInBackground(String... params){
        if(requestType.equals("Upload"))
        {
            String url = "https://people.cs.clemson.edu/~varshac/CPSC6820/Project/UploadDataToExternalDatabase.php";
            AddDataToExternalDatabase(DeviceID, Steps, HBPM, timestamp,url);
            return "";
        }
        DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httppost = new HttpPost(getURL());
        List<NameValuePair> myParams = new ArrayList<NameValuePair>();
        InputStream inputStream = null;
        String result = "";
        try{
            httppost.setEntity(new UrlEncodedFormEntity(myParams));
            HttpResponse response = httpClient.execute(httppost);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }
            result = sb.toString();

        } catch (Exception e){} finally {
            try {
                if(inputStream != null) inputStream.close();
            } catch (Exception squish){}
        }
        Database.getData = result;
        return result;
    }

    @Override
    protected void onPostExecute(String result){
        Database.getData = result;
    }

    public void AddDataToExternalDatabase(String DeviceID, float Steps, float HBPM, long timestamp, String URL){
        // Building Parameters
        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        //String date = (DateFormat.format("dd-MM-yyyy",today.getTime())).toString();
        long millisecond = today.getTime();
        Log.i("todayat12",Long.toString(millisecond));
        Log.i("AddDataToExtDatabase",Long.toString(timestamp));
        JSONParser jsonParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("DeviceId", DeviceID ));
        params.add(new BasicNameValuePair("Steps", Float.toString(Steps)));
        params.add(new BasicNameValuePair("HBPM", Float.toString(HBPM)));
        params.add(new BasicNameValuePair("timestamp", Long.toString(timestamp)));
        params.add(new BasicNameValuePair("today", Long.toString(millisecond)));
        JSONObject json = jsonParser.makeHttpRequest(URL, "POST", params);
    }
}
