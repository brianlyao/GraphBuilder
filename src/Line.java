import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

public class Line extends GraphComponent {
	
	public static final int LINE = 0;
	public static final int CURVE = 1;
	public static final int LOOP = 2;
	
	private static final int LINE_THICKNESS = 2; // Initial value of edge weight
	
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
	
	private int type;
	
	private Color color;
	private int weight;
	private String text;
	
	private GUI gui;
	
	public Line(Circle c1, Circle c2, Color c, int w, GUI g){
		super();
		this.c1 = c1;
		this.c2 = c2;
//		this.c1.addEdge(this);
//		if(this.c1 != this.c2)
//			this.c2.addEdge(this);
		color = c;
		this.weight = w;
		gui = g;
		
		p1 = new Point2D.Double();
		p2 = new Point2D.Double();
		control = new Point2D.Double();
	}
	
	public void displayProperties(){
		
	}
	
	public void setPoints(double startX, double startY, double contrX, double contrY, double endX, double endY){
		p1.setLocation(startX, startY);
		p2.setLocation(endX, endY);
		control.setLocation(contrX, contrY);;
	}
	
	public QuadCurve2D.Double getCurve(){
		QuadCurve2D.Double curve = new QuadCurve2D.Double();
		curve.setCurve(p1, control, p2);
		return curve;
	}
	
	public boolean hasEndpoint(Circle c){
		return c == c1 || c == c2;
	}
	
	public Circle[] getEndpoints(){
		return new Circle[] {c1, c2};
	}
	
	public Point2D.Double[] getBezierPoints(){
		return new Point2D.Double[] {p1, control, p2};
	}
	
	public Point2D.Double getCenter(){
		return center;
	}
	
	public double getRadius(){
		return radius;
	}
	
	public void setCenter(Point2D.Double p){
		center = p;
	}
	
	public void setRadius(double r){
		radius = r;
	}
	
	public int getType(){
		return type;
	}
	
	public void setType(int t){
		type = t;
	}
	
	public Color getColor(){
		return color;
	}
	
	public int getWeight(){
		return weight;
	}
}
