package com.puzzle.jigsaw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class Intro extends Activity implements OnClickListener {
	static int callTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);  //remove the title bar
		setContentView(R.layout.activity_intro);
		
		Button buttonPlay = (Button) findViewById(R.id.button_play);
		buttonPlay.setOnClickListener(this); //set listener for the Play button
		callTime++;
		if(callTime > 1) {
			buttonPlay.setText(R.string.return_text); //if user returns to this view, the text on the Play button should change to Return
		}
		
		ImageView imageView = (ImageView) findViewById(R.id.org_pic);
		Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.pic);
		imageView.setImageBitmap(image);
		
		Button buttonInstructions = (Button) findViewById(R.id.button_instructions1);
		buttonInstructions.setOnClickListener(this); //set listener for the See Instructions button
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_play: //the Play button was tapped
			startActivity(new Intent(this, Play.class)); //start the puzzle
			break;
		case R.id.button_instructions1: // the instructions button was touched
			/* Show the instructions in a dialog */
			showDialog(this, R.string.instructions_title, getString(R.string.instructions_message));
		}
	}
	
	static void showDialog(Context context, int title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}