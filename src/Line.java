import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;


public class Line extends GraphComponent {
	private static final int LINE_THICKNESS = 2;
	private Circle c1;
	private Circle c2;
	private Color color;
	private int weight;
	
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
	}
	
	public void displayProperties(){
		
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
