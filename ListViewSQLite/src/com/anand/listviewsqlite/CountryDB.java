package com.anand.listviewsqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CountryDB extends SQLiteOpenHelper {
	
	private static final int DB_VERSION = 1;
	private static final String COUNTRY_TABLE_NAME = "countries";
	
	CountryDB(Context context, String name, SQLiteDatabase.CursorFactory factory) {
		super(context, name, factory, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase dbCountries) {
		createTable(dbCountries);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase dbCountries, int oldVersion, int newVersion) {
		String dropSQL = "DROP TABLE IF EXISTS " + COUNTRY_TABLE_NAME + ";";
		dbCountries.execSQL(dropSQL);
		createTable(dbCountries);
	}
	
	private void createTable(SQLiteDatabase dbCountries) {
		String createSQL = "CREATE TABLE " + COUNTRY_TABLE_NAME + " (" +
	                       "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				           "Name TEXT, " +
	                       "Pop TEXT, " +
				           "Area TEXT);";
		dbCountries.execSQL(createSQL);
		
		String insertSQL = "INSERT INTO " + COUNTRY_TABLE_NAME +
		                    " (Name, Pop, Area) " +
				            " SELECT 'Argentina' AS Name, '41,769,726' AS Pop, '1,068,296' AS Area" +
		                    " UNION SELECT 'Australia', '21,766,711', '2,967,893'" + 
				            " UNION SELECT 'India', '1,189,172,906', '1,269,338'" +
		                    " UNION SELECT 'United States', '313,232,044', '3,718,691';";
		dbCountries.execSQL(insertSQL);
	}
}