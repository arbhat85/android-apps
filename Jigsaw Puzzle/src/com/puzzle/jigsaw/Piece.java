package com.puzzle.jigsaw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

/* 
 * The Piece class is used to store the information that is relevant to a single piece
 * It provides methods to retrieve and set relevant information
 */
public class Piece {
	private Bitmap borderedPhoto; //photo associated with the Piece if it's placed incorrectly (indicated by a border)
	private Bitmap correctPhoto; //photo associated with the Piece is it's placed correctly (no border)
	private int currentPosition; //the current position of the piece
	private int correctPosition; //the correct position of the piece
	private int currentOrientation; //the current orientation of the piece
	public final static int CORRECT_ORIENTATION = 0; //the correct orientation for all pieces is defined to be 0
	static final Random rand = new Random(); //initialize random number generator used to randomly determine the orientation of the piece
	
	private static int occurrence = 0; //tracks the number of times the constructor was called
	private static ArrayList<Integer> numbers = null; //array to choose the piece position randomly
	
	private final static int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3; //constants values used to indicate the orientation
	
	public Piece(Bitmap pic) {
		
		/* Store a randomized array of numbers from 0-NUM_TOTAL during the first call of the constructor
		 * The currentPosition of the pieces in subsequent calls will be assigned from this array */
		if (occurrence == 0) {
			generateRandomPositionsArray();	//generate the random array
		}
		
		correctPhoto = pic; //set the correctPhoto to be the unbordered photo
		
		setBorderedPhoto(pic); //border the photo and assign it to borderedPhoto
		
		setCurrentOrientation(); //randomly generate an orientation and rotate the photo accordingly

		//correct orientation for all Pieces is defined to be 0
		
		occurrence++;
		if (occurrence == Inputs.getNumPieces()) { //reset occurrence if the user taps on the Play Again button
			occurrence = 0;
		}
	}
	
	/* Create a randomized array of numbers from 0 - NUM_TOTAL-1
	 * The currentPosition will be assigned from this array */
	private void generateRandomPositionsArray() {
		numbers = new ArrayList<Integer>(Inputs.getNumPieces());
		for (int i=0; i<Inputs.getNumPieces(); i++) { //generate the numbers array
			numbers.add(i, i);
		}
		Collections.shuffle(numbers); //randomly shuffle the array
	}
	
	/* Border the photo and assign it to borderedPhoto */
	private void setBorderedPhoto(Bitmap photo) {
		borderedPhoto = L.drawBorder(photo, Color.YELLOW, 2);
	}
	
	/* Randomly generate an orientation and rotate the photo accordingly */
	private void setCurrentOrientation() {
		currentPosition = numbers.get(occurrence); //set a number from the numbers array as the currentPosition
		correctPosition = occurrence; //correctPosition is the same as the order in which the Pieces are created
		currentOrientation = rand.nextInt(3); //randomly assign an orientation from 0-3 (0-up, 1-right, 2-down, 3-left)
		rotatePhoto(currentOrientation); //rotate the photo based on the currentOrientation
	}
	
	/* Update the currentOrientation */
	private void updateCurrentOrientation(int angle) {
		/* Angle can only be 90 degrees. Increment currentOrientation and resent to 0 if > 3 */
		if (angle == 90) {
			if (++currentOrientation > LEFT) {
				currentOrientation = UP;
			}
		} else {
			Log.e("rotatePhoto", "Invalid angle!");
		}
	}
	
	/* Rotate the borderedPhoto */
	private void updatePhoto(int angle) {
		Bitmap img = borderedPhoto;
		Matrix matrix = new Matrix();
		matrix.postRotate(angle); //rotate the photo	
		borderedPhoto = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true); //set the photo
	}
	
	/* 
	 * PUBLIC METHODS
	 */
	
	/* Return the borderedPhoto associated with the Piece */
	public Bitmap getPhoto() {
		return borderedPhoto;
	}
	
	/* Set the borderedPhoto associated with the Piece */
	public void setPhoto(Bitmap img) {
		borderedPhoto = img;
	}
	
	/* Return the correct photo associated with the Piece */
	public Bitmap getCorrectPhoto() {
		return correctPhoto;
	}
	
	/* The correct photo association is done by the Piece constructor and will not change afterward
	 * Therefore, there is no setCorrectPhoto() method */
	
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
	
	/* Rotate the photo associated with the Piece
	 * Note: caller has to assign the rotated photo to the corresponding ImageView */
	public void rotatePhoto(int orientationOrAngle) {
		int angle = 0, orientation = -1;
		
		/* Parameter is either the orientation or the angle by which the photo should be rotated. 
		 * Orientation ranges from 0 to 3, so if the parameter is more than 3, it's an angle */
		if (orientationOrAngle > LEFT) {
			angle = orientationOrAngle;
			updateCurrentOrientation(angle); //update currentOrientation
		} else {
			orientation = orientationOrAngle;
			
			/* Get the angle by which the photo needs to be rotated based on the orientation */
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
		
		updatePhoto(angle); //rotate the photo (caller has to assign the photo to ImageView)
	}
	
}