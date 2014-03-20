package com.example.arinterface;

import vuforia.LoadingDialogHandler;
import vuforia.SampleApplicationSession;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyGLSurfaceView extends GLSurfaceView {
	
	
	MyRenderer renderer;
    public MyGLSurfaceView(Context context,SampleApplicationSession session, WebView webPreview){
        super(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setEGLContextClientVersion(2);
        renderer = new MyRenderer(context,session,webPreview);
        setRenderer(renderer);

    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent e) {
  
        return true;
    }
}
