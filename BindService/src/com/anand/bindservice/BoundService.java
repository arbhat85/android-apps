package com.anand.bindservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BoundService extends Service {
	
	private final IBinder binder = new LocalBinder();
	
	public class LocalBinder extends Binder {
		BoundService getService() {
			return BoundService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.i("service","bound!");
		return binder;
	}
	
	public int add (int a, int b) {
		return a+b;
	}
}