package com.puzzle.jigsaw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class PreviewActivity extends Activity implements OnClickListener {
	Button buttonBack = null;
	ImageView ivPreview = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.hideIcon(getActionBar());
		setContentView(R.layout.preview);

		ivPreview = (ImageView) findViewById(R.id.imageView_preview);
		ivPreview.setImageBitmap(L.photoBitmaps[Inputs.getPicPosition()]);
		
		buttonBack = (Button) findViewById(R.id.button_back);
		buttonBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		startActivity(new Intent(this, PlayActivity.class));
	}
}
