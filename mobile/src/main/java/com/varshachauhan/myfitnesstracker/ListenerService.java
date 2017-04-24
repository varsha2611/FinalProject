package com.varshachauhan.myfitnesstracker;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by varsha Chauhan on 04/16/2017.
 */
public class ListenerService extends WearableListenerService implements DataApi.DataListener,GoogleApiClient.ConnectionCallbacks{
    private static final String START_ACTIVITY = "/mobile";
    private static final String SENSOR_STEPS = "/steps";
    private static final String SENSOR_HBPM = "/hbpm";
    private GoogleApiClient mApiClient;
    private Context context = this;
    String DeviceId ="";

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
    public void onMessageReceived(MessageEvent messageEvent) {
        String path = messageEvent.getPath();
        Log.i("MessageRecieved",path);
        if( messageEvent.getPath().equalsIgnoreCase( START_ACTIVITY ) ) {
            String response = new String (messageEvent.getData());
            DeviceId = messageEvent.getSourceNodeId();
            Log.i("MessageRecieved",response);
            Intent intent = new Intent( this, AddDevice.class );
            intent.putExtra("response",response);
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity( intent );
        }
        else {
            super.onMessageReceived(messageEvent);
        }
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
                    Intent intent = new Intent( this, MainActivity.class );
                    intent.putExtra("Steps",Steps);
                    intent.putExtra("HBPM",HBPM);
                    intent.putExtra("DeviceId",DeviceID);
                    Log.i("DeviceId Here",DeviceID);
                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity( intent );
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
            Wearable.MessageApi.addListener( mApiClient, this );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
