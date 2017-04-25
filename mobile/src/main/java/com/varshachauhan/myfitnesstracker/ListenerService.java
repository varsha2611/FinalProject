package com.varshachauhan.myfitnesstracker;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
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
    String DeviceId ="";
    Database dataBase;

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
        if( messageEvent.getPath().equalsIgnoreCase( START_ACTIVITY ) ) {
            String response = new String (messageEvent.getData());
            DeviceId = messageEvent.getSourceNodeId();
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
                dataBase = new Database(this.getApplicationContext());
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/mobile") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    Float Steps = dataMap.getFloat("Steps");
                    Float HBPM = dataMap.getFloat("HBPM");
                    String DeviceID = dataMap.getString("DeviceId");
                    Long timestamp = dataMap.getLong("TimeStamp");

                    /* Add Values to Database*/
                    dataBase.InsertSensorDataIntoTable(Steps,HBPM,timestamp,DeviceID);
                    Intent intent = new Intent( this, AddDevice.class );
                    intent.putExtra("DeviceId",DeviceID);
                    intent.putExtra("type","Upload");
                    intent.putExtra("Steps",Float.toString(Steps));
                    intent.putExtra("HBPM", Float.toString(HBPM));
                    intent.putExtra("TimeStamp",Long.toString(timestamp));
                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
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
