package com.aroundme.api.common;

import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class AroundMeServerUtils {

	public static Key getKeyFromString(String kind, String keyValue) {
		return KeyFactory.createKey(kind, keyValue);
	}

	public static double distance(GeoPt loc1, GeoPt loc2, char unit) {
		if (loc1 == null || loc2 == null)
			return 99999999;
		return distance(loc1.getLatitude(), loc1.getLongitude(),
				loc2.getLatitude(), loc2.getLongitude(), unit);
	}

	public static double distance(float lat1, float lon1, float lat2,
			float lon2, char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		return (dist);
	}

	public static boolean isSameLocation(GeoPt loc1, GeoPt loc2) {
		if (loc1 == null && loc2 == null)
			return true;
		if (loc1 == null || loc2 == null)
			return false;
		double distance = (distance(loc1, loc2, 'k') * 1000);
		return distance < 300;

	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts decimal degrees to radians : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts radians to decimal degrees : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

}