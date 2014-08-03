package com.anand.intentservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void startService(View v) {
    	EditText sleepTime = (EditText) findViewById(R.id.editSeconds);
    	long secondsToSleep = Long.parseLong(sleepTime.getText().toString());
    	
    	Intent intent = new Intent(MainActivity.this, Sleeper.class);
    	intent.putExtra("seconds", secondsToSleep);
    	startService(intent);
    }
}
