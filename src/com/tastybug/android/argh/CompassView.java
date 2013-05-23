package com.tastybug.android.argh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class CompassView extends View {
	
	private Paint   mPaint = new Paint();
    private Path    mPath = new Path();
    private OrientationManager orientation;
    
    public CompassView (Context context, OrientationManager orientation) {
    	super(context);
    	this.orientation = orientation;
    	
    	// Construct a wedge-shaped path
        mPath.moveTo(0, -50);
        mPath.lineTo(-20, 60);
        mPath.lineTo(0, 50);
        mPath.lineTo(20, 60);
        mPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	invalidate();
    	
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setTextSize(14);
    	canvas.drawText("Kompass: " + orientation.getMValue0() + "(norm: " + orientation.getNormalizedOrientation() + ") L/R:" + orientation.getMValue1() + " V/H:" + orientation.getMValue2(), 50, 20, paint);
    	
    	paint = mPaint;

        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);

        int w = canvas.getWidth();
        int h = canvas.getHeight();
        int cx = w / 2;
        int cy = h / 2;

        canvas.translate(cx, cy);
        
        canvas.rotate(-(orientation.getNormalizedOrientation()));
        canvas.drawPath(mPath, mPaint);
    	
        super.onDraw(canvas);
    }

}