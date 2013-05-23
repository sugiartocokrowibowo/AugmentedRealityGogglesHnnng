package com.tastybug.android.argh;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

/*
 * das hier laesst sich sicher als singleton umsetzen
 */
public class OrientationManager implements LocationListener {
	
	private SensorManager mSensorManager;
	private LocationManager mLocationManager;
	private Location location = getFallbackLocation();
	private Sensor mSensor, mSensor2;

	float[] mValues = new float[]{0f, 0f, 0f};
	float[] accelerometerValues = new float[]{0f, 0f, 0f};
    float[] geomagneticMatrix = new float[]{0f, 0f, 0f};

	// aktuelle blickrichtung, 0 grad gleich nord
	float normalizedOrientation = 0;
	
    
	public OrientationManager (SensorManager sensorManager, final LocationManager locationManager) {

		mSensorManager = sensorManager;
		mLocationManager = locationManager;
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor2 = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        boolean gps_enabled=false;
        boolean network_enabled=false;
        try{gps_enabled=mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

        //don't start listeners if no provider is enabled
        if(!gps_enabled && !network_enabled)
            throw new RuntimeException("Neither GPS nor Network-Location is available.");

        new Thread(){
            public void run(){
            	Looper.prepare();
            	LocationManager mLocation = locationManager;
                mLocation.requestLocationUpdates("gps", 10000L, 2, OrientationManager.this, Looper.myLooper());
                Looper.loop();
            }
        }.start();

	}
	
	public void decouple () {
		mSensorManager.unregisterListener(mListener);
	}
	
	public void couple () {
		mSensorManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mListener, mSensor2, SensorManager.SENSOR_DELAY_NORMAL);
        
	}
	
	public Location getLocation () {
		return this.location;
	}

	public static Location getFallbackLocation () {
		Location fallbackLocation = new Location("debug");
		fallbackLocation.setLongitude(13.406206369400024);
		fallbackLocation.setLatitude(52.5550081980071);
		
		return fallbackLocation;
	}
	
	/**
	 * Lenkraddrehung.
	 * 
	 * @return
	 */
	public float getMValue0 () {
		return mValues[0]; 
	}
	
	/**
	 * Links-/Rechtskippen
	 * 
	 * @return
	 */
	public float getMValue1 () {
		return mValues[1]; 
	}
	

	/**
	 * Vorn-/Hintenkippen.
	 * 
	 * @return
	 */
	public float getMValue2 () {
		return mValues[2]; 
	}
	
	
    public void onLocationChanged(Location location) {
    	this.location = location;
    }
    
    public void onProviderDisabled(String provider) {}
    public void onProviderEnabled(String provider) {}
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    /**
     * Aktuelle Blickrichtung, Kompasssicht auf Nord.
     * 
     * @return
     */
    public float getNormalizedOrientation () {
    	return normalizedOrientation;
    }
    
    public float getNormalizedBearing (Location referenzPunkt) {
    	return location.bearingTo(referenzPunkt) < 0 ? 360+location.bearingTo(referenzPunkt) : location.bearingTo(referenzPunkt);
    }

    public float getRelativeBearing (Location referenzPunkt) {
    	float normalizedBearing = getNormalizedBearing(referenzPunkt);
    	return ((normalizedBearing-normalizedOrientation) > 0 ? 360-(normalizedBearing-normalizedOrientation) : (normalizedOrientation-normalizedBearing));
    }

    public float getBearingDelta (Location referenzPunkt) {
    	float normalizedBearing = getNormalizedBearing(referenzPunkt);
    	return ((normalizedBearing-normalizedOrientation) > 0 ? (normalizedBearing-normalizedOrientation) : -(normalizedOrientation-normalizedBearing));
    }
    
    private final SensorEventListener mListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
           Log.d("ARActivity", "sensorChanged (" + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + ")");
           boolean sensorReady = false;
           switch (event.sensor.getType()) {
               case Sensor.TYPE_ACCELEROMETER:
                   accelerometerValues = event.values.clone();
                   break;
               case Sensor.TYPE_MAGNETIC_FIELD:
                   geomagneticMatrix = event.values.clone();
                   sensorReady = true;
                   break;
               default:
                   break;
           }   

           if (geomagneticMatrix != null && accelerometerValues != null && sensorReady) {
               sensorReady = false;

               float[] R = new float[16];
               float[] I = new float[16];

               SensorManager.getRotationMatrix(R, I, accelerometerValues, geomagneticMatrix);

               float[] actual_orientation = new float[3];
               SensorManager.getOrientation(R, actual_orientation);

               mValues[0] = actual_orientation[0]*57.2957795f;
               mValues[1] = actual_orientation[1]*57.2957795f;
               mValues[2] = actual_orientation[2]*57.2957795f;
               
               normalizedOrientation = mValues[0] < -90 ? (360+90+mValues[0]) : (90+mValues[0]);
           }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
	
}
