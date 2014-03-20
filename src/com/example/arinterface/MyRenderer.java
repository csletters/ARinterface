package com.example.arinterface;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.json.JSONException;


import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.TrackableResult;

import shapes.Square;
import textpackage.TextLibrary;
import textpackage.Words;
import twitterRetriever.JsonRetriever;
import vuforia.SampleApplicationSession;


import loaders.RawResourceReader;
import loaders.ShaderHandles;
import loaders.ShaderHelper;
import loaders.TextureHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.opengl.Matrix;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyRenderer implements Renderer {

	int widthView,heightView;
	Context mActivityContext;
	private loadWebBitmap preview;

	float[] projection = new float[16];
	float[] view = new float[16];
	float[] model = new float[16];
	float[] rotMatrix = new float[16];
	float[] mv = new float[16];
	int[] previewTexture;
	int[] defaultTexture;
	int[] texTexture;
	int[] clockBackground;
	int[] frameTexture;
	int[] chromeTexture;
	int[] twitterTexture;
	int maxScroll = 0;
	int scrollPosition = 0;
	
	com.qualcomm.vuforia.Renderer vuforiaRenderer;
	SampleApplicationSession vuforiaAppSession;
	ArrayList<ShaderHandles> shaderPrograms = new ArrayList<ShaderHandles>();
	 WebView webPreview;
	Square square;
	boolean loadingFinished = true;
	boolean redirect = false;
	boolean loadnewPreview = false;
	boolean canScroll = false;
	boolean displayTwitterText = false;
	float ratio;
	float elapsedtime = 0;
	float startTime = 0;
	Bitmap  webpage;
	TextLibrary textCreator;
	Words time;
	Words twitterText;
	JsonRetriever words;
	
	public MyRenderer(final Context activityContext,SampleApplicationSession session, WebView webPreview)
	{
		mActivityContext = activityContext;
		vuforiaAppSession = session;
		this.webPreview = webPreview;
		initWebviewclient();
		webPreview.loadUrl("http://www.gamespot.com/");
		words = new JsonRetriever();
		words.retrieveTwitterData("twitterapi");
		
	}
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		if(previewTexture != null)
			 scrollCounter();
		if(loadnewPreview == true)
		{
			previewTexture = TextureHelper.loadTexture(webpage);
			webpage.recycle();
			webpage =null;
			loadnewPreview = false;
		}
		State state = vuforiaRenderer.begin();
		vuforiaRenderer.drawVideoBackground();
		if (state.getNumTrackableResults() > 0) {
			
			TrackableResult result = state.getTrackableResult(0);
			Trackable trackable = result.getTrackable();
			Matrix44F modelViewMatrix_Vuforia = Tool.convertPose2GLMatrix(result.getPose());
			mv = modelViewMatrix_Vuforia.getData();
			projection = vuforiaAppSession.getProjectionMatrix().getData();
			GLES20.glUseProgram(shaderPrograms.get(0).programHandle);
		
			//make frame
			Matrix.scaleM(mv, 0, 1000.0f, 850.0f, 1.0f);
			Matrix.translateM(mv, 0, -0.5f, -0.5f, -1.0f);
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glUniform1f(GLES20.glGetUniformLocation(shaderPrograms.get(0).programHandle, "fading"),0.6f);
			square.draw(projection, mv, shaderPrograms.get(0),frameTexture[0]);
			GLES20.glUniform1f(GLES20.glGetUniformLocation(shaderPrograms.get(0).programHandle, "fading"),0.0f);
			GLES20.glDisable(GLES20.GL_BLEND);
			mv = modelViewMatrix_Vuforia.getData();
		//make web
	/*
	 * 	old pos and scale
	 * 	Matrix.scaleM(mv, 0, ratio*400.0f, 400.0f, 1.0f);
	 *  Matrix.translateM(mv, 0, 0.0f, 0.0f, 5.0f);
	 */
			Matrix.translateM(mv, 0, 0.0f, 310.0f, 5.0f);
			Matrix.scaleM(mv, 0, 250.0f, 50.0f, 1.0f);
			GLES20.glEnable(GLES20.GL_BLEND);
			square.draw(projection, mv, shaderPrograms.get(0),chromeTexture[0]);
			GLES20.glDisable(GLES20.GL_BLEND);
		
			mv = modelViewMatrix_Vuforia.getData();
			Matrix.scaleM(mv, 0, ratio*700.0f, 600.0f, 1.0f);
			Matrix.translateM(mv, 0, 0.0f, -0.5f, 5.0f);
			if(previewTexture != null)
				square.draw(projection, mv, shaderPrograms.get(0),previewTexture[0]);
			else
				square.draw(projection, mv, shaderPrograms.get(0),defaultTexture[0]);
			
		
		
			//make clock
			updateTime();
		
			mv = modelViewMatrix_Vuforia.getData();
			Matrix.translateM(mv, 0, -400.0f, 300.0f, 5.0f);
			Matrix.scaleM(mv, 0, 50.0f, 50.0f, 1.0f);
			GLES20.glEnable(GLES20.GL_BLEND);
			time.draw(projection, mv, shaderPrograms.get(0), texTexture[0]);
			GLES20.glDisable(GLES20.GL_BLEND);
		
			updateDate();
			mv = modelViewMatrix_Vuforia.getData();
			Matrix.translateM(mv, 0, -400.0f, 250.0f, 5.0f);
			Matrix.scaleM(mv, 0, 25.0f, 25.0f, 1.0f);
			GLES20.glEnable(GLES20.GL_BLEND);
			time.draw(projection, mv, shaderPrograms.get(0), texTexture[0]);
			GLES20.glDisable(GLES20.GL_BLEND);
			
			//draw twitter
			mv = modelViewMatrix_Vuforia.getData();
			Matrix.translateM(mv, 0, -400.0f, 0.0f, 5.0f);
			Matrix.scaleM(mv, 0, 300.0f, 75.0f, 1.0f);
			GLES20.glEnable(GLES20.GL_BLEND);
			square.draw(projection, mv, shaderPrograms.get(0), twitterTexture[0]);
			GLES20.glDisable(GLES20.GL_BLEND);
			
			if(words.isFinished)
			{
				createTwitterText();
				displayTwitterText = true;
				words.isFinished = false;
			}
			
			if(displayTwitterText)
			{
				mv = modelViewMatrix_Vuforia.getData();
				Matrix.translateM(mv, 0, -450.0f, -50.0f, 5.0f);
				Matrix.scaleM(mv, 0, 12.0f, 12.0f, 1.0f);
				GLES20.glEnable(GLES20.GL_BLEND);
				twitterText.draw(projection, mv, shaderPrograms.get(0), texTexture[0]);
				GLES20.glDisable(GLES20.GL_BLEND);
			}
			
			
		}
		vuforiaRenderer.end();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		GLES20.glViewport(0, 0, width, height);
		ratio = (float)width/height;
		Matrix.perspectiveM(projection, 0, 60f, ratio, 0.1f, 600f);
		Matrix.setLookAtM(view, 0, 0.0f, 0.0f, 2, 0, 0.0f, 0, 0, 1, 0);
		Matrix.setIdentityM(model, 0);
		widthView = width;
		heightView = height;
		vuforiaAppSession.onSurfaceChanged(width, height);
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GLES20.glClearDepthf(1.0f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LESS);
		GLES20.glDepthMask(true);
		
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		 
		 vuforiaRenderer = com.qualcomm.vuforia.Renderer.getInstance();
		 vuforiaAppSession.onSurfaceCreated();
		 
		// create shader program for objects with basic textures and lighting
		ShaderHandles shader = new ShaderHandles();
		shader.programHandle = createShader(R.raw.vertex, R.raw.fragment);
		initBasicHandles(shader);
		shaderPrograms.add(shader);
		
		defaultTexture = TextureHelper.loadTexture(mActivityContext,R.drawable.questionmark);
		texTexture = TextureHelper.loadTexture(mActivityContext,R.drawable.text);
		frameTexture = TextureHelper.loadTexture(mActivityContext,R.drawable.screen);
		chromeTexture = TextureHelper.loadTexture(mActivityContext,R.drawable.chrome);
		twitterTexture = TextureHelper.loadTexture(mActivityContext,R.drawable.twitter);
		
		square = new Square();
		textCreator = new TextLibrary();
	}
	
	
	public int createShader(int vertex, int fragment) {
		String vertexShaderCode = RawResourceReader.readTextFileFromRawResource(mActivityContext, vertex);
		String fragmentShaderCode = RawResourceReader.readTextFileFromRawResource(mActivityContext, fragment);

		int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		int mProgram;

		mProgram = ShaderHelper.createAndLinkProgram(vertexShaderHandle,fragmentShaderHandle);

		return mProgram;
	}

	public void initBasicHandles( ShaderHandles shader) {
		// attributes
		shader.positionHandle = GLES20.glGetAttribLocation(shader.programHandle,"aPosition");
		shader.mTextureCoordinateHandle = GLES20.glGetAttribLocation(shader.programHandle, "aTexCord");

		// uniforms
		shader.mvHandle = GLES20.glGetUniformLocation(shader.programHandle, "mv");
		shader.projectionHandle = GLES20.glGetUniformLocation(shader.programHandle,	"projection");
		shader.mTextureUniformHandle.add(GLES20.glGetUniformLocation(shader.programHandle, "uTexture"));
	}
	
	
	public void initWebviewclient()
	{
		//create on page loaded rules
		webPreview.setWebViewClient(new WebViewClient()
		{

			@Override
			   public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
			       if (!loadingFinished) {
			          redirect = true;
			       }
			   loadingFinished = false;
			   view.loadUrl(urlNewString);
			   return true;
			   }

			   @Override
			   public void onPageStarted(WebView view, String url,Bitmap favicon) {
			        loadingFinished = false;
			    }

			   @Override
			   public void onPageFinished(WebView view, String url) {
			       if(!redirect){
			          loadingFinished = true;
			       }

			       if(loadingFinished && !redirect){
			    	   Log.w("adsdas","asdsada");
			    	   preview =  (loadWebBitmap) new loadWebBitmap().execute(view.getScrollY());
			       } else{
			          redirect = false; 
			       }

			    }

			});
			/*opens the webpage  in an invisible webview and generates a bitmap.
			 * This method of getting a bitmap requires the user to download the webpage even if the user
			 * only wants to see metadata on a service.
			 */

	}
	
	//Due to android threading issues .5 sec wait is required for preview image
	public class loadWebBitmap extends AsyncTask<Integer, Void, Integer>
	{

		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				 Thread.sleep(500);
		    	 Bitmap  b = Bitmap.createBitmap(webPreview.getWidth(), webPreview.getHeight(), Bitmap.Config.ARGB_4444);
		    	 Canvas c = new Canvas( b );
		    	 //thread safety stuff
		    	 webPreview.draw( c );
		    	 webpage  = b.copy(b.getConfig(), false);
		    	 loadnewPreview = true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}
	
	public void updateTime()
	{
		String month = "";
		Time now = new Time();
		now.setToNow();
		textCreator.newWord();
		textCreator.addWords(Integer.toString(now.hour)+":"+Integer.toString(now.minute)+":"+Integer.toString(now.second));
		time = textCreator.createText();
	}
	
	public void updateDate()
	{
		String month = "";
		String day = "";
		Time now = new Time();
		now.setToNow();
		if(now.month == 0)
			month = "January";
		else if(now.month == 1)
			month = "February";
		else if(now.month == 2)
			month = "March";
		else if(now.month == 3)
			month = "April";
		else if(now.month == 4)
			month = "May";
		else if(now.month == 5)
			month = "June";
		else if(now.month == 6)
			month = "July";
		else if(now.month == 7)
			month = "August";
		else if(now.month == 8)
			month = "September";
		else if(now.month == 0)
			month = "October";
		else if(now.month == 10)
			month = "Novemeber";
		else
			month = "December";
		
		if(now.weekDay == 0)
			day = "Monday";
		else if(now.weekDay == 2)
			day = "Tuesday";
		else if(now.weekDay == 3)
			day = "Wednesday";
		else if(now.weekDay == 4)
			day = "Thursday";
		else if(now.weekDay == 5)
			day = "Friday";
		else if(now.weekDay == 6)
			day = "Saturday";
		else
			day = "Sunday";
		
		
		textCreator.newWord();
		textCreator.addWords(month+" "+Integer.toString(now.year));
		textCreator.addWords(day+" "+ Integer.toString(now.monthDay));
		time = textCreator.createText();
	}
	
	//timer that activates 0.5 sec after the user stops scrolling+
	public void scrollCounter() {
		float diff = ((System.nanoTime() - startTime) / 1000000000.0f);
		startTime = System.nanoTime();
		elapsedtime += diff;
		if (elapsedtime > 0.5) {
			elapsedtime = 0;
			if(canScroll == true)
			{
				/*webPreview.scrollTo(0, scrollPosition);
				Bitmap  b = Bitmap.createBitmap(webPreview.getWidth(), webPreview.getHeight(), Bitmap.Config.ARGB_4444);
				webPreview.setDrawingCacheEnabled(true);
		    	b = Bitmap.createBitmap(webPreview.getDrawingCache());
		    	webPreview.setDrawingCacheEnabled(false);
		    	//webpage  = b.copy(b.getConfig(), false);
		    	scrollPosition += 100;
		    	canScroll = webPreview.canScrollVertically(1);*/
		    	//new scrolling().execute(webPreview);
			}

		}

	}
	
	public void createTwitterText()
	{
		try {
			String sentence = words.articles.getJSONObject(0).getString("text");
			String[] tokens = sentence.split("[ ]+");
			textCreator.newWord();
			int count = 0;
			for(int x = 0;x+4 <= tokens.length-1;x+=4)
			{
				textCreator.addWords(tokens[x]+" "+tokens[x+1]+" "+tokens[x+2]+" "+tokens[x+3]+" ");
				count +=4;
			}
			String stringEnd = "";
			for(int x=count; x<tokens.length; x++ )
			{
				stringEnd += tokens[x]+" ";
			}
			textCreator.addWords(stringEnd);
			
			
			twitterText = textCreator.createText();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
