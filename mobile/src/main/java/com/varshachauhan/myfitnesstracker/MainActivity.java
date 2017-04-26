package com.varshachauhan.myfitnesstracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
{//Helper Stuff
    Database dataBase;
    private GoogleApiClient mApiClient;
    private Context context = this;
    String response = "empty";
    float heartRate =0.0f;
    float Steps=0.0f;
    private static final String WEAR_MESSAGE_PATH = "/mobile";

    public void toast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void generateRecentData(final Context myContext) {
        TextView todayDate = (TextView) findViewById(R.id.todayDate);
        TextView curCalories = (TextView) findViewById(R.id.curCalories);
        TextView curSteps = (TextView) findViewById(R.id.curSteps);
        TextView curHBPM = (TextView) findViewById(R.id.curHBPM);
        TextView curDevices = (TextView) findViewById(R.id.curDevices);

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy");
        todayDate.setText(dateFormat.format(new Date()));

        int calories = 0;
        curCalories.setText(Integer.toString(calories));

        curSteps.setText(Float.toString(Steps));
        Log.i("setting value",Float.toString(heartRate));

        curHBPM.setText(Float.toString(heartRate));

        int devices = 0;
        curDevices.setText(Integer.toString(devices));
    }

    private void generateAchievementData(final Context myContext, final String curDev) {
        TextView currentDevice = (TextView) findViewById(R.id.achievementTitle);
        currentDevice.setText("Achievements for " + curDev);

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy");
        String currentDate = dateFormat.format(new Date());

        int maxStp = 0;
        TextView maxSteps = (TextView) findViewById(R.id.maxSteps);
        maxSteps.setText(Integer.toString(maxStp));
        String maxStpDay = currentDate;
        TextView maxStepsDay = (TextView) findViewById(R.id.maxStepsDay);
        maxStepsDay.setText(maxStpDay);

        int maxCal = 0;
        TextView maxCalories = (TextView) findViewById(R.id.maxCalories);
        maxCalories.setText(Integer.toString(maxCal));
        String maxCalDay = currentDate;
        TextView maxCaloriesDay = (TextView) findViewById(R.id.maxCaloriesDay);
        maxCaloriesDay.setText(maxCalDay);

        int maxSlp = 0;
        TextView maxSleep = (TextView) findViewById(R.id.maxSleep);
        maxSleep.setText(Integer.toString(maxSlp));
        String maxSlpDay = currentDate;
        TextView maxSleepDay = (TextView) findViewById(R.id.maxSleepDay);
        maxSleepDay.setText(maxSlpDay);

        int minSlp = 0;
        TextView minSleep = (TextView) findViewById(R.id.minSleep);
        minSleep.setText(Integer.toString(minSlp));
        String minSlpDay = currentDate;
        TextView minSleepDay = (TextView) findViewById(R.id.minSleepDay);
        minSleepDay.setText(minSlpDay);
    }

    /*
    new getData(userName, "", "checkUser");
        try {
            if(Database.getData.equals("AVAILABLE\n")){
                //actually register an account
                new getData(userName, passWord, "addUser");
                try{
                    if(Database.getData.equals("SUCCESS\n")){
                        toast("\"" + userName + "\" registered");
                        return true;
                    }
                } catch (Exception e){
                    toast("Error registering account");
                }
            } else if(Database.getData.equals("TAKEN\n")) {
                toast("Sorry, \"" + userName + "\" is already taken");
            } else {
                toast("Problem checking username availability");
            }
        } catch (Exception e){
            toast("Error contacting server");
        }

    public void pullUserDevices(){
        new getData(user, pass);
        try{
            JSONArray array = new JSONArray(getData);
            for(int i = 0; i < array.length(); i++){
                JSONObject object = array.getJSONObject(i);
                String watch = object.getString("DeviceId");
                if(!checkRow("watches", "deviceId", watch)){
                    db.execSQL("insert into watches (deviceId, nickname) values (" + watch + ", \"Watch #" + watch + "\")");
                }
            }
            //create table watches(deleted bool default false, deviceId text not null, nickname text not null, lastsynch timestamp)
            //create table datagrams(deleted bool default false, watch_id text not null, sent_time timestamp, hbpm integer, steps integer, calories integer, sleep integer)");//, foreign key(WatchId) references Watches(WatchId))
        } catch (Exception e){}
    } */

    private void generateLeaderBoard(Context myContext) throws ExecutionException, InterruptedException {
        TableLayout leaderBoard = (TableLayout) findViewById(R.id.leaderBoard);
        leaderBoard.removeAllViews();
        new getData("", "", "leaderBoard");
        try{
            JSONArray array = new JSONArray(Database.getData);
            for(int i = 0; i < array.length(); i++){
                JSONObject object = array.getJSONObject(i);
                String deviceJ = object.getString("DeviceName");
                String stepsJ = object.getString("Steps");
                Log.i("Leader" + i, deviceJ + "\t" + stepsJ);

                TableRow exampleRow = new TableRow(myContext);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                exampleRow.setLayoutParams(lp);
                TextView device = new TextView(myContext);
                device.setText(deviceJ);
                device.setTextSize(20);
                device.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
                exampleRow.addView(device);
                TextView steps = new TextView(myContext);
                steps.setText(stepsJ);
                steps.setTextSize(20);
                steps.setGravity(Gravity.RIGHT);
                steps.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
                exampleRow.addView(steps);
                exampleRow.setPadding(100, 30, 100, 0);
                leaderBoard.addView(exampleRow);
            }
        } catch (Exception e){
            TableRow connection = new TableRow(myContext);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            connection.setLayoutParams(lp);
            TextView message = new TextView(myContext);
            message.setText("SERVER UNAVAILABLE");
            message.setGravity(Gravity.CENTER);
            connection.addView(message);
            leaderBoard.addView(connection);
            toast("cannot connect to server");
        }





    }

    /* Device Menu
     *    Generate the device table
     *    Add a new device
     */
    //Generate the device table
    private void generateDeviceTable(final Context myContext) {
        TableLayout deviceTable = (TableLayout) findViewById(R.id.deviceTable);
        deviceTable.removeAllViews();
        Cursor c = dataBase.select("select deviceId, nickname from watches where deleted = \"false\"");
        int count = 0;
        if (c.moveToFirst()) {
            do {
                final String deviceId = c.getString(0);
                final String nickname = c.getString(1);
                TableRow row = new TableRow(myContext);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                Button deviceButton = new Button(myContext);
                deviceButton.setText(nickname);
                deviceButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        final AlertDialog.Builder deviceContext = new AlertDialog.Builder(MainActivity.this);
                        deviceContext.setTitle(nickname);
                        final LinearLayout body = new LinearLayout(MainActivity.this);
                        TextView devCalories = new TextView(MainActivity.this);
                        int lcalories = 0;                                                      //Here is where you can set values to be displayed from the device view
                        devCalories.setText(Integer.toString(lcalories));
                        body.addView(devCalories);
                        TextView devSteps = new TextView(MainActivity.this);
                        float lsteps = 0;
                        devSteps.setText(Float.toString(lsteps));
                        body.addView(devSteps);
                        TextView devHBPM = new TextView(MainActivity.this);
                        float lhbpm = 0;
                        devHBPM.setText(Float.toString(lhbpm));
                        body.addView(devHBPM);
                        deviceContext.setView(body);
                        deviceContext.setPositiveButton("Delete device", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final AlertDialog.Builder deleteDevice = new AlertDialog.Builder(MainActivity.this);
                                deleteDevice.setTitle("Delete " + nickname + "?");
                                final TextView body = new TextView(MainActivity.this);
                                body.setText("Are you sure you want to delete \"" + nickname + "\"\t(" + deviceId + ")");
                                deleteDevice.setView(body);
                                deleteDevice.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //dataBase.exec("delete from watches where _id = '" + _id + "'");
                                        dataBase.exec("update watches set deleted = \"true\" where deviceId = '" + deviceId + "'");
                                        toast("Deleted " + nickname);
                                        generateDeviceTable(myContext);
                                    }
                                });
                                deleteDevice.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        toast("\"" + nickname + "\" was not deleted");
                                    }
                                });
                                deleteDevice.create();
                                deleteDevice.show();
                            }
                        });
                        deviceContext.setNeutralButton("Change nickname", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final AlertDialog.Builder changeNickname = new AlertDialog.Builder(MainActivity.this);
                                changeNickname.setTitle("Change Device Nickname");
                                final EditText newName = new EditText(MainActivity.this);
                                newName.setText(nickname);
                                changeNickname.setView(newName);
                                changeNickname.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String newNickname = newName.getText().toString();
                                        if (!newNickname.trim().equals("")) {
                                            dataBase.exec("update watches set nickname = \"" + newNickname + "\" where deviceId = \"" + deviceId+"\"");
                                        } else {
                                            toast("New nickname is blank");
                                        }
                                        generateDeviceTable(myContext);
                                    }
                                });
                                changeNickname.create();
                                changeNickname.show();
                            }
                        });
                        deviceContext.create();
                        deviceContext.show();
                    }
                });
                row.addView(deviceButton);
                deviceTable.addView(row, count);
                count += 1;
            } while (c.moveToNext());
        }
    }

    //Add a new device
    private void createNewDeviceButton(final Context myContext) {
        //New Device Button
        FloatingActionButton newDevice = (FloatingActionButton) findViewById(R.id.newDevice);
        newDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                toast("Sending request to connected wearable");
                Intent i = new Intent(MainActivity.this, SendNotification.class);
                String strName = "Varsha";
                i.putExtra("user", strName);
                startActivity(i);
                finish();
            }

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context myContext = this;
        setContentView(R.layout.activity_main);
        dataBase = new Database(this.getApplicationContext());
        final TextView currentContext = (TextView) findViewById(R.id.currentContext);
        final ViewFlipper currentWindow = (ViewFlipper) findViewById(R.id.window_context);
        String hbpm = getIntent().getStringExtra("HBPM");
        String steps = getIntent().getStringExtra("Steps");
        if(hbpm!=null)
            heartRate = Float.parseFloat(hbpm);
        if(steps != null)
            Steps = Float.parseFloat(steps);
        generateRecentData(myContext);


        //testing for login stuff
        ImageButton loginButton = (ImageButton) findViewById(R.id.loadSideViewButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                //toast("test");
            }
        });



        //Achievement Button
        ImageButton achievementButton = (ImageButton) findViewById(R.id.achievementButton);
        achievementButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentContext.setText("Personal Achievements");
                generateAchievementData(myContext, "<Current Device>");
                currentWindow.setDisplayedChild(currentWindow.indexOfChild(findViewById(R.id.achievementView)));
            }
        });

        //Devices Button
        ImageButton deviceButton = (ImageButton) findViewById(R.id.devicesButton);
        deviceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // dataBase.pullUserDevices();
                currentContext.setText("Devices");
                currentWindow.setDisplayedChild(currentWindow.indexOfChild(findViewById(R.id.devicesView)));
                generateDeviceTable(myContext);
                createNewDeviceButton(myContext);
            }
        });

        //Leader Board Button
        ImageButton leaderBoardButton = (ImageButton) findViewById(R.id.leaderBoardButton);
        leaderBoardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentContext.setText("Leader Board");
                try {
                    generateLeaderBoard(myContext);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentWindow.setDisplayedChild(currentWindow.indexOfChild(findViewById(R.id.leaderView)));
            }
        });

        //Home Button
        ImageButton recentDataButton = (ImageButton) findViewById(R.id.homeButton);
        recentDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentContext.setText("Recent Stats");
                generateRecentData(myContext);
                currentWindow.setDisplayedChild(currentWindow.indexOfChild(findViewById(R.id.homeView)));
            }
        });
    }
}
