package components;
import java.awt.Color;

import uielements.GUI;


public class Arrow extends Line {
	
	private static final long serialVersionUID = 2657908946109244199L;

	public Arrow(Circle source, Circle sink, Color c, int weight, GUI g) {
		super(source, sink, c, weight, g);
	}
	
}
