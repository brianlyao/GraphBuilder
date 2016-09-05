package components;

import java.awt.Color;
import java.awt.geom.Point2D;

import context.GraphBuilderContext;

/** An edge whose endpoints are the same node. */
public class SelfEdge extends Edge {

	private static final long serialVersionUID = -8346635669774916440L;
	
	// A self-edge is drawn as a circular arc
	private Point2D.Double arcPoint1;
	private Point2D.Double arcPoint2;
	private Point2D.Double center;
	private double radius;
	
	/**
	 * @param node     The node which is the start and end node of this self-edge.
	 * @param c        The edge's visual color.
	 * @param w        The edge's visual weight (thickness).
	 * @param ctxt     The context (graph) this edge exists in.
	 * @param directed Whether this edge is directed.
	 * @param id       The id this node is assigned.
	 */
	public SelfEdge(Node node, Color c, int w, boolean directed, GraphBuilderContext ctxt, int id) {
		super(node, node, c, w, directed, ctxt, id);
	}
	
	public Point2D.Double getCenter() {
		return center;
	}
	
	public void setCenter(Point2D.Double p) {
		center = p;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setRadius(double r) {
		radius = r;
	}
	
	public String toStorageString() {
		return super.toStorageString();
	}

}
