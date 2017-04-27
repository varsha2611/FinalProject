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
        dataBase = new Database(this.getApplicationContext());
        SyncToExternalDatabase syncActivity = new SyncToExternalDatabase();
        syncActivity.execute();
    }
    public class SyncToExternalDatabase extends AsyncTask<String,String,String> {

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
                        JSONObject data = datagrams.getJSONObject(i);
                        String deviceId = data.getString("DeviceId");
                        Float Steps = Float.parseFloat(data.getString("Steps"));
                        Float HBPM = Float.parseFloat(data.getString("HBPM"));
                        Float Calories = Float.parseFloat(data.getString("Calories"));
                        Float fSleep = Float.parseFloat(data.getString("Sleep"));
                        long TimeStamp = Long.parseLong(data.getString("TimeStamp"));
                        if(false == dataBase.ValueInInternalDatabase(deviceId,TimeStamp))
                            dataBase.InsertSensorDataIntoTable(HBPM, Steps,Calories,fSleep,TimeStamp, deviceId);

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
