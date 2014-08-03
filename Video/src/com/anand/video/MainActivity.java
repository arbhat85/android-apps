package com.anand.video;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;


public class MainActivity extends Activity {

	VideoView vView = null;
	MediaController mc = null;
	
	/* Called when the activity is first created */
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        vView = (VideoView) findViewById(R.id.vvMp4);
        mc = new MediaController(this);
        vView.setMediaController(mc);
        vView.setVideoPath("/mnt/sdcard/video.mp4");
        vView.requestFocus();
        mc.show();
        vView.start();
    }
}