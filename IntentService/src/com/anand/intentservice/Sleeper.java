package com.anand.intentservice;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

public class Sleeper extends IntentService {
	
	long seconds;

	public Sleeper() {
		super("Sleeper");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		seconds = intent.getExtras().getLong("seconds");
		long millis = seconds * 1000;
		try {
			Thread.sleep(millis);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, String.format("Slept %d seconds", seconds), Toast.LENGTH_SHORT).show();
	}
}