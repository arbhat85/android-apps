package com.puzzle.jigsaw;

import java.util.ArrayList;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.DragEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class Play extends Activity implements OnClickListener, OnLongClickListener {
	/* Constant variables used as indices into arrays */
	final static int WIDTH = 0, HEIGHT = 1;
	final static int ROWS = 0, COLS = 1, TOTAL = 2;
	
	static TextView msgText = null;
	static Button viewAgainButton = null;
	GridLayout piecesGrid = null; //GridLayout to which the ImageViews will be added
	static ArrayList<Piece> jigsawPieces; //array of pieces that will hold the individual pieces in order of currentPosition
	static ArrayList<ImageView> pieceViews; //array of ImageViews that will hold the individual images in order of currentPosition
	static ArrayList<int[]> pieceViewLocations = null; //array of coordinates that will hold the locations of each pieceView relative to piecesGrid
	static boolean complete = false; //variable to keep track of whether the Jigsaw puzzle has been solved
	static int[] NUM = new int[3]; //variable that holds the number of rows and columns in piecesGrid
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    
		/* Randomize the number of rows and columns */
		NUM = Helper.randomizeNumRowsCols();
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); //remove the title bar
	    
	    /* Note: the positioning of the pieces MUST be completely dynamic
		 * Different devices have different resolutions/screen sizes
		 * and it's impossible for the app to work across all these devices if values are hard coded */
	    
	    /* Dynamically calculate the screen positions and sizes of the individual pieces
	     * Store the starting (x,y) of all the pieces in pieceViewLocations */
		pieceViewLocations = InitDisplay.initialize(getScreenDimensions(), getRootLayoutPadding());
		
	    setContentView(R.layout.activity_play);

	    /* Create an array of ImageViews to store the individual piece images */
	    createPieceViews(); 

	    /* Generate the puzzle */
		generatePuzzle(this.getResources(), R.drawable.pic);

		/* Get handles to Buttons and ImageViews and add listeners to them */
		addListeners();
	}
	
	/**********************************************************************
	 *************************** Core Methods *****************************
	 **********************************************************************/
	
	/* Generate the puzzle */
	void generatePuzzle(Resources res, int drawable_id) {
		/* Call generatePuzzle() to break the picture into 9 pieces and randomize their
		 * positions and orientations. Convert the drawable to bitmap and send it */ 
		ArrayList<Piece> pieces = Helper.createPieces
				(BitmapFactory.decodeResource(this.getResources(), R.drawable.pic));
		if (pieces == null) {
			return;
		}
		
		/* Initialize jigsawPieces */
		jigsawPieces = new ArrayList<Piece>();
		for (int i=0; i<NUM[TOTAL]; i++) {
			jigsawPieces.add(i, pieces.get(i));
		}
		
		/* Copy the pieces from the 'pieces' array to jigsawPieces and sort it by currentPosition.
		 * Note that the 'pieces' array is sorted by correctPosition*/
		for (int i=0; i<NUM[TOTAL]; i++) {
			/* Manipulate the array such that jigsawPieces stores pieces sorted by currentPosition */
			jigsawPieces.set(pieces.get(i).getPosition(), pieces.get(i));
		}
		
		/* Assign the correct images to the ImageViews */
		for (int i=0; i<NUM[TOTAL]; i++) {
			pieceViews.get(i).setImageBitmap(jigsawPieces.get(i).getImage());
		}
		
		/* Set the rows and columns for the GridLayout and add the individual ImageViews to it */
		drawGridLayout();
		
		/* Set the complete flag to false to indicate that the puzzle is not solved */
		complete = false;
		
		/* Check if any of the pieces are already placed correctly by chance (or if the puzzle is already solved) */
		for (int i=0; i<NUM[TOTAL]; i++) {
			Helper.checkCompleteness(jigsawPieces.get(i), this);
		}
	}
	
	/* Clean up the layout, initialize and generate the puzzle again
	 * Called when user clicks on Play Again after finishing a puzzle */
	void generateNewPuzzle() {
		/* Clean up and initialize the layout for the new puzzle */
		initLayout();

		/* Make appropriate changes to the UI */
		msgText.setText(R.string.good_luck_string); //change text to 'Good Luck!'
		viewAgainButton.setText(R.string.see_again_string); //change text to "View Original Image"
		
		/* Generate the puzzle again */
		generatePuzzle(this.getResources(), R.drawable.pic);
	}
	
	/* Clean up and initialize the layout for the new puzzle */
	void initLayout() {
		/* Randomize the number of rows and columns */
		NUM = Helper.randomizeNumRowsCols();

		/* Remove all the children of the gridContainer. It will be added again */
		piecesGrid.removeAllViews();
		
		/* Dynamically calculate the screen positions and sizes of the individual pieces
	     * Store the starting (x,y) of all the pieces in pieceViewLocations */
		pieceViewLocations = InitDisplay.initialize(getScreenDimensions(), getRootLayoutPadding());
		
		/* Create an array of ImageViews to store the individual piece images */
		createPieceViews();
		
		/* Add listeners to the ImageViews that were created above */
		addImageViewListeners();
	}
	
	/**********************************************************************
	 ************************** Event Listeners ***************************
	 **********************************************************************/
	
	/* Event handlers are defined in the Helper class */
		
	/* Click listener for button_viewAgain and the ImageViews */
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.button_viewAgain: //view image again button was tapped
			if (!complete) { //puzzle has not been solved
				startActivity(new Intent(this, Intro.class)); //show the complete picture
			} else { //puzzle has been solved and the 'Play Again' button has been tapped
				generateNewPuzzle(); //generate a new puzzle
			}
			break;
		case R.id.button_instructions2: //the instructions button was tapped
			/* Show the instructions in a dialog */
			Intro.showDialog(this, R.string.instructions_title, getString(R.string.instructions_message));
			break;
		default: //one of the ImageViews was tapped
			Helper.handleImageViewTap(view, getApplicationContext()); //handle the tap event
        	break;
		}
	}
		
	/* Long click listener for the ImageViews */
	@Override
	public boolean onLongClick(View view) {
		/* Set up the drag */
		ClipData clipData = ClipData.newPlainText("", "");
		DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
		
		/* Start dragging the item touched */
		view.startDrag(clipData, shadowBuilder, view, 0);
		return true;
	}

	/* Drag listener for the ImageViews */
	private class DragListener implements OnDragListener {
		@Override
		public boolean onDrag(View view, DragEvent dragEvent) {
			/* Handle drag events */
			switch (dragEvent.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED: //no action necessary
		        break;
		    case DragEvent.ACTION_DRAG_ENTERED: //no action necessary
		        break;
		    case DragEvent.ACTION_DRAG_EXITED: //no action necessary
		        break;
		    case DragEvent.ACTION_DROP:
		    	Helper.handleDragEvent(view, dragEvent, getApplicationContext()); //handle the drag event
		        break;
		    case DragEvent.ACTION_DRAG_ENDED: //no action necessary
		        break;
			}
			
			return true;
		}
	}
		
	/**********************************************************************
	 ****** Helper Methods (that can not be moved out of the class) *******
	 **********************************************************************/

	/* Create ImageViews array to store piece images that will be added to the layout and displayed later */
	void createPieceViews() {
	    pieceViews = new ArrayList<ImageView>();
	    for (int i=0; i<NUM[TOTAL]; i++) {
			pieceViews.add(new ImageView(this));
		}
	}
	
	/* Get the width and height of the screen */
	Point getScreenDimensions() {
		Display display = getWindowManager().getDefaultDisplay();
		Point screenDimensions = new Point();
		display.getSize(screenDimensions);
		return screenDimensions;
	}
	
	/* Get the padding of the root layout */
	int[] getRootLayoutPadding() {
		int LEFT=0, RIGHT=1, TOP=2, BOTTOM=3;
		int[] padding = new int[4];
		padding[LEFT] = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
		padding[RIGHT] = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
		padding[TOP] = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
		padding[BOTTOM] = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
		return padding;
	}

	/* Set the rows and columns for the GridLayout and add the individual ImageViews to it */
	void drawGridLayout() {
		piecesGrid = (GridLayout) findViewById(R.id.layout_container); //get a reference to the GridLayout
		piecesGrid.setColumnCount(NUM[COLS]); //set number of columns
		piecesGrid.setRowCount(NUM[ROWS]); //set number of rows
		for (int i=0; i<NUM[TOTAL]; i++) {
			piecesGrid.addView(pieceViews.get(i), i); //add the ImageViews
		}
	}
	
	/* Get handles to Buttons and ImageViews and add listeners to them */
	void addListeners() {
		/* Get a handle to the views */
		msgText = (TextView) findViewById(R.id.msgView); //TextView handle
		viewAgainButton = (Button) findViewById(R.id.button_viewAgain); //Button handle
		
		/* Add a click listener to the View Again button */
		viewAgainButton.setOnClickListener(this);
		
		/* Add a click listener to the Instructions button */
		Button buttonInstructions = (Button) findViewById(R.id.button_instructions2);
		buttonInstructions.setOnClickListener(this);
		
		/* Add listeners to the ImageViews */
		addImageViewListeners();
	}
	
	/* Add listeners to the ImageViews */
	void addImageViewListeners() {
		for (int i=0; i<NUM[TOTAL]; i++) {
			pieceViews.get(i).setOnClickListener(this); //OnClickListener for rotation 
			pieceViews.get(i).setOnLongClickListener(this); //OnLongClickListener to start dragging
			pieceViews.get(i).setOnDragListener(new DragListener()); //OnDragListener for dragging			
		}
	}

}