package com.tastybug.android.argh;

import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

import com.tastybug.android.test.R;

public class LocationMarkerView extends View implements OnTouchListener {
	
	Display display;
	Drawable myImage;
	private OrientationManager orientation;
    private Path    mPath = new Path();

    private float touchX = 0, touchY = 0;
    
    public LocationMarkerView(Context context, OrientationManager orientation) {
    	super(context);
    	this.orientation = orientation;
    	display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    	Resources res = context.getResources();
        myImage = res.getDrawable(R.drawable.flag);

        setOnTouchListener(this);
        mPath.moveTo(0, -50);
        mPath.lineTo(-20, 60);
        mPath.lineTo(0, 50);
        mPath.lineTo(20, 60);
        mPath.close();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {

        
        int k = 0;
        for (AreaOfInterest area : POIManager.getSharedInstance().getAreasPoints()) {
        	drawMarker(canvas, area, k++);
        	
        	for (PointOfInterest poi : area.getPOIs()) {
        		drawMarker(canvas, poi, k++);
        	}
        }
        drawPointer(canvas, POIManager.getSharedInstance().getAreasPoints().get(0));
        
        super.onDraw(canvas);
    }

    private void drawMarker (Canvas canvas, NamedLocation namedLocation, int offset) {
    	canvas.translate(0,0);
    	
    	Paint touchDotPaint = new Paint();
    	touchDotPaint.setStyle(Paint.Style.FILL);
    	touchDotPaint.setColor(Color.BLUE);
    	
    	Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setTextSize(14);
        
        float bearing = orientation.getNormalizedBearing(namedLocation);
        float relBearing = orientation.getRelativeBearing(namedLocation);
    	float bearingDelta = orientation.getBearingDelta(namedLocation);

    	if (touchX != 0 || touchY != 0)
    		canvas.drawCircle(touchX, touchY, 10, touchDotPaint);
    	
    	canvas.drawText(namedLocation.getName() + " " + orientation.getLocation().distanceTo(namedLocation) + "(m), bear: " + bearing + ", rel bear: " + relBearing + ", delta bear: " + bearingDelta, 
    					50, (100+(offset*20)), paint);
    	
    	if (bearingDelta > -50 && bearingDelta < 50) {
    		int screenRadius = display.getWidth()/2;
    		int radWidth = screenRadius/50;
    		int displayCenter = display.getWidth()/2;
    		
    		int centerX = displayCenter + (int)bearingDelta*radWidth;
    		int centerY = display.getHeight()/2;
    		
    		canvas.translate(centerX,centerY);
    		myImage.setBounds(0 , 
					  		  0, 
					  		  myImage.getMinimumWidth(), 
					  		  myImage.getMinimumHeight());
    		
    		Paint mini = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.RED);
            paint.setTextSize(10);
            
    		canvas.drawText(namedLocation.getName(), 0, (myImage.getMinimumHeight())+20, mini);
    		canvas.drawText((int)orientation.getLocation().distanceTo(namedLocation) + "(m)", 0, (myImage.getMinimumHeight())+40, mini);
        	
    		
    		myImage.draw(canvas);
    		canvas.translate(-centerX,-centerY);
    	}

        super.onDraw(canvas);
    }

	public boolean onTouch(View arg0, MotionEvent arg1) {
		this.touchX = arg1.getX();
		this.touchY = arg1.getY();
		return false;
	}
    
    private void drawPointer (Canvas canvas, NamedLocation pointOfReference) {
    	canvas.translate(0,0);
    	
    	Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setTextSize(14);

        Location location = orientation.getLocation();
    	
    	canvas.drawText("Long:" + Location.convert(location.getLongitude(), Location.FORMAT_SECONDS) + " Lati:"
    			+ Location.convert(location.getLatitude(), Location.FORMAT_SECONDS) + " Alt:"
    			+ location.getAltitude() + " Acc:"
    			+ location.getAccuracy() + " Who:"
    			+ location.getProvider() + " When:"
    			+ new Date(location.getTime()).toLocaleString()
    			, 50, 60, paint);
    	
    	paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

        int w = canvas.getWidth();
        int h = canvas.getHeight();
        int cx = w / 2;
        int cy = h / 2;

        canvas.translate(cx, cy);
        canvas.rotate(-(orientation.getRelativeBearing(pointOfReference)));
        canvas.drawPath(mPath, paint);
    }
}
