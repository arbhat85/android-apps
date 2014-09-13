package com.puzzle.jigsaw;

import android.content.Context;

public class Game {
	private static boolean inProgress = false; //variable to keep track of whether a game is in progress
	
	/**********************************************************************
	 ********************** GameInProgress Methods ************************
	 **********************************************************************/
	
	/* Return TRUE if a game is in progress, FALSE otherwise */
	static boolean isInProgess() {
		return inProgress;
	}
	
	/* Set the gameInProgress variable to TRUE or FALSE */
	static void setInProgess(boolean flag) {
		inProgress = flag;
	}
	
	/**********************************************************************
	 ************************** Check Methods *****************************
	 **********************************************************************/
	
	/* Check if Jigsaw has been solved or if this Piece is in the right position and orientation */
	static void checkCompleteness(Piece piece, Context context) {
		if (isSolved()) { //puzzle is solved
    		PlayActivity.puzzleComplete(context); //cleanup
    	} else {
    		if(Game.isPieceCorrect(piece)) { //piece is in the correct position and orientation
    			Game.markPiece(piece, true); //mark the piece as correct
    		}
    	}
	}
	
	/* Check if Jigsaw has been solved or if this Piece is in the right position and orientation */
	static void checkCompleteness(Piece piece1, Piece piece2, Context context) {
		if (isSolved()) { //puzzle is solved
    		PlayActivity.puzzleComplete(context); //cleanup
    	} else {
    		if(isPieceCorrect(piece1)) { //piece is in the correct position and orientation
    			markPiece(piece1, true); //mark the piece as correct
    		}
    		if(Game.isPieceCorrect(piece2)) { //piece is in the correct position and orientation
    			markPiece(piece2, true); //mark the piece as correct
    		}
    	}
	}
	
	/* Check if all the pieces are in the correct position and orientation */
	static boolean isSolved() {
		for (int i=0; i<Inputs.getNumPieces(); i++) {	
			if (!isPieceCorrect(Pieces.jigsawPieces.get(i))) { //check if the piece is in the correct position and orientation
				return false; //return if even once piece is not
			}
		}
		return true; //if the method reached here, then it means that all the pieces are correctly placed
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
	
	/* Mark the piece as correctly/incorrectly placed by showing the appropriate photo in the ImageView */
	static void markPiece(Piece piece, boolean flag) {
		/* flag = true means that the piece is correctly placed */
		if (flag) {
			Pieces.pieceViews.get(piece.getPosition()).setImageBitmap(piece.getCorrectPhoto()); //show correctPhoto
		} else {
			Pieces.pieceViews.get(piece.getPosition()).setImageBitmap(piece.getPhoto()); //show the bordered photo
		}
		//(Note: pieceViews is arranged in the same order as jigsawPieces. Both are arranged by currentPosition)
	}
}
