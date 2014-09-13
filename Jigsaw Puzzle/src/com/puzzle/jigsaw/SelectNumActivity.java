package com.puzzle.jigsaw;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SelectNumActivity extends Activity implements OnClickListener {
	private static int NUM_OPTIONS; //maximum number of grid layout options
	private RadioGroup radioGrid = null;
	private ArrayList<RadioButton> radioButtons = new ArrayList<RadioButton>(); //these are dynamically added to the layout
	private Button buttonOkay = null;
	private Button buttonCancel = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.hideIcon(getActionBar());
		setContentView(R.layout.select_num);
		
		NUM_OPTIONS = 6;
		
		/* Add the RadioButtons to the layout */
		addRadioButtons();
		
		/* Add a click listener to the buttons */
		buttonOkay = (Button) findViewById(R.id.button_okay);
		buttonOkay.setOnClickListener(this);
		
		buttonCancel = (Button) findViewById(R.id.button_cancel);
		buttonCancel.setOnClickListener(this);
		L.setMargin(buttonCancel, L.RIGHT, 0);
	}
	
	/* Add RadioButtons to the layout dynamically */
	void addRadioButtons() {
		radioGrid = (RadioGroup) findViewById(R.id.radiogroup_grid); //get a handle to the group to which the buttons are to be added
		for (int i=0; i<NUM_OPTIONS; i++) {
			addDivider();
			radioButtons.add(new RadioButton(this));
			radioButtons.get(i).setText((i+2)*(i+2) + " (" + (i+2) + "x" + (i+2) + " grid)"); //the relationship between i and NUM.ROWS: NUM.ROWS = i + 2
			radioButtons.get(i).setPadding(0, 30, 0, 30);
			radioGrid.addView(radioButtons.get(i));
		}
		addDivider();
		
		radioGrid.check(radioButtons.get(Inputs.getNumDimension()-2).getId()); //by default, select the current grid dimensions
		radioButtons.get(Inputs.getNumDimension()-2).setText(Inputs.getNumPieces() + 
				" (" + Inputs.getNumDimension() + "x" + Inputs.getNumDimension() + " grid) (current selection)"); //add a 'current grid' tag next to the current dimensions
	}
	
	void addDivider() {
		View divider = new View(this);
		divider.setBackgroundColor(Color.LTGRAY);
		divider.setPadding(0, 30, 0, 30);
		divider.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4));
		radioGrid.addView(divider);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.button_okay:
			String selectedText, num;
			
			selectedText = ((RadioButton) findViewById(radioGrid.getCheckedRadioButtonId())).getText().toString(); 
			if (selectedText.charAt(1) == ' ') {
				num = selectedText.substring(0, 1);
			} else {
				num = selectedText.substring(0, 2);
			}
			
			Inputs.setNumPieces(Integer.parseInt(num));
		case R.id.button_cancel:
			if (L.gameStarted) {
				startActivity(new Intent (this, PlayActivity.class));
			} else {
				startActivity(new Intent (this, WelcomeActivity.class));
			}
			break;
		}
	}
	
}