package ru.osm.dkiselev.atlasgenerator.util;

public class Point {
	
	private double lon;
	private double lat;
	
	public double getLon() {
		return lon;
	}
	
	public void setLon(double lon) {
		this.lon = lon;
	}
	
	public double getLat() {
		return lat;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public Point(double lon, double lat) {
		super();
		this.lon = lon;
		this.lat = lat;
	}
	 public Point() {
		
	}
}
