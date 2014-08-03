/*
 * Copyright (C) 2013 Code Here Now - A subsidiary of Mobs & Geeks
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file 
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.codeherenow.sicalculator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SICalculatorActivity extends Activity implements OnSeekBarChangeListener, OnClickListener {

	private EditText p;
	private EditText r;
	private TextView tText;
	private SeekBar t;
	private Button calc;
	private TextView i;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sicalculator);
		
		p = (EditText) findViewById(R.id.principal);
		r = (EditText) findViewById(R.id.rate);
		tText = (TextView) findViewById(R.id.timeText);
		t = (SeekBar) findViewById(R.id.time);
		calc = (Button) findViewById(R.id.calculate);
		i = (TextView) findViewById(R.id.result);

		t.setOnSeekBarChangeListener(this);
		calc.setOnClickListener(this);
	}
	
	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		tText.setText(String.format("%d Year(s)", t.getProgress()));
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onClick(View v) {
		double principal = Double.parseDouble(p.getText().toString());
		double rate = Double.parseDouble(r.getText().toString());
		int time = t.getProgress();
		
		double interest = principal * rate * time / 100;
		i.setText(String.format("The interest for %.0f at a rate of %.2f%% for %d year(s) is $%.2f.",
				       principal, rate, time, interest));
	}
}