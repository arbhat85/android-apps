package com.puzzle.jigsaw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class InstructionsActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.hideIcon(getActionBar());
		setContentView(R.layout.instructions);
		
		final ImageButton imageButtonBack = (ImageButton) findViewById(R.id.imageButton_back);
		Button buttonBack = (Button) findViewById(R.id.button_back);
		imageButtonBack.setOnClickListener(this);
		buttonBack.setOnClickListener(this);
		
		final RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout_relative);
		ViewTreeObserver vto = layout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				View divider = findViewById(R.id.divider1);
				int dividerThickness = getDividerThickness();
				divider.setLayoutParams(new RelativeLayout.LayoutParams (dividerThickness,
																		 layout.getHeight()));
				divider.setX((int) (imageButtonBack.getX()) - dividerThickness);
			}
		});
	}
	
	int getDividerThickness() {
		return getResources().getDimensionPixelSize(R.dimen.divider_thickness);
	}

	@Override
	public void onClick(View v) {
		if (L.gameStarted) {
			startActivity(new Intent(this, PlayActivity.class));
		} else {
			startActivity(new Intent(this, WelcomeActivity.class));
		}
	}

}