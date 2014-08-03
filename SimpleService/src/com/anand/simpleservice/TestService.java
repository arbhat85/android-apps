package com.anand.simpleservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class TestService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		// This is a normal service, so we will not use this
		return null;
	}
	
	public void onCreate() {
		super.onCreate();
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		int[] numArray = intent.getExtras().getIntArray("numbers");
		float avg = findAverage(numArray);
		
		Toast.makeText(getApplicationContext(), String.format("Average: %f", avg), Toast.LENGTH_SHORT).show();
		return startId;
	}
	
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_SHORT).show();
	}
	
	float findAverage(int[] nums) {
		int sum = 0;
		for(int i = 0; i < nums.length; i++) {
			sum += nums[i];
		}
		return (float)sum/nums.length;
	}
}