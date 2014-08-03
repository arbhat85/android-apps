package com.anand.audio;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

	MediaPlayer mp = null;			//for playing from the /res/raw folder
	MediaPlayer local_mp = null;	//for playing from the SD card
	
	/* Called when the activity is first created */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void onStop() {
    	super.onStop();
    	if(mp != null) {
    		mp.release();
    		mp = null;
    	}
    	if(local_mp != null) {
    		local_mp.release();
    		local_mp = null;
    	}
    }
    
    /*
     * Demonstrates playing a sound on button click:
     * sound is stored in /res/raw as an MP3 file
     * @param v - button clicked
     */
    public void play(View v) {
    	if(mp != null) {
    		mp.release();
    	}
    	switch(v.getId()) {
    	case R.id.button1:
    		mp = MediaPlayer.create(getApplicationContext(), R.raw.a);
    		break;
    	}
    	mp.start();
    }
    
    /*
     * Demonstrates playing sound stored on the SD card
     * @param v - button that was clicked
     */
    public void playLocalAudio(View v) {
    	//This file must be moved to the SD card of the
    	//emulator or device
    	String sdPath = "/mnt/sdcard/b.mp3";
    	local_mp = new MediaPlayer();
    	try {
    		local_mp.setDataSource(sdPath);
    		local_mp.prepare();
    		local_mp.start();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
}