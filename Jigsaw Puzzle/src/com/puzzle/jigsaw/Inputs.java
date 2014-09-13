package com.puzzle.jigsaw;

/* Keeps track of the most current grid dimensions */
public class Inputs {
	private static int numDimension;
	private static int numPieces = numDimension*numDimension;
	private static int picPosition;
	private static boolean showSettingsWarning;
	private static boolean changed = false;
	
	private static int oldNumDimension;
	private static int oldPicPosition;
	
	static final int defaultNumPieces = 9;
	static final int defaultPicPosition = 1;
	static final boolean defaultShowSettingsWarning = true;
	
	static void setNumPieces(int value) {
		if (value != oldNumDimension) {
			oldNumDimension = value;
			changed = true;
		}
		
		numPieces = value;
		numDimension = (int) Math.sqrt(numPieces);
		L.preferences.edit().putInt("numPieces", numPieces).commit();	
	}
	
	static int getNumPieces() {
		return numPieces;
	}
	
	static int getNumDimension() {
		return numDimension;
	}
	
	static void setPicPosition(int position) {
		if (position != oldPicPosition) {
			oldPicPosition = position;
			changed = true;
		}
		
		picPosition = position;
		if (position == L.NUM_PHOTOS-1) {
			position = 1;
		}
		L.preferences.edit().putInt("picPosition", position).commit();
	}
	
	static int getPicPosition() {
		return picPosition;
	}
	
	static void setShowSettingsWarning(boolean flag) {
		showSettingsWarning = flag;
		L.preferences.edit().putBoolean("showSettingsWarning", flag).commit();
	}
	
	static boolean getShowSettingsWarning() {
		return showSettingsWarning;
	}
	
	static void restoreDefaultSettings() {
		setNumPieces(defaultNumPieces);
		setPicPosition(defaultPicPosition);
		setShowSettingsWarning(defaultShowSettingsWarning);
	}
	
	static boolean getChanged() {
		boolean flag = changed;
		changed = false;
		return flag;
	}
}