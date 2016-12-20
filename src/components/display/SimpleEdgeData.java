package components.display;

import java.awt.Color;
import java.awt.geom.Point2D;

/** An instance contains the data for drawing a simple edge on the editor panel. */
public class SimpleEdgeData extends EdgeData {
	
	// The edge is a quadratic bezier curve OR a linear bezier (in the latter case, control is null)
	private Point2D.Double p1;
	private Point2D.Double p2;
	private Point2D.Double control;
	
	/**
	 * Copy constructor.
	 * 
	 * @param sed The simple edge data object to copy.
	 */
	public SimpleEdgeData(SimpleEdgeData sed) {
		super(sed);
		p1 = new Point2D.Double();
		p2 = new Point2D.Double();
		control = new Point2D.Double();
		p1.setLocation(sed.p1);
		p2.setLocation(sed.p2);
		control.setLocation(sed.control);
	}
	
	/**
	 * Instantiate a data object for a simple edge. Should only be invoked by the SimpleEdge constructor.
	 * 
	 * @param simpleEdge The simple edge this data describes.
	 * @param c          The edge's visual color.
	 * @param w          The edge's visual weight (thickness).
	 */
	public SimpleEdgeData(Color c, int w, String txt) {
		super(c, w, txt);
		
		p1 = new Point2D.Double();
		p2 = new Point2D.Double();
		control = new Point2D.Double();
	}
	
	/** 
	 * Set the three points necessary for a bezier curve.
	 * 
	 * @param startX The x-coordinate of the point at t = 0.
	 * @param startY The y-coordinate of the point at t = 0.
	 * @param contrX The x-coordinate of the control point.
	 * @param contrY The y-coordinate of the control point.
	 * @param endX   The x-coordinate of the point at t = 1.
	 * @param endY   The y-coordinate of the point at t = 1.
	 */
	public void setPoints(double startX, double startY, double contrX, double contrY, double endX, double endY) {
		p1.setLocation(startX, startY);
		p2.setLocation(endX, endY);
		control.setLocation(contrX, contrY);
	}
	
	/**
	 * Get the bezier control points for this curve.
	 * 
	 * @return An array containing the start, control, and end points, respectively.
	 */
	public Point2D.Double[] getBezierPoints() {
		return new Point2D.Double[] {p1, control, p2};
	}

	
}
