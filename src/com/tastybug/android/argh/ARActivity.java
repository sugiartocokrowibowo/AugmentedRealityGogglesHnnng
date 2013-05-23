package com.tastybug.android.argh;

import java.io.IOException;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;


public class ARActivity extends Activity {
	
	OrientationManager orientation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
		
        orientation = new OrientationManager((SensorManager)getSystemService(Context.SENSOR_SERVICE),
        									 (LocationManager)getSystemService(Context.LOCATION_SERVICE));
        getSystemService("");
        
        CamView camView = new CamView(this);
        CompassView compassView = new CompassView(this, orientation);
        LocationMarkerView locationMarkerView = new LocationMarkerView(this, orientation);
        MediaView mediaView = new MediaView (this);

        setContentView(camView);
        addContentView(compassView, new LayoutParams (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addContentView(locationMarkerView, new LayoutParams (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addContentView(mediaView, new LayoutParams (LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
    }

    @Override
    protected void onResume()
    {
        Log.d("ARActivity", "onResume");
        super.onResume();
        orientation.couple();
    }

    @Override
    protected void onStop()
    {
        Log.d("ARActivity", "onStop");
        orientation.decouple();
        super.onStop();
    }

	class CamView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
	    SurfaceHolder mHolder;
	    Camera mCamera;
	    
	    CamView(Context context) {
	        super(context);
	        
	        // Install a SurfaceHolder.Callback so we get notified when  the
	        // underlying surface is created and destroyed.
	        mHolder = getHolder();
	        mHolder.addCallback(this);
	        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    }
	    
	    public void surfaceCreated(SurfaceHolder holder) {
	        // The Surface has been created, acquire the camera and tell it where
	        // to draw.
	    	
	    	if (mCamera == null) {
	    		mCamera = Camera.open();
	    		
	            try {
	            	mCamera.setPreviewDisplay(holder);
	
	                // TODO test how much setPreviewCallbackWithBuffer is faster
	            	mCamera.setPreviewCallback(this);
	            } catch (IOException e) {
	            	mCamera.release();
	            	mCamera = null;
	            }
	        }
	    }
	
	    public void surfaceDestroyed(SurfaceHolder holder) {
	        // Surface will be destroyed when we return, so stop the preview.
	        // Because the CameraDevice object is not a shared resource,  it's very
	        // important to release it when the activity is paused.
	    	if (mCamera != null) {
	    		mCamera.stopPreview();
	    		mCamera.setPreviewCallback(null);
	    		mCamera.release();
	    		mCamera = null;
	        }
	    }
	
	    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	        // Now that the size is known, set up the camera parameters and begin
	        // the preview.
	        Camera.Parameters parameters = mCamera.getParameters();
	        parameters.setPreviewSize(w, h);
	        mCamera.setParameters(parameters);
	        mCamera.startPreview();
	    }
	
		public void onPreviewFrame(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
		}
	}
}