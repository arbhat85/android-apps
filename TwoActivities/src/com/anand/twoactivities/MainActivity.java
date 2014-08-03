package com.anand.twoactivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void startActivityTwo(View v) {
    	Intent intent = new Intent(MainActivity.this, ActivityTwo.class);
    	startActivity(intent);
    }
    
    protected void onResume() {
    	super.onResume();
    	Log.i("TwoActivities", "MainActivity Resumed");
    }
    
    protected void onPause() {
    	super.onPause();
    	Log.i("TwoActivities", "MainActivity Paused");
    }
    
    protected void onStop() {
    	super.onStop();
    	Log.i("TwoActivities", "MainActivity Stopped");
    }
}