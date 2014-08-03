package com.anand.bindservice;

import com.anand.bindservice.BoundService.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {

	BoundService theService;
	boolean isBound = false;
    
	private ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			//We've bound to LocalService. Cast the IBinder and get LocalService instance
			LocalBinder binder = (LocalBinder) service;
			theService = binder.getService();
			isBound = true;
		}
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			isBound = false;
		}
	};
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    protected void onStart() {
    	super.onStart();
    	Log.i("onStart", "activity started");
    	Intent intent = new Intent(MainActivity.this, BoundService.class);
    	bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
    
    protected void onPause() {
    	super.onPause();
    	if (isBound) {
    		unbindService(connection);
    		isBound = false;
    	}
    }
    
    public void addClicked(View v) {
    	if(isBound) {
    		Log.i("bound", String.format("sum: %d", theService.add(2,3)));
    	}
    }
}