package com.anand.listviewsqlite;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	private CountryDB countryDatabase = null;
	private Cursor countries = null;
	private ListView lvCountries = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Create the database helper
        CursorFactory factory = null;
        countryDatabase = new CountryDB(this, "countryDB", factory);
        
        String[] columnNames = {"Name", "Pop", "Area", BaseColumns._ID};
        lvCountries = (ListView) findViewById(R.id.lvCountries);
        
        int[] targetLayoutIDs = {R.id.textName, R.id.textPop, R.id.textArea};
        
        SQLiteDatabase db = countryDatabase.getReadableDatabase();
        countries = db.query("countries", columnNames, null, null, null, null, null);
        CursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.country_item, countries, columnNames, targetLayoutIDs, 0);
        lvCountries.setAdapter(adapter);
    }
    
    public void onPause() {
    	super.onPause();
    	countryDatabase.close();
    }
}
