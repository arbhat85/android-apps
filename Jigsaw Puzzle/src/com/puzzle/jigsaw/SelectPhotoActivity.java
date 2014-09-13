package com.puzzle.jigsaw;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class SelectPhotoActivity extends Activity implements OnClickListener {
	private GridLayout gridPhotos = null;
	private ImageButton[] imageButtons = null; 
	private int currentPosition = -1, previousPosition = -1;
	private Button buttonOkay = null;
	private Button buttonCancel = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.hideIcon(getActionBar());
		setContentView(R.layout.select_image);
		
		gridPhotos = (GridLayout) findViewById(R.id.layout_grid);
		
		buttonOkay = (Button) findViewById(R.id.button_okay);
		buttonOkay.setOnClickListener(this);
		
		buttonCancel = (Button) findViewById(R.id.button_cancel);
		buttonCancel.setOnClickListener(this);
		L.setMargin(buttonCancel, L.RIGHT, 0);
		
		drawInitialBorder();

		addImageButtons();
	}
	
	void drawInitialBorder() {
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout1);
		ViewTreeObserver vto = layout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				currentPosition = Inputs.getPicPosition();
				imageButtons[currentPosition].setImageBitmap
					(L.drawBorder(L.photoBitmaps[currentPosition], Color.GREEN, 4));
			}
		});
	}
	

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.button_okay:
			Inputs.setPicPosition(currentPosition);
		case R.id.button_cancel:
			if (L.gameStarted) {
				startActivity(new Intent(this, PlayActivity.class));
			} else {
				startActivity(new Intent(this, WelcomeActivity.class));
			}
			break;
		default:
			//an imageButton was tapped
			int selectedPosition = L.getIndexFromXY(view);
			if (currentPosition != selectedPosition) {
				toggleBorders(selectedPosition);
			}	
		}
	}
	
	void toggleBorders(int selectedPosition) {
		previousPosition = currentPosition;
		currentPosition = selectedPosition;
		imageButtons[currentPosition].setImageBitmap
			(L.drawBorder(L.photoBitmaps[currentPosition], Color.GREEN, 4));
		if (previousPosition != -1) {
			imageButtons[previousPosition].setImageBitmap
				(L.photoBitmaps[previousPosition]);
		}
	}
	
	void addImageButtons() {
		imageButtons = new ImageButton[L.NUM_PHOTOS];
		
		for (int i=0; i<L.NUM_PHOTOS; i++) {
			ImageButton imageButton = new ImageButton(this);
			imageButton.setImageBitmap(L.photoBitmaps[i]);
			imageButton.setPadding(L.PADDING, L.PADDING, L.PADDING, L.PADDING);
			imageButton.setOnClickListener(this);
			gridPhotos.addView(imageButton, i); //add the ImageButton
			imageButtons[i] = imageButton;
		}
		imageButtons[L.NUM_PHOTOS-1].setEnabled(L.phonePhotoSelected);
	}
	
}