package ru.osm.dkiselev.atlasgenerator.util;

import static java.lang.Math.cos;
import static java.lang.Math.toRadians;

import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


public class Calculator {
	
	private static double METERS_IN_DEGREE = 111111;
	private static double POLE = 20037508.34;;
	
	public static Point addOffset(Point p, double de, double dn)
	{
		//Position, decimal degrees
		double lat = p.getLat();
		double lon = p.getLon();

		return new Point(lon + de / (METERS_IN_DEGREE * cos(toRadians(lat))), lat + dn / METERS_IN_DEGREE);
	}
	
	public static double getArea(Area a) { 
		ArrayList<Point2D.Double> pointList = new ArrayList<Point2D.Double>();
	    double[] coords = new double[6];
	    int type;
	    PathIterator it = a.getPathIterator(null);
	    while (!it.isDone()) {
	        type = it.currentSegment(coords);
	        if (type == PathIterator.SEG_MOVETO) {
	            pointList.clear();
	            pointList.add(new Point2D.Double(coords[0], coords[1]));
	        } else if (type == PathIterator.SEG_LINETO) {
	            pointList.add(new Point2D.Double(coords[0], coords[1]));
	        } else if (type == PathIterator.SEG_CLOSE) {
	        	pointList.add(pointList.get(0));
	        }
	        it.next();
	    }
	    return Math.abs(polyArea(pointList));
	}

	private static double polyArea(ArrayList<Double> pointList) {
		double sum = 0;
		for (int i = 0; i < pointList.size() -1; i++)
		{
		    sum += pointList.get(i).x * pointList.get(i+1).y - pointList.get(i).y * pointList.get(i + 1).x;
		}
		return sum / 2;
	}

	public static Point getCenterPoint(BBOX b) {
		return 
			new Point(
				Math.min(b.getLeft(), b.getRight()) + Math.abs(b.getLeft() - b.getRight()) / 2, 
				Math.min(b.getTop(), b.getBottom()) + Math.abs(b.getTop() - b.getBottom()) / 2);
	}

	public static Point getCenterPoint(Area area) {
		Rectangle2D b = area.getBounds2D();
		return 	new Point(b.getCenterX(), b.getCenterY());
	}
	
	public static Point forwardMercator(Point p) {
		
		double x = p.getLon() * POLE / 180;
		double y = Math.log(Math.tan((90 + p.getLat()) * Math.PI / 360)) / Math.PI * POLE;
	    y = Math.max(-20037508.34, Math.min(y, 20037508.34));
	    
	    return new Point(x, y);
	}

	public static BBOX forwardMercator(BBOX bbox) {
		
		Point p1 = forwardMercator(new Point(bbox.getLeft(), bbox.getBottom()));
		Point p2 = forwardMercator(new Point(bbox.getRight(), bbox.getTop()));
		
		return new BBOX(p2.getLat(), p1.getLat(), p1.getLon(), p2.getLon());
	}
}
