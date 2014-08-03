package com.anand.twoactivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ActivityTwo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        Log.i("TwoActivities", "ActivityTwo Created");
    }
    
    public void startActivityOne(View v) {
    	Intent intent = new Intent(ActivityTwo.this, MainActivity.class);
    	startActivity(intent);
    }
    
    protected void onPause() {
    	super.onPause();
    	Log.i("TwoActivities", "ActivityTwo Paused");
    }
    
    protected void onStop() {
    	super.onStop();
    	Log.i("TwoActivities", "ActivityTwo Stopped");
    }
    
}