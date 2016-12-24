package components;

import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import components.display.NodePanel;
import context.GraphBuilderContext;

/** An instance represents a node (visually represented by a circle) placed on the editor panel. */
public class Node extends GraphComponent {
	
	// The JPanel containing this node's visual appearance
	private NodePanel nodePanel;
	
	private HashSet<Edge> undirectedEdges;
	private HashSet<Edge> outgoingDirectedEdges;
	private HashSet<Edge> incomingDirectedEdges;
	
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
		
		undirectedEdges = new HashSet<>();
		outgoingDirectedEdges = new HashSet<>();
		incomingDirectedEdges = new HashSet<>();
		
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
	 * Add an edge containing this node as an endpoint to this node's data.
	 * 
	 * @param e The edge to add.
	 */
	public void addEdge(Edge e) {
		if (!e.isDirected()) {
			undirectedEdges.add(e);
		} else {
			if (e.getEndpoints().getFirst() == this) {
				outgoingDirectedEdges.add(e);
			} else if (e.getEndpoints().getSecond() == this){
				incomingDirectedEdges.add(e);
			}
		}
	}
	
	/**
	 * Remove an edge containing this node as an endpoint to this node's data.
	 * 
	 * @param e The edge to remove.
	 */
	public void removeEdge(Edge e) {
		if (!e.isDirected()) {
			undirectedEdges.remove(e);
		} else {
			if (e.getEndpoints().getFirst() == this) {
				outgoingDirectedEdges.remove(e);
			} else if (e.getEndpoints().getSecond() == this){
				incomingDirectedEdges.remove(e);
			}
		} 
	}
	
	/**
	 * Get the set of undirected edges sharing this node as an endpoint.
	 * 
	 * @return The set of connected undirected edges.
	 */
	public Set<Edge> getUndirectedEdges() {
		return undirectedEdges;
	}
	
	/**
	 * Get the set of directed edges leaving this node.
	 * 
	 * @return The set of outgoing directed edges.
	 */
	public Set<Edge> getOutgoingDirectedEdges() {
		return outgoingDirectedEdges;
	}
	
	/**
	 * Get the set of directed edges entering this node.
	 * 
	 * @return The set of incoming directed edges.
	 */
	public Set<Edge> getIncomingDirectedEdges() {
		return incomingDirectedEdges;
	}
	
	/**
	 * Get the set of nodes directly connected to this node via an edge. If we want, we
	 * may choose to disregard neighbors linked only by a directed edge pointing toward
	 * this node. 
	 * 
	 * @param followDirected true if we want to disregard neighbors with a directed edge
	 *                       toward this node, false otherwise.
	 * @return The set of neighbor nodes.
	 */
	public Set<Node> getNeighbors(boolean followDirected) {
		Set<Node> neighbors = new HashSet<>();
		for (Edge e : undirectedEdges) {
			Node n = e.getOtherEndpoint(this);
			if (!neighbors.contains(n)) {
				neighbors.add(n);
			}
		}
		
		for (Edge e : outgoingDirectedEdges) {
			Node n = e.getOtherEndpoint(this);
			if (!neighbors.contains(n)) {
				neighbors.add(n);
			}
		}
		
		if (!followDirected) {
			for (Edge e : incomingDirectedEdges) {
				Node n = e.getOtherEndpoint(this);
				if (!neighbors.contains(n)) {
					neighbors.add(n);
				}
			}
		}
		
		return neighbors;
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
