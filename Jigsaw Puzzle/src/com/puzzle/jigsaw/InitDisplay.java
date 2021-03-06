package com.puzzle.jigsaw;

import java.util.ArrayList;

/*
 * The InitDisplay class is used to discover the type of screen and calculate the optimal
 * size for each piece specifically for the device that this app is running on
 * 
 * Note: the positioning of the pieces MUST be completely dynamic
 * Different devices have different resolutions/screen sizes
 * and it's impossible for the app to work across all these devices if values are hard coded
 */
public class InitDisplay {
	private static int[] picDimensions = new int[2]; //size of the picture
	private static int[] pieceDimensions = new int[2]; //size of each piece

	private final static int WIDTH = 0, HEIGHT = 1;
	
	/* Dynamically calculate the locations and sizes of the pieces */
	public static ArrayList<int[]> initialize() {
		
		/* The ImageViews will be display as a NUM_ROWS x NUM_COLS grid */	
		
		/* Get the available width for the photo */
		picDimensions[WIDTH] = L.screenDimensions.x /*- ( L.rootLayoutPadding[LEFT] + L.rootLayoutPadding[RIGHT] )*/;
		picDimensions[HEIGHT] = L.screenDimensions.x/* - ( L.rootLayoutPadding[TOP] + L.rootLayoutPadding[BOTTOM] )*/;
		
		/* Make sure the picWidth is divisible by NUM[ROWS] because
		 * the ImageViews will be displayed as a NUM_ROWS x NUM_COLS grid */
		while (picDimensions[WIDTH] % Inputs.getNumDimension() != 0) {
			picDimensions[WIDTH]--;
		}
		/* Making an assumption that the height will be > width, which is (almost) always true
		 * Also ensuring the photo has square dimensions because each of the piece is going to be a square */
		picDimensions[HEIGHT] = picDimensions[WIDTH];
		
		/* Divide the piece width and height by the number of rows and columns respectively
		 * to get each piece's width and height */
		pieceDimensions[WIDTH] = picDimensions[WIDTH]/Inputs.getNumDimension();
		pieceDimensions[HEIGHT] = picDimensions[HEIGHT]/Inputs.getNumDimension();
		
		/* Map the locations of each piece and return */
		return setImageViewLocations();
	}
	
	/* Set the locations of the individual pieces */
	private static ArrayList<int[]> setImageViewLocations() {
		/* Coordinates are relative to the parent and the parent starts at (0,0)
		 * Therefore, the first ImageView has coordinates of (0,0) */
		
		/* An example of a 3x3 grid is shown below:
		 * (coordinates are relative to the parent layout
		 * 
		 *    (0,0)----------(width,0)----------(2*width,0)----------(3*width,0)
		 *		|				 |					 |					  |
		 *		|		0		 |		   1		 |			2		  |
		 *		|				 |					 |					  |
		 * (0,height)------(width,height)----(2*width,height)------(3*width,height)
		 *		|				 |					 |					  |
		 *		|		3		 |		   4		 |			5		  |
		 *		|				 |					 |					  |
		 * (0,2*height)---(width,2*height)---(2*width,2*height)---(3*width,2*height)
		 *		|				 |					 |					  |
		 *		|		6		 |		   7		 |			8		  |
		 *		|				 |					 |					  |
		 * (0,3*height)---(width,3*height)---(2*width,3*height)---(3*width,3*height)
		 */

		ArrayList<int[]> imageViewLocations = new ArrayList<int[]>(Inputs.getNumPieces()); //array that will contain all the locations
		int[] xy;
		for (int i=0; i<Inputs.getNumDimension(); i++) {
			for (int j=0; j<Inputs.getNumDimension(); j++) {
				xy = new int[2];
				xy[0] = j*pieceDimensions[WIDTH];
				xy[1] = i*pieceDimensions[HEIGHT];
				imageViewLocations.add(xy);
			}
		}
		return imageViewLocations;
	}
	
	/* Return the dimensions of the picture */
	public static int[] getPicDimensions() {
		return picDimensions;
	}
	
	/* Return the dimensions of each piece */
	public static int[] getPieceDimensions() {
		return pieceDimensions;
	}
	
}