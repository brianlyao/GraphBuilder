package util;

import graph.components.gb.GBNode;
import ui.GBFrame;

import java.awt.*;
import java.util.Collection;

/**
 * A utility class with coordinate-related procedures.
 *
 * @author Brian
 */
public class CoordinateUtils {

	/**
	 * Returns the closest point on the grid from the given point.
	 *
	 * @param g    The GBFrame with the grid in question.
	 * @param from The point we want to compute the closest grid point from.
	 * @return The point corresponding to the point on the grid closest to "from".
	 */
	public static Point closestGridPoint(GBFrame g, Point from) {
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
		if (minDist == dist1) {
			newPoint = new Point(lowX, lowY);
		} else if (minDist == dist2) {
			newPoint = new Point(lowX, highY);
		} else if (minDist == dist3) {
			newPoint = new Point(highX, lowY);
		} else {
			newPoint = new Point(highX, highY);
		}

		return newPoint;
	}

	/**
	 * Enforce the boundaries of the workspace on the point, so it remains "in bounds" as specified by the parameters.
	 *
	 * @param test        The point we want to test for in/out of bounds.
	 * @param lowerBoundX The lower bound of the X coordinate.
	 * @param upperBoundX The upper bound of the X coordinate.
	 * @param lowerBoundY The lower bound of the Y coordinate.
	 * @param upperBoundY The upper bound of the Y coordinate.
	 */
	public static void enforceBoundaries(Point test, int lowerBoundX, int upperBoundX, int lowerBoundY, int upperBoundY) {
		test.x = Math.max(test.x, lowerBoundX);
		test.x = Math.min(test.x, upperBoundX);
		test.y = Math.max(test.y, lowerBoundY);
		test.y = Math.min(test.y, upperBoundY);
	}

	/**
	 * Find the "center of mass" of a collection of nodes, treating all nodes
	 * as having equal mass.
	 *
	 * @param nodes The collection of nodes.
	 * @return The point at the center of mass.
	 */
	public static Point centerOfMass(Collection<GBNode> nodes) {
		int xSum = 0;
		int ySum = 0;
		for (GBNode node : nodes) {
			Point center = node.getPanel().getCenter();
			xSum += center.x;
			ySum += center.y;
		}
		return new Point(xSum / nodes.size(), ySum / nodes.size());
	}

}
