package ru.vano.thebegin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

public class Table extends Activity {
	SV surf;
	Random random;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		surf = new SV(this);
		setContentView(surf);
		random = new Random();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Col+");
		menu.add(0, 1, 0, "Col-");
		menu.add(0, 2, 0, "Row+");
		menu.add(0, 3, 0, "Row-");
		menu.add(0, 4, 0, "width +5px");
		menu.add(0, 5, 0, "width -5px");
		menu.add(0,6,0,"padding+");
		menu.add(0,7,0,"padding-");
		menu.add(0,8,0,"update/redraw");
		menu.add(0,9,0,"Put new ball");
		menu.add(0,10,10,"6 random balls");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case 0:
			SV.draw.setRunning(false);
			Log.d("game","we set running to false");
			while(!Draw.can_operate) continue;
			Log.d("game","now we can operate");
			GameInfo.COLLUMNS++;
			surf = new SV(this);
			Log.d("game","new surfaceview object been crated");
			setContentView(surf);
			Log.d("game","set new copntent view");
			break;
		case 1:
			SV.draw.setRunning(false);
			while(!Draw.can_operate) continue;
			Log.d("game","set running to false");
			GameInfo.COLLUMNS--;
			surf = new SV(this);
			Log.d("game","new surfaceview object been crated");
			setContentView(surf);
			Log.d("game","set new copntent view");
			break;
		case 2:
			SV.draw.setRunning(false);
			while(!Draw.can_operate) continue;
			Log.d("game","set running to false");
			GameInfo.ROWS++;
			surf = new SV(this);
			Log.d("game","new surfaceview object been crated");
			setContentView(surf);
			Log.d("game","set new copntent view");
			break;
		case 3:
			SV.draw.setRunning(false);
			while(!Draw.can_operate) continue;
			Log.d("game","set running to false");
			GameInfo.ROWS--;
			surf = new SV(this);
			Log.d("game","new surfaceview object been crated");
			setContentView(surf);
			Log.d("game","set new copntent view");
			break;
		case 4:
			SV.draw.setRunning(false);
			while(!Draw.can_operate) continue;
			Log.d("game","set running to false");
			GameInfo.FIELD_WIDTH +=5;
			surf = new SV(this);
			Log.d("game","new surfaceview object been crated");
			setContentView(surf);
			Log.d("game","set new copntent view");
			break;
		case 5:
			SV.draw.setRunning(false);
			while(!Draw.can_operate) continue;
			Log.d("game","set running to false");
			GameInfo.FIELD_WIDTH -=5;
			surf = new SV(this);
			Log.d("game","new surfaceview object been crated");
			setContentView(surf);
			Log.d("game","set new copntent view");
			break;
		case 6:
			SV.draw.setRunning(false);
			while(!Draw.can_operate) continue;
			Log.d("game","set running to false");
			GameInfo.FIELD_PADDING++;
			surf = new SV(this);
			Log.d("game","new surfaceview object been crated");
			setContentView(surf);
			Log.d("game","set new copntent view");
			break;
		case 7:
			SV.draw.setRunning(false);
			while(!Draw.can_operate) continue;
			Log.d("game","set running to false");
			GameInfo.FIELD_PADDING--;
			surf = new SV(this);
			Log.d("game","new surfaceview object been crated");
			setContentView(surf);
			Log.d("game","set new copntent view");
			break;
		case 8:
			SV.draw.setRunning(false);
			while(!Draw.can_operate) continue;
			Log.d("game","set running to false");
			surf = new SV(this);
			Log.d("game","new surfaceview object been crated");
			setContentView(surf);
			Log.d("game","set new copntent view");
			break;
		case 9:
			if(SV.should_animate_path) break;
			Toast.makeText(this, "Tap somewhere on the desk to add a ball", Toast.LENGTH_LONG).show();
			SV.ball_creating_mode = true;
			break;
		case 10:
			long rand_ball_start_time = System.currentTimeMillis();
			int tempX = 0, tempY = 0 ,it=0;
			boolean stop = false;
			if(SV.should_animate_path) break;
			for(int i=0; i<6; i++) {
				stop = false;
				while(!stop) {
					tempX = random.nextInt(GameInfo.COLLUMNS);
					tempY = random.nextInt(GameInfo.ROWS);
					it++;
					if(SV.pos_type[tempX][tempY] == 0) stop = true;
					else continue;
				}
				SV.pos_type[tempX][tempY] = 1;
			}
			Draw.recheck();
			Log.d("game","Rndom balls created in "+String.valueOf(System.currentTimeMillis() - rand_ball_start_time)+" ms with "+String.valueOf(it)+" iterations.");
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}

//
class SV extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
Canvas canvas;
SurfaceHolder holder;
static int width, height, SIDE;
int n;
static int TABLE_LEFT, TABLE_TOP, TABLE_RIGHT_MARGIN, COL_SIZE, FIELDS, PADDING, size, SIDE_SIZE, FIELD_HEIGHT;
static int last_x = 0, last_y = 0; // last x, y positions for fields drawing
static int X_plus, y_plus; // pluses when drawing new field
static double const1 = Math.sqrt(3)/2;
static int[] ball_blue_x = {2,4}, ball_blue_y = {0,4}; // Presaved positions of blue balls
static int[][] pos_type, pos_x, pos_y;
static int[] pos_rotation; // Arrays(length is the same as amount of parts in the table)
static boolean[] movable, rotatable; // We can know whether this or that mirror can be rotated , etc.
static ArrayList<Integer> pos_type_on, pos_x_on, pos_y_on;
static ArrayList<Boolean> movable_on, rotatable_on;
static ArrayList<ArrayList<Integer>> routes_x, routes_y, routes_x_final, routes_y_final; //for several routes
static ArrayList<Integer> route_x, route_y; // for one route
static Bitmap field, scaled_field, fon, ball_blue, ball_blue_scaled;
static BitmapFactory.Options options;
static int touchX, touchY; // last x and y positions 
static boolean isBallSelected = false, isFound = false, startField = true, should_create_route = false, first_route_looking_run = true, should_animate_path = false;; // whether some ball is already selected
static int[] coord_start = new int[2], coord_dest = new int[2]; // this array can tell us what field we have just selected
static int doubled_side; // no comments
int rX, rY;
static Draw draw;
static Path path;
static PathMeasure path_measure;
static boolean was_paused, should_stop = false, stop_making_route = false, ball_creating_mode = false;
int step, final_step;
int coordX, coordY; // temp coordination variables
int[][] route_index;
boolean[][] route_field_visited;
ArrayList<Integer> operateX, operateY;
long start_route_time;
int[] route_final_x, route_final_y;
static ArrayList<Integer> pos_x_in_on, pos_y_in_on;
int[] selected_coord = new int[2];
long creation_started_time;
	public SV(Context context) {
		super(context);
		this.getHolder().addCallback(this);
		creation_started_time = System.currentTimeMillis();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("game","surface is creating...");
		this.holder = holder;
		setOnTouchListener(this);
		canvas = this.holder.lockCanvas(); // we get canvas to find out screen's dimensions
		width = canvas.getWidth();
		this.holder.unlockCanvasAndPost(canvas); // done!
		height = canvas.getHeight();
		TABLE_LEFT = GameInfo.TABLE_LEFT *width/100; // Margins and paddings in pixels
		TABLE_TOP = GameInfo.TABLE_TOP *height/100;
		TABLE_RIGHT_MARGIN = GameInfo.TABLE_RIGHT_MARGIN *width/100;
		FIELDS = GameInfo.COLLUMNS * GameInfo.ROWS; // How much is there in this table
		PADDING = GameInfo.PADDING * COL_SIZE/100;
		SIDE_SIZE = width * GameInfo.FIELD_WIDTH/100; // Pixel width of a field
		last_x = TABLE_LEFT + GameInfo.FIELD_PADDING; // Positions for first field
		options = new BitmapFactory.Options();
		options.inScaled = false; // We shouldn't scale our bitmap
		field = BitmapFactory.decodeResource(getResources(), R.drawable.field, options); // create bitmap
		fon = BitmapFactory.decodeResource(getResources(), R.drawable.fon, options);
		scaled_field = Bitmap.createScaledBitmap(field, GameInfo.FIELD_WIDTH, (int) (const1*GameInfo.FIELD_WIDTH), true); // create scaled bitmap according to size
		field.recycle(); // delete previous bitmap
		field = null;
		ball_blue = BitmapFactory.decodeResource(getResources(), R.drawable.blue_ball, options); //got blue ball
		ball_blue_scaled = Bitmap.createScaledBitmap(ball_blue, GameInfo.FIELD_WIDTH, scaled_field.getHeight(), true); // downscaled version of a ball
		ball_blue.recycle();
		ball_blue = null;
		FIELD_HEIGHT = scaled_field.getHeight();
		Log.d("canvas","f_he = "+String.valueOf(FIELD_HEIGHT));
		SIDE = GameInfo.FIELD_WIDTH /2; // side of field in pixels
		pos_x = new int[GameInfo.COLLUMNS][GameInfo.ROWS];
		pos_y = new int[GameInfo.COLLUMNS][GameInfo.ROWS];
		pos_type_on = new ArrayList<Integer>(); // empty ArL 
		pos_x_on = new ArrayList<Integer>();
		pos_y_on = new ArrayList<Integer>();
		route_index = new int[GameInfo.COLLUMNS][GameInfo.ROWS];
		for (int col_no=0; col_no<GameInfo.COLLUMNS; col_no++) { // that's for drawing a complete field
			switch(col_no % 2) {
			case 0:
				for (int row_no=0; row_no<GameInfo.ROWS; row_no++) {
					last_y = TABLE_TOP+ row_no*FIELD_HEIGHT+GameInfo.FIELD_PADDING*2*row_no-1;
					pos_x[col_no][row_no] = last_x; // store x and y positions of this or that field
					pos_y[col_no][row_no] = last_y;
				}
				break;
			case 1:
				for (int row_no=0; row_no<GameInfo.ROWS; row_no++) {
					last_y =(int) (TABLE_TOP +row_no*FIELD_HEIGHT + GameInfo.FIELD_PADDING*row_no*2 + const1*SIDE + GameInfo.FIELD_PADDING);
					pos_x[col_no][row_no] = last_x; // store x and y positions of this or that field
					pos_y[col_no][row_no] = last_y;
				}
				break;
			}
			last_x += 1.5* SIDE + GameInfo.FIELD_PADDING;
		}
		Log.d("game","new arrays created");
		pos_type = new int[GameInfo.COLLUMNS][GameInfo.ROWS]; // We create unbelievablely cool thing - 2-stored array!
		for (int i=0; i<GameInfo.COLLUMNS; i++)	for(int b=0; b<GameInfo.ROWS; b++) {
			pos_type[i][b] = 0; // We fill our general array with 0, which means all the fields are clear without any ball
			route_index[i][b] = -1;
		}
		for(int i=0; i<ball_blue_x.length; i++)	{
			pos_type[ball_blue_x[i]][ball_blue_y[i]] = 1; // // We fill our general array with all provided objects.  1 - means blue ball is in this field 
			route_index[ball_blue_x[i]][ball_blue_y[i]] = -5;
		}
		Draw.recheck(); // we create cashed arraylists every time anything changes in order to make our drawings faster
		draw = new Draw(holder); // we start drawing thread
		draw.start();
		draw.setRunning(true);
		Log.d("game","surface has been created in "+String.valueOf(System.currentTimeMillis() - creation_started_time));
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("game", "surface destroying method is starting");
		draw.setRunning(false);
		should_stop = true;
		while(!Draw.can_operate) continue;
		was_paused = true;
		Log.d("game", "surface destroying method is reached");
		//Log.d("game","running is "+String.valueOf(draw.running));
	}
	
	
	
	public void select() { // we use this method to select a ball or to move it somewhere
		for(int row_no=0; row_no<GameInfo.ROWS; row_no++) {
			if(isFound) break;
			for(int col_no=0; col_no<GameInfo.COLLUMNS; col_no++) { // here we want to find out what field we've just tapped
				if((touchX > pos_x[col_no][row_no]) && (touchX < (pos_x[col_no][row_no] + GameInfo.FIELD_WIDTH)) && (touchY > pos_y[col_no][row_no]) && (touchY < (pos_y[col_no][row_no] + FIELD_HEIGHT))) {
					Log.d("game","Field row= "+String.valueOf(row_no)+", col= "+String.valueOf(col_no));
					selected_coord[0] = col_no;
					selected_coord[1] = row_no;
					isFound = true; 
					break;
				}
			}
		}
		if(!isFound) {
			Log.d("game", "No field was selected!");
			startField = true;
		}
		if(isFound) { // we should do something if tapped
			if(ball_creating_mode) { // we only should create a new ball(god mode!)
				pos_type[selected_coord[0]][selected_coord[1]] = 1;
				Draw.recheck();
				ball_creating_mode = false;
				startField = true;
			}
			else { // that's for making a route
				if(startField) { // check if it was a fist(starting) point
					if(pos_type[selected_coord[0]][selected_coord[1]] == 1) { // check if we tapped a ball
						coord_start[0] = selected_coord[0]; // now we found where we tapped
						coord_start[1] = selected_coord[1];
						startField = false;
						Log.d("game","we tapped a ball");
					}
				}
				else { // finish point 
					if(pos_type[selected_coord[0]][selected_coord[1]] == 0) {
						coord_dest[0] = selected_coord[0]; // now we found where we tapped
						coord_dest[1] = selected_coord[1];
						should_create_route = true;
						Log.d("game","We should create route from start: "+String.valueOf(coord_start[0])+" "+String.valueOf(coord_start[1])+" and finish: "+String.valueOf(coord_dest[0])+" "+String.valueOf(coord_dest[1]));
						startField = true;
						start_route_time = System.currentTimeMillis();
						
					}
					else {
						startField = true;
						Log.d("game","We shouldn't create a route");
					}
				}
			}
		}
		isFound = false;
		if(should_create_route) { // we check whether we should create route
			step = 0;
			for (int i=0; i<GameInfo.COLLUMNS; i++)	for(int b=0; b<GameInfo.ROWS; b++) route_index[i][b] = -1;
			for(int i=0; i<pos_type_on.size(); i++){
				if(pos_type_on.get(i) == 1) route_index[pos_x_in_on.get(i)][pos_y_in_on.get(i)] = -5;
			}
			operateX = new ArrayList<Integer>();
			operateY = new ArrayList<Integer>();
			operateX.add(coord_start[0]);
			operateY.add(coord_start[1]);
			while(operateX.size() >0) {
				size = operateX.size(); // 1 
				for(int i=0;i<size; i++) {
					coordX = operateX.get(i);
					coordY = operateY.get(i);
					switch(coordX % 2) {
					case 0:
						for(int b=0; b<6; b++) { // we look at every field around this
							switch(b) {
							case 0:
								rX = coordX - 1;
								rY = coordY - 1;
								break;
							case 1:
								rX = coordX - 1;
								rY = coordY;
								break;
							case 2:
								rX = coordX;
								rY = coordY - 1;
								break;
							case 3:
								rX = coordX;
								rY = coordY + 1;
								break;
							case 4:
								rX = coordX + 1;
								rY = coordY - 1;
								break;
							case 5:
								rX = coordX + 1;
								rY = coordY;
								break;
							}
							if((rX < 0) || (rX > (GameInfo.COLLUMNS-1)) || (rY < 0) || (rY > (GameInfo.ROWS - 1)) || (pos_type[rX][rY] != 0)) continue; // we check if our field is out of bounds
							if((route_index[rX][rY] == -1) || (route_index[rX][rY] > step)) {
								route_index[rX][rY] = step; // we store step number in every field
							}
							else continue;
							operateX.add(rX);
							operateY.add(rY);
						}
						break;
					case 1:
						for(int b=0; b<6; b++) { // we look at every field around this
							switch(b) {
							case 0:
								rX = coordX - 1;
								rY = coordY;
								break;
							case 1:
								rX = coordX - 1;
								rY = coordY + 1;
								break;
							case 2:
								rX = coordX;
								rY = coordY - 1;
								break;
							case 3:
								rX = coordX;
								rY = coordY + 1;
								break;
							case 4:
								rX = coordX + 1;
								rY = coordY;
								break;
							case 5:
								rX = coordX + 1;
								rY = coordY + 1;
								break;
							}
							if((rX < 0) || (rX > (GameInfo.COLLUMNS-1)) || (rY < 0) || (rY > (GameInfo.ROWS - 1)) || (pos_type[rX][rY] != 0)) continue; // we check if our field is out of bounds
							if((route_index[rX][rY] == -1) || (route_index[rX][rY] > step)) {
								route_index[rX][rY] = step; // we store step number in every field
							}
							else continue;
							operateX.add(rX);
							operateY.add(rY);
						}
						break;	
					}
				};
				for(int i=0; i<size;i++) {
					operateX.remove(0);
					operateY.remove(0);
				}
				step++;
			}
			final_step = route_index[coord_dest[0]][coord_dest[1]]; // step in a destination point
			if(final_step == -1) {
				Log.d("game","Unable to reach the destination point");
				Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
			}
			else {
					route_final_x = new int[final_step + 2]; // in these arrays we're going to put a final, easy-to-understand route
					route_final_y = new int[final_step + 2];
					route_final_x[0] = coord_start[0];
					route_final_y[0] = coord_start[1];
					step = final_step - 1;
					coordX = coord_dest[0];
					coordY = coord_dest[1];
					route_final_x[final_step + 1] = coordX;
					route_final_y[final_step + 1] = coordY;
					while(step>=0) { // now we're going to look through the neiborh fields in order to find the closest way
						switch(coordX % 2) {
						case 0:
							for(int b=0; b<6; b++) { // we look at every field around this
								switch(b) {
								case 0:
									rX = coordX - 1;
									rY = coordY - 1;
									break;
								case 1:
									rX = coordX - 1;
									rY = coordY;
									break;
								case 2:
									rX = coordX;
									rY = coordY - 1;
									break;
								case 3:
									rX = coordX;
									rY = coordY + 1;
									break;
								case 4:
									rX = coordX + 1;
									rY = coordY - 1;
									break;
								case 5:
									rX = coordX + 1;
									rY = coordY;
									break;
								}
								if((rX < 0) || (rX > (GameInfo.COLLUMNS-1)) || (rY < 0) || (rY > (GameInfo.ROWS - 1)) || (pos_type[rX][rY] != 0)) continue; // we check if our field is out of bounds
								if(route_index[rX][rY] == step) {
									route_final_x[step+1] = rX;
									route_final_y[step+1] = rY;
									coordX = rX;
									coordY = rY;
									break;
								}
								else continue;
							}
							break;
						case 1:
							for(int b=0; b<6; b++) { // we look at every field around this
								switch(b) {
								case 0:
									rX = coordX - 1;
									rY = coordY;
									break;
								case 1:
									rX = coordX - 1;
									rY = coordY + 1;
									break;
								case 2:
									rX = coordX;
									rY = coordY - 1;
									break;
								case 3:
									rX = coordX;
									rY = coordY + 1;
									break;
								case 4:
									rX = coordX + 1;
									rY = coordY;
									break;
								case 5:
									rX = coordX + 1;
									rY = coordY + 1;
									break;
								}
								if((rX < 0) || (rX > (GameInfo.COLLUMNS-1)) || (rY < 0) || (rY > (GameInfo.ROWS - 1)) || (pos_type[rX][rY] != 0)) continue; // we check if our field is out of bounds
								if(route_index[rX][rY] == step) {
									route_final_x[step+1] = rX;
									route_final_y[step+1] = rY;
									coordX = rX;
									coordY = rY;
									break;
								}
								else continue;
							}
							break;	
						}
						step--;
					}
					Log.d("game","Route is ready("+String.valueOf(final_step + 1)+" moves). It took "+String.valueOf(System.currentTimeMillis() - start_route_time)+" ms.");
					Log.d("game","X: "+Arrays.toString(route_final_x));
					Log.d("game","Y: "+Arrays.toString(route_final_y));
					path = new Path();
					for(int i=1; i<=(final_step+1); i++) { // we're making a path to move a ball
						path.moveTo(pos_x[route_final_x[i-1]][route_final_y[i-1]], pos_y[route_final_x[i-1]][route_final_y[i-1]]);
						path.lineTo(pos_x[route_final_x[i]][route_final_y[i]], pos_y[route_final_x[i]][route_final_y[i]]);
					} // maybe there we should add lastpoint?
					path_measure = new PathMeasure(path, false);
					Draw.route_length = path_measure.getLength(); // we get the length of the route
					Log.d("game","path length = "+String.valueOf(Draw.route_length));
					should_animate_path = true; // start the animation of moving ball!
				}
			}
		should_create_route = false;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchX = (int) event.getX();
			touchY = (int) event.getY();
			if(!should_animate_path) select(); // start route making 
		}
		return true;
	}
	
}

class Draw extends Thread {
		
		public Draw(SurfaceHolder holder) {
			this.holder = holder;
			Log.d("game", "new thread created");
		}
		int i = 0;
		SurfaceHolder holder;
		Canvas canvas;
		static boolean running = false, can_operate = false, should_stop_drawing = false;
		static boolean canvasLocked = false;
		long last_update_time = System.currentTimeMillis();
		static float route_length, distance = 0; // amount of objects which should be drawn
		int active_x, active_y, size; // coordinates of an active(moving) ball
		float[] active_pos = new float[2]; // this array is for moving ball
		boolean first_ballMoving_start = true;
		@Override
		public void run() {
			Log.d("game", "run method recahed");
			while(running) {
				can_operate = false;
				//Log.d("game", "was paused(1st) - "+String.valueOf(SV.was_paused)+String.valueOf(i)+" running is "+String.valueOf(this.running));
				while ((System.currentTimeMillis() - last_update_time) <= 20) continue; // Check whether we should draw a new frame
				canvas = holder.lockCanvas(); // got canvas for drawing
				canvasLocked = true;
				//if(SV.should_stop) break; // when we use it, options menu don't work, and i can't find any solution!
				//Log.d("game", "was paused(2nd) - "+String.valueOf(SV.was_paused)+String.valueOf(i)+" running is "+String.valueOf(this.running));
				canvas.drawColor(Color.WHITE); // Filling our canvas with white color
				//Log.d("game","drawn "+String.valueOf(i)+" running is "+String.valueOf(this.running))
				canvas.drawBitmap(SV.fon, SV.TABLE_LEFT, SV.TABLE_TOP,null);
				for (int col_no=0; col_no<GameInfo.COLLUMNS; col_no++) { // that's for drawing a complete field
					switch(col_no % 2) {
					case 0:
						for (int row_no=0; row_no<GameInfo.ROWS; row_no++) canvas.drawBitmap(SV.scaled_field, SV.pos_x[col_no][row_no], SV.pos_y[col_no][row_no], null);
						break;
					case 1:
						for (int row_no=0; row_no<GameInfo.ROWS; row_no++) {
							canvas.drawBitmap(SV.scaled_field, SV.pos_x[col_no][row_no], SV.pos_y[col_no][row_no], null);
						}
						break;
					}
				}
				for (int i=0; i<SV.pos_type_on.size(); i++) { //we draw only balls
					canvas.drawBitmap(SV.ball_blue_scaled, SV.pos_x_on.get(i), SV.pos_y_on.get(i), null);
				}
				if(SV.should_animate_path) { // we check whether we should animate our ball
					if(first_ballMoving_start) {
						SV.pos_type[SV.coord_start[0]][SV.coord_start[1]] = 0;
						recheck();
						first_ballMoving_start = false;
					}
					SV.path_measure.getPosTan(distance, active_pos, null);
					canvas.drawBitmap(SV.ball_blue_scaled, active_pos[0], active_pos[1], null);
					distance +=GameInfo.MOVING_SPEED;
					if(distance >= route_length) { // ball is completely animated
						if(SV.path_measure.nextContour()) distance = 0;
						else {
							SV.pos_type[SV.coord_dest[0]][SV.coord_dest[1]] = 1;
							recheck();
							SV.should_animate_path = false;
							first_ballMoving_start = true;
							distance = 0;
						}
					}
				}
				holder.unlockCanvasAndPost(canvas);
				canvasLocked = false;
				i++;
			}
			Log.d("game", "end of the draw thread is reached");
			Log.d("game","running is "+String.valueOf(running));
			SV.was_paused = false;
			SV.should_stop = false;
			can_operate = true;
	}
		
		public static void recheck() { // we use this method to refill our cashed arL
			SV.pos_type_on = new ArrayList<Integer>();
			SV.pos_x_on = new ArrayList<Integer>();
			SV.pos_y_on = new ArrayList<Integer>();
			SV.pos_x_in_on = new ArrayList<Integer>();
			SV.pos_y_in_on = new ArrayList<Integer>();
			for (int i=0; i<GameInfo.COLLUMNS; i++) {
				for(int b=0; b<GameInfo.ROWS; b++) {
					if(SV.pos_type[i][b] != 0) { // we store only non-empty fields in our so-called cashed arraylists
						SV.pos_type_on.add(SV.pos_type[i][b]);
						SV.pos_x_on.add(SV.pos_x[i][b]);
						SV.pos_y_on.add(SV.pos_y[i][b]);
						SV.pos_x_in_on.add(i);
						SV.pos_y_in_on.add(b);
					}
				}
			}
		}
		
		public void setRunning(boolean run) {
			this.running = run;
			if(run == false) should_stop_drawing = true;
		}
		
		public void stop_drawing() {
		
		}
}
