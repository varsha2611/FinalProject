package com.varshachauhan.myfitnesstracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.varshachauhan.myfitnesstracker.DatabaseHandler.FeedEntry.COLUMN_NAME_TIMESTAMP;

public class SyncActivity extends Activity {

    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    float iHeartRate =0.0f;
    float iSteps = 0.0f;
    long time = 0;

    // products JSONArray
    JSONArray images = null;
    // Hashmap for ListView

    private static String url_update_external = "https://people.cs.clemson.edu/~varshac/CPSC6820/Project/UpdateExternalDatabase.php";
    DatabaseHandler.FeedEntry.FeedReaderDbHelper mDBHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDBHandler = new DatabaseHandler.FeedEntry.FeedReaderDbHelper(this);
        //setContentView(R.layout.activity_sync);
        iHeartRate = Float.parseFloat(getIntent().getStringExtra("HBPM"));
        iSteps = Float.parseFloat(getIntent().getStringExtra("Steps"));
        time = Long.parseLong(getIntent().getStringExtra("time"));
        new SyncToExternalDatabase().execute();
        Log.i("INSYNC ", "Activity");
    }
    public class SyncToExternalDatabase extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SyncActivity.this);
            pDialog.setMessage("Backing up data. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... args) {
            /**
             * getting All products from url
             * */
            // Building Parameters
            mDBHandler.UpdateExternalDatabase(iHeartRate,iSteps,time);
           /* List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_update_external, "GET", params);
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                String newImagesInInternalDB = " '-1' ";

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    images = json.getJSONArray("images");

                    // looping through All Products
                    for (int i = 0; i < images.length(); i++) {
                        JSONObject c = images.getJSONObject(i);

                        // Storing each json item in variable
                        // String id = c.getString(DatabaseHandler.FeedEntry.COLUMN_NAME_IMAGE_ID);
                        String date = c.getString(COLUMN_NAME_TIMESTAMP);
                        //String deleteFlag = c.getString(DatabaseHandler.FeedEntry.COLUMN_NAME_DELETE_FLAG);

                        //Get same row in internal database
                       {
                            Cursor res = mDBHandler.getRowFromDatabase(imageName);
                            if(res != null )
                            {
                                if (res.moveToFirst() && res.getCount() > 0)
                                {
                                    do
                                    {

                                    }while(res.moveToNext());
                                }
                                else
                                {

                                }
                                res.close();
                            }
                        }
                    }
                    } else
                {
                    // no products found
                    // Launch Add New product Activity
                    Toast.makeText(getApplicationContext(),"Nothing to Update",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(),
                            MainActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            return null;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            Intent i = new Intent(getApplicationContext(),
                    MainActivity.class);
            // Closing all previous activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            // updating UI from Background Thread
        }

    }

}
