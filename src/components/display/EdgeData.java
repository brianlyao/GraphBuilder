package components.display;

import java.awt.Color;

/** Abstract class for an edge's data. */
public abstract class EdgeData {
	
	private Color color; // Edge color
	private int weight; // Weight (thickness) of the line
	private String text; // Text to display next to the edge
	
	/**
	 * Copy constructor.
	 * 
	 * @param ed The edge data object to copy.
	 */
	public EdgeData(EdgeData ed) {
		this(new Color(ed.color.getRGB()), ed.weight, new String(ed.text));
	}
	
	/**
	 * Create a new edge data object
	 * 
	 * @param c The edge's visual color.
	 * @param w The edge's visual weight (thickness).
	 */
	public EdgeData(Color c, int w, String txt) {
		color = c;
		weight = w;
		text = txt;
	}
	
	/**
	 * Get the display color of this edge.
	 * 
	 * @return The Color object containing the edge's color.
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Get the display weight (thickness) of this edge.
	 * 
	 * @return The integer weight of this edge.
	 */
	public int getWeight() {
		return weight;
	}
	
	/**
	 * Get the text displayed at this edge.
	 * 
	 * @return The String containing the displayed text.
	 */
	public String getText() {
		return text;
	}
	
}
