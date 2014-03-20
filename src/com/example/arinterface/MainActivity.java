package com.example.arinterface;

import java.util.ArrayList;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.DataSet;
import com.qualcomm.vuforia.ImageTracker;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import vuforia.LoadingDialogHandler;
import vuforia.SampleApplicationControl;
import vuforia.SampleApplicationException;
import vuforia.SampleApplicationSession;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;

public class MainActivity extends Activity implements SampleApplicationControl {

	private GLSurfaceView mGLView;
	SampleApplicationSession vuforiaAppSession;
	private ArrayList<String> mDatasetStrings = new ArrayList<String>();
    private RelativeLayout mUILayout;
    LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
    private static final String LOGTAG = "ImageTargets";
    private boolean mContAutofocus = false;
    private DataSet mCurrentDataset;
    private int mCurrentDatasetSelectionIndex = 0;
    private boolean mExtendedTracking = false;
    private boolean mSwitchDatasetAsap = false;
    boolean mIsDroidDevice = false;
	WebView webPreview;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        vuforiaAppSession = new SampleApplicationSession(this);
        
        startLoadingAnimation();
        webPreview = new WebView(this);
        webPreview.setVisibility(View.INVISIBLE);
        addContentView(webPreview, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        mDatasetStrings.add("StonesAndChips.xml");
        mDatasetStrings.add("Tarmac.xml");
        vuforiaAppSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mGLView = new MyGLSurfaceView(this,vuforiaAppSession,webPreview);
        mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith("droid");
		
	}
    // Called when the activity will start interacting with the user.
    @Override
    protected void onResume()
    {
        Log.d(LOGTAG, "onResume");
        super.onResume();
        
        // This is needed for some Droid devices to force portrait
        if (mIsDroidDevice)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        
        try
        {
            vuforiaAppSession.resumeAR();
        } catch (SampleApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }
        
        // Resume the GL view:
        if (mGLView != null)
        {
        	mGLView.setVisibility(View.VISIBLE);
        	mGLView.onResume();
        }
        
    }
	
	@Override
	public boolean doInitTrackers() {
        // Indicate if the trackers were initialized correctly
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        Tracker tracker;
        
        // Trying to initialize the image tracker
        tracker = tManager.initTracker(ImageTracker.getClassType());
        if (tracker == null)
        {
            Log.e(
                LOGTAG,
                "Tracker not initialized. Tracker already initialized or the camera is already started");
            result = false;
        } else
        {
            Log.i(LOGTAG, "Tracker successfully initialized");
        }
        return result;
	}
	@Override
	public boolean doLoadTrackersData() {
		TrackerManager tManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) tManager
            .getTracker(ImageTracker.getClassType());
        if (imageTracker == null)
            return false;
        
        if (mCurrentDataset == null)
            mCurrentDataset = imageTracker.createDataSet();
        
        if (mCurrentDataset == null)
            return false;
        
        if (!mCurrentDataset.load(
            mDatasetStrings.get(mCurrentDatasetSelectionIndex),
            DataSet.STORAGE_TYPE.STORAGE_APPRESOURCE))
            return false;
        
        if (!imageTracker.activateDataSet(mCurrentDataset))
            return false;
        
        int numTrackables = mCurrentDataset.getNumTrackables();
        for (int count = 0; count < numTrackables; count++)
        {
            Trackable trackable = mCurrentDataset.getTrackable(count);
            if(isExtendedTrackingActive())
            {
                trackable.startExtendedTracking();
            }

            String name = "Current Dataset : " + trackable.getName();
            trackable.setUserData(name);
            Log.d(LOGTAG, "UserData:Set the following user data "
                + (String) trackable.getUserData());
        }
        
        return true;
	}
	@Override
	public boolean doStartTrackers() {
		// Indicate if the trackers were started correctly
        boolean result = true;
        
        Tracker imageTracker = TrackerManager.getInstance().getTracker(
            ImageTracker.getClassType());
        if (imageTracker != null)
            imageTracker.start();
        
        return result;
	}
	@Override
	public boolean doStopTrackers() {
        // Indicate if the trackers were stopped correctly
        boolean result = true;
        
        Tracker imageTracker = TrackerManager.getInstance().getTracker(
            ImageTracker.getClassType());
        if (imageTracker != null)
            imageTracker.stop();
        
        return result;
	}
	@Override
	public boolean doUnloadTrackersData() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean doDeinitTrackers() {
		 // Indicate if the trackers were deinitialized correctly
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ImageTracker.getClassType());
        
        return result;
	}
	@Override
	public void onInitARDone(SampleApplicationException exception) {
		// TODO Auto-generated method stub
		 if (exception == null)
	        {
	            
	            // Now add the GL surface view. It is important
	            // that the OpenGL ES surface view gets added
	            // BEFORE the camera is started and video
	            // background is configured.
	            addContentView(mGLView, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
	            
	            // Sets the UILayout to be drawn in front of the camera
	            mUILayout.bringToFront();
	            
	            // Sets the layout background to transparent
	            mUILayout.setBackgroundColor(Color.TRANSPARENT);
	            
	            try
	            {
	                vuforiaAppSession.startAR(CameraDevice.CAMERA.CAMERA_DEFAULT);
	            } catch (SampleApplicationException e)
	            {
	                Log.e(LOGTAG, e.getString());
	            }
	            
	            boolean result = CameraDevice.getInstance().setFocusMode(
	                CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
	            
	            if (result)
	                mContAutofocus = true;
	            else
	                Log.e(LOGTAG, "Unable to enable continuous autofocus");
	            
	            /*mSampleAppMenu = new SampleAppMenu(this, this, "Image Targets",
	                mGlView, mUILayout, null);
	            setSampleAppMenuSettings();*/
	            
	        } else
	        {
	            Log.e(LOGTAG, exception.getString());
	            finish();
	        }
	}
	
	@Override
	public void onQCARUpdate(State state) {
		if (mSwitchDatasetAsap)
        {
            mSwitchDatasetAsap = false;
            TrackerManager tm = TrackerManager.getInstance();
            ImageTracker it = (ImageTracker) tm.getTracker(ImageTracker
                .getClassType());
            if (it == null || mCurrentDataset == null
                || it.getActiveDataSet() == null)
            {
                Log.d(LOGTAG, "Failed to swap datasets");
                return;
            }
            
            doUnloadTrackersData();
            doLoadTrackersData();
        }
	}
	
    private void startLoadingAnimation()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        mUILayout = (RelativeLayout) inflater.inflate(R.layout.camera_overlay,
            null, false);
        
        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);
        

        
        // Adds the inflated layout to the view
        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        
    }
    
    boolean isExtendedTrackingActive()
    {
        return mExtendedTracking;
    }
    

}
