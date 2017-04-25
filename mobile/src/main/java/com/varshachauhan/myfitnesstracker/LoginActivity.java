package com.varshachauhan.myfitnesstracker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

public class LoginActivity extends Activity {

    //private Database db = new Database(this.getApplicationContext());

    public void toast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void startMain(Context myContext){
        Intent main = new Intent(myContext, MainActivity.class);
        finish();
        startActivity(main);
    }

    private boolean verifyChoice(String userName, String passWord){
        if(userName.equals(""))
            return false;
        if(passWord.equals(""))
            return false;
        return true;
    }

    private boolean register(String userName, String passWord){
        //check if username is valid
        //register account
        return true;
    }


    /*public void pullUserDevices(){
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

    private boolean login(String userName, String passWord) {
        Log.i("login", "starting function");
        new getData(userName, passWord, "validateLogin");
        Log.i("login", "executed getData");
        try {
            Thread.sleep(3000);
            Log.i("getData", Database.getData);
            if(Database.getData.equals("Login Successful")){
                Log.i("login", "Database.getData is what is expected");
                new Database(this).setLogin(userName, passWord);
                Log.i("login", "username and password saved to internal database");
                return true;
            } else {
                Log.i("login", "Database.getData does not equal");
                toast("Invalid login");
                return false;
            }
        } catch (Exception e){
            Log.i("login", "there was an exception");
            toast("Could not access server");
            return false;
        }
    }

    private void registerAccount(final Context myContext){
        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener(){
           public void onClick (View v){
               final AlertDialog.Builder registerDialog = new AlertDialog.Builder(LoginActivity.this);
               TextView title = new TextView(myContext);
               title.setGravity(Gravity.CENTER);
               title.setText("Register Account");
               title.setTextSize(33);
               title.setTextColor(Color.BLACK);
               registerDialog.setCustomTitle(title);
               LinearLayout registerBody = new LinearLayout(myContext);
               registerBody.setOrientation(LinearLayout.VERTICAL);

               final EditText userName = new EditText(myContext);
               userName.setHint("username");
               userName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
               userName.setGravity(Gravity.CENTER);
               registerBody.addView(userName);

               LinearLayout registerMini = new LinearLayout(myContext);
               registerMini.setOrientation(LinearLayout.HORIZONTAL);
               final EditText age = new EditText(myContext);
               age.setHint("age");
               age.setInputType(InputType.TYPE_CLASS_NUMBER);
               age.setGravity(Gravity.CENTER);
               age.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f));
               registerMini.addView(age);
               final EditText password = new EditText(myContext);
               password.setHint("password");
               password.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.7f));
               password.setGravity(Gravity.CENTER);
               registerMini.addView(password);
               registerBody.addView(registerMini);

               LinearLayout registerMoni = new LinearLayout(myContext);
               registerMoni.setOrientation(LinearLayout.HORIZONTAL);
               final EditText weight = new EditText(myContext);
               weight.setInputType(InputType.TYPE_CLASS_NUMBER);
               weight.setHint("weight");
               weight.setGravity(Gravity.CENTER);
               weight.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f));
               registerMoni.addView(weight);
               final EditText passWord = new EditText(myContext);
               passWord.setHint("re-enter password");
               passWord.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
               //passWord.setTransformationMethod(new PasswordTransformationMethod());
               passWord.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.7f));
               passWord.setGravity(Gravity.CENTER);
               registerMoni.addView(passWord);
               registerBody.addView(registerMoni);

               registerDialog.setView(registerBody);
               registerDialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       if (!age.getText().toString().equals("") && !weight.getText().toString().equals("")) {
                           String user = userName.getText().toString();
                           String pass = passWord.getText().toString();
                           Integer uAge = Integer.parseInt(age.getText().toString());
                           Integer uWeight = Integer.parseInt(weight.getText().toString());
                           if (verifyChoice(user, pass)) {
                               if (register(user, pass)) {
                                   toast("Welcome " + user + " <" + uAge + "/" + uWeight + ">");
                                   startMain(myContext);
                               } else {
                                   toast("Could not register account");
                               }
                           } else {
                               toast("username and password cannot be empty");
                           }
                       } else {
                           toast("age and weight are needed to calculate calories");
                       }
                   }
               });
               registerDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       toast("No account registered");
                   }
               });

               registerDialog.create();
               registerDialog.show();
           }
        });
    }

    private void accountLogin(final Context myContext){
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final AlertDialog.Builder loginDialog = new AlertDialog.Builder(LoginActivity.this);
                TextView title = new TextView(myContext);
                title.setGravity(Gravity.CENTER);
                title.setText("Login");
                title.setTextSize(33);
                title.setTextColor(Color.BLACK);
                loginDialog.setCustomTitle(title);
                LinearLayout loginBody = new LinearLayout(myContext);
                loginBody.setOrientation(LinearLayout.VERTICAL);

                final EditText username = new EditText(myContext);
                username.setHint("username");
                username.setGravity(Gravity.CENTER);
                username.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                loginBody.addView(username);

                final EditText password = new EditText(myContext);
                password.setHint("password");
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password.setGravity(Gravity.CENTER);
                password.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                loginBody.addView(password);

                loginDialog.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which){
                        String user = username.getText().toString();
                        String pass = password.getText().toString();
                        if(verifyChoice(user, pass)) {
                            Log.i("login", "before");
                            if (login(user, pass)) {
                                Log.i("login", "after");
                                toast("welcome back " + username.getText().toString());
                                startMain(myContext);
                            } else {
                                Log.i("login", "did not login");
                                toast("error logging in");
                            }
                            Log.i("login", "after all");
                        } else {
                            toast("username and password cannot be empty");
                        }
                    }
                });
                loginDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) { }
                });
                loginDialog.setView(loginBody);
                loginDialog.create();
                loginDialog.show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context myContext = LoginActivity.this;
        Log.i("test", "test");
        /*if(Database.isLoggedIn()){
            startMain(myContext);
        }*/
        Log.i("test", "test2");
        setContentView(R.layout.activity_login);
        registerAccount(myContext);
        accountLogin(myContext);
    }
}