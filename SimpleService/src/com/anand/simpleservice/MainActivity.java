package com.anand.simpleservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	
	private EditText numbersText;
	private static Intent averagingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        numbersText = (EditText) findViewById(R.id.editNumbers);
    }
    
    public void startService(View v) {
    	String[] strNumbers = numbersText.getText().toString().split(" ");
    	int[] numbers = new int[strNumbers.length];
    	for (int i = 0; i < strNumbers.length; i++) {
    		numbers[i] = Integer.parseInt(strNumbers[i]);
    	}
    	
    	averagingIntent =new Intent(MainActivity.this, TestService.class);
    	averagingIntent.putExtra("numbers", numbers);
    	
    	startService(averagingIntent);
    }
    
    public void stopService(View v) {
    	stopService(averagingIntent);
    }
}
