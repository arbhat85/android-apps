package com.puzzle.jigsaw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

/* 
 * The Piece class is used to store the information that is relevant to a single piece
 * It provides methods to retrieve and set relevant information
 */
public class Piece {
	private Bitmap borderedImage; //image associated with the Piece if it's placed incorrectly (indicated by a border)
	private Bitmap correctImage; //image associated with the Piece is it's placed correctly (no border)
	private int currentPosition; //the current position of the piece
	private int correctPosition; //the correct position of the piece
	private int currentOrientation; //the current orientation of the piece
	public final static int CORRECT_ORIENTATION = 0; //the correct orientation for all pieces is defined to be 0
	static final Random rand = new Random(); //initialize random number generator used to randomly determine the orientation of the piece
	private int NUM_TOTAL; //holds the value of rows*columns of the puzzle grid
	
	private static int occurrence = 0; //tracks the number of times the constructor was called
	private static ArrayList<Integer> numbers = null; //array to choose the piece position randomly
	
	private final static int TOTAL = 2; //constant value used as an index into Play.NUM
	private final static int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3; //constants values used to indicate the orientation
	
	public Piece(Bitmap pic) {
		NUM_TOTAL = Play.NUM[TOTAL];
		
		/* Store a randomized array of numbers from 0-NUM_TOTAL during the first call of the constructor
		 * The currentPosition of the pieces in subsequent calls will be assigned from this array */
		if (occurrence == 0) {
			generateRandomPositionsArray();	//generate the random array
		}
		
		correctImage = pic; //set the correctImage to be the unbordered image
		
		setBorderedImage(pic); //border the image and assign it to borderedImage
		
		setCurrentOrientation(); //randomly generate an orientation and rotate the image accordingly

		//correct orientation for all Pieces is defined to be 0
		
		occurrence++;
		if (occurrence == NUM_TOTAL) { //reset occurrence if the user taps on the Play Again button
			occurrence = 0;
		}
	}
	
	/* Create a randomized array of numbers from 0 - NUM_TOTAL-1
	 * The currentPosition will be assigned from this array */
	private void generateRandomPositionsArray() {
		numbers = new ArrayList<Integer>(NUM_TOTAL);
		for (int i=0; i<NUM_TOTAL; i++) { //generate the numbers array
			numbers.add(i, i);
		}
		Collections.shuffle(numbers); //randomly shuffle the array
	}
	
	/* Border the image and assign it to borderedImage */
	private void setBorderedImage(Bitmap pic) {
		int WIDTH = 0, COLOR = 1;
		int[] border = new int[2]; //border[] specifies the width and color of the border
		border[WIDTH] = 2;
		border[COLOR] = Color.YELLOW;
		
		RectF borderRect = new RectF(border[WIDTH], border[WIDTH],
				pic.getWidth()-border[WIDTH], pic.getHeight()-border[WIDTH]); //mark the image area within the bordered image
		
		borderedImage = Bitmap.createBitmap(pic); //create a new Bitmap that contains the original image
		Canvas canvas = new Canvas(borderedImage); //create a new canvas to draw the bordered image
		canvas.drawColor(border[COLOR]); //fill the entire canvas with the specified color
		canvas.drawBitmap(pic, null, borderRect, null); //draw the original image into the area marked above
	}
	
	/* Randomly generate an orientation and rotate the image accordingly */
	private void setCurrentOrientation() {
		currentPosition = numbers.get(occurrence); //set a number from the numbers array as the currentPosition
		correctPosition = occurrence; //correctPosition is the same as the order in which the Pieces are created
		currentOrientation = rand.nextInt(3); //randomly assign an orientation from 0-3 (0-up, 1-right, 2-down, 3-left)
		rotateImage(currentOrientation); //rotate the image based on the currentOrientation
	}
	
	/* Update the currentOrientation */
	private void updateCurrentOrientation(int angle) {
		/* Angle can only be 90 degrees. Increment currentOrientation and resent to 0 if > 3 */
		if (angle == 90) {
			if (++currentOrientation > LEFT) {
				currentOrientation = UP;
			}
		} else {
			Log.i("rotateImage", "Invalid angle!");
		}
	}
	
	/* Rotate the borderedImage */
	private void updateImage(int angle) {
		Bitmap img = borderedImage;
		Matrix matrix = new Matrix();
		matrix.postRotate(angle); //rotate the image	
		borderedImage = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true); //set the image
	}
	
	/* 
	 * PUBLIC METHODS
	 */
	
	/* Return the borderedImage associated with the Piece */
	public Bitmap getImage() {
		return borderedImage;
	}
	
	/* Set the borderedImage associated with the Piece */
	public void setImage(Bitmap img) {
		borderedImage = img;
	}
	
	/* Return the correct image associated with the Piece */
	public Bitmap getCorrectImage() {
		return correctImage;
	}
	
	/* The correct image association is done by the Piece constructor and will not change afterward
	 * Therefore, there is no setCorrectImage() method */
	
	/* Return the current position of the Piece */
	public int getPosition() {
		return currentPosition;
	}
	
	/* Set the current position of the Piece */
	public void setPosition (int position) {
		currentPosition = position;
	}
	
	/* Return the correct position of the Piece */
	public int getCorrectPosition() {
		return correctPosition;
	}
	
	/* Return the current orientation of the Piece */
	public int getOrientation() {
		return currentOrientation;
	}
	
	/* Set the current orientation of the Piece */
	public void setOrientation(int orientation) {
		currentOrientation = orientation;
	}
	
	/* Correct orientation for all Pieces is defined to be 0 */
	
	/* Rotate the image associated with the Piece
	 * Note: caller has to assign the rotated image to the corresponding ImageView */
	public void rotateImage(int orientationOrAngle) {
		int angle = 0, orientation = -1;
		
		/* Parameter is either the orientation or the angle by which the image should be rotated. 
		 * Orientation ranges from 0 to 3, so if the parameter is more than 3, it's an angle */
		if (orientationOrAngle > LEFT) {
			angle = orientationOrAngle;
			updateCurrentOrientation(angle); //update currentOrientation
		} else {
			orientation = orientationOrAngle;
			
			/* Get the angle by which the image needs to be rotated based on the orientation */
			switch(orientation) {
			case UP: //Do not rotate. Up orientation
				return;
			case RIGHT: //Rotate 90 degrees right - Right orientation
				angle = 90;
				break;
			case DOWN: //Rotate 180 degrees right - Down orientation
				angle = 180;
				break;
			case LEFT: //Rotate 270 degrees right - Left orientation
				angle = 270;
				break;
			}
		}
		
		updateImage(angle); //rotate the image (caller has to assign the image to ImageView)
	}
	
}