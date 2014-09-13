package com.puzzle.jigsaw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends Activity implements OnClickListener {
	private ImageView ivCurrentPhoto = null;
	private Button buttonChangePhoto = null;
	private Button buttonChangeNum = null;
	private Button buttonPlay = null;
	private Button buttonInstructions = null;
	private Button buttonRestoreDefaultSettings = null;
	private TextView tvCurrentNum = null;
	private Context context = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.welcome);
		
		L.init(this, getWindowManager().getDefaultDisplay());
		context = this;
		
		addListeners();
		
		L.preferences = PreferenceManager.getDefaultSharedPreferences(this);
		Inputs.setNumPieces(L.preferences.getInt("numPieces", Inputs.defaultNumPieces));
		Inputs.setPicPosition(L.preferences.getInt("picPosition", Inputs.defaultPicPosition));
		Inputs.setShowSettingsWarning(L.preferences.getBoolean("showSettingsWarning", Inputs.defaultShowSettingsWarning));
		
		ivCurrentPhoto.setImageBitmap(L.photoBitmaps[Inputs.getPicPosition()]);
		tvCurrentNum.setText(Inputs.getNumPieces()+" ("+Inputs.getNumDimension()+"x"+Inputs.getNumDimension()+" grid)");
		
		L.setMargin(buttonPlay, L.BOTTOM, 0);
		L.setMargin(buttonInstructions, L.BOTTOM, 0);
		
		setImageViewHeight();
		
		buttonChangeNum.setTextColor(0xff33b5e5);
		buttonChangeNum.setTextSize(17);
		buttonChangePhoto.setTextColor(0xff33b5e5);
		buttonChangePhoto.setTextSize(17);
	}
	
	void setImageViewHeight() {
		RelativeLayout layoutParent = (RelativeLayout)findViewById(R.id.layout_parent);
		ViewTreeObserver vto = layoutParent.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				ImageView ivHeader = (ImageView) findViewById(R.id.iv_header);
				LinearLayout layoutButtons = (LinearLayout) findViewById(R.id.layout_buttons);
				TextView tv = (TextView) findViewById(R.id.textView1);
				RelativeLayout layoutSettings = (RelativeLayout)findViewById(R.id.layout_settings);
				int totalHeight = ivHeader.getHeight() + layoutButtons.getHeight() +
						tv.getHeight() + buttonChangePhoto.getHeight();
				totalHeight += ivHeader.getPaddingTop() + ivHeader.getPaddingBottom();
				totalHeight += layoutButtons.getPaddingTop() + layoutButtons.getPaddingBottom();
				totalHeight += tv.getPaddingTop() + tv.getPaddingBottom();
				totalHeight += buttonChangePhoto.getPaddingTop() + buttonChangePhoto.getPaddingBottom();
				int height = L.screenDimensions.y - totalHeight;
				ivCurrentPhoto.setLayoutParams(new RelativeLayout.LayoutParams
													(layoutSettings.getWidth(), height));
			}
		});
	}
	
	void addListeners() {
		ivCurrentPhoto = (ImageView) findViewById(R.id.imageView_currentPhoto);
		buttonChangePhoto = (Button) findViewById(R.id.button_changePhoto);
		buttonChangeNum = (Button) findViewById(R.id.button_changeNum);
		buttonPlay = (Button) findViewById(R.id.button_play);
		buttonInstructions = (Button) findViewById(R.id.button_instructions);
		buttonRestoreDefaultSettings = (Button) findViewById(R.id.button_restoreDefaultSettings);
		tvCurrentNum = (TextView)findViewById(R.id.textView_currentNum);
		
		buttonChangePhoto.setOnClickListener(this);
		buttonChangeNum.setOnClickListener(this);
		buttonPlay.setOnClickListener(this);
		buttonInstructions.setOnClickListener(this);
		buttonRestoreDefaultSettings.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.button_play) {
			L.gameStarted = true;
			startActivity(new Intent(this, PlayActivity.class));
		} else if(view.getId() == R.id.button_instructions) {
			startActivity(new Intent(this, InstructionsActivity.class));
		} else if (view.getId() == R.id.button_restoreDefaultSettings) {
			Inputs.restoreDefaultSettings();
			ivCurrentPhoto.setImageBitmap(L.photoBitmaps[Inputs.getPicPosition()]);
			tvCurrentNum.setText(Inputs.getNumPieces()+" ("+Inputs.getNumDimension()+"x"+Inputs.getNumDimension()+" grid)");
			Toast.makeText(this, R.string.toast_settings_defaultsRestored, Toast.LENGTH_SHORT).show();
			
			
		} else {
			//user tapped on change photo button or change num button
			if (view.getId() == R.id.button_changePhoto) {
				showSettingsOptions();
			} else if (view.getId() == R.id.button_changeNum) {
				startNewActivity(1);
			}
		}
	}
	
	void startNewActivity(int choice) {
		switch(choice) {
		case 0:
			startActivity(new Intent(this, SelectPhotoActivity.class));
			break;
		case 1:
			startActivity(new Intent(this, SelectNumActivity.class));
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
	    
	    L.getSelectedPic(requestCode, resultCode, imageReturnedIntent, getContentResolver());
	    if (L.phonePhotoSelected) {
	    	ivCurrentPhoto.setImageBitmap(L.photoBitmaps[L.NUM_PHOTOS-1]);
	    }
	}
	
	void showSettingsOptions() {
		String[] changeOptions = new String[3];
		changeOptions[0] = getString(R.string.dialogbox_changePhoto_app);
		changeOptions[1] = getString(R.string.dialogbox_changePhoto_phone);
		changeOptions[2] = getString(R.string.dialogbox_cancel);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);	
		builder.setTitle(R.string.dialogbox_changePhoto_title);					
		builder.setItems(changeOptions, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
				case 0: //select photo from app
					startNewActivity(0);
					break;
				case 1: //select photo from gallery
					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
					photoPickerIntent.setType("image/*");
					startActivityForResult(photoPickerIntent, L.SELECT_PHOTO);
					break;
				case 2:
					//do nothing
					break;
				}
			}
		});
		builder.create().show();
	}
	
}