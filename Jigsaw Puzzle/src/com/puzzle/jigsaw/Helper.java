package com.puzzle.jigsaw;

import java.util.ArrayList;
import java.util.Collections;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Helper {
	/* Constants that are used as indices into arrays */
	static final int ROWS = 0, COLS = 1, TOTAL = 2;
	static final int WIDTH = 0, HEIGHT = 1;
	
	/**********************************************************************
	 ************************** Piece Methods *****************************
	 **********************************************************************/
	
	/* Break the given picture into [rows*cols] pieces and randomize their positions and orientations */
	static ArrayList<Piece> createPieces (Bitmap image) {
		int[] picSize = InitDisplay.getPicDimensions();
		int[] pieceSize = InitDisplay.getPieceDimensions();
		
		/* Scale the image to the dynamically calculated values */
		Bitmap imageScaled = Bitmap.createScaledBitmap(image, picSize[WIDTH], picSize[HEIGHT], false);
		
		/* The imageScaled bitmap now contains the given image in scaled bitmap form. Break it and
		 * assign it to [rows*cols] small Jigsaw pieces after randomizing their positions and orientations
		 * The image is being broken into a 3x3 grid. i represents rows while j represents columns */
		
		ArrayList<Piece> pieces = new ArrayList<Piece>(Play.NUM[TOTAL]);
		Bitmap imgPiece = null;
		int offsetX = 0, offsetY  = 0;
		int pos = 0;
		
		for (int i=0; i<Play.NUM[COLS]; i++) {
			/* offsetX represents the x coordinate while offsetY represents the y coordinate */
			offsetX = 0; //start from (0,0)
			for (int j=0; j<Play.NUM[ROWS]; j++) {
				/* Extract a specific area of the imageScaled bitmap and store it in imgPiece.
				 * Coordinates for the extraction are specified using offsetX and offsetY */
				imgPiece = Bitmap.createBitmap
						(imageScaled, offsetX, offsetY, pieceSize[WIDTH], pieceSize[HEIGHT]);
				
				/* Create a Jigsaw piece and add it to the pieces array */
				Piece piece = new Piece (imgPiece); //create a new piece with the extracted bitmap image
				pieces.add(pos, piece); //add the piece to the pieces array
				
				offsetX += pieceSize[WIDTH]; //move to the next x coordinate
				pos++;
			}
			offsetY += pieceSize[HEIGHT]; //move to the next y coordinate
		}
		
		return pieces;
	}
	
	/* Use imageViewLocations to map the coordinates of the ImageView to its position on the screen 
	 * Note: imageViewLocations is populated dynamically in the InitDisplay class */
	static Piece getPieceFromXY(float x, float y) {
		/* Coordinates are relative to the parent and the parent starts at (0,0)
		 * Therefore, the first ImageView has coordinates of (0,0)
		 * The layout of the ImageViews within the parent layout is as follows:
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
		 * 
		 */
		
		int i;
		for (i=0; i<Play.NUM[TOTAL]; i++) {
			if ( (Play.pieceViewLocations.get(i)[0] == x) && (Play.pieceViewLocations.get(i)[1] == y)) {
				break;
			}
		}
		return Play.jigsawPieces.get(i); //return the piece at the corresponding position
	}
	
	/* Swap the currentPosition of the pieces and update 
	 * jigsawPieces to ensure it is sorted by currentPosition */
	static void swapPositions(Piece src, Piece dst) {
		/* Swap the current positions of the pieces */
		int tempPosition = src.getPosition();
    	src.setPosition(dst.getPosition());
    	dst.setPosition(tempPosition);
    	
    	/* Swap the pieces in jigsawPieces to ensure that it is sorted by currentPosition */
		Play.jigsawPieces.set(src.getPosition(), src);
		Play.jigsawPieces.set(dst.getPosition(), dst);
	}
	
	/* Swap the images in the ImageViews */
	static void swapImages(ImageView src, ImageView dst) {
		Drawable tempImage = src.getDrawable();
    	src.setImageDrawable(dst.getDrawable());
    	dst.setImageDrawable(tempImage);
	}
	
	/**********************************************************************
	 ************************** Check Methods *****************************
	 **********************************************************************/
	
	/* Check if Jigsaw has been solved or if this Piece is in the right position and orientation */
	static void checkCompleteness(Piece piece, Context context) {
		if (Helper.isSolved()) { //puzzle is solved
    		puzzleComplete(context); //cleanup
    	} else {
    		if(Helper.isPieceCorrect(piece)) { //piece is in the correct position and orientation
    			Helper.markPiece(piece, true); //mark the piece as correct
    		}
    	}
	}
	
	/* Check if Jigsaw has been solved or if this Piece is in the right position and orientation */
	static void checkCompleteness(Piece piece1, Piece piece2, Context context) {
		if (isSolved()) { //puzzle is solved
    		puzzleComplete(context); //cleanup
    	} else {
    		if(isPieceCorrect(piece1)) { //piece is in the correct position and orientation
    			markPiece(piece1, true); //mark the piece as correct
    		}
    		if(Helper.isPieceCorrect(piece2)) { //piece is in the correct position and orientation
    			markPiece(piece2, true); //mark the piece as correct
    		}
    	}
	}
	
	/* Check if all the pieces are in the correct position and orientation */
	static boolean isSolved() {
		for (int i=0; i<Play.NUM[TOTAL]; i++) {	
			if (!isPieceCorrect(Play.jigsawPieces.get(i))) { //check if the piece is in the correct position and orientation
				return false; //return if even once piece is not
			}
		}
		return true; //if the method reached here, then it means that all the pieces are correctly placed
	}
	
	/* Perform cleanup once the puzzle has been solved */
	static void puzzleComplete(Context context) { 
		/* Inform the user that the puzzzle is complete through a Toast and a TextView */
		Toast.makeText(context, R.string.finished_toast, Toast.LENGTH_SHORT).show(); //Toast message
		Play.msgText.setText(R.string.finished_text); //TextView message
		
		/* Reset the UI */
		Play.viewAgainButton.setText(R.string.play_again); //change the button text to 'Play Again'
		disableImageViews(); //disable all the ImageViews so user can't move them around
		
		Play.complete = true; //set complete to true to indicate that the puzzle is complete	
		for (int i=0;i<Play.NUM[TOTAL];i++) { //mark all pieces as correct (the unbordered image is shown)
			Helper.markPiece(Play.jigsawPieces.get(i), true);
		}
	}
	
	/* Check if the piece is in the correct position and orientation */
	static boolean isPieceCorrect(Piece piece) {
		/* Position is correct if currentPositon = correctPosition
		 * Orientation is correct if currentOrientation = Piece.CORRECT_ORIENTATION */
		if (piece.getPosition() == piece.getCorrectPosition() && piece.getOrientation() == Piece.CORRECT_ORIENTATION) {
			return true;
		}
		return false;
	}
	
	/* Mark the piece as correctly/incorrectly placed by showing the appropriate image in the ImageView */
	static void markPiece(Piece piece, boolean flag) {
		/* flag = true means that the piece is correctly placed */
		if (flag) {
			Play.pieceViews.get(piece.getPosition()).setImageBitmap(piece.getCorrectImage()); //show correctImage
		} else {
			Play.pieceViews.get(piece.getPosition()).setImageBitmap(piece.getImage()); //show the bordered image
		}
		//(Note: pieceViews is arranged in the same order as jigsawPieces. Both are arranged by currentPosition)
	}

	
	/**********************************************************************
	 ************************** Event Handlers ****************************
	 **********************************************************************/
	
	static void handleImageViewTap(View view, Context context) {
		/* Get the ImageView and rotate it by 90 degrees */
		Piece piece = Helper.getPieceFromXY(view.getX(), view.getY()); //get Piece associated with ImageView
    	piece.rotateImage(90); //single taps will rotate the image by 90 degrees
    	Play.pieceViews.get(piece.getPosition()).setImageBitmap(piece.getImage()); //set rotated image to ImageView

    	Helper.markPiece(piece, false); //mark this piece as incorrect. It will be marked correct if necessary in the call in the next line
    	Helper.checkCompleteness(piece, context); //check if Jigsaw has been solved or if this Piece is in the right position and orientation
	}
	
	/* A drag event has occurred; i.e. one ImageView (referred to as dragImageView) has
	 * been dragged and dropped on another ImageView (referred to as dropImageView) */
	static void handleDragEvent (View view, DragEvent dragEvent, Context context) {
    	ImageView dragImageView = (ImageView) dragEvent.getLocalState(); //ImageView being dragged
    	ImageView dropImageView = (ImageView) view; //ImageView being dropped on
    	
    	/* Get the pieces associated with the drag and drop ImageViews */
    	Piece dragPiece = getPieceFromXY(dragImageView.getX(), dragImageView.getY());
    	Piece dropPiece = getPieceFromXY(dropImageView.getX(), dropImageView.getY());

    	/* Swap the currentPosition of the pieces and update
    	 * jigsawPieces to ensure it is sorted by currentPosition */
    	swapPositions(dragPiece, dropPiece); //swap the current positions of the drag and drop pieces
    	swapImages(dragImageView, dropImageView); //swap the images in the drag and drop ImageViews
    	
    	/* Mark the pieces as incorrect. They will be marked as
    	 * correct if necessary in the call in the following line*/
    	markPiece(dragPiece, false);
    	markPiece(dropPiece, false);
    	checkCompleteness(dragPiece, dropPiece, context); //check if Jigsaw has been solved or if this Piece is in the right position and orientation
	}
	
	/**********************************************************************
	 ************************** Other Methods *****************************
	 **********************************************************************/
	
	/* Randomize the number of rows and columns */
	static int[] randomizeNumRowsCols() {
		/* Possible layouts are grids of either 2x2, 3x3, or 4x4
		 * Beyond that gets too cluttered on a phone screen
		 * Change NUM_ROWS to change the maximum rows and columns in the puzzle grid */
		
		final int NUM_ROWS = 4;
		ArrayList<Integer> rowcol = new ArrayList<Integer>(NUM_ROWS);
		
		for (int i=2; i<=NUM_ROWS; i++) {
			rowcol.add(i);
		}
	    Collections.shuffle(rowcol); //randomly shuffle the numbers
	    
	    int[] NUM = new int[3];
	    NUM[ROWS] = rowcol.get(0); //picking the first element of a randomly shuffled array will give a random number
	    NUM[COLS] = NUM[ROWS]; //set cols = rows because we will only deal with a square grid
	    NUM[TOTAL] = NUM[ROWS] * NUM[COLS];
	    
	    return NUM;
	}
	
	/* Disable all the ImageViews */
	static void disableImageViews() {
		for (int i=0; i<Play.NUM[TOTAL]; i++) {
			Play.pieceViews.get(i).setEnabled(false);
		}
	}
	
}