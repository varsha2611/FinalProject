package com.varshachauhan.myfitnesstracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;


public class SendNotification extends Activity  implements MessageApi.MessageListener,GoogleApiClient.ConnectionCallbacks
{
    private static final String START_ACTIVITY = "/start_activity";
    private static final String WEAR_MESSAGE_PATH = "/mobile";
    private static final String SENSOR_STEPS = "/steps";
    private static final String SENSOR_HBPM = "/hbpm";
    private String response="empty";
    private String HeartRate = "empty";
    private String Steps = "empty";
    private GoogleApiClient mApiClient;

    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        response = getIntent().getStringExtra("response");
        HeartRate = getIntent().getStringExtra("HBPM");
        Steps = getIntent().getStringExtra("Steps");
        initGoogleApiClient();
        init();
        StartMainActivity();
    }

    public void StartMainActivity()
    {
        Intent intent = new Intent( this, MainActivity.class );
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity( intent );
        finish();
    }

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .build();

        mApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    private void init()
    {
        if(response != null)
            sendMessage(WEAR_MESSAGE_PATH, response);
        else if(HeartRate != null)
            sendMessage(WEAR_MESSAGE_PATH,HeartRate);
        else if(Steps != null)
            sendMessage(SENSOR_STEPS,Steps);
    }

    private void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    String message = text;
                    Log.i("message",text);
                    if(message.equals("Accept"))
                     message= node.getId();
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, message.getBytes() ).await();
                    Log.i("path",path);
                    Log.i("message",text);
                }

                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }).start();
    }

    @Override
    public void onConnected(Bundle bundle) {

        sendMessage(START_ACTIVITY, "");
    }

    @Override
    public void onConnectionSuspended(int i) {

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
                if( messageEvent.getPath().equalsIgnoreCase( WEAR_MESSAGE_PATH ) ) {
                }
            }
        });
    }
}