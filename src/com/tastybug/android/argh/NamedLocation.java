package com.tastybug.android.argh;

import android.location.Location;

public abstract class NamedLocation extends Location implements MediaSubject {

	protected String name;
	
	public NamedLocation (String name, double longitude, double latitude) {
		super("database");
		setLongitude(longitude);
		setLatitude(latitude);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
