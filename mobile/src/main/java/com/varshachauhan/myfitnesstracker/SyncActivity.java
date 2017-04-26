package com.varshachauhan.myfitnesstracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SyncActivity extends AppCompatActivity {

    JSONParser jParser = new JSONParser();
    Database dataBase;
    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    // products JSONArray
    JSONArray datagrams = null;

    private static String getDeviceDataForUser = "https://people.cs.clemson.edu/~varshac/CPSC6820/Project/GetDatagramsFromExternalDatabase.php";
    //DatabaseHandler.FeedEntry.FeedReaderDbHelper mDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        String type = getIntent().getStringExtra("type");
        String DeviceId = getIntent().getStringExtra("DeviceId");
        String sSteps = getIntent().getStringExtra("Steps");
        String sHBPM = getIntent().getStringExtra("HBPM");
        String stimestamp = getIntent().getStringExtra("TimeStamp");
        float Steps=0.0f;
        float HBPM =0.0f;
        long timestamp=0;
        try
        {
            if(sSteps !=null && sHBPM !=null && stimestamp!=null) {
                Steps = Float.parseFloat(sSteps);
                HBPM = Float.parseFloat(sHBPM);
                timestamp = Long.parseLong(stimestamp);
            }
        }catch(Exception e)
        {
            Log.i("Exception","HERE");
        }

        dataBase = new Database(this.getApplicationContext());
        SyncToExternalDatabase syncActivity = new SyncToExternalDatabase(DeviceId,Steps,HBPM,timestamp,type);
        syncActivity.execute();
    }
    public class SyncToExternalDatabase extends AsyncTask<String,String,String> {
        private String type;
        private String DeviceID;
        private float Steps;
        private float HBPM;
        private long timestamp;

        public SyncToExternalDatabase(String DeviceID, float Steps, float HBPM, long timestamp, String type) {
            this.DeviceID = DeviceID;
            this.Steps=Steps;
            this.HBPM=HBPM;
            this.timestamp=timestamp;
            this.type = type;
    }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... args) {

            DownLoadFromExternalDataBase();
                return null;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            Intent i = new Intent(getApplicationContext(),
                    MainActivity.class);
            // Closing all previous activities
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            // updating UI from Background Thread
        }
        public void DownLoadFromExternalDataBase()
        {
            /**
             * getting All products from url
             * */
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(getDeviceDataForUser, "GET", params);
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                String newImagesInInternalDB = " '-1' ";

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    datagrams = json.getJSONArray("datagrams");

                    // looping through All Products
                    for (int i = 0; i < datagrams.length(); i++)
                    {
                        Log.i("inside ","DownloadFromExternal");
                        JSONObject data = datagrams.getJSONObject(i);
                        String deviceId = data.getString("DeviceId");
                        Float Steps = Float.parseFloat(data.getString("Steps"));
                        Float HBPM = Float.parseFloat(data.getString("HBPM"));
                        Float Calories = Float.parseFloat(data.getString("Calories"));
                        long TimeStamp = Long.parseLong(data.getString("TimeStamp"));
                        if(false == dataBase.ValueInInternalDatabase(deviceId,TimeStamp))
                            dataBase.InsertSensorDataIntoTable(HBPM, Steps, TimeStamp, deviceId);

                    }
                } else
                {
                    Intent i = new Intent(getApplicationContext(),
                            MainActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}
