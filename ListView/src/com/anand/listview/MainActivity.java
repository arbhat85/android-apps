package com.anand.listview;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	String[] countries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        countries = getResources().getStringArray(R.array.countries);
        this.setListAdapter(new ArrayAdapter<String>(
        		this,
        		R.layout.list_element,
        		R.id.countryName,
        		countries));
        
        ListView listview = getListView();
        
        listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String country = countries[position];
        		Toast.makeText(MainActivity.this,String.format("%s was chosen.", country),
        				Toast.LENGTH_SHORT).show();
			}
        	
        });
    }
}
