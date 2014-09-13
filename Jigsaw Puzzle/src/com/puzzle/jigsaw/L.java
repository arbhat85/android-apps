package com.puzzle.jigsaw;

import java.io.FileNotFoundException;
import java.io.InputStream;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Display;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class L {	
	private static int[] resourceIds = null;
	private static boolean called = false;
	private static int imgWidth = 0;
	
	static final int NUM_PHOTOS = 22;
	static final int NUM_ROWS = 2;
	static final int NUM_COLS = NUM_PHOTOS/NUM_ROWS;
	static final int SELECT_PHOTO = 100;
	static final int RESULT_OK = -1;
	static final int PADDING = 5;
	static final int LEFT = 0, RIGHT = 1, TOP = 2, BOTTOM = 3;
	
	static SharedPreferences preferences = null;
	
	static Point screenDimensions = null;
	static int[] rootLayoutPadding = null;
	static Bitmap[] photoBitmaps = null;
	static boolean phonePhotoSelected = false;
	static boolean startNewGame = false;
	static boolean gameStarted = false;
	
	static void init(Context context, Display display) {
		if (called == false) {
			screenDimensions = new Point();
			
			addResourceIds();
			display.getSize(screenDimensions);
			getScaledBitmapsFromResourceIds(context);
			getRootLayoutPadding(context.getResources());
			
			called = true;
		}
	}
	
	private static void addResourceIds() {
		resourceIds = new int[NUM_PHOTOS];
		resourceIds[0] = R.drawable.ayers_rock;
		resourceIds[1] = R.drawable.big_ben;
		resourceIds[2] = R.drawable.colosseum;
		resourceIds[3] = R.drawable.easter_island;
		resourceIds[4] = R.drawable.eiffel_tower;
		resourceIds[5] = R.drawable.golden_gate;
		resourceIds[6] = R.drawable.liberty;
		resourceIds[7] = R.drawable.london_bridge;
		resourceIds[8] = R.drawable.louvre;
		resourceIds[9] = R.drawable.machu_pichu;
		resourceIds[10] = R.drawable.nyc;
		resourceIds[11] = R.drawable.opera_house;
		resourceIds[12] = R.drawable.partheneon;
		resourceIds[13] = R.drawable.pisa_tower;
		resourceIds[14] = R.drawable.pyramids;
		resourceIds[15] = R.drawable.rio;
		resourceIds[16] = R.drawable.sagrada_familia;
		resourceIds[17] = R.drawable.st_petersburg;
		resourceIds[18] = R.drawable.stone_henge;
		resourceIds[19] = R.drawable.taj_mahal;
		resourceIds[20] = R.drawable.pic;
		resourceIds[21] = R.drawable.background;
	}
	
	private static void getScaledBitmapsFromResourceIds(Context context) {
		photoBitmaps = new Bitmap[NUM_PHOTOS];
		
		int screenWidth = screenDimensions.x;
			
		imgWidth = screenWidth/2 - 2*PADDING;
			
		for (int i=0; i<NUM_PHOTOS; i++) {
			photoBitmaps[i] = BitmapFactory.decodeResource(context.getResources(), resourceIds[i]);
			Bitmap photoUnscaled = BitmapFactory.decodeResource(context.getResources(), resourceIds[i]);
			photoBitmaps[i] = Bitmap.createScaledBitmap(photoUnscaled, imgWidth, imgWidth, false);
		}
	}
	
	static void hideIcon(ActionBar actionBar) {
		actionBar.setIcon(android.R.color.transparent);
	}
	
	static Bitmap drawBorder(Bitmap pic, int color, int thickness) {
		int WIDTH = 0, COLOR = 1;
		int[] border = new int[2]; //border[] specifies the width and color of the border
		border[WIDTH] = thickness;
		border[COLOR] = color;

		RectF borderRect = new RectF(border[WIDTH], border[WIDTH],
				pic.getWidth()-border[WIDTH], pic.getHeight()-border[WIDTH]); //mark the photo area within the bordered photo
		
		Bitmap borderedPic = Bitmap.createBitmap(pic); //create a new Bitmap that contains the original photo
		Canvas canvas = new Canvas(borderedPic); //create a new canvas to draw the bordered photo
		canvas.drawColor(border[COLOR]); //fill the entire canvas with the specified color
		canvas.drawBitmap(pic, null, borderRect, null); //draw the original photo into the area marked above
		
		return borderedPic;
	}
	
	/* Swap the photos in the ImageViews */
	private static void swapPhotos(ImageView src, ImageView dst) {
		Drawable tempPhoto = src.getDrawable();
    	src.setImageDrawable(dst.getDrawable());
    	dst.setImageDrawable(tempPhoto);
	}
	
	/**********************************************************************
	 ************************** Event Handlers ****************************
	 **********************************************************************/
	
	static void handleImageViewTap(View view, Context context) {
		/* Get the ImageView and rotate it by 90 degrees */
		Piece piece = Pieces.getPieceFromXY(view.getX(), view.getY()); //get Piece associated with ImageView
    	piece.rotatePhoto(90); //single taps will rotate the photo by 90 degrees
    	Pieces.pieceViews.get(piece.getPosition()).setImageBitmap(piece.getPhoto()); //set rotated photo to ImageView

    	Game.markPiece(piece, false); //mark this piece as incorrect. It will be marked correct if necessary in the call in the next line
    	Game.checkCompleteness(piece, context); //check if Jigsaw has been solved or if this Piece is in the right position and orientation
	}
	
	/* A drag event has occurred; i.e. one ImageView (referred to as dragImageView) has
	 * been dragged and dropped on another ImageView (referred to as dropImageView) */
	static void handleDragEvent (View view, DragEvent dragEvent, Context context) {
    	ImageView dragImageView = (ImageView) dragEvent.getLocalState(); //ImageView being dragged
    	ImageView dropImageView = (ImageView) view; //ImageView being dropped on
    	
    	/* Get the pieces associated with the drag and drop ImageViews */
    	Piece dragPiece = Pieces.getPieceFromXY(dragImageView.getX(), dragImageView.getY());
    	Piece dropPiece = Pieces.getPieceFromXY(dropImageView.getX(), dropImageView.getY());

    	/* Swap the currentPosition of the pieces and update
    	 * jigsawPieces to ensure it is sorted by currentPosition */
    	Pieces.swapPositions(dragPiece, dropPiece); //swap the current positions of the drag and drop pieces
    	swapPhotos(dragImageView, dropImageView); //swap the photos in the drag and drop ImageViews
    	
    	/* Mark the pieces as incorrect. They will be marked as
    	 * correct if necessary in the call in the following line*/
    	Game.markPiece(dragPiece, false);
    	Game.markPiece(dropPiece, false);
    	Game.checkCompleteness(dragPiece, dropPiece, context); //check if Jigsaw has been solved or if this Piece is in the right position and orientation
	}
	
	/**********************************************************************
	 ************************** Other Methods *****************************
	 **********************************************************************/
	
	/* Disable all the ImageViews */
	static void disableImageViews() {
		for (int i=0; i<Inputs.getNumPieces(); i++) {
			Pieces.pieceViews.get(i).setEnabled(false);
		}
	}
	
	static AlertDialog.Builder showSettingsWarning(Context context) {
		TextView tvWarningTitle = new TextView(context);
		tvWarningTitle.setTextSize(22);
		tvWarningTitle.setTextColor(0xff33b5e5);
		tvWarningTitle.setText(R.string.dialogbox_settings_warning_title);;
		
		TextView tvSpace1 = new TextView(context);
		tvSpace1.setTextSize(15);
		tvSpace1.setText("");
		
		View divider = new View(context);
		divider.setBackgroundColor(0xff33b5e5);
		divider.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4));
		
		TextView tvSpace2 = new TextView(context);
		tvSpace2.setTextSize(7);
		tvSpace2.setText("");
		
		TextView tvWarningMessage = new TextView(context);
		tvWarningMessage.setTextSize(18);
		tvWarningMessage.setTextColor(Color.BLACK);
		tvWarningMessage.setText(R.string.dialogbox_settings_warning_message);
		
		TextView tvSpace3 = new TextView(context);
		tvSpace3.setTextSize(7);
		tvSpace3.setText("");
		
		CheckBox cboxWarning = new CheckBox(context);
		cboxWarning.setText(R.string.dialogbox_settings_warning_checkboxtext);
		cboxWarning.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Inputs.setShowSettingsWarning(!isChecked);
			}
		});
		
		LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(new LinearLayout.LayoutParams
				(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(
				(int) context.getResources().getDimension(R.dimen.activity_horizontal_margin),
				(int) context.getResources().getDimension(R.dimen.activity_vertical_margin),
				(int) context.getResources().getDimension(R.dimen.activity_horizontal_margin),
				(int) context.getResources().getDimension(R.dimen.activity_vertical_margin));
		
		layout.addView(tvWarningTitle);
		layout.addView(tvSpace1);
		layout.addView(divider);
		layout.addView(tvSpace2);
		layout.addView(tvWarningMessage);
		layout.addView(tvSpace3);
		layout.addView(cboxWarning);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(layout);
		
		return builder;
	}
	
	static void getSelectedPic(int requestCode, int resultCode, Intent imageReturnedIntent, ContentResolver contentResolver) {
		switch(requestCode) { 
	    case L.SELECT_PHOTO:
	        if(resultCode == RESULT_OK){  
	            Uri selectedPhotoUrl = imageReturnedIntent.getData();
	            InputStream imageStream = null;
				try {
					imageStream = contentResolver.openInputStream(selectedPhotoUrl);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            Bitmap selectedPhoto = BitmapFactory.decodeStream(imageStream);
	            
	            photoBitmaps[L.NUM_PHOTOS-1] = Bitmap.createScaledBitmap(selectedPhoto, L.imgWidth, L.imgWidth, false);
	            resourceIds[L.NUM_PHOTOS-1] = -1; //indicates it's a photo from the gallery
	            Inputs.setPicPosition(L.NUM_PHOTOS-1);
	            phonePhotoSelected = true;
	        } else {
	        	/* No photo was selected from the phone gallery.
	        	 * Probably the Back button was pressed */
	        	//do nothing
	        }
		}
	}
	
	/* Crate a dialog to be shown to the user.
	 * If 'show' is TRUE, show the dialog, else just return the builder */
	static AlertDialog.Builder showDialog(Context context, int title, int message,
			boolean show) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);	
		builder.setTitle(title);
		builder.setMessage(message);

		if (show) {
			builder.create().show();
			return null; //dialog has already been show. No user input was required
		}		
		return builder; //user input is required. Return builder so that the caller can handle user input
	}
	
	static int getIndexFromXY(View view) {
		int position = 0;
		int width = L.imgWidth + 2*L.PADDING;
		
		for (int i=0; i<L.NUM_COLS; i++) {
			for (int j=0; j<L.NUM_ROWS; j++) {
				if (view.getX()==j*width && view.getY()==i*width) {
					return position;
				}
				position++;
			}
		}
		return -1;
	}
	
	/* Get the padding of the root layout */
	static void getRootLayoutPadding(Resources res) {
		int LEFT=0, RIGHT=1, TOP=2, BOTTOM=3;
		rootLayoutPadding = new int[4];
		rootLayoutPadding[LEFT] = (int) res.getDimension(R.dimen.activity_horizontal_margin);
		rootLayoutPadding[RIGHT] = (int) res.getDimension(R.dimen.activity_horizontal_margin);
		rootLayoutPadding[TOP] = (int) res.getDimension(R.dimen.activity_vertical_margin);
		rootLayoutPadding[BOTTOM] = (int) res.getDimension(R.dimen.activity_vertical_margin);
	}
	
	static void setMargin(View view, int edge, int margin) {		
		if (edge == LEFT) {
			((MarginLayoutParams) view.getLayoutParams()).leftMargin = margin;	
		} else if (edge == RIGHT) {
			((MarginLayoutParams) view.getLayoutParams()).rightMargin = margin;
		} else if (edge == TOP) {
			((MarginLayoutParams) view.getLayoutParams()).topMargin = margin;
		} else if (edge == BOTTOM) {
			((MarginLayoutParams) view.getLayoutParams()).bottomMargin = margin;
		}
	}
	
}