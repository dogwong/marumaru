package com.project.marumaru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	Button btnEnd;
	Button btnRestart;
	Button btnStart;
	SeekBar sbSpeed;
	CheckBox cbStandardSE;
	CheckBox cbResponseSE;
	TextView tvSpeed;
	TextView tvPrefect;
	TextView tvGreat;
	TextView tvGood;
	TextView tvMiss;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		//
		
		/*btnEnd = (Button) findViewById(R.id.btnEnd);
		btnEnd.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		btnRestart = (Button) findViewById(R.id.btnRestart);
		btnRestart.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				startGame();
			}
		});*/
		
		btnStart = (Button) findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				startGame();
				
			}
		});
		
		tvPrefect = (TextView) findViewById(R.id.tvPrefect);
		tvGreat = (TextView) findViewById(R.id.tvGreat);
		tvGood = (TextView) findViewById(R.id.tvGood);
		tvMiss = (TextView) findViewById(R.id.tvMiss);
		tvSpeed = (TextView) findViewById(R.id.tvSpeed);
		
		sbSpeed = (SeekBar) findViewById(R.id.sbSpeed);
		sbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				tvSpeed.setText(Integer.toString(sbSpeed.getProgress() + 1));
			}
		});
		tvSpeed.setText(Integer.toString(sbSpeed.getProgress() + 1));
		
		
		cbStandardSE = (CheckBox) findViewById(R.id.cbStandardSE);
		cbResponseSE = (CheckBox) findViewById(R.id.cbResponseSE);
		
		//finish();
		//setContentView(new Panel(this));
	}
	
	void startGame () {
		Intent i = new Intent(this, GameActivity.class);
		i.putExtra("Speed", Integer.toString(sbSpeed.getProgress()));
		i.putExtra("SEStandard", (cbStandardSE.isChecked())?"1":"0");
		i.putExtra("SEResponse", (cbResponseSE.isChecked())?"1":"0");
		//Log.w("test", "Process = " + sbSpeed.getProgress());
		startActivityForResult(i, 4444);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == 4444) {
			Log.w("test", data.getExtras().toString());
			if (data.hasExtra("Prefect")) {
				tvPrefect.setText(data.getExtras().getString("Prefect"));
				tvGreat.setText(data.getExtras().getString("Great"));
				tvGood.setText(data.getExtras().getString("Good"));
				tvMiss.setText(data.getExtras().getString("Miss"));
			}
		}
	}
	/*
	class Panel extends SurfaceView implements SurfaceHolder.Callback{
		private DrawingThread drawingThread;
		public Panel(Context context) {
			super(context);
			getHolder().addCallback(this);
			drawingThread = new DrawingThread(getHolder(), this);
		}
		@Override
		public void onDraw(Canvas canvas) {
			Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			canvas.drawColor(Color.DKGRAY);
			canvas.drawBitmap(icon, 100, 100, null);
		}
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			// TODOAuto-generated method stub
			
		}
		public void surfaceCreated(SurfaceHolder holder) {
			drawingThread.setRunning(true);
			drawingThread.start();
		}
		public void surfaceDestroyed(SurfaceHolder holder) {
			// we have to tell thread to shut down & wait for it to finish, or else
			boolean retry = true;
			drawingThread.setRunning(false);
			while(retry) {
				try{
					drawingThread.join();
					retry = false;
				} catch(InterruptedException e) {
					// try it again and again...
					
				}
			}
		}
	}
	class DrawingThread extends Thread {
		private SurfaceHolder msurfaceHolder;
		private Panel mpanel;
		private boolean mrun= false;
		
		private int frameID = 0;
		
		
		public DrawingThread(SurfaceHolder surfaceHolder, Panel panel) {
			msurfaceHolder = surfaceHolder;
			mpanel = panel;
		}
		public void setRunning(boolean run) {
			mrun= run;
		}
		@Override
		public void run() {
			Canvas c;
			while(mrun) {
				c = null;
				try{
					c = msurfaceHolder.lockCanvas(null);
					synchronized(msurfaceHolder) {
						frameID ++;
						Paint mPaint = new Paint();
						mPaint.setColor(Color.rgb(255, 255, 255));
						
						c.drawRect(new RectF(20,20,50,50), mPaint);
						
						//if (frameID > 300) frameID = 0;
						//Log.i("test", Integer.toString(frameID));
						mpanel.draw(c);
					}
				} finally{
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if(c != null) {
						msurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
	}
    	*/	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
