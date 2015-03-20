package com.aroundme.DAL;

import java.util.Date;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class UserLocation {
	@Id
	Long id;
	GeoPt point;
	Date timeStamp;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public GeoPt getPoint() {
		return point;
	}

	public void setPoint(GeoPt point) {
		this.point = point;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

}
