package com.varshachauhan.myfitnesstracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddDevice extends AppCompatActivity {
    Database dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBase = new Database(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        String response = getIntent().getStringExtra("response");
        if(response != null) {
            if (response.equals("Decline"))
            {
                toast("Sorry !!! wearable device declined your request");
                StartMainActivity();
            }
            else
            {
                toast("Wearable device accepted your request");
                CreateEditorForDeviceDetails(response);
            }
        }


    }
    public void toast(String message) {
        Toast.makeText(AddDevice.this, message, Toast.LENGTH_SHORT).show();
    }
    public void StartMainActivity()
    {
        Intent intent = new Intent( this, MainActivity.class );
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity( intent );
    }
    public void CreateEditorForDeviceDetails(String nodeId)
    {
        final AlertDialog.Builder addDevice = new AlertDialog.Builder(AddDevice.this);
        addDevice.setTitle("Add Device");
        LinearLayout deviceView = new LinearLayout(AddDevice.this);
        deviceView.setOrientation(LinearLayout.VERTICAL);
        final TextView deviceName = new TextView(AddDevice.this);
        deviceName.setText(nodeId);
        deviceName.setHint("device id of watch");
        deviceView.addView(deviceName);
        final EditText nickName = new EditText(AddDevice.this);
        nickName.setHint("optional nickname");
        deviceView.addView(nickName);
        addDevice.setView(deviceView);
        addDevice.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String device = deviceName.getText().toString();
                String name = nickName.getText().toString();
                if (!dataBase.checkRow("watches", "deviceId", device)) {
                            if (name.trim().equals(""))
                            {
                                String deviceName = "Unknown Watch";
                                dataBase.exec("insert into watches (deviceid, nickname) values (\"" + device + "\", \"Unknown Watch \")");
                                toast("Added Watch #" + device);
                                StartMainActivity();
                            }
                            else
                            {
                                dataBase.exec("insert into watches (deviceid, nickname) values (\"" + device + "\", \"" + name + "\")");
                                toast("Added " + name);
                                StartMainActivity();
                            }
                        } else
                            {
                            toast("Device #" + device + " already added");
                            StartMainActivity();
                        }

            }
        });
        addDevice.create();
        addDevice.show();
    }
}
