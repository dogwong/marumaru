package com.project.marumaru;

import java.util.Random;

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
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.ITextureAtlas;
import org.andengine.opengl.texture.atlas.ITextureAtlas.ITextureAtlasStateListener;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.source.BaseTextureAtlasSource;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;

import com.project.marumaru.GameActivity.BitmapTextureAtlasSource;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.view.Display;
import android.widget.Toast;


public class StartupScene extends BaseGameActivity implements IOnSceneTouchListener {

	static int cameraWidth = 0;
	static int cameraHeight = 0;
	
	static VertexBufferObjectManager VBOM;
	
	// Variable for screen
	static Scene vScene;
	static int vScreenMidPointX;
	static int vScreenMidPointY;
	static int vScreenNotePathLength;
	static int vScreenNoteGenerateLength;
	static boolean vScreenTouchHandled[];
	
	static Point vNoteGeneratePoint[];
	static Point vNoteEndPoint[];
	
	//private BitmapTextureAtlas mBitmapTextureAtlas;
	private BitmapTextureAtlas btaGameLogo;
	private BitmapTextureAtlas btaGameLogo2;
	private BitmapTextureAtlas btaGameBackground;
	private BitmapTextureAtlas btaGameBackground2;
	private ITextureRegion txGameLogo;
	private ITextureRegion txGameLogo2;
	private ITextureRegion txGameBackground;
	private ITextureRegion txGameBackground2;
	
	static Sprite spGameLogo;
	static Sprite spGameBackground;
	
	static Text txtGameStart;
	static Text txtVersion;
	
	static int iDisplayMode = 1; // 1 = title, 2 = introduction
	static int iGameStartFrameID;
	static int iIdleFrameCount;
	
	
	// Settings for screen
	static float sScreenNotePathScale = (143.0f / 160.0f);
	static float sScreenNoteGenerateScale = (40.0f / 160.0f);
	static float sScreenTouchAreaScale = 2.0f;
	
	// Setting for note
	static int sNoteAppearTime[] = {2000, 1200, 1000, 850, 700, 600, 500, 400, 300};
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
	
	
	// Variable for introduction
	static int t_frameID;
	
	private BitmapTextureAtlas btaHand;
	private ITextureRegion txHand;
	
	static Sprite t_sBorder;
	static Sprite t_sHand;
	static Sprite t_note_normal;
	static Sprite t_note_double1;
	static Sprite t_note_double2;
	
	static Text t_txtIntroduction;
	static Text t_txtDescription;
	
	static PointF t_drawCirclePrevPoint;
	
	
	// Font
	static Font fNormal;
	static Font fVersion;
	
	
	
	// Variable for tutorial
	
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		
		// Setting up for the screen
		final Display display = getWindowManager().getDefaultDisplay();
	    cameraWidth = display.getWidth();
	    cameraHeight = display.getHeight();
	    vScreenMidPointX = cameraWidth / 2;
	    vScreenMidPointY = cameraHeight / 2;
	    vScreenNotePathLength = (int) ((float) vScreenMidPointX * sScreenNotePathScale);
	    vScreenNoteGenerateLength = (int) ((float) vScreenMidPointX * sScreenNoteGenerateScale);
	    
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
		
		Camera mCamera = new Camera(0, 0, cameraWidth, cameraHeight);
        final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(cameraWidth, cameraHeight), mCamera);
        
        iGameStartFrameID = 0;
        
        // Update Settings
        
        vNoteNormalSize = (int) ((float) cameraWidth * (30.0f / 320.0f));
        vNoteNormalStroke = (int)  ((float) cameraWidth * (5.0f / 320.0f));
        
        vScreenTouchHandled = new boolean[]{false, false, false, false, false, false, false, false};
		
		return engineOptions;
	}
	
	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		
		VBOM = this.getVertexBufferObjectManager();
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		final ITextureAtlasStateListener.TextureAtlasStateAdapter<IBitmapTextureAtlasSource> textureAtlasStateListener = new ITextureAtlasStateListener.TextureAtlasStateAdapter<IBitmapTextureAtlasSource>() {
			@Override
			public void onTextureAtlasSourceLoadExeption(final ITextureAtlas<IBitmapTextureAtlasSource> pTextureAtlas, final IBitmapTextureAtlasSource pBitmapTextureAtlasSource, final Throwable pThrowable) {
				StartupScene.this.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(StartupScene.this, "Failed loading TextureSource: " + pBitmapTextureAtlasSource.toString(), Toast.LENGTH_LONG).show();
					}
				});
			}
		};
		
		
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
		txNoteNormal = (TextureRegion) TextureRegionFactory.createFromSource(texture, source, 0, 0);
		
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
		
		
		

		//this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1200, 720, TextureOptions.BILINEAR, textureAtlasStateListener);
		this.btaGameBackground = new BitmapTextureAtlas(this.getTextureManager(), 1200, 1200, TextureOptions.BILINEAR, textureAtlasStateListener);
		this.btaGameBackground2 = new BitmapTextureAtlas(this.getTextureManager(), 1400, 1400, TextureOptions.BILINEAR, textureAtlasStateListener);
		this.btaGameLogo = new BitmapTextureAtlas(this.getTextureManager(), 470, 83, TextureOptions.BILINEAR, textureAtlasStateListener);
		this.btaGameLogo2 = new BitmapTextureAtlas(this.getTextureManager(), 700, 700, TextureOptions.BILINEAR, textureAtlasStateListener);
		this.txGameLogo = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.btaGameLogo, this, "title_logo.png", 0, 0);
		this.txGameLogo2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.btaGameLogo2, this, "title_logo_2.png", 0, 0);
		this.txGameBackground  = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.btaGameBackground, this, "title_background.png", 0, 0);
		this.txGameBackground2  = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.btaGameBackground2, this, "title_background_2.png", 0, 0);
		//this.mBitmapTextureAtlas.load();
		this.btaGameBackground.load();
		this.btaGameBackground2.load();
		this.btaGameLogo.load();
		this.btaGameLogo2.load();
		
		// introduction
		this.btaHand = new BitmapTextureAtlas(this.getTextureManager(), 86, 102, TextureOptions.BILINEAR, textureAtlasStateListener);
		this.txHand  = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.btaHand, this, "hand.png", 0, 0);
		this.btaHand.load();
		
		
		fNormal = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 32, Color.WHITE);
		fNormal.load();
		fVersion = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 24, Color.WHITE);
		fVersion.load();
    	
		txtGameStart = new Text(0, 20, fNormal, "Touch screen to start", new TextOptions(HorizontalAlign.CENTER), VBOM);
		txtVersion = new Text(0, 20, fVersion, "v20130611a  \nIVE 12-13 41983F 3C OOPA Project  \nGroup ChanYC & LeeHC  ", new TextOptions(HorizontalAlign.RIGHT), VBOM);
		txtVersion.setColor(0.6f, 0.6f, 0.6f);
		t_txtIntroduction = new Text(0, 20, fNormal, "Introduction", new TextOptions(HorizontalAlign.CENTER), VBOM);
		t_txtDescription = new Text(0, 20, fNormal, "Listen to the music", 50, new TextOptions(HorizontalAlign.CENTER), VBOM);
		
    	
    	
		
        
		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		final Scene scene = new Scene();
        scene.setBackground(new Background(0.2f, 0.133f, 0.133f));
		//scene.setBackground(new Background(1.0f, 1.0f, 1.0f));
		
        PointF midpoint_point = new PointF(vScreenMidPointX, vScreenMidPointY);
        
        scene.setOnSceneTouchListener(this);
        scene.registerUpdateHandler(new TimerHandler(1f / 60.0f, true, new ITimerCallback() {
        	@Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
            	//Place what you want to happen here!
            	onUpdate(pTimerHandler);
            	
            }
        	
        	
		}));
        
        vScene = scene;
        pOnCreateSceneCallback.onCreateSceneFinished(scene);
        
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		
		iDisplayMode = 1;
		iIdleFrameCount = 0;
		iGameStartFrameID = 0;
		
		Random rnd = new Random();
		int bgid = (int) (Math.floor(rnd.nextDouble() * 2.0f));
		//bgid = 0;
		if (bgid == 0) {
			spGameBackground = new Sprite(0.0f, 0.0f, txGameBackground, VBOM);
			spGameBackground.setPosition(0.0f, 0.0f);
			spGameBackground.setScaleCenter(0.0f, 0.0f);
			pScene.attachChild(spGameBackground);
			
			spGameLogo = new Sprite(0.0f, 0.0f, txGameLogo, VBOM);
			spGameLogo.setScaleCenter(0.0f, 0.0f);
			pScene.attachChild(spGameLogo);
			
			float scale_percentage = 1.0f;
			scale_percentage = (float) cameraWidth / spGameBackground.getWidth();
			
			
			spGameBackground.setScale(scale_percentage);
			spGameLogo.setScale(scale_percentage);
			
			spGameBackground.setPosition((cameraWidth - spGameBackground.getWidthScaled()) / 2.0f, (cameraHeight - spGameBackground.getHeightScaled()) / 10.0f * -1.0f);
			spGameLogo.setPosition((cameraWidth - spGameLogo.getWidthScaled()) / 2.0f, (cameraHeight - spGameLogo.getHeightScaled()) / 10.0f * 3.5f);
			
		} else if (bgid == 1) {
			spGameBackground = new Sprite(0.0f, 0.0f, txGameBackground2, VBOM);
			spGameBackground.setPosition(0.0f, 0.0f);
			spGameBackground.setScaleCenter(0.5f, 0.5f);
			pScene.attachChild(spGameBackground);
			
			spGameLogo = new Sprite(0.0f, 0.0f, txGameLogo2, VBOM);
			spGameLogo.setScaleCenter(0.0f, 0.0f);
			pScene.attachChild(spGameLogo);
			
			float logo_scale_percentage = 1.0f;
			logo_scale_percentage = (float) cameraWidth / spGameLogo.getWidth();
			
			float bg_scale_percentage = 1.0f;
			bg_scale_percentage = ((float) cameraHeight * 1.2f) / spGameBackground.getHeight();
			
			spGameBackground.setScale(bg_scale_percentage);
			spGameLogo.setScale(logo_scale_percentage);
			
			//Log.w("test", "hs = " + sGameBackground.getHeightScaled() + " h = " + sGameBackground.getHeight());
			//sGameBackground.setPosition(0.0f, sGameBackground.getHeightScaled() - sGameBackground.getHeight());
			
			spGameBackground.setPosition((cameraWidth - spGameBackground.getWidthScaled()) / 2.0f, ((float) cameraHeight / 10.0f * 1.0f) - spGameBackground.getHeightScaled() / 2.0f + spGameLogo.getHeightScaled() / 2.0f);
			spGameLogo.setPosition((cameraWidth - spGameLogo.getWidthScaled()) / 2.0f, ((float) cameraHeight / 10.0f * 1.0f));
			
		}
		
		txtGameStart.setPosition((cameraWidth - txtGameStart.getWidth()) / 2.0f, (cameraHeight - txtGameStart.getHeight()) / 10.0f * 8.4f);
		pScene.attachChild(txtGameStart);
		txtVersion.setPosition((cameraWidth - txtVersion.getWidth()), (cameraHeight - txtGameStart.getHeight()) / 10.0f * 9.2f);
		pScene.attachChild(txtVersion);
		
		
		t_txtIntroduction.setPosition((cameraWidth - t_txtIntroduction.getWidth()) / 2.0f, (cameraHeight - t_txtIntroduction.getHeight()) / 10.0f * 1.0f);
		t_txtDescription.setPosition((cameraWidth - t_txtDescription.getWidth()) / 2.0f, (cameraHeight - t_txtDescription.getHeight()) / 10.0f * 6.0f);
		pScene.attachChild(t_txtIntroduction);
		pScene.attachChild(t_txtDescription);
		t_txtIntroduction.setAlpha(0.0f);
		t_txtDescription.setAlpha(0.0f);
		
		t_note_normal = new Sprite(0.0f, 0.0f, txNoteNormal, VBOM);
		t_note_normal.setAlpha(0.0f);
		//vScene.attachChild(t_note_normal);
		
		t_sHand = new Sprite(0.0f, 0.0f, txHand, VBOM);
		t_sHand.setAlpha(0.0f);
		//vScene.attachChild(t_sHand);
		
		
		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
		//spGameLogo.setPosition(0.0f, cameraHeight / 2);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
		
		return false;
	}
	
	private void onUpdate (final TimerHandler pTimerHandler) {
		// TODO onUpdate
		
		if (iDisplayMode == 1) {
			iIdleFrameCount++;
			iGameStartFrameID++;
			if (iIdleFrameCount >= 700 && iGameStartFrameID == 120) {
				iGameStartFrameID--;
				startIntroduction();
			}
		} else if (iDisplayMode == 2) {
			t_frameID++;
			if (t_frameID < 50) {
				spGameLogo.setAlpha(spGameLogo.getAlpha() - 0.02f);
				spGameBackground.setAlpha(spGameBackground.getAlpha() - 0.02f);
			} else if (t_frameID == 50) {
				spGameLogo.setAlpha(0.0f);
				spGameBackground.setAlpha(0.0f);
			} else if (t_frameID < 80) {
				float frame_passed = (float) (t_frameID - 50);
				//0.2f, 0.133f, 0.133f
				vScene.setBackground(new Background(0.2f * (1.0f - (frame_passed / 30.0f)), 0.133f * (1.0f - (frame_passed / 30.0f)), 0.133f * (1.0f - (frame_passed / 30.0f))));
			} else if (t_frameID < 110) {
				t_txtIntroduction.setAlpha(t_txtIntroduction.getAlpha() + 0.0333f);
			} else if (t_frameID <= 230) { // 120 needed
				if (t_frameID == 110) {
					t_txtIntroduction.setAlpha(1.0f);
					vScene.setBackground(new Background(0.0f, 0.0f, 0.0f));
				}
				float frame_passed = (float) (t_frameID - 110);
				PointF p = new PointF();
				Float degree = ((frame_passed) / 120.0f) * 360.0f + 180.0f;
				p.x = (float) ((float) vScreenNotePathLength * Math.sin(degree / 180.f * Math.PI)) + vScreenMidPointX;
				p.y = (float) ((float) vScreenNotePathLength * Math.cos(degree / 180.f * Math.PI)) + vScreenMidPointY;
				
				if (frame_passed == 0) {t_drawCirclePrevPoint = p;} else {
					Line l = new Line(t_drawCirclePrevPoint.x, t_drawCirclePrevPoint.y, p.x, p.y, VBOM);
					l.setLineWidth(3.0f);
	            	l.setColor(0.9f, 0.9f, 0.9f);
	            	vScene.attachChild(l);
	            	
	            	t_drawCirclePrevPoint = p;
				}
			} else if (t_frameID < 290) {
				
			} else if (t_frameID < 340) {
				t_txtDescription.setAlpha(t_txtDescription.getAlpha() + 0.02f);
			} else if (t_frameID == 340) {
				t_txtDescription.setAlpha(1.0f);
			} else if (t_frameID < 390) {
				
			} else if (t_frameID < 440) {
				
			} else if (t_frameID == 500) {
				t_txtDescription.setText("Note will appear along\nthe music plays");
				t_txtDescription.setPosition((cameraWidth - t_txtDescription.getWidth()) / 2.0f, (cameraHeight - t_txtDescription.getHeight()) / 10.0f * 6.0f);
				t_note_normal.setAlpha(0.0f);
				t_note_normal.setScaleCenter(t_note_normal.getWidth() / 2.0f, t_note_normal.getHeight() / 2.0f);
				t_note_normal.setPosition((float) vNoteGeneratePoint[7].x - t_note_normal.getWidth() / 2.0f, (float) vNoteGeneratePoint[7].y - t_note_normal.getHeight() / 2.0f);
				t_note_normal.setScale(0.2f);
				t_note_normal.setAlpha(0.2f);
				vScene.detachChild(t_note_normal);
				vScene.attachChild(t_note_normal);
				
			} else if (t_frameID < 580) {
				t_note_normal.setAlpha(t_note_normal.getAlpha() + 0.01f);
				t_note_normal.setScale(t_note_normal.getScaleX() + 0.01f);
			} else if (t_frameID == 580) {
				t_note_normal.setAlpha(1.0f);
				t_note_normal.setScale(1.0f);
				
				t_sHand.setAlpha(0.0f);
				t_sHand.setPosition((float) vNoteEndPoint[7].x - t_sHand.getWidth(), (float) vNoteGeneratePoint[7].y);
				t_sHand.setScaleCenter(t_sHand.getWidth(), t_sHand.getHeight());
				t_sHand.setScale(0.8f);
				vScene.detachChild(t_sHand);
				vScene.attachChild(t_sHand);
			} else if (t_frameID < 630) {
				t_sHand.setAlpha(t_sHand.getAlpha() + 0.02f);
			} else if (t_frameID == 630) {
				t_sHand.setAlpha(1.0f);
				t_txtDescription.setText("Follow the rhythm...");
				t_txtDescription.setPosition((cameraWidth - t_txtDescription.getWidth()) / 2.0f, (cameraHeight - t_txtDescription.getHeight()) / 10.0f * 6.0f);
			} else if (t_frameID < 790) {
				float frame_passed = (float) (t_frameID - 630);
				float px = ((float) vNoteGeneratePoint[7].x) + (((float) vNoteEndPoint[7].x) - ((float) vNoteGeneratePoint[7].x)) * frame_passed / 360.0f;
				float py = ((float) vNoteGeneratePoint[7].y) + (((float) vNoteEndPoint[7].y) - ((float) vNoteGeneratePoint[7].y)) * frame_passed / 360.0f;
				t_note_normal.setPosition(px - t_note_normal.getWidth() / 2.0f, py - t_note_normal.getHeight() / 2.0f);
			} else if (t_frameID == 790) {
				float frame_passed = (float) (t_frameID - 630);
				float px = ((float) vNoteGeneratePoint[7].x) + (((float) vNoteEndPoint[7].x) - ((float) vNoteGeneratePoint[7].x)) * frame_passed / 360.0f;
				float py = ((float) vNoteGeneratePoint[7].y) + (((float) vNoteEndPoint[7].y) - ((float) vNoteGeneratePoint[7].y)) * frame_passed / 360.0f;
				t_note_normal.setPosition(px - t_note_normal.getWidth() / 2.0f, py - t_note_normal.getHeight() / 2.0f);
				
				t_txtDescription.setText("Click on the note when\nit's origin reach the circle");
				t_txtDescription.setPosition((cameraWidth - t_txtDescription.getWidth()) / 2.0f, (cameraHeight - t_txtDescription.getHeight()) / 10.0f * 6.0f);
			} else if (t_frameID < 890) {
				float frame_passed = (float) (t_frameID - 630);
				float px = ((float) vNoteGeneratePoint[7].x) + (((float) vNoteEndPoint[7].x) - ((float) vNoteGeneratePoint[7].x)) * frame_passed / 360.0f;
				float py = ((float) vNoteGeneratePoint[7].y) + (((float) vNoteEndPoint[7].y) - ((float) vNoteGeneratePoint[7].y)) * frame_passed / 360.0f;
				t_note_normal.setPosition(px - t_note_normal.getWidth() / 2.0f, py - t_note_normal.getHeight() / 2.0f);
				
				float frame_passed_hand = (float) (t_frameID - 790);
				float h_px = ((float) vNoteEndPoint[7].x) + (((float) vNoteEndPoint[7].x) - ((float) vNoteEndPoint[7].x)) * frame_passed_hand / 125.0f;
				float h_py = ((float) vNoteGeneratePoint[7].y) + (((float) vNoteEndPoint[7].y) - ((float) vNoteGeneratePoint[7].y)) * frame_passed_hand / 125.0f;
				t_sHand.setPosition(h_px - t_sHand.getWidth(), h_py);
			} else if (t_frameID < 940) {
				float frame_passed = (float) (t_frameID - 630);
				float px = ((float) vNoteGeneratePoint[7].x) + (((float) vNoteEndPoint[7].x) - ((float) vNoteGeneratePoint[7].x)) * frame_passed / 360.0f;
				float py = ((float) vNoteGeneratePoint[7].y) + (((float) vNoteEndPoint[7].y) - ((float) vNoteGeneratePoint[7].y)) * frame_passed / 360.0f;
				t_note_normal.setPosition(px - t_note_normal.getWidth() / 2.0f, py - t_note_normal.getHeight() / 2.0f);
				
				float frame_passed_hand = (float) (t_frameID - 890);
				float h_px = ((float) vNoteEndPoint[7].x) + (((float) vNoteEndPoint[7].x) - ((float) vNoteEndPoint[7].x)) * (100.0f + frame_passed_hand / 2.0f) / 125.0f;
				float h_py = ((float) vNoteGeneratePoint[7].y) + (((float) vNoteEndPoint[7].y) - ((float) vNoteGeneratePoint[7].y)) * (100.0f + frame_passed_hand / 2.0f) / 125.0f;
				t_sHand.setPosition(h_px - t_sHand.getWidth(), h_py);
				t_sHand.setScale(0.8f + frame_passed_hand / 50.0f * 0.4f);
			} else if (t_frameID < 990) {
				float frame_passed = (float) (t_frameID - 630);
				float px = ((float) vNoteGeneratePoint[7].x) + (((float) vNoteEndPoint[7].x) - ((float) vNoteGeneratePoint[7].x)) * frame_passed / 360.0f;
				float py = ((float) vNoteGeneratePoint[7].y) + (((float) vNoteEndPoint[7].y) - ((float) vNoteGeneratePoint[7].y)) * frame_passed / 360.0f;
				t_note_normal.setPosition(px - t_note_normal.getWidth() / 2.0f, py - t_note_normal.getHeight() / 2.0f);
				
				float frame_passed_hand = (float) (t_frameID - 890);
				float h_px = ((float) vNoteEndPoint[7].x) + (((float) vNoteEndPoint[7].x) - ((float) vNoteEndPoint[7].x)) * (100.0f + frame_passed_hand / 2.0f) / 125.0f;
				float h_py = ((float) vNoteGeneratePoint[7].y) + (((float) vNoteEndPoint[7].y) - ((float) vNoteGeneratePoint[7].y)) * (100.0f + frame_passed_hand / 2.0f) / 125.0f;
				t_sHand.setPosition(h_px - t_sHand.getWidth(), h_py);				
				if (t_frameID >= 970) {
					float frame_passed_hand_size = (float) (t_frameID - 970);
					t_sHand.setScale(1.2f - frame_passed_hand_size / 20.0f * 0.5f);
				}
			} else if (t_frameID == 990) {
				t_txtDescription.setText("Prefect!");
				t_txtDescription.setPosition((cameraWidth - t_txtDescription.getWidth()) / 2.0f, (cameraHeight - t_txtDescription.getHeight()) / 10.0f * 6.0f);
			} else if (t_frameID < 1040) {
				float frame_passed = (float) (t_frameID - 990);
				t_note_normal.setAlpha(t_note_normal.getAlpha() - 0.02f);
				t_note_normal.setScale(t_note_normal.getScaleX() + 0.015f);
			} else if (t_frameID == 1080) {
				t_txtDescription.setText("More score will get\nif you click it on time");
				t_txtDescription.setPosition((cameraWidth - t_txtDescription.getWidth()) / 2.0f, (cameraHeight - t_txtDescription.getHeight()) / 10.0f * 6.0f);
			} else if (t_frameID < 1330) {
				
			} else if (t_frameID < 1380) {
				t_sHand.setAlpha(t_sHand.getAlpha() - 0.02f);
			} else if (t_frameID == 1380) {
				t_txtDescription.setText("Enjoy...\nMaru Maru!");
				t_txtDescription.setPosition((cameraWidth - t_txtDescription.getWidth()) / 2.0f, (cameraHeight - t_txtDescription.getHeight()) / 10.0f * 6.0f);
			} else if (t_frameID < 1560) {
				
			} else if (t_frameID <= 1680) { // 120 needed
				float frame_passed = (float) (t_frameID - 1560);
				PointF p = new PointF();
				Float degree = ((frame_passed) / 120.0f) * 360.0f + 180.0f;
				p.x = (float) ((float) vScreenNotePathLength * Math.sin(degree / 180.f * Math.PI)) + vScreenMidPointX;
				p.y = (float) ((float) vScreenNotePathLength * Math.cos(degree / 180.f * Math.PI)) + vScreenMidPointY;
				
				if (frame_passed == 0) {t_drawCirclePrevPoint = p;} else {
					Line l = new Line(t_drawCirclePrevPoint.x, t_drawCirclePrevPoint.y, p.x, p.y, VBOM);
					l.setLineWidth(3.0f);
	            	l.setColor(0.0f, 0.0f, 0.0f);
	            	vScene.attachChild(l);
	            	
	            	t_drawCirclePrevPoint = p;
				}
				t_txtIntroduction.setAlpha(1.0f - frame_passed / 120.0f);
				t_txtDescription.setAlpha(1.0f - frame_passed / 120.0f);
			} else if (t_frameID > 1680 && iGameStartFrameID == 119) {
				startTitleScreen();
			}
			
			if (t_frameID >= 50) {
				iGameStartFrameID++;
			}
		}
		
		txtGameStart.setAlpha((float) Math.sin(((float) iGameStartFrameID) * 1.5f / 180.0f * Math.PI));
		
		if (iGameStartFrameID >= 120) iGameStartFrameID = 0;
	}
	
	//
	//
	// Function 
	//
	//
	
	private void startIntroduction () {
		iDisplayMode = 2;
		t_frameID = 0;
		txtGameStart.setColor(1.0f, 1.0f, 1.0f);
		
	}
	
	private void startTitleScreen () {
		vScene.detachChildren();
		iDisplayMode = 1;
		t_frameID = 0;
		txtGameStart.setColor(1.0f, 1.0f, 1.0f);
		
		iIdleFrameCount = 0;
		iDisplayMode = 1;
		iIdleFrameCount = 0;
		iGameStartFrameID = 0;
		
		Random rnd = new Random();
		int bgid = (int) (Math.floor(rnd.nextDouble() * 2.0f));
		//bgid = 0;
		if (bgid == 0) {
			vScene.setBackground(new Background(0.2f, 0.133f, 0.133f));
			
			vScene.detachChild(spGameBackground);
			spGameBackground.reset();
			spGameBackground = new Sprite(0.0f, 0.0f, txGameBackground, VBOM);
			spGameBackground.setPosition(0.0f, 0.0f);
			spGameBackground.setScaleCenter(0.0f, 0.0f);
			vScene.attachChild(spGameBackground);
			
			vScene.detachChild(spGameLogo);
			spGameLogo.reset();
			spGameLogo = new Sprite(0.0f, 0.0f, txGameLogo, VBOM);
			spGameLogo.setScaleCenter(0.0f, 0.0f);
			vScene.attachChild(spGameLogo);
			
			float scale_percentage = 1.0f;
			scale_percentage = (float) cameraWidth / spGameBackground.getWidth();
			
			
			spGameBackground.setScale(scale_percentage);
			spGameLogo.setScale(scale_percentage);
			
			spGameBackground.setPosition((cameraWidth - spGameBackground.getWidthScaled()) / 2.0f, (cameraHeight - spGameBackground.getHeightScaled()) / 10.0f * -1.0f);
			spGameLogo.setPosition((cameraWidth - spGameLogo.getWidthScaled()) / 2.0f, (cameraHeight - spGameLogo.getHeightScaled()) / 10.0f * 3.5f);
			
		} else if (bgid == 1) {
			vScene.detachChild(spGameBackground);
			spGameBackground.reset();
			spGameBackground = new Sprite(0.0f, 0.0f, txGameBackground2, VBOM);
			spGameBackground.setPosition(0.0f, 0.0f);
			spGameBackground.setScaleCenter(0.5f, 0.5f);
			vScene.attachChild(spGameBackground);
			
			vScene.detachChild(spGameLogo);
			spGameLogo.reset();
			spGameLogo = new Sprite(0.0f, 0.0f, txGameLogo2, VBOM);
			spGameLogo.setScaleCenter(0.0f, 0.0f);
			vScene.attachChild(spGameLogo);
			
			float logo_scale_percentage = 1.0f;
			logo_scale_percentage = (float) cameraWidth / spGameLogo.getWidth();
			
			float bg_scale_percentage = 1.0f;
			bg_scale_percentage = ((float) cameraHeight * 1.2f) / spGameBackground.getHeight();
			
			spGameBackground.setScale(bg_scale_percentage);
			spGameLogo.setScale(logo_scale_percentage);
			
			//Log.w("test", "hs = " + sGameBackground.getHeightScaled() + " h = " + sGameBackground.getHeight());
			//sGameBackground.setPosition(0.0f, sGameBackground.getHeightScaled() - sGameBackground.getHeight());
			
			spGameBackground.setPosition((cameraWidth - spGameBackground.getWidthScaled()) / 2.0f, ((float) cameraHeight / 10.0f * 1.0f) - spGameBackground.getHeightScaled() / 2.0f + spGameLogo.getHeightScaled() / 2.0f);
			spGameLogo.setPosition((cameraWidth - spGameLogo.getWidthScaled()) / 2.0f, ((float) cameraHeight / 10.0f * 1.0f));
			
		}
		
		txtGameStart.setPosition((cameraWidth - txtGameStart.getWidth()) / 2.0f, (cameraHeight - txtGameStart.getHeight()) / 10.0f * 8.4f);
		vScene.attachChild(txtGameStart);
		txtVersion.setPosition((cameraWidth - txtVersion.getWidth()), (cameraHeight - txtGameStart.getHeight()) / 10.0f * 9.2f);
		vScene.attachChild(txtVersion);
		
		t_txtIntroduction.setPosition((cameraWidth - t_txtIntroduction.getWidth()) / 2.0f, (cameraHeight - t_txtIntroduction.getHeight()) / 10.0f * 1.0f);
		t_txtDescription.setPosition((cameraWidth - t_txtDescription.getWidth()) / 2.0f, (cameraHeight - t_txtDescription.getHeight()) / 10.0f * 6.0f);
		vScene.attachChild(t_txtIntroduction);
		vScene.attachChild(t_txtDescription);
		t_txtIntroduction.setAlpha(0.0f);
		t_txtDescription.setAlpha(0.0f);
		t_txtDescription.setText("Listen to the music");
		t_txtDescription.setPosition((cameraWidth - t_txtDescription.getWidth()) / 2.0f, (cameraHeight - t_txtDescription.getHeight()) / 10.0f * 6.0f);
		
		t_note_normal = new Sprite(0.0f, 0.0f, txNoteNormal, VBOM);
		t_note_normal.setAlpha(0.0f);
		//vScene.attachChild(t_note_normal);
		
		t_sHand = new Sprite(0.0f, 0.0f, txHand, VBOM);
		t_sHand.setAlpha(0.0f);
	}
	
	
	// My class
	
	class Circle {
		private VertexBufferObjectManager vbom;
		
		public float radius;
		public PointF position;
		public int circleDensity = 12;
		public Line[] line_list;
				
		Circle () {	vbom = StartupScene.this.getVertexBufferObjectManager(); }//lines = new Line[CIRCLE_DENSITY];
		Circle (float r, PointF p) {
			vbom = StartupScene.this.getVertexBufferObjectManager();
			radius = r;
			position = p;
		}
		
		public void draw (Scene scene) {
			PointF prev_point = new PointF();
			line_list = new Line[circleDensity];
			for (int i = 0; i <= circleDensity; i++) {
				PointF p = new PointF();
				Float degree = ((float) i) / ((float) circleDensity) * 360.0f;
				p.x = (float) (radius * Math.sin(degree / 180.f * Math.PI)) + position.x;
				p.y = (float) (radius * Math.cos(degree / 180.f * Math.PI)) + position.y;
				
				if (i == 0) {prev_point = p; continue;}
				
				Line l = new Line(prev_point.x, prev_point.y, p.x, p.y, vbom);
				l.setLineWidth(3.0f);
            	l.setColor(0.0f, 0.0f, 09.0f);
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