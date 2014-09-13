package com.puzzle.jigsaw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	static boolean handled = false;
	private static Context context = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.hideIcon(getActionBar());
		setContentView(R.layout.settings_background);
		
		context = this;
		
		String[] settingsOptions = new String[5];
		settingsOptions[0] = getString(R.string.dialogbox_changePhoto_app);
		settingsOptions[1] = getString(R.string.dialogbox_changePhoto_phone);
		settingsOptions[2] = getString(R.string.dialogbox_changeNum);
		settingsOptions[3] = getString(R.string.dialogbox_settings_restoreDefaults);
		settingsOptions[4] = getString(R.string.dialogbox_cancel);
				
		AlertDialog.Builder sBuilder = new AlertDialog.Builder(this);	
		sBuilder.setTitle(R.string.dialogbox_settings_title);					
		sBuilder.setItems(settingsOptions, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
				case 0:
					startNewActivity(0);
					break;
				case 1:
					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
					photoPickerIntent.setType("image/*");
					startActivityForResult(photoPickerIntent, L.SELECT_PHOTO);
					break;
				case 2:
					startNewActivity(1);
					break;
				case 3:
					Inputs.restoreDefaultSettings();
					Toast.makeText(context, R.string.toast_settings_defaultsRestored, Toast.LENGTH_SHORT).show();
				case 4:
					goBack();
					break;
				}
			}
		});
		sBuilder.create().show();
		
		handled = true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (!handled) {
			goBack();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		handled = false;
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
	
	void goBack() {
		startActivity(new Intent(this, PlayActivity.class));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

	    L.getSelectedPic(requestCode, resultCode, imageReturnedIntent, getContentResolver());
	    
	    startActivity(new Intent(this, PlayActivity.class));
	}
}