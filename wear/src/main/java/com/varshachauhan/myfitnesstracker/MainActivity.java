package com.varshachauhan.myfitnesstracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends WearableActivity implements SensorEventListener, DataApi.DataListener,
        MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks
{

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);
    //Alex's test comment

    private BoxInsetLayout mContainerView;
    private TextView mStepsView;
    private TextView mHBPMView;
    private TextView mDate;
    private ImageButton mNotification;
    private SensorManager mSensorManager;
    DatabaseHandler.FeedEntry.FeedReaderDbHelper mDBHandler;
    private Sensor mStepCount;
    private Sensor mHeartRate;
    private Sensor mSleepHours;
    private String sStepCount = "0";
    private String sHeartRate = "0";
    private String sSleepHours = "0";
    long SensorTimeStamp=0;
    private static final String WEAR_MESSAGE_PATH = "/message";
    private static final String WEAR_NODEID_PATH ="/nodeId";
    private GoogleApiClient mApiClient;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mStepsView = (TextView) findViewById(R.id.nbofsteps);
        mHBPMView = (TextView) findViewById(R.id.hbpm);
        mDate = (TextView)findViewById(R.id.date);
        mNotification = (ImageButton)findViewById(R.id.notification);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepCount = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mHeartRate = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSleepHours = mSensorManager.getDefaultSensor(Sensor.TYPE_MOTION_DETECT);
        mDBHandler = new DatabaseHandler.FeedEntry.FeedReaderDbHelper(this);
        View view;
        initGoogleApiClient();
        LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.activity_main, null);
        final ImageButton button = (ImageButton) view.findViewById(R.id.notification);
        mNotification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.i("inside function","this definition");

            }
        });
        final ImageButton sendDataBtn = (ImageButton) view.findViewById(R.id.sendData);
        mNotification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.i("inside function","this definition");
                OnButtonClickSendData(v);

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        int check = 0;
        if(id==R.id.sync)
        {
            if(id==R.id.sync)
            {
                Intent infoIntent = new Intent(this, SyncActivity.class);
                infoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(infoIntent);
                finish();
            }
        }
        return true;
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {

        Date date = new Date();
        String currentDate  = (DateFormat.format("MMM dd HH:mm:ss", date.getTime())).toString();
        mContainerView.setBackgroundColor(getResources().getColor(android.R.color.white));
        mStepsView.setTextColor(getResources().getColor(android.R.color.black));
        mHBPMView.setTextColor(getResources().getColor(android.R.color.black));
        mDate.setText(currentDate);
        mStepsView.setText(sStepCount);
        mHBPMView.setText(sHeartRate);
        mDBHandler.WriteValuesToDatabase(sHeartRate,sStepCount,SensorTimeStamp);
    }
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override

    public final void onSensorChanged(SensorEvent event) {
        long sensorTimeReference = 0l;
        long myTimeReference = 0l;
        float StepCount = 0;
        float HeartRate = 0;
        float MotionDetect = 0;
        if(sensorTimeReference == 0l && myTimeReference == 0l) {
            sensorTimeReference = event.timestamp;
            myTimeReference = System.currentTimeMillis();
        }
        /*  HR= 119
            Age = 34
            Weight = 80kg
            EE = (-55.0969 + 0.6309 x 119 + 0.1988 x 80 + 0.2017 x 34)
            EE = -55.0969 + 75 + 16 + 7 (using 1sf)
            EE = 43KJ/min
            EE = 43KJ/min x 15 min
            EE = 643KJ x 0.23Kcal/KJ
            EE = 154Kcal
         */
        // set event timestamp to current time in milliseconds
        SensorTimeStamp = myTimeReference +
                Math.round((event.timestamp - sensorTimeReference) / 1000000.0);
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER)
        {
            String sEvent = event.toString();
            StepCount=event.values[0];
            sStepCount = Float.toString(StepCount);
            AddSensorDataToDataLayer();
        }
        if(event.sensor.getType() == Sensor.TYPE_HEART_RATE)
        {
            String sEvent = event.toString();
            HeartRate=event.values[0];
            sHeartRate = Float.toString(HeartRate);
            AddSensorDataToDataLayer();
        }
        if(event.sensor.getType() == Sensor.TYPE_MOTION_DETECT)
        {
            String sEvent = event.toString();
            MotionDetect=event.values[0];
            sSleepHours = Float.toString(MotionDetect);
        }
        updateDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mStepCount, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mHeartRate, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSleepHours, SensorManager.SENSOR_DELAY_FASTEST);
        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void AddSensorDataToDataLayer()
    {
        Float fHBPM = Float.parseFloat(sHeartRate);
        Float fSteps = Float.parseFloat(sStepCount);
        String DeviceId = mDBHandler.getDeviceId();
        if(DeviceId !=null)
            Log.i("DeviceId in watch",DeviceId);

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/mobile");
        putDataMapReq.getDataMap().putFloat("Steps", fSteps);
        putDataMapReq.getDataMap().putFloat("HBPM", fHBPM);
        if(DeviceId != null)
         putDataMapReq.getDataMap().putString("DeviceId",DeviceId);
        putDataMapReq.setUrgent();
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mApiClient, putDataReq);
    }

    public void OnButtonClickSendData(View view)
    {
        Intent i = new Intent(MainActivity.this, SendNotification.class);
        i.putExtra("HBPM", sHeartRate);
        Log.i("Called", "SendNotification");
        startActivity(i);
        finish();
    }
    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks( this )
                .build();
        Wearable.DataApi.addListener(mApiClient, this);
        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }

        @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onMessageReceived( final MessageEvent messageEvent ) {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                Log.i("path",messageEvent.getPath());
                Log.i("Data",new String(messageEvent.getData()));
                if( messageEvent.getPath().equalsIgnoreCase( WEAR_MESSAGE_PATH ) ) {
                    String user = new String (messageEvent.getData());
                    AlertDialog.Builder DeviceAddRequest = new AlertDialog.Builder(context);
                    DeviceAddRequest.setMessage(user + " has requested to add the device");
                    DeviceAddRequest.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent i = new Intent(MainActivity.this, SendNotification.class);
                            i.putExtra("response","Decline");
                            startActivity(i);
                            finish();
                        }
                    });
                    DeviceAddRequest.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent i = new Intent(MainActivity.this, SendNotification.class);
                            i.putExtra("response","Accept");
                            startActivity(i);
                            finish();
                        }
                    });
                    DeviceAddRequest.show();

                }
                else if(messageEvent.getPath().equalsIgnoreCase( WEAR_NODEID_PATH ) ) {
                    String DeviceId = new String(messageEvent.getData());
                    mDBHandler.StoreDeviceIdToDatabase(DeviceId);
                }
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener( mApiClient, this );
    }

    @Override
    protected void onStop() {
        if ( mApiClient != null ) {
            Wearable.MessageApi.removeListener( mApiClient, this );
            if ( mApiClient.isConnected() ) {
                mApiClient.disconnect();
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if( mApiClient != null )
            mApiClient.unregisterConnectionCallbacks( this );
        super.onDestroy();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/mobile") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String Steps = Float.toString(dataMap.getFloat("Steps"));
                    String HBPM = Float.toString(dataMap.getFloat("HBPM"));
                    String DeviceID = dataMap.getString("DeviceId");
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }
}
