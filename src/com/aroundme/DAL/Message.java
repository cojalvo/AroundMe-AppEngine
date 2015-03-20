package com.aroundme.DAL;

import java.util.Date;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Message {
	@Id
	Long id;
	String content;
	String from;
	String to;
	Date timestamp;
	boolean downloaded;
	GeoPt location;
	int readRadius;

	public boolean isDownloaded() {
		return downloaded;
	}

	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}

	public GeoPt getLocation() {
		return location;
	}

	public void setLocation(GeoPt lcoation) {
		this.location = lcoation;
	}

	public int getReadRadius() {
		return readRadius;
	}

	public void setReadRadius(int readRadius) {
		this.readRadius = readRadius;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContnet() {
		return content;
	}

	public void setContnet(String contnet) {
		this.content = contnet;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
