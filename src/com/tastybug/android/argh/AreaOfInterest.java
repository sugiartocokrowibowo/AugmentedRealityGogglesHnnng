package com.tastybug.android.argh;

import java.util.ArrayList;
import java.util.List;

public class AreaOfInterest extends NamedLocation {

	private List<PointOfInterest> pois = new ArrayList<PointOfInterest>();
	
	public AreaOfInterest (String name, double longitude, double latitude) {
		super(name, longitude, latitude);
	}

	public synchronized void setPOIs (List<PointOfInterest> pois) {
		this.pois.clear();
		this.pois.addAll(pois);
	}
	
	public synchronized List<PointOfInterest> getPOIs () {
		return pois;
	}
}
