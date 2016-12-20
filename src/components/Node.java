package components;

import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;

import components.display.NodePanel;
import context.GraphBuilderContext;

/** An instance represents a node (visually represented by a circle) placed on the editor panel. */
public class Node extends GraphComponent {
	
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
	 * Copy constructor.
	 * 
	 * @param node The node (and its node panel) to copy.
	 */
	public Node(Node node) {
		super(node.getContext());
		nodePanel = new NodePanel(node.getNodePanel(), this);
	}
	
	/**
	 * Get the node panel on which this node is drawn.
	 * 
	 * @return The corresponding node panel.
	 */
	public NodePanel getNodePanel() {
		return nodePanel;
	}
	
	/**
	 * Add an edge one of whose endpoints is this node.
	 * 
	 * @param e The edge to add.
	 */
	public void addEdge(Edge e) {
		edges.add(e);
	}
	
	/**
	 * Get the set of edges sharing this node as an endpoint.
	 * 
	 * @return The set of neighboring edges.
	 */
	public HashSet<Edge> getEdges() {
		return edges;
	}
	
	@Override
	public String toString() {
		Point coords = nodePanel.getCoords();
		return String.format("Node[x=%d,y=%d,r=%d,text=%s,id=%d]", coords.x, coords.y, nodePanel.getRadius(), nodePanel.getText() == null ? "" : nodePanel.getText(), getID());
	}
	
	@Override
	public String toStorageString() {
		Point coords = nodePanel.getCoords();
		return String.format("N:%d,%d,%d,%d,%s,%d,%d,%d", getID(), coords.x, coords.y, nodePanel.getRadius(),
				nodePanel.getText(), nodePanel.getFillColor().getRGB(), nodePanel.getBorderColor().getRGB(),
				nodePanel.getTextColor().getRGB());
	}
	
}
