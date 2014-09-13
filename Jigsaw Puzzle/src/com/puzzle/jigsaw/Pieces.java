package com.puzzle.jigsaw;

import java.util.ArrayList;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class Pieces {
	
	static ArrayList<Piece> jigsawPieces; //array of pieces that will hold the individual pieces in order of currentPosition
	static ArrayList<ImageView> pieceViews; //array of ImageViews that will hold the individual photos in order of currentPosition
	
	/* Constants that are used as indices into arrays */
	private static final int WIDTH = 0, HEIGHT = 1;
	
	/**********************************************************************
	 ************************** Piece Methods *****************************
	 **********************************************************************/
	
	/* Break the given picture into [rows*cols] pieces and randomize their positions and orientations */
	static ArrayList<Piece> createPieces (Bitmap photo) {
		int[] picSize = InitDisplay.getPicDimensions();
		int[] pieceSize = InitDisplay.getPieceDimensions();
		
		/* Scale the photo to the dynamically calculated values */
		Bitmap photoScaled = Bitmap.createScaledBitmap(photo, picSize[WIDTH], picSize[HEIGHT], false);
		
		/* The photoScaled bitmap now contains the given photo in scaled bitmap form. Break it and
		 * assign it to [rows*cols] small Jigsaw pieces after randomizing their positions and orientations
		 * The photo is being broken into a 3x3 grid. i represents rows while j represents columns */
		
		ArrayList<Piece> pieces = new ArrayList<Piece>(Inputs.getNumPieces());
		Bitmap imgPiece = null;
		int offsetX = 0, offsetY  = 0;
		int pos = 0;
		
		for (int i=0; i<Inputs.getNumDimension(); i++) {
			/* offsetX represents the x coordinate while offsetY represents the y coordinate */
			offsetX = 0; //start from (0,0)
			for (int j=0; j<Inputs.getNumDimension(); j++) {
				/* Extract a specific area of the photoScaled bitmap and store it in imgPiece.
				 * Coordinates for the extraction are specified using offsetX and offsetY */
				imgPiece = Bitmap.createBitmap
						(photoScaled, offsetX, offsetY, pieceSize[WIDTH], pieceSize[HEIGHT]);
				
				/* Create a Jigsaw piece and add it to the pieces array */
				Piece piece = new Piece (imgPiece); //create a new piece with the extracted bitmap photo
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
		for (i=0; i<Inputs.getNumPieces(); i++) {
			if ( (PlayActivity.pieceViewLocations.get(i)[0] == x) && (PlayActivity.pieceViewLocations.get(i)[1] == y)) {
				break;
			}
		}
		return jigsawPieces.get(i); //return the piece at the corresponding position
	}
	
	/* Swap the currentPosition of the pieces and update 
	 * jigsawPieces to ensure it is sorted by currentPosition */
	static void swapPositions(Piece src, Piece dst) {
		/* Swap the current positions of the pieces */
		int tempPosition = src.getPosition();
    	src.setPosition(dst.getPosition());
    	dst.setPosition(tempPosition);
    	
    	/* Swap the pieces in jigsawPieces to ensure that it is sorted by currentPosition */
		jigsawPieces.set(src.getPosition(), src);
		jigsawPieces.set(dst.getPosition(), dst);
	}
}
