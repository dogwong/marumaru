package com.project.marumaru;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.source.BaseTextureAtlasSource;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Display;


public class GameActivity extends BaseGameActivity implements IOnSceneTouchListener {
	//final int mCameraWidth = 480;  
    //final int mCameraHeight = 320;
	static int cameraWidth = 0;
	static int cameraHeight = 0;
	
	static VertexBufferObjectManager VBOM;
	
	
	// Font
	static Font fDebugBottom;
	
	// Variable for game
	static MediaPlayer vMediaPlayer;
	static int vBMP = 154;//154; //
	static int vDifficulty = 5;
	static int vNoteSpeed = 3;
	static boolean vSEStandard;
	static boolean vSEResponse;
	static Note[] vNotes;
	static long vGameBeforeStartBeat[];
	static int vGameBeforeStartBeatPlayed;
	static long vGameStartTime; // Millisecond
	static int vGameNextGenerateNoteId = 0;
	static int vGameFrameToEnd = 0;
	static int vGameResultScore;
	static int vGameResultPerfect;
	static int vGameResultGreat;
	static int vGameResultGood;
	static int vGameResultMiss;
	//static Array vNotes;
	
	
	// Variable for screen
	static Scene vScene;
	static int vScreenMidPointX;
	static int vScreenMidPointY;
	static int vScreenNotePathLength;
	static int vScreenNoteGenerateLength;
	static boolean vScreenTouchHandled[];
	
	static Point vNoteGeneratePoint[];
	static Point vNoteEndPoint[];
	
	// Variable for Note
	static Bitmap note_circle_normal;
	static Note vNoteNormal;
	static Note vNoteNormal2;
	static LinkedList<Note> vNoteOnScreen;
	
	// Settings for screen
	static float sScreenNotePathScale = (143.0f / 160.0f);
	static float sScreenNoteGenerateScale = (40.0f / 160.0f);
	static float sScreenTouchAreaScale = 2.0f;
	
	// Setting for note
	static int sNoteAppearTime[] = {2000, 1200, 1000, 850, 700, 550, 400, 250, 100};
	//                   Setting     1      2    3    4    5    6    7    8    9
	//                   Variable    0      1    2    3    4    5    6    7    8
	static float sNoteGenerateAlpha = 0.2f;
	static float sNoteGenerateScale = 0.2f;
	static float sNoteGenerateTimeScale = 0.3f;
	
	
	// Setting for game
	static int sGameEarlyGoodTiming = 210; // 200 220
	static int sGameEarlyGreatTiming = 120; // 100 140
	static int sGameEarlyPerfectTiming = 45; // 40 60
	static int sGameLatePerfectTiming = 45; // 40 60
	static int sGameLateGreatTiming = 120; // 100 150
	
	// Graphic variable
	static int vNoteNormalSize, vNoteNormalStroke;
	static TextureRegion txNoteNormal, txNoteSpecial, txNoteLongStart, txNoteLongMiddle, txNoteLongEnd, txNoteStar, txNoteStarPath;
	static TextureRegion txNoteNormalDouble, txNoteSpecialDouble, txNoteLongStartDouble, txNoteLongMiddleDouble, txNoteLongEndDouble, txNoteStarDouble, txNoteStarPathDouble;
	
	// Sound Variable
	static Sound sBeatClap;
	
	// Debug
	static Text dTimeText;
	static Text dPerfectText;
	static Text dGreatText;
	static Text dGoodText;
	static Text dMissText;
	static Text dNoteDebugText;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		// TODO onCreateEngineOptions
		
		// Setting up for the screen
		final Display display = getWindowManager().getDefaultDisplay();
	    cameraWidth = display.getWidth();
	    cameraHeight = display.getHeight();
	    vScreenMidPointX = cameraWidth / 2;
	    vScreenMidPointY = cameraHeight / 2;
	    vScreenNotePathLength = (int) ((float) vScreenMidPointX * sScreenNotePathScale);
	    vScreenNoteGenerateLength = (int) ((float) vScreenMidPointX * sScreenNoteGenerateScale);
		// 320
	    // 480
	    Log.i("Screen", "camera: w=" + Integer.toString(cameraWidth) + " h=" + Integer.toString(cameraHeight));
	    
	    // setting up variable for game
	    vNoteGeneratePoint = new Point[8];
	    vNoteEndPoint = new Point[8];
	    for (int i = 0; i < 8; i++) {
	    	int x, y, x2, y2;
	    	y = vScreenMidPointY - (int) (vScreenNoteGenerateLength * Math.cos((45.0f * ((float) (i) + 0.5f))  / 180.0f * Math.PI));
	    	x = vScreenMidPointX + (int) (vScreenNoteGenerateLength * Math.sin((45.0f * ((float) (i) + 0.5f))  / 180.0f * Math.PI));
	    	y2 = vScreenMidPointY - (int) (vScreenNotePathLength * Math.cos((45.0f * ((float) (i) + 0.5f))  / 180.0f * Math.PI));
	    	x2 = vScreenMidPointX + (int) (vScreenNotePathLength * Math.sin((45.0f * ((float) (i) + 0.5f))  / 180.0f * Math.PI));
	    	
	    	vNoteGeneratePoint[i] = new Point(x, y);
	    	vNoteEndPoint[i] = new Point(x2, y2);
	    }
	    
	    vNoteSpeed = Integer.parseInt(getIntent().getStringExtra("Speed"));
	    vSEStandard = (Integer.parseInt(getIntent().getStringExtra("SEStandard")) == 1)?true:false;
	    vSEResponse = (Integer.parseInt(getIntent().getStringExtra("SEResponse")) == 1)?true:false;
	    
	    
		Camera mCamera = new Camera(0, 0, cameraWidth, cameraHeight);
        final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(cameraWidth, cameraHeight), mCamera);
        engineOptions.getTouchOptions().setNeedsMultiTouch(true);
        engineOptions.getAudioOptions().setNeedsSound(true);
        
        // Update Settings
        
        vNoteNormalSize = (int) ((float) cameraWidth * (30.0f / 320.0f));
        vNoteNormalStroke = (int)  ((float) cameraWidth * (5.0f / 320.0f));
        
        vScreenTouchHandled = new boolean[]{false, false, false, false, false, false, false, false};
        
        vGameResultScore = 0;
        vGameResultPerfect = 0;
        vGameResultGreat = 0;
        vGameResultGood = 0;
        vGameResultMiss = 0;
        
        
        Log.i("test", "onCreateEngine");
        return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		// TODO onCreateResources
		
		VBOM = this.getVertexBufferObjectManager();
		
		vMediaPlayer = MediaPlayer.create(this, R.raw.senbonzakura);
		sBeatClap = SoundFactory.createSoundFromAsset(this.getSoundManager(), this.getApplicationContext(), "snd/se_sad06.wav");
		sBeatClap.setVolume(0.25f);
		
		Bitmap noteNormalBitmap = Bitmap.createBitmap(vNoteNormalSize + 4, vNoteNormalSize + 4, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(noteNormalBitmap);
		
		// draw normal note
		Paint paintNoteNormal = new Paint();
		paintNoteNormal.setARGB(255, 213, 132, 164);
		paintNoteNormal.setAntiAlias(true);
		paintNoteNormal.setStrokeWidth(vNoteNormalStroke);
		paintNoteNormal.setStyle(Paint.Style.STROKE);
		
		c.drawColor(Color.argb(0, 255, 255, 255));
		float stroke_width_half = (float) vNoteNormalStroke / 2.0f;
		RectF f = new RectF(stroke_width_half + 2.0f, stroke_width_half + 2.0f, (float) vNoteNormalSize - stroke_width_half + 2.0f, (float) vNoteNormalSize - stroke_width_half + 2.0f);
		c.drawArc(f, 0.0f, 360.0f, true, paintNoteNormal);
		
		BitmapTextureAtlasSource source = new BitmapTextureAtlasSource(noteNormalBitmap);
		BitmapTextureAtlas texture = new BitmapTextureAtlas(this.getTextureManager(), noteNormalBitmap.getWidth(), noteNormalBitmap.getHeight());
		texture.addTextureAtlasSource(source, 0, 0);
		texture.load();
		vNoteNormal = new Note();
		vNoteNormal2 = new Note();
		txNoteNormal = (TextureRegion) TextureRegionFactory.createFromSource(texture, source, 0, 0);
		
		
		// draw long note (open downward first)
		noteNormalBitmap = Bitmap.createBitmap(vNoteNormalSize + 4, vNoteNormalSize + 4, Bitmap.Config.ARGB_8888);
		c = new Canvas(noteNormalBitmap);
		c.drawArc(f, 180.0f, 180.0f, false, paintNoteNormal);
		source = new BitmapTextureAtlasSource(noteNormalBitmap);
		texture = new BitmapTextureAtlas(this.getTextureManager(), noteNormalBitmap.getWidth(), noteNormalBitmap.getHeight());
		texture.addTextureAtlasSource(source, 0, 0);
		texture.load();
		txNoteLongStart = (TextureRegion) TextureRegionFactory.createFromSource(texture, source, 0, 0);
		
		// draw long note (open upward)
		noteNormalBitmap = Bitmap.createBitmap(vNoteNormalSize + 4, vNoteNormalSize + 4, Bitmap.Config.ARGB_8888);
		c = new Canvas(noteNormalBitmap);
		c.drawArc(f, 0.0f, 180.0f, false, paintNoteNormal);
		source = new BitmapTextureAtlasSource(noteNormalBitmap);
		texture = new BitmapTextureAtlas(this.getTextureManager(), noteNormalBitmap.getWidth(), noteNormalBitmap.getHeight());
		texture.addTextureAtlasSource(source, 0, 0);
		texture.load();
		txNoteLongEnd = (TextureRegion) TextureRegionFactory.createFromSource(texture, source, 0, 0);
		
		// draw long note (middle part)
		noteNormalBitmap = Bitmap.createBitmap(vNoteNormalSize + 4, vScreenNotePathLength, Bitmap.Config.ARGB_8888);
		float bitmap_size = (float) (vNoteNormalSize + 4);
		c = new Canvas(noteNormalBitmap);
		c.drawColor(Color.rgb(255, 30, 30));
		Log.w("test", "width = " + vNoteNormalStroke + " half = " + stroke_width_half);
		c.drawLine(2.0f + stroke_width_half, 0.0f, 2.0f + stroke_width_half, vScreenNotePathLength, paintNoteNormal);
		c.drawLine(bitmap_size - 2.0f - (float) Math.ceil(stroke_width_half), 0.0f, bitmap_size - 2.0f - (float) Math.ceil(stroke_width_half), vScreenNotePathLength, paintNoteNormal);
		//c.drawLine(2.0f + (stroke_width_half - 1.0f) / 2.0f, 0.0f, 2.0f + (stroke_width_half - 1.0f) / 2.0f, (float) vScreenNotePathLength, paintNoteNormal);
		//c.drawLine((float) vNoteNormalSize - 3.0f + (stroke_width_half - 1.0f) / 2.0f, 0.0f, (float) vNoteNormalSize - 3.0f + (stroke_width_half - 1.0f) / 2.0f, (float) vScreenNotePathLength, paintNoteNormal);
		source = new BitmapTextureAtlasSource(noteNormalBitmap);
		texture = new BitmapTextureAtlas(this.getTextureManager(), noteNormalBitmap.getWidth(), noteNormalBitmap.getHeight());
		texture.addTextureAtlasSource(source, 0, 0);
		texture.load();
		txNoteLongMiddle = (TextureRegion) TextureRegionFactory.createFromSource(texture, source, 0, 0);		
		vNoteNormal.texture = txNoteLongStart;
		vNoteNormal2.texture = txNoteLongMiddle;
		
		// draw normal double note
		noteNormalBitmap = Bitmap.createBitmap(vNoteNormalSize + 4, vNoteNormalSize + 4, Bitmap.Config.ARGB_8888);
		c = new Canvas(noteNormalBitmap);
		
		// draw normal note
		paintNoteNormal = new Paint();
		paintNoteNormal.setARGB(255, 234, 228, 6);
		paintNoteNormal.setAntiAlias(true);
		paintNoteNormal.setStrokeWidth(vNoteNormalStroke);
		paintNoteNormal.setStyle(Paint.Style.STROKE);
		
		c.drawColor(Color.argb(0, 255, 255, 255));
		stroke_width_half = vNoteNormalStroke / 2;
		f = new RectF(stroke_width_half + 2.0f, stroke_width_half + 2.0f, (float) vNoteNormalSize - stroke_width_half + 2.0f, (float) vNoteNormalSize - stroke_width_half + 2.0f);
		c.drawArc(f, 0.0f, 360.0f, true, paintNoteNormal);
		
		source = new BitmapTextureAtlasSource(noteNormalBitmap);
		texture = new BitmapTextureAtlas(this.getTextureManager(), noteNormalBitmap.getWidth(), noteNormalBitmap.getHeight());
		texture.addTextureAtlasSource(source, 0, 0);
		texture.load();
		txNoteNormalDouble = (TextureRegion) TextureRegionFactory.createFromSource(texture, source, 0, 0);
		
		/*
		Paint paintBlack = new Paint(), paintTrans = new Paint();
		paintBlack.setARGB(255, 0, 0, 0);
		paintBlack.setAntiAlias(true);
		paintTrans.setARGB(0, 0, 0, 0);
		paintTrans.setAntiAlias(true);
		paintBlack.setStrokeWidth(5.0f);
		paintBlack.setStyle(Paint.Style.STROKE);*/
		
		
		
		//c.drawCircle(15, 15, 15, paintBlack);
		//c.drawCircle(15, 15, 10, paintTrans);
		
		
		fDebugBottom = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 24, Color.WHITE);
		fDebugBottom.load();
		
		dTimeText = new Text(120, cameraHeight - 60, fDebugBottom, "", 15, new TextOptions(HorizontalAlign.LEFT), VBOM);
		dPerfectText = new Text(120, 20 + 30 * 0, fDebugBottom, "Perfect : ", 15, new TextOptions(HorizontalAlign.LEFT), VBOM);
		dGreatText = new Text(120, 20 + 30 * 1, fDebugBottom, "   Great : ", 15, new TextOptions(HorizontalAlign.LEFT), VBOM);
		dGoodText = new Text(120, 20 + 30 * 2, fDebugBottom, "   Good : ", 15, new TextOptions(HorizontalAlign.LEFT), VBOM);
		dMissText = new Text(120, 20 + 30 * 3, fDebugBottom, "    Miss : ", 15, new TextOptions(HorizontalAlign.LEFT), VBOM);
		//dNoteDebugText = new Text(0, 0, fDebugBottom, "", 15, new TextOptions(HorizontalAlign.CENTER), VBOM);

		
		readNoteFromFile(R.raw.senbonzakura_ms);
		
		
		
		Log.i("test", "onCreateResource");
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		// TODO onCreateScene
		final Scene scene = new Scene();
        scene.setBackground(new Background(0.0f, 0.0f, 0.0f));//new Background(0.09804f, 0.6274f, 0.8784f)
        
        PointF midpoint_point = new PointF(vScreenMidPointX, vScreenMidPointY);
        Circle bgCircle = new Circle((float) vScreenNotePathLength, midpoint_point);
        
        bgCircle.circleDensity = 180;
        bgCircle.draw(scene);
        
        vNoteOnScreen = new LinkedList <Note>();
        
        //vNoteNormal.addToScreen(scene);
        //vNoteNormal2.addToScreen(scene);
        //vNoteNormal2.sprite.setHeight(100.0f);
        //vNoteNormal2.setPosition((int) (vNoteNormal.sprite.getWidth() / 2.0f), (int) (vNoteNormal.sprite.getWidth() / 2.0f + vNoteNormal2.sprite.getHeight() / 2));
        //Log.w("test", "x = " + vNoteNormal.sprite.getWidth() / 2.0f + ", y = " + (vNoteNormal.sprite.getWidth() / 2.0f + vNoteNormal2.sprite.getHeight() / 2));
        //Line debug_l = new Line(0.0f, (vNoteNormal.sprite.getWidth() / 2.0f + vNoteNormal2.sprite.getHeight() / 2), 100.0f, (vNoteNormal.sprite.getWidth() / 2.0f + vNoteNormal2.sprite.getHeight() / 2), VBOM);
        //debug_l.setLineWidth(1.0f);
        //debug_l.setColor(0, 0, 0);
    	
        scene.attachChild(dTimeText);
        scene.attachChild(dPerfectText);
        scene.attachChild(dGreatText);
        scene.attachChild(dGoodText);
        scene.attachChild(dMissText);
        
        vGameResultScore = 0;
        vGameResultPerfect = 0;
        vGameResultGreat = 0;
        vGameResultGood = 0;
        vGameResultMiss = 0;
        vGameFrameToEnd = 0;
        
        //scene.attachChild(debug_l);
        
        
        // TESTING:
        //vNotes = new Note[100];
        //for (int i = 0; i < 10; i++) {      	
        	//Array.set(vNotes, i, new NoteNormal(1.0f, i % 8));
        	//vNotes[i] = new NoteNormal(1.0f * (float) i, i % 8);
        //}
        
	    /*for (int i = 0; i < 8; i++) {
	    	Line l = new Line(vScreenMidPointX, vScreenMidPointY, vNoteGeneratePoint[i].x, vNoteGeneratePoint[i].y, VBOM);
	    	l.setLineWidth(3.0f);
        	l.setColor((float) i / 7, 0.0f, 0.0f);
	    	scene.attachChild(l);
	    	
	    	l = new Line(vNoteGeneratePoint[i].x, vNoteGeneratePoint[i].y, vNoteEndPoint[i].x, vNoteEndPoint[i].y, VBOM);
	    	l.setLineWidth(3.0f);
        	l.setColor(0.6f, 0.6f, (float) i / 7);
	    	scene.attachChild(l);
	    	
	    	//Log.i("draw line", "p=" + vNoteGeneratePoint[i].x + "," + vNoteGeneratePoint[i].y);
	    }*/
        
        /*for (int i = 0; i < 8; i++) {
	    	Line l = new Line(vScreenMidPointX, vScreenMidPointY, vNoteGeneratePoint[i].x, vNoteGeneratePoint[i].y, VBOM);
	    	l.setLineWidth(3.0f);
        	l.setColor(0.9f, 0.9f, 0.9f);
	    	scene.attachChild(l);
	    	
	    	l = new Line(vNoteGeneratePoint[i].x, vNoteGeneratePoint[i].y, vNoteEndPoint[i].x, vNoteEndPoint[i].y, VBOM);
	    	l.setLineWidth(3.0f);
        	l.setColor(0.8f, 0.8f, 0.8f);
	    	scene.attachChild(l);
	    	
	    	//Log.i("draw line", "p=" + vNoteGeneratePoint[i].x + "," + vNoteGeneratePoint[i].y);
	    }*/
        
        
	    scene.setOnSceneTouchListener(this);
	    scene.setTouchAreaBindingOnActionDownEnabled(true);
        
        scene.registerUpdateHandler(new TimerHandler(1f / 60.0f, true, new ITimerCallback() {
        	
        	@Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
            	//Place what you want to happen here!
            	onUpdate(pTimerHandler);

            }
        	
        	
		}));
        
        Log.i("test", "onCreateScene");
        vScene = scene;
        pOnCreateSceneCallback.onCreateSceneFinished(scene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		// TODO onPopulateScene
		
		
		//vGameStartTime = System.currentTimeMillis();
		//vMediaPlayer.start();
		vGameNextGenerateNoteId = 0;
		
		for (int i = 0; i < vNotes.length; i++) {
			//vNotes[i].generated = vNotes[i].ended = false;
			//Log.w("test", "1 end = " + vNotes[i].ended);
			/*vNotes[i].sprite.setVisible(true);
			vNotes[i].sprite.setAlpha(sNoteGenerateAlpha);
			vNotes[i].setPosition(vNoteGeneratePoint[vNotes[i].hitPosition].x, vNoteGeneratePoint[vNotes[i].hitPosition].y);
			//vNotes[i].setGenEndPoint();
			vNotes[i].setScale(sNoteGenerateScale);
			pScene.attachChild(vNotes[i].sprite);*/
			if (vNotes[i].noteType == 1) {
				vNotes[i].addToScreen(pScene);
			}
		}
		
		
		long current_time = System.currentTimeMillis();
		vGameBeforeStartBeat = new long[5];
		vGameBeforeStartBeatPlayed = 0;
		for (int i = 0; i < 5; i++){
			vGameBeforeStartBeat[i] = current_time + (long) (1000.0f / ((float) vBMP / 60.0f) * (float) (i + 1));
			if (i == 4) vGameStartTime = vGameBeforeStartBeat[i];
		}
		
		
		Log.i("test", "Populate");
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public void onGameDestroyed() {
		vMediaPlayer.stop();
		vMediaPlayer.release();
	}
	//final Circle c = new Circle(15.0f, new PointF(50.0f, 100.0f));
	//c.position = new PointF(50.0f + (float) frameID, 100.0f + (float) frameID);
	//c.draw(scene);
	
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, final TouchEvent pSceneTouchEvent)
	// TODO onSceneTouchEvent
	{
	    if (pSceneTouchEvent.isActionDown())
	    {
	        //execute action.
	    	Log.i("touch", "touch down = " + pSceneTouchEvent.getPointerID());
	    	if (pSceneTouchEvent.getPointerID() == 0) {
	    		//vNoteNormal.sprite.setPosition(pSceneTouchEvent.getX() - vNoteNormal.sprite.getWidth() / 2.0f, pSceneTouchEvent.getY() - vNoteNormal.sprite.getHeight() / 2.0f);
	    		if (pSceneTouchEvent.getX() <= 50.0f && pSceneTouchEvent.getY() <= 50.0f) finish();
	    	}
	    	int touchDownPosition = -1;
	    	for(int i = 0; i < 8; i++) {
				if (PointF.length(pSceneTouchEvent.getX() - vNoteEndPoint[i].x, pSceneTouchEvent.getY() - vNoteEndPoint[i].y) <= vNoteNormalSize * sScreenTouchAreaScale) {
					touchDownPosition = i;
				}
			}
	    	if (touchDownPosition >= 0 && vScreenTouchHandled != null) {
	    		vScreenTouchHandled[touchDownPosition] = true;
	    	}
	    	
	    	
	    	for(Note i : vNoteOnScreen) {
				if (!i.ended) {
					if (touchDownPosition == i.hitPosition) {
						if (i.hitTime <= getMusicCurrentTime() + sGameEarlyGoodTiming) {
							float time_to_perfect = i.hitTime - getMusicCurrentTime();
							
							i.ended = true;
							i.sprite.setVisible(false);
							if (time_to_perfect > sGameEarlyGreatTiming) {
								vGameResultGood += 1;
							} else if (time_to_perfect > sGameEarlyPerfectTiming) {
								vGameResultGreat += 1;
								if (vSEResponse) sBeatClap.play();
							} else if (time_to_perfect > -sGameLatePerfectTiming) {
								vGameResultPerfect += 1;
								if (vSEResponse) sBeatClap.play();
							} else if (time_to_perfect > -sGameLateGreatTiming) {
								vGameResultGreat += 1;
								if (vSEResponse) sBeatClap.play();
							}
							Log.i("test", "hit note! t = " + time_to_perfect);
							break;
						}
					}
				}
			}
	    	
	    	
	    } else if (pSceneTouchEvent.isActionMove()){
	    	//Log.i("touch", "touch move = " + pSceneTouchEvent.getPointerID());
	    	if (pSceneTouchEvent.getPointerID() == 0) {
	    		//vNoteNormal.sprite.setPosition(pSceneTouchEvent.getX() - vNoteNormal.sprite.getWidth() / 2.0f, pSceneTouchEvent.getY() - vNoteNormal.sprite.getHeight() / 2.0f);
	    	}
	    } else if (pSceneTouchEvent.isActionUp()){
	    	Log.i("touch", "touch up = " + pSceneTouchEvent.getPointerID());
	    }
	    return false;
	}
	
	private void onUpdate (final TimerHandler pTimerHandler) {
		// TODO onUpdate
		//dTimeText.setText("Time : "/* + (System.currentTimeMillis() - vGameStartTime)*/);
		//vNoteNormal.sprite.setPosition(vNoteNormal.sprite.getX(), vNoteNormal.sprite.getY() + 1);
		//dTimeText.detachSelf();
		//vScene.detachChild(dTimeText);
		//dTimeText.setText("Time :" + (System.currentTimeMillis() - vGameStartTime));
		
		//vScene.attachChild(dTimeText);
		
		
		
		if (vGameBeforeStartBeatPlayed <= 4) {
			if (System.currentTimeMillis() >= vGameBeforeStartBeat[vGameBeforeStartBeatPlayed]) {
				Log.i("test", "clap:" + vGameBeforeStartBeatPlayed);
				if (vGameBeforeStartBeatPlayed <= 3) {
					vGameBeforeStartBeatPlayed++;
					sBeatClap.play();
				} else {
					vGameBeforeStartBeatPlayed++;
					vMediaPlayer.start();
					vGameStartTime = System.currentTimeMillis();
				}
			}
		} else {
			dTimeText.setText("Time : " + getMusicCurrentTime());
			dPerfectText.setText("Perfect : " + vGameResultPerfect);
			dGreatText.setText("   Great : " + vGameResultGreat);
			dGoodText.setText("   Good : " + vGameResultGood);
			dMissText.setText("    Miss : " + vGameResultMiss);
		}
		//for (int i = 0; i < 8; i++) {
			//if (vScreenTouchHandled[i]) {
				//Log.i("test", "touch down btn id = " + (i + 1));
			//}
		//}
		if (vGameFrameToEnd > 0) {
			vGameFrameToEnd--;
		}
		
		if (vGameNextGenerateNoteId == vNotes.length && vGameFrameToEnd == 0) {
			Intent data = new Intent();
			data.putExtra("Prefect", Integer.toString(vGameResultPerfect));
			data.putExtra("Great", Integer.toString(vGameResultGreat));
			data.putExtra("Good", Integer.toString(vGameResultGood));
			data.putExtra("Miss", Integer.toString(vGameResultMiss));
			setResult(RESULT_OK, data);
			finish();
		}
		
		boolean generating = true;
		while (generating) {
			if (vGameNextGenerateNoteId < vNotes.length) {
				if (vNotes[vGameNextGenerateNoteId] != null) {
					if (vNotes[vGameNextGenerateNoteId].hitTime <= getMusicCurrentTime() + sNoteAppearTime[vNoteSpeed] - ((System.currentTimeMillis() < vGameStartTime) ? (vGameStartTime - System.currentTimeMillis()):0)) {
					//if (vNotes[vGameNextGenerateNoteId].hitTime >= getMusicCurrentTime() - sNoteAppearTime[vNoteSpeed] - 
					//		((System.currentTimeMillis() < vGameStartTime) ? (vGameStartTime - System.currentTimeMillis()):0)) {
						if (!vNotes[vGameNextGenerateNoteId].generated) {
							//vNotes[vGameNextGenerateNoteId].generated = true;
							//vNotes[vGameNextGenerateNoteId].sprite.setVisible(true);
							vNotes[vGameNextGenerateNoteId].generate();
							Log.i("Gen note", "id=" + vGameNextGenerateNoteId + ", pos = " + vNotes[vGameNextGenerateNoteId].hitPosition);
							vNoteOnScreen.addLast(vNotes[vGameNextGenerateNoteId]);
							vGameNextGenerateNoteId++;
						}
					} else generating = false;
				} else generating = false;
			} else if (vGameNextGenerateNoteId == vNotes.length && vGameFrameToEnd == 0) {
				vGameFrameToEnd = 300;
			} else generating = false;
		}
		LinkedList <Note> temp_note = new LinkedList <Note>();;
		for(Note i : vNoteOnScreen) {
			if (!i.ended) {
				/*if (vScreenTouchHandled[i.hitPosition]) {
					if (i.hitTime <= getMusicCurrentTime() + sGameEarlyGoodTiming) {
						float time_to_perfect = i.hitTime - getMusicCurrentTime();
						i.ended = true;
						i.sprite.setVisible(false);
						if (time_to_perfect > sGameEarlyGreatTiming) {
							vGameResultGood += 1;
						} else if (time_to_perfect > sGameEarlyPerfectTiming) {
							vGameResultGreat += 1;
						} else if (time_to_perfect > -sGameLatePerfectTiming) {
							vGameResultPerfect += 1;
						} else if (time_to_perfect > -sGameLateGreatTiming) {
							vGameResultGreat += 1;
						}
						Log.i("test", "hit note! t = " + time_to_perfect);
					}
				}*/
				if (!i.ended) {
					i.updatePosition();
					temp_note.add(i);
				}
			}
		}
		//vNoteOnScreen = temp_note;
		
		vScreenTouchHandled = new boolean[]{false, false, false, false, false, false, false, false};
	}
	
	
	//
	//
	// Function
	//
	//
	public void DstopRun(String reason) {
		//Toast.makeText(this, reason, Toast.LENGTH_LONG).show();
		Log.e("test", reason);
		//finish();
	}
	
	public void readNoteFromFile(int raw) {
		String strFromFile = "";
		InputStream is = getResources().openRawResource(raw);
		if (is!=null) {
			try {
				InputStreamReader tmp = new InputStreamReader(is);
				BufferedReader reader = new BufferedReader(tmp);
				String str;
				StringBuffer buf = new StringBuffer();
				while ((str= reader.readLine()) != null) {
					buf.append(str+"\n");
				}
				strFromFile = buf.toString();
				is.close();
			} catch (Exception e) {
				DstopRun("Error: Load notes failed: " + e.toString());
				//Toast.makeText(this, "Error:" + e.toString(), Toast.LENGTH_LONG).show();
				finish();
			}
		}
		if (strFromFile.contains("(" + vDifficulty + ")")) {
			strFromFile = strFromFile.substring(strFromFile.indexOf("(" + vDifficulty + ")") + 4);
			if (strFromFile.contains("(")) {
				strFromFile = strFromFile.substring(0, strFromFile.indexOf("("));
			}
			String[] strArray = strFromFile.split("\n");
			int noteCount = 0;
			Log.i("test", "length = " + strArray.length);
			for (int i = 0; i < strArray.length; i++){
				String[] noteAry = strArray[i].split(",");
				Log.i("Test", "noteAry[i][1] = '" + noteAry[1].toString() + "'");
				
				
				switch (Integer.parseInt(noteAry[1])) {
				case 1:
					if (noteAry[2].length() == 1) {
						noteCount += 1;
					} else if (noteAry[2].length() == 2) {
						noteCount += 2;
					}
					break;
				case 2:
					if (noteAry[2].length() == 1) {
						noteCount += 1;
					} else if (noteAry[2].length() == 2) {
						noteCount += 2;
					}
					break;
				default:
					DstopRun("Error: Unknown note id");
					
				}
				
			}
			vNotes = new Note[noteCount];
			Log.i("test", "count = " + noteCount);
			int noteId = 0;
			for (int i = 0; i < strArray.length; i++){
				String[] noteAry = strArray[i].split(",");
				int noteType = Integer.parseInt(noteAry[1]);
				/*boolean noteDouble = false;
				String[] notePosition = new String[2];
				
				//Log.i("test", "length=" + noteAry[2].length());
				if (noteType == "1") {
					if (noteAry[2].length() == 1) {
						notePosition[0] = noteAry[2].intern();
						Log.i("test", noteAry[0] + " " + noteAry[1] + " " + noteAry[2]);
					}
				} else if (noteAry[1] == "2") {
					
				} else {
					DstopRun("Error: Unknown note id");
				}
				*/
				
				
				if (noteType == 1) {
					if (noteAry[2].length() == 1) {
						vNotes[noteId++] = new NoteNormal(Integer.parseInt(noteAry[0]), Integer.parseInt(noteAry[2]) - 1, false);
						Log.i("test", noteAry[0] + " " + noteAry[1] + " " + noteAry[2]);
					} else if (noteAry[2].length() == 2) {
						vNotes[noteId++] = new NoteNormal(Integer.parseInt(noteAry[0]), Integer.parseInt(noteAry[2].substring(0, 1)) - 1, true);
						vNotes[noteId++] = new NoteNormal(Integer.parseInt(noteAry[0]), Integer.parseInt(noteAry[2].substring(1, 2)) - 1, true);
						Log.i("test", noteAry[0] + " " + noteAry[1] + " " + noteAry[2].substring(0, 1));
						Log.i("test", noteAry[0] + " " + noteAry[1] + " " + noteAry[2].substring(1, 2));
					}
				} else if (noteType == 2) {
					if (noteAry[2].length() == 1) {
						boolean doubleNote = false;
						if (noteId > 0) {if (vNotes[noteId - 1].hitTime == Integer.parseInt(noteAry[0])) {doubleNote = true;}}
						vNotes[noteId++] = new NoteLong(Integer.parseInt(noteAry[0]), Integer.parseInt(noteAry[3]), Integer.parseInt(noteAry[2].substring(0, 1)) - 1, doubleNote);
					} else if (noteAry[2].length() == 2) {
						vNotes[noteId++] = new NoteLong(Integer.parseInt(noteAry[0]), Integer.parseInt(noteAry[3]), Integer.parseInt(noteAry[2].substring(0, 1)) - 1, true);
						vNotes[noteId++] = new NoteLong(Integer.parseInt(noteAry[0]), Integer.parseInt(noteAry[3]), Integer.parseInt(noteAry[2].substring(1, 2)) - 1, true);
						Log.i("test", noteAry[0] + " " + noteAry[1] + " " + noteAry[2].substring(0, 1) + " " + noteAry[3]);
						Log.i("test", noteAry[0] + " " + noteAry[1] + " " + noteAry[2].substring(1, 2) + " " + noteAry[3]);
					}
				} else {
					DstopRun("Error: Unknown note id");
				}
			}
			Log.i("test", "1 = " + vNotes[0].hitPosition);
			
			
		} else {
			//Toast.makeText(this, "Error: This song don't have this level", Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	private int getMusicCurrentTime() {
		int result = (int) ((System.currentTimeMillis() - vGameStartTime > (long) 0)?(System.currentTimeMillis() - vGameStartTime):(0));//vMediaPlayer.getCurrentPosition();
		//Log.i("test", "Music current = " + result);
		return result;
	}
	
	
	
	
	//
	//
	// My Class
	//
	//
	class Note {
		// TODO Note
		public int noteType;
		public boolean generated;
		public boolean ended;
		public boolean se_played;
		public int hitTime;
		public int hitPosition;
		public boolean doubleNote;
		public TextureRegion texture;
		public Sprite sprite;
		public Text debug_txt;
		
		public void addToScreen (Scene scene) {
			sprite = new Sprite(0.0f, 0.0f, texture, VBOM);
			sprite.setScaleCenter(texture.getWidth() / 2, texture.getHeight() / 2);
			//debug_txt = new Text(0, 0, fDebugBottom, "", 15, new TextOptions(HorizontalAlign.CENTER), VBOM);
			//sprite.attachChild(debug_txt);
			//scene.attachChild(sprite);
			
			
			sprite.setVisible(false);
			sprite.setAlpha(sNoteGenerateAlpha);
			setPosition(vNoteGeneratePoint[hitPosition].x, vNoteGeneratePoint[hitPosition].y);
			//vNotes[i].setGenEndPoint();
			setScale(sNoteGenerateScale);
			scene.attachChild(sprite);
		}
		
		public void setPosition (int x, int y) {
			sprite.setPosition((float) x - sprite.getWidth() / 2, (float) y - sprite.getHeight() / 2);
		}
		
		public void setScale (float scale) {
			sprite.setScale(scale);
		}
		
		public void generate () {
			this.generated = true;
			this.sprite.setVisible(true);
		}
		
		public void updatePosition () {
			if (ended) return;
			int time_before_music = (int) ((System.currentTimeMillis() < vGameStartTime) ? (vGameStartTime - System.currentTimeMillis()):0);
			float percentage_to_hit = 1.0f - (((float) (hitTime - getMusicCurrentTime()) + time_before_music) / (float) sNoteAppearTime[vNoteSpeed]);
			//getMusicCurrentTime() + sNoteAppearTime[vNoteSpeed]
			if (percentage_to_hit <= sNoteGenerateTimeScale) {
				float percentage_alpha = percentage_to_hit / sNoteGenerateTimeScale * (1.0f - sNoteGenerateAlpha) + sNoteGenerateAlpha;
				float percentage_scale = percentage_to_hit / sNoteGenerateTimeScale * (1.0f - sNoteGenerateScale) + sNoteGenerateScale;
				sprite.setAlpha(percentage_alpha);
				setScale(percentage_scale);
			} else if (percentage_to_hit < 1.0f) {
				sprite.setAlpha(1.0f);
				setScale(1.0f);
				float percentage_path = (percentage_to_hit - sNoteGenerateTimeScale) / (1.0f - sNoteGenerateTimeScale);
				int locX = vNoteGeneratePoint[hitPosition].x + (int) ((float) (vNoteEndPoint[hitPosition].x - vNoteGeneratePoint[hitPosition].x) * (percentage_path));
				int locY = vNoteGeneratePoint[hitPosition].y + (int) ((float) (vNoteEndPoint[hitPosition].y - vNoteGeneratePoint[hitPosition].y) * percentage_path);
				setPosition(locX, locY);
			} else {
				//sprite.setAlpha(0.2f);
				//sprite.setVisible(false);
				//ended = true;
				if (!se_played) {
					se_played = true;
					if (vSEStandard) sBeatClap.play();
				}
				float percentage_path = (percentage_to_hit - sNoteGenerateTimeScale) / (1.0f - sNoteGenerateTimeScale);
				if (percentage_path > 1.0f) percentage_path = percentage_path + (percentage_path - 1.0f) * 0.2f; 
				int locX = vNoteGeneratePoint[hitPosition].x + (int) ((float) (vNoteEndPoint[hitPosition].x - vNoteGeneratePoint[hitPosition].x) * percentage_path);
				int locY = vNoteGeneratePoint[hitPosition].y + (int) ((float) (vNoteEndPoint[hitPosition].y - vNoteGeneratePoint[hitPosition].y) * percentage_path);
				setPosition(locX, locY);
				float fade_out_percentage = (float) (getMusicCurrentTime() - hitTime) / (float) sGameLateGreatTiming;
				if (fade_out_percentage > 1.0f) {
					fade_out_percentage = 1.0f;
					vGameResultMiss += 1;
					ended = true;
				}
				setScale(1.0f - fade_out_percentage);
				sprite.setAlpha(1.0f - fade_out_percentage);
			}
		}
	}
	class NoteNormal extends Note {
		NoteNormal (int hitTime, int hitPosition, boolean doubleNote) {
			noteType = 1;
			generated = false;
			ended = false;
			se_played = false;
			this.hitTime = hitTime;
			this.hitPosition = hitPosition;
			this.doubleNote = doubleNote;
			this.texture = (doubleNote)?txNoteNormalDouble:txNoteNormal;
			sprite = new Sprite(0.0f, 0.0f, texture, VBOM);
			sprite.setScaleCenter(texture.getWidth() / 2, texture.getHeight() / 2);
		}
	}
	class NoteLong extends Note {
		public Sprite middleSprite;
		public Sprite endSprite;
		public int endTime;
		public boolean startNoteHit = false;
		NoteLong (int hitTime, int endTime, int hitPosition, boolean doubleNote) {
			//txNoteLongStart
			noteType = 2;
			generated = false;
			ended = false;
			se_played = false;
			this.hitTime = hitTime;
			this.endTime = endTime;
			this.hitPosition = hitPosition;
			this.doubleNote = doubleNote;
			this.texture = (doubleNote)?txNoteLongStartDouble:txNoteLongStart;
			sprite = new Sprite(0.0f, 0.0f, texture, VBOM);
			sprite.setScaleCenter(texture.getWidth() / 2, texture.getHeight() / 2);
			sprite.setRotation(45.0f * (float) hitPosition + 22.5f);
			middleSprite = new Sprite(0.0f, 0.0f, (doubleNote)?txNoteLongMiddleDouble:txNoteLongMiddle, VBOM);
			endSprite = new Sprite(0.0f, 0.0f, (doubleNote)?txNoteLongEndDouble:txNoteLongEnd, VBOM);
			endSprite.setScaleCenter(texture.getWidth() / 2, texture.getHeight() / 2);
			endSprite.setRotation(45.0f * (float) hitPosition + 22.5f);
		}
		
		@Override
		public void addToScreen (Scene scene) {
			sprite = new Sprite(0.0f, 0.0f, texture, VBOM);
			sprite.setScaleCenter(texture.getWidth() / 2, texture.getHeight() / 2);
			//debug_txt = new Text(0, 0, fDebugBottom, "", 15, new TextOptions(HorizontalAlign.CENTER), VBOM);
			sprite.attachChild(debug_txt);
			scene.attachChild(sprite);
			scene.attachChild(middleSprite);
			scene.attachChild(endSprite);
		}
		
		@Override
		public void updatePosition () {
			if (ended) return;
			int time_before_music = (int) ((System.currentTimeMillis() < vGameStartTime) ? (vGameStartTime - System.currentTimeMillis()):0);
			float percentage_to_hit = 1.0f - (((float) (hitTime - getMusicCurrentTime()) + time_before_music) / (float) sNoteAppearTime[vNoteSpeed]);
			//getMusicCurrentTime() + sNoteAppearTime[vNoteSpeed]
			if (percentage_to_hit <= sNoteGenerateTimeScale) {
				float percentage_alpha = percentage_to_hit / sNoteGenerateTimeScale * (1.0f - sNoteGenerateAlpha) + sNoteGenerateAlpha;
				float percentage_scale = percentage_to_hit / sNoteGenerateTimeScale * (1.0f - sNoteGenerateScale) + sNoteGenerateScale;
				sprite.setAlpha(percentage_alpha);
				endSprite.setAlpha(percentage_alpha);
				setScale(percentage_scale);
			} else if (percentage_to_hit < 1.0f) {
				sprite.setAlpha(1.0f);
				endSprite.setAlpha(1.0f);
				setScale(1.0f);
				float percentage_path = (percentage_to_hit - sNoteGenerateTimeScale) / (1.0f - sNoteGenerateTimeScale);
				int locX = vNoteGeneratePoint[hitPosition].x + (int) ((float) (vNoteEndPoint[hitPosition].x - vNoteGeneratePoint[hitPosition].x) * (percentage_path));
				int locY = vNoteGeneratePoint[hitPosition].y + (int) ((float) (vNoteEndPoint[hitPosition].y - vNoteGeneratePoint[hitPosition].y) * percentage_path);
				setPosition(locX, locY);
			} else {
				//sprite.setAlpha(0.2f);
				//sprite.setVisible(false);
				//ended = true;
				if (!se_played) {
					se_played = true;
					sBeatClap.play();
				}
				
				/*
				float percentage_path = (percentage_to_hit - sNoteGenerateTimeScale) / (1.0f - sNoteGenerateTimeScale);
				if (percentage_path > 1.0f) percentage_path = percentage_path + (percentage_path - 1.0f) * 0.2f; 
				int locX = vNoteGeneratePoint[hitPosition].x + (int) ((float) (vNoteEndPoint[hitPosition].x - vNoteGeneratePoint[hitPosition].x) * percentage_path);
				int locY = vNoteGeneratePoint[hitPosition].y + (int) ((float) (vNoteEndPoint[hitPosition].y - vNoteGeneratePoint[hitPosition].y) * percentage_path);
				setPosition(locX, locY);
				float fade_out_percentage = (float) (getMusicCurrentTime() - hitTime) / (float) sGameLateGreatTiming;
				if (fade_out_percentage > 1.0f) {
					fade_out_percentage = 1.0f;
					vGameResultMiss += 1;
					ended = true;
				}
				setScale(1.0f - fade_out_percentage);
				sprite.setAlpha(1.0f - fade_out_percentage);
				*/
			}
		}
		@Override
		public void setPosition (int x, int y) {
			sprite.setPosition((float) x - sprite.getWidth() / 2, (float) y - sprite.getHeight() / 2);
			endSprite.setPosition((float) x - endSprite.getWidth() / 2, (float) y - endSprite.getHeight() / 2);
		}
		
		public void setStartPosition (int x, int y) {
			sprite.setPosition((float) x - sprite.getWidth() / 2, (float) y - sprite.getHeight() / 2);
		}
		
		public void setEndPosition (int x, int y) {
			endSprite.setPosition((float) x - endSprite.getWidth() / 2, (float) y - endSprite.getHeight() / 2);
		}
		
		@Override
		public void setScale (float scale) {
			sprite.setScale(scale);
			endSprite.setScale(scale);
		}
		@Override
		public void generate () {
			this.generated = true;
			this.sprite.setVisible(true);
			this.middleSprite.setVisible(true);
			this.endSprite.setVisible(true);
		}
	}
	class Circle {
		private VertexBufferObjectManager vbom;
		
		public float radius;
		public PointF position;
		public int circleDensity = 12;
				
		Circle () {	vbom = GameActivity.this.getVertexBufferObjectManager(); }//lines = new Line[CIRCLE_DENSITY];
		Circle (float r, PointF p) {
			vbom = GameActivity.this.getVertexBufferObjectManager();
			radius = r;
			position = p;
		}
		
		public void draw (Scene scene) {
			PointF prev_point = new PointF();
			for (int i = 0; i <= circleDensity; i++) {
				PointF p = new PointF();
				Float degree = ((float) i) / ((float) circleDensity) * 360.0f;
				p.x = (float) (radius * Math.sin(degree / 180.f * Math.PI)) + position.x;
				p.y = (float) (radius * Math.cos(degree / 180.f * Math.PI)) + position.y;
				
				if (i == 0) {prev_point = p; continue;}
				
				Line l = new Line(prev_point.x, prev_point.y, p.x, p.y, vbom);
				l.setLineWidth(3.0f);
            	l.setColor(0.9f, 0.9f, 0.9f);
            	scene.attachChild(l);
            	
				prev_point = p;
			}
		}
	}
	//
	//
	// Other Class
	//
	//
	public class BitmapTextureAtlasSource extends BaseTextureAtlasSource implements IBitmapTextureAtlasSource 
	{
	    private final int[] mColors;
	 
	    public BitmapTextureAtlasSource(Bitmap pBitmap)
	    {
	    	super(0,0, pBitmap.getWidth(), pBitmap.getHeight());
	        
	        mColors = new int[mTextureWidth * mTextureHeight];
	        
	        for(int y = 0; y < mTextureHeight; ++y)
	        {
	        	for( int x = 0; x < mTextureWidth; ++x)
	        	{
	        		mColors[x + y * mTextureWidth] = pBitmap.getPixel(x, y);
	        	}
	        }
	    }

		@Override
		public Bitmap onLoadBitmap(Config pBitmapConfig)
		{
			return Bitmap.createBitmap(mColors, mTextureWidth, mTextureHeight, Bitmap.Config.ARGB_8888);
		}

		@Override
		public IBitmapTextureAtlasSource deepCopy()
		{
			return new BitmapTextureAtlasSource(Bitmap.createBitmap(mColors, mTextureWidth, mTextureHeight, Bitmap.Config.ARGB_8888));
		}
	}
	
}




