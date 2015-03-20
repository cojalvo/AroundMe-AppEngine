package com.aroundme.api.common;

import java.util.Date;

import com.google.appengine.api.datastore.GeoPt;

public class AroundMeItem 
{
	GeoPt location;
	Date lastSeen;
	public GeoPt getLocation() {
		return location;
	}
	public void setLocation(GeoPt location) {
		this.location = location;
	}
	public Date getLastSeen() {
		return lastSeen;
	}
	public void setLastSeen(Date tiemstamp) {
		this.lastSeen = tiemstamp;
	}
	
}
