package components;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import context.GraphBuilderContext;

/** An edge whose endpoints are two distinct nodes. */
public class SimpleEdge extends Edge {

	private static final long serialVersionUID = 8112193022247906350L;
	
	public static final int LINEAR = 0;
	public static final int CURVED = 1;
	
	// The edge is a quadratic bezier curve OR a linear bezier (in the latter case, control is null)
	private Point2D.Double p1;
	private Point2D.Double p2;
	private Point2D.Double control;
	
	private int type;
	
	/**
	 * @param n1       The first node endpoint.
	 * @param n2       The second node endpoint.
	 * @param c        The edge's color.
	 * @param w        The edge's weight.
	 * @param directed Whether this edge is directed.
	 * @param type     The type of the simple edge (linear or curved).
	 * @param ctxt     The context (graph) this edge exists in.
	 * 
	 */
	public SimpleEdge(Node n1, Node n2, Color c, int w, boolean directed, int type, GraphBuilderContext ctxt, int id) {
		super(n1, n2, c, w, directed, ctxt, id);
		this.type = type;
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
	
	/** Get the object for the quadratic bezier. */
	public QuadCurve2D.Double getCurve() {
		QuadCurve2D.Double curve = new QuadCurve2D.Double();
		curve.setCurve(p1, control, p2);
		return curve;
	}
	
	public Point2D.Double[] getBezierPoints() {
		return new Point2D.Double[] {p1, control, p2};
	}
	
	public int getSimpleEdgeType() {
		return type;
	}
	
	public void setSimpleEdgeType(int newType) {
		type = newType;
	}
	
	public String toStorageString() {
		return super.toStorageString() + "," + type;
	}

}
