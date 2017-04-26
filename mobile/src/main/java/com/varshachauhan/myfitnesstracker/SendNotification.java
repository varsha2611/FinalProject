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


public class SendNotification extends Activity  implements MessageApi.MessageListener,GoogleApiClient.ConnectionCallbacks
{
    private static final String START_ACTIVITY = "/start_activity";
    private static final String WEAR_NODEID_PATH ="/nodeId";
    private static final String WEAR_MESSAGE_PATH = "/message";
    private String user="empty";
    private String nodeId = "empty";
    private GoogleApiClient mApiClient;

    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = getIntent().getStringExtra("user");
        nodeId = getIntent().getStringExtra("NodeId");
        Log.i("SendNotification","Sending message");
        if(user != null)
        Log.i("user value",user);
        if(nodeId != null)
            Log.i("nodeId",nodeId);
        initGoogleApiClient();
        init();
        StartMainActivity();
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
        if(user != null)
            sendMessage(WEAR_MESSAGE_PATH, user);
        else if(nodeId != null)
            sendMessage(WEAR_NODEID_PATH, nodeId);
    }

    private void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                }

                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }).start();
    }

    public void StartMainActivity()
    {
        Intent intent = new Intent( this, MainActivity.class );
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity( intent );
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
        Log.i("message",user);
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                if( messageEvent.getPath().equalsIgnoreCase( WEAR_MESSAGE_PATH ) ) {
                    String user = new String (messageEvent.getData());
                    Log.i("message",user);
                }
            }
        });
    }
}