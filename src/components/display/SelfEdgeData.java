package components.display;

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * An instance contains the data for drawing a self-edge on the editor panel.
 * 
 * @author Brian
 */
public class SelfEdgeData extends EdgeData {
	
	// A self-edge is drawn as a circular arc
//	private Point2D.Double arcPoint1;
//	private Point2D.Double arcPoint2;
	private Point2D.Double arcCenter;
	private double radius;
	private double offsetAngle;
	
	/**
	 * Copy constructor.
	 * 
	 * @param sed The self edge data object to be copied.
	 */
	public SelfEdgeData(SelfEdgeData sed) {
		super(sed);
//		arcPoint1 = new Point2D.Double();
//		arcPoint2 = new Point2D.Double();
		arcCenter = new Point2D.Double();
		radius = sed.radius;
		offsetAngle = sed.offsetAngle;
	}
	
	/**
	 * Instantiate a data object for a self-edge. Should only be invoked by the SelfEdge constructor.
	 * 
	 * @param c           The edge's visual color.
	 * @param w           The edge's visual weight (thickness).
	 * @param txt         The text associated with this edge.
	 * @param offsetAngle The visual angle at which the self-edge is positioned (relative to 0, which is on the right).
	 */
	public SelfEdgeData(Color c, int w, String txt, double offsetAngle) {
		super(c, w, txt);
		this.offsetAngle = offsetAngle;
	}
	
	public Point2D.Double getArcCenter() {
		return arcCenter;
	}
	
	public void setArcCenter(Point2D.Double p) {
		arcCenter = p;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setRadius(double r) {
		radius = r;
	}
	
	public double getOffsetAngle() {
		return offsetAngle;
	}
	
}
