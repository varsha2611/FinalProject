package com.varshachauhan.myfitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoActivity extends WearableActivity {

    /**
     * oncreate Method for Info Activity
     * @param savedInstanceState
     */
    private ImageButton mHome;
    private ImageButton mAchievements;
    private BoxInsetLayout mContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        mHome = (ImageButton)findViewById(R.id.home);
        mAchievements = (ImageButton)findViewById(R.id.leaderboard);
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mContainerView.setBackgroundColor(getResources().getColor(android.R.color.white));
        setAmbientEnabled();
        mHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent i = new Intent(InfoActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        mAchievements.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(InfoActivity.this, InfoActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
