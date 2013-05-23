package com.tastybug.android.argh;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;

/*
 * geht vermutlich als singleton
 * 
    	// location brandenburger tor
//    	referenzpunkt.setProvider("Br-Tor");
//    	referenzpunkt.setLongitude(13.37778);
//    	referenzpunkt.setLatitude(52.51628);
    	
    	// baecker schoenfliesser/bornholmer
    	referenzpunkt.setProvider("Baecker");
    	referenzpunkt.setLongitude(13.408614993095398);
    	referenzpunkt.setLatitude(52.554143875214);
    	
 */
public class POIManager {

	private Location currentLocation;
	ArrayList<AreaOfInterest> locations = new ArrayList<AreaOfInterest>();
	
	private static POIManager SINGLETON;
	
	private POIManager () {
		AreaOfInterest area = new AreaOfInterest("BornholmerQuadrat", 13.406340479850769, 52.55419279960027);
		locations.add(area);
		
		area.getPOIs().add(new PointOfInterest("RO", 13.406624794006348, 52.55441948187806));
		area.getPOIs().add(new PointOfInterest("RU", 13.406597971916199, 52.5538959908185));
		area.getPOIs().add(new PointOfInterest("LU", 13.405785262584686, 52.55387805176185));
		area.getPOIs().add(new PointOfInterest("LO", 13.40592473745346, 52.554585823524));
	}
	
	public static POIManager getSharedInstance () {
		if (SINGLETON == null)
			SINGLETON = new POIManager();
		
		return SINGLETON;
	}
		
	public List<AreaOfInterest> getAreasPoints () {
		// hier filtern, was ueberhaupt zu sehen is
		// extra filter implementieren, der konfigurierbar is: visibilityfilter
		
		return locations;
	}
	 
	public void setCurrentLocation (Location location) {
		this.currentLocation = location;
	}
}
