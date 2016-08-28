package components;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import uielements.GUI;

/** An instance is an edge of the graph. Visually represented by a line, quadratic bezier curve, or loop. */
public class Line extends GraphComponent {
	
	private static final long serialVersionUID = -6840630886505529490L;
	
	public static final int LINE = 0;
	public static final int CURVE = 1;
	public static final int LOOP = 2;
	
	private static final int LINE_THICKNESS = 2; // Initial value of edge weight
	
	private GUI gui;
	
	// Endpoint nodes of the edge. For directed edges, c1 is the source, c2 is the sink
	private Circle c1;
	private Circle c2;
	
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
	 * 
	 * */
	public Line(Circle c1, Circle c2, Color c, int w, GUI g) {
		super();
		this.c1 = c1;
		this.c2 = c2;
		color = c;
		this.weight = w;
		gui = g;
		
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
	
	/** Returns whether the specified node is one of this edge's endpoints. */
	public boolean hasEndpoint(Circle c) {
		return c == c1 || c == c2;
	}
	
	public Circle[] getEndpoints() {
		return new Circle[] {c1, c2};
	}
	
	public Point2D.Double[] getBezierPoints() {
		return new Point2D.Double[] {p1, control, p2};
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
