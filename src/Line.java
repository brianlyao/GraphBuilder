import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.QuadCurve2D;

public class Line extends GraphComponent {
	private static final int LINE_THICKNESS = 2;
	private Circle c1;
	private Circle c2;
	private Point2D.Double p1;
	private Point2D.Double p2;
	private Point2D.Double control;
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
	
	public Color getColor(){
		return color;
	}
	
	public int getWeight(){
		return weight;
	}
}
