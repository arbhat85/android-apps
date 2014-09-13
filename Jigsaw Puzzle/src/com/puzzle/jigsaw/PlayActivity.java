package com.puzzle.jigsaw;

import java.util.ArrayList;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends Activity implements OnClickListener, OnLongClickListener {
	private static ActionBar actionBar = null;
	
	static TextView msgText = null;
	GridLayout piecesGrid = null; //GridLayout to which the ImageViews will be added
	static ArrayList<int[]> pieceViewLocations = null; //array of coordinates that will hold the locations of each pieceView relative to piecesGrid
	int numDim = 0; //internal variable that tracks the grid dimensions
	Context context = null;
	
	static int currentImage = Inputs.getPicPosition();
	static int currentNum = Inputs.getNumDimension();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		actionBar = getActionBar();
		L.hideIcon(actionBar); //hide app icon from the title bar
		
		context = this;
		
	    /* Note: the positioning of the pieces MUST be completely dynamic
		 * Different devices have different resolutions/screen sizes
		 * and it's impossible for the app to work across all these devices if values are hard coded */
	    
	    /* Dynamically calculate the screen positions and sizes of the individual pieces
	     * Store the starting (x,y) of all the pieces in pieceViewLocations */
		pieceViewLocations = InitDisplay.initialize();
		
	    setContentView(R.layout.play);

	    /* Create an array of ImageViews to store the individual piece images */
	    createPieceViews(); 

	    /* Generate the puzzle */
		generatePuzzle(this.getResources(), L.photoBitmaps[Inputs.getPicPosition()]);

		/* Get handles to Buttons and ImageViews and add listeners to them */
		addListeners();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (Inputs.getChanged()) {
			generateNewPuzzle();
		}
	}
	
	/**********************************************************************
	 *************************** Core Methods *****************************
	 **********************************************************************/
	
	/* Generate the puzzle */
	void generatePuzzle(Resources res, Bitmap photo) {
		/* Call generatePuzzle() to break the picture into 9 pieces and randomize their
		 * positions and orientations. Convert the drawable to bitmap and send it */ 
		ArrayList<Piece> pieces = Pieces.createPieces(photo);
		if (pieces == null) {
			return;
		}
		
		/* Initialize jigsawPieces */
		Pieces.jigsawPieces = new ArrayList<Piece>();
		for (int i=0; i<Inputs.getNumPieces(); i++) {
			Pieces.jigsawPieces.add(i, pieces.get(i));
		}
		
		/* Copy the pieces from the 'pieces' array to jigsawPieces and sort it by currentPosition.
		 * Note that the 'pieces' array is sorted by correctPosition*/
		for (int i=0; i<Inputs.getNumPieces(); i++) {
			/* Manipulate the array such that jigsawPieces stores pieces sorted by currentPosition */
			Pieces.jigsawPieces.set(pieces.get(i).getPosition(), pieces.get(i));
		}
		
		/* Assign the correct photos to the ImageViews */
		for (int i=0; i<Inputs.getNumPieces(); i++) {
			Pieces.pieceViews.get(i).setImageBitmap(Pieces.jigsawPieces.get(i).getPhoto());
		}
		
		/* Set the rows and columns for the GridLayout and add the individual ImageViews to it */
		drawGridLayout();
		
		/* Set GameInProgress to TRUE since a puzzle has just been created */
		Game.setInProgess(true);
		
		/* Check if any of the pieces are already placed correctly by chance (or if the puzzle is already solved) */
		for (int i=0; i<Inputs.getNumPieces(); i++) {
			Game.checkCompleteness(Pieces.jigsawPieces.get(i), this);
		}
	}
	
	/* Clean up the layout, initialize and generate the puzzle again
	 * Called when user clicks on Play Again after finishing a puzzle */
	void generateNewPuzzle() {
		/* Clean up and initialize the layout for the new puzzle */
		initLayout();

		actionBar.setTitle(R.string.title_activity_play_goodLuck); //change title text to Good Luck!
		
		/* Generate the puzzle again */
		generatePuzzle(this.getResources(), L.photoBitmaps[Inputs.getPicPosition()]);
	}
	
	/* Clean up and initialize the layout for the new puzzle */
	void initLayout() {
		/* Remove all the children of the gridContainer. It will be added again */
		piecesGrid.removeAllViews();
		
		/* Dynamically calculate the screen positions and sizes of the individual pieces
	     * Store the starting (x,y) of all the pieces in pieceViewLocations */
		pieceViewLocations = InitDisplay.initialize();
		
		/* Create an array of ImageViews to store the individual piece photos */
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
		case R.id.button_instructions: //the instructions button was tapped
			startActivity(new Intent(this, InstructionsActivity.class));
			break;
		case R.id.button_settings:
			if(Inputs.getShowSettingsWarning()) {
				AlertDialog.Builder builder = L.showSettingsWarning(this);
				builder.setPositiveButton(R.string.dialogbox_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(context, SettingsActivity.class));
					}
				});
				builder.setNegativeButton(R.string.dialogbox_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//do nothing
					}
				});
				builder.create().show();
			} else {
				startActivity(new Intent(this, SettingsActivity.class));
			}
			break;
		case R.id.button_viewPic:
			startActivity(new Intent(this, PreviewActivity.class));
			break;
		case R.id.button_newGame:
			if (Game.isInProgess()) {
				/* If user clicks on New Game when a game is in progress, get confirmation
				 * that the current game will be lost. This is done via a dialog */
				AlertDialog.Builder builder = L.showDialog(this, R.string.dialogbox_new_game_title,
						R.string.dialogbox_new_game_message, false);
				builder.setPositiveButton(R.string.dialogbox_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						generateNewPuzzle();
					}
				});
				builder.setNegativeButton(R.string.dialogbox_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Do nothing
					}
				});
				builder.create().show();
			} else {
				/* A game is not in progress, so start one */ 
				generateNewPuzzle();
			}
			break;
		default: //one of the ImageViews was tapped
			L.handleImageViewTap(view, getApplicationContext()); //handle the tap event
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
		    	L.handleDragEvent(view, dragEvent, getApplicationContext()); //handle the drag event
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

	/* Create ImageViews array to store piece photos that will be added to the layout and displayed later */
	void createPieceViews() {
	    Pieces.pieceViews = new ArrayList<ImageView>();
	    for (int i=0; i<Inputs.getNumPieces(); i++) {
			Pieces.pieceViews.add(new ImageView(this));
		}
	}

	/* Set the rows and columns for the GridLayout and add the individual ImageViews to it */
	void drawGridLayout() {
		piecesGrid = (GridLayout) findViewById(R.id.layout_container); //get a reference to the GridLayout
		piecesGrid.setColumnCount(Inputs.getNumDimension()); //set number of columns
		piecesGrid.setRowCount(Inputs.getNumDimension()); //set number of rows
		for (int i=0; i<Inputs.getNumPieces(); i++) {
			piecesGrid.addView(Pieces.pieceViews.get(i), i); //add the ImageViews
		}
	}
	
	/* Get handles to Buttons and ImageViews and add listeners to them */
	void addListeners() {
		/* Get handles to the buttons */
		Button buttonInstructions = (Button) findViewById(R.id.button_instructions);
		Button buttonSettings = (Button) findViewById(R.id.button_settings);
		Button buttonSeePhoto = (Button) findViewById(R.id.button_viewPic);
		Button buttonNewGame = (Button) findViewById(R.id.button_newGame);
		
		L.setMargin(buttonInstructions, L.RIGHT, 0);
		L.setMargin(buttonSeePhoto, L.RIGHT, 0);
		
		/* Add click listeners to the buttons */
		buttonInstructions.setOnClickListener(this);
		buttonSettings.setOnClickListener(this);
		buttonSeePhoto.setOnClickListener(this);
		buttonNewGame.setOnClickListener(this);
		
		/* Add listeners to the ImageViews */
		addImageViewListeners();
	}
	
	/* Add listeners to the ImageViews */
	void addImageViewListeners() {
		for (int i=0; i<Inputs.getNumPieces(); i++) {
			Pieces.pieceViews.get(i).setOnClickListener(this); //OnClickListener for rotation 
			Pieces.pieceViews.get(i).setOnLongClickListener(this); //OnLongClickListener to start dragging
			Pieces.pieceViews.get(i).setOnDragListener(new DragListener()); //OnDragListener for dragging			
		}
	}
	
	/* Perform cleanup once the puzzle has been solved */
	static void puzzleComplete(Context context) { 
		/* Inform the user that the puzzle is complete through a Toast and the action bar */
		Toast.makeText(context, R.string.toast_finished, Toast.LENGTH_SHORT).show(); //Toast message
		actionBar.setTitle(R.string.title_activity_play_congrats); //action bar message

		L.disableImageViews(); //disable all the ImageViews so user can't move them around
		
		Game.setInProgess(false); //the puzzle has been solved; hence, theere is no game in progress	
		for (int i=0;i<Inputs.getNumPieces();i++) { //mark all pieces as correct (the unbordered photo is shown)
			Game.markPiece(Pieces.jigsawPieces.get(i), true);
		}
	}

}