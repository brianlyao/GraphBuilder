package components;
import java.awt.Color;

import uielements.Editor;

public class DirectedEdge extends Edge {
	
	private static final long serialVersionUID = 2657908946109244199L;

	public DirectedEdge(Node source, Node sink, Color c, int weight, Editor ed) {
		super(source, sink, c, weight, ed);
	}
	
}
