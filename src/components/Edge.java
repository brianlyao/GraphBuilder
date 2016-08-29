package components;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import uielements.Editor;

/** An instance is an edge of the graph. Visually represented by a line, quadratic bezier curve, or loop. */
public class Edge extends GraphComponent {
	
	private static final long serialVersionUID = -6840630886505529490L;
	
	public static final int LINE = 0;
	public static final int CURVE = 1;
	public static final int LOOP = 2;
	
	private static final int LINE_THICKNESS = 2; // Initial value of edge weight
	
	private Editor editor; // The editor in which this edge is displayed
	
	// Endpoint nodes of the edge. For directed edges, c1 is the source, c2 is the sink
	private Node c1;
	private Node c2;
	
	// If the edge is a quadratic bezier curve OR a linear bezier (in the latter case, control is null)
	private Point2D.Double p1;
	private Point2D.Double p2;
	private Point2D.Double control;
	
	// If the edge is a self-edge (and thus an arc)
	private Point2D.Double arcPoint1;
	private Point2D.Double arcPoint2;
	private Point2D.Double center;
	private double radius;
	
	private int type; // What type of edge is this?
	
	private Color color; // Edge color
	private int weight; // Weight (thickness) of the line
	private String text; // Text to display next to the edge
	
	/** Create a new edge. 
	 * 
	 * @param c1 A node.
	 * @param c2 Another node. We draw this edge from c1 to c2.
	 * @param c  The edge's visual color.
	 * @param w  The edge's visual weight (thickness).
	 * @param ed The Editor this edge will be displayed on.
	 */
	public Edge(Node c1, Node c2, Color c, int w, Editor ed) {
		super();
		this.c1 = c1;
		this.c2 = c2;
		color = c;
		this.weight = w;
		editor = ed;
		
		p1 = new Point2D.Double();
		p2 = new Point2D.Double();
		control = new Point2D.Double();
	}
	
	/** Set the three points necessary for a bezier curve. */
	public void setPoints(double startX, double startY, double contrX, double contrY, double endX, double endY) {
		p1.setLocation(startX, startY);
		p2.setLocation(endX, endY);
		control.setLocation(contrX, contrY);;
	}
	
	/** Get the object for the quadratic bezier. */
	public QuadCurve2D.Double getCurve() {
		QuadCurve2D.Double curve = new QuadCurve2D.Double();
		curve.setCurve(p1, control, p2);
		return curve;
	}
	
	/** 
	 * Returns whether the specified node is one of this edge's endpoints.
	 * 
	 * @param c The node we want to check to see if it is one of this edge's endpoints.
	 * @return true if c is an endpoint of this edge, and false otherwise.
	 */
	public boolean hasEndpoint(Node c) {
		return c == c1 || c == c2;
	}
	
	/** 
	 * Get the endpoints of this edge.
	 * 
	 * @return An array of two nodes containing both endpoints.
	 */
	public Node[] getEndpoints() {
		return new Node[] {c1, c2};
	}
	
	public Point2D.Double[] getBezierPoints() {
		return new Point2D.Double[] {p1, control, p2};
	}
	
	public Point2D.Double getCenter() {
		if(type != Edge.LOOP)
			return null;
		return center;
	}
	
	public void setCenter(Point2D.Double p) {
		center = p;
	}
	
	public double getRadius() {
		if(type != Edge.LOOP)
			return Double.NaN;
		return radius;
	}
	
	public void setRadius(double r) {
		radius = r;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int t) {
		type = t;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getWeight() {
		return weight;
	}
	
}
