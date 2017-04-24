package com.varshachauhan.myfitnesstracker;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShowMyAchievements extends WearableActivity {
    DatabaseHandler.FeedEntry.FeedReaderDbHelper mDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_achievements);
        setAmbientEnabled();
        mDBHandler = new DatabaseHandler.FeedEntry.FeedReaderDbHelper(this);
        SetValuesForTextView();
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

    }
    public void SetValuesForTextView()
    {
        String[] TopThreeValuesSteps;
        String[] TopThreeValuesCalories;
        TopThreeValuesSteps = mDBHandler.getMaxValueOfColumn("STEPS");
        TopThreeValuesCalories = mDBHandler.getMaxValueOfColumn("CALORIES");
        BoxInsetLayout mContainerView= (BoxInsetLayout) findViewById(R.id.container);
        mContainerView.setBackgroundColor(getResources().getColor(android.R.color.white));
        TextView Max_Step1=(TextView) findViewById(R.id.MaxStep1);
        TextView Max_Step2=(TextView) findViewById(R.id.MaxStep2);
        TextView Max_Step3=(TextView) findViewById(R.id.MaxStep3);
        TextView Max_Calorie1=(TextView) findViewById(R.id.MaxCalories1);
        TextView Max_Calorie2=(TextView) findViewById(R.id.MaxCalories2);
        TextView Max_Calorie3=(TextView) findViewById(R.id.MaxCalories3);
        Max_Step1.setText(TopThreeValuesSteps[0]);
        Max_Step2.setText(TopThreeValuesSteps[1]);
        Max_Step3.setText(TopThreeValuesSteps[2]);
        Max_Calorie1.setText(TopThreeValuesCalories[0]);
        Max_Calorie2.setText(TopThreeValuesCalories[1]);
        Max_Calorie3.setText(TopThreeValuesCalories[2]);
    }
}
