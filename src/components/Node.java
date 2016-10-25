package components;

import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;

import components.display.NodePanel;

import context.GraphBuilderContext;

/** An instance represents a node (visually represented by a circle) placed on the editor panel. */
public class Node extends GraphComponent {

	/** An instance is an unordered pair of Nodes. */
	public static class Pair {
		
		private final Node node1;
		private final Node node2;
		
		public Pair(Node n1, Node n2) {
			node1 = n1;
			node2 = n2;
		}
		
		public Pair(Edge e) {
			Node[] ends = e.getEndpoints();
			node1 = ends[0];
			node2 = ends[1];
		}
		
		/**
		 * Check if this pair has the specified node.
		 * 
		 * @param n The node we want to compare with the pair's contents.
		 * @return true if n is contained in this pair, false otherwise.
		 */
		public boolean hasNode(Node n) {
			return n == node1 || n == node2;
		}
		
		public Node getFirst() {
			return node1;
		}
		
		public Node getSecond() {
			return node2;
		}
		
		/**
		 * Checks if the the two nodes in both pairs are identical to each other.
		 */
		public boolean equals(Object o) {
			if(o == null)
				return false;
			Pair other = (Pair) o;
			return (node1 == other.node1 && node2 == other.node2) || (node1 == other.node2 && node2 == other.node1);
		}
		
		public int hashCode() {
			int code1 = node1 == null ? 1 : node1.hashCode();
			int code2 = node2 == null ? 1 : node2.hashCode();
			return code1 * code2;
		}
		
	}
	
	// The JPanel containing this node's visual appearance
	private NodePanel nodePanel;
	
	private HashSet<Edge> edges;
	
	/**
	 * Creates a node whose panel is at the top left corner with a default radius.
	 * 
	 * @param ctxt The context (graph) this node is a part of.
	 * @param id   The id this node is assigned.
	 */
	public Node(GraphBuilderContext ctxt, int id){
		this(0, 0, NodePanel.DEFAULT_RADIUS, ctxt, id);
	}
	
	/** 
	 * Creates a node with the specified visual location and radius. 
	 * 
	 * @param x    The x-coordinate of the circle's top left corner.
	 * @param y    The y-coordinate of the circle's top left corner.
	 * @param r    The radius of the circle in pixels.
	 * @param ctxt The context (graph) this node is a part of.
	 * @param id   The id this node is assigned.
	 */
	public Node(int x, int y, int r, GraphBuilderContext ctxt, int id) {
		this(x, y, r, null, ctxt, id);
	}
	
	/** 
	 * Creates a node with the specified visual location, radius, and color. 
	 * 
	 * @param x    The x-coordinate of the circle's top left corner.
	 * @param y    The y-coordinate of the circle's top left corner.
	 * @param r    The radius of the circle in pixels. 
	 * @param c    The fill color of the circle.
	 * @param ctxt The context (graph) this node is a part of.
	 * @param id   The id this node is assigned.
	 */
	public Node(int x, int y, int r, Color c, GraphBuilderContext ctxt, int id) {
		this(x, y, r, null, c, null, null, ctxt, id);
	}

	/** 
	 * Creates a node with the specified visual location, radius, color, and border color. 
	 * 
	 * @param x    The x-coordinate of the circle's top left corner.
	 * @param y    The y-coordinate of the circle's top left corner.
	 * @param r    The radius of the circle in pixels. 
	 * @param c    The fill color of the circle.
	 * @param lc   The border color of the circle.
	 * @param ctxt The context (graph) this node is a part of.
	 * @param id   The id this node is assigned.
	 */
	public Node(int x, int y, int r, Color c, Color lc, GraphBuilderContext ctxt, int id) {
		this(x, y, r, null, c, lc, null, ctxt, id);
	}
	
	/**
	 * Creates a node with the specified visual location, radius, text, color, and text color. 
	 * 
	 * @param x    The x-coordinate of the circle's top left corner.
	 * @param y    The y-coordinate of the circle's top left corner.
	 * @param r    The radius of the circle in pixels.
	 * @param txt  The text displayed on the circle.
	 * @param c    The fill color of the circle.
	 * @param tc   The color of the circle's text.
	 * @param ctxt The context (graph) this node is a part of.
	 * @param id   The id this node is assigned.
	 */
	public Node(int x, int y, int r, String txt, Color c, Color tc, GraphBuilderContext ctxt, int id) {
		this(x, y, r, txt, c, null, tc, ctxt, id);
	}
	
	/**
	 * Creates a node with the specified visual location, radius, text, color, border color, and text color.
	 * 
	 * @param x    The x-coordinate of the circle's top left corner.
	 * @param y    The y-coordinate of the circle's top left corner.
	 * @param r    The radius of the circle in pixels.
	 * @param txt  The text displayed on the circle.
	 * @param c    The fill color of the circle.
	 * @param lc   The border color of the circle.
	 * @param tc   The color of the circle's text.
	 * @param ctxt The context (graph) this node is a part of.
	 * @param id   The id this node is assigned.
	 */
	public Node(int x, int y, int r, String txt, Color c, Color lc, Color tc, GraphBuilderContext ctxt, int id) {
		
		super(ctxt, id);
		nodePanel = new NodePanel(x, y, r, txt, c, lc, tc, ctxt.getGUI().getEditor(), this);
		
		// This component should be deselected on creation
		setSelected(false);
		
	}
	
	/**
	 * Get the node panel on which this node is drawn.
	 * 
	 * @return The corresponding node panel.
	 */
	public NodePanel getNodePanel() {
		return nodePanel;
	}
	
	public void addEdge(Edge l) {
		edges.add(l);
	}
	
	public HashSet<Edge> getEdges() {
		return edges;
	}
	
	public String toString() {
		Point coords = nodePanel.getCoords();
		return String.format("Node[x=%d,y=%d,r=%d,text=%s,id=%d]", coords.x, coords.y, nodePanel.getRadius(), nodePanel.getText() == null ? "" : nodePanel.getText(), getID());
	}
	
	public String toStorageString() {
		Point coords = nodePanel.getCoords();
		return String.format("N:%d,%d,%d,%d,%s,%d,%d,%d", getID(), coords.x, coords.y, nodePanel.getRadius(),
				nodePanel.getText(), nodePanel.getFillColor().getRGB(), nodePanel.getBorderColor().getRGB(),
				nodePanel.getTextColor().getRGB());
	}
	
}
