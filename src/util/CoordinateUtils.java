package util;

import java.awt.Point;

import ui.GUI;

public class CoordinateUtils {
	
	public static Point closestGridPoint(GUI g, Point from) {
		int level = g.getGridSettingsDialog().getGridLevel();
		
		// Compute the minimum distance from each of the four points
		int lowX = from.x - (from.x % level);
		int lowY = from.y - (from.y % level);
		int highX = lowX + level;
		int highY = lowY + level;
		double dist1 = Point.distance(lowX, lowY, from.x, from.y);
		double dist2 = Point.distance(lowX, highY, from.x, from.y);
		double dist3 = Point.distance(highX, lowY, from.x, from.y);
		double dist4 = Point.distance(highX, highY, from.x, from.y);
		double minDist = Math.min(dist1, Math.min(dist2, Math.min(dist3, dist4)));
		
		// Return the closest point
		Point newPoint;
		if(minDist == dist1)
			newPoint = new Point(lowX, lowY);
		else if(minDist == dist2)
			newPoint = new Point(lowX, highY);
		else if(minDist == dist3)
			newPoint = new Point(highX, lowY);
		else
			newPoint = new Point(highX, highY);
		return newPoint;
	}
	
}
