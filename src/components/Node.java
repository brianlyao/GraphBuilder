package components;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import components.display.NodePanel;
import context.GraphBuilderContext;

/**
 * An instance represents a node component of a graph.
 * 
 * @author Brian
 */
public class Node extends GraphComponent {
	
	// The JPanel containing this node's visual appearance
	private NodePanel nodePanel;
	
	private Map<Node, Set<Edge>> undirectedEdges;
	private Map<Node, Set<Edge>> outgoingDirectedEdges;
	private Map<Node, Set<Edge>> incomingDirectedEdges;
	
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
		nodePanel = new NodePanel(x, y, r, txt, c, lc, tc, ctxt, this);
		
		undirectedEdges = new HashMap<Node, Set<Edge>>();
		outgoingDirectedEdges = new HashMap<Node, Set<Edge>>();
		incomingDirectedEdges = new HashMap<Node, Set<Edge>>();
		
		// This component should be de-selected on creation
		setSelected(false);
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param node The node to copy.
	 */
	public Node(Node node) {
		super(node.getContext());
		nodePanel = new NodePanel(node.getNodePanel(), this);
		
		undirectedEdges = new HashMap<Node, Set<Edge>>();
		outgoingDirectedEdges = new HashMap<Node, Set<Edge>>();
		incomingDirectedEdges = new HashMap<Node, Set<Edge>>();
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
		Node other = e.getOtherEndpoint(this);
		if (!e.isDirected()) {
			if (!undirectedEdges.containsKey(other)) {
				undirectedEdges.put(other, new HashSet<Edge>());
			}
			undirectedEdges.get(other).add(e);
		} else {
			if (e.getEndpoints().getFirst() == this) {
				if (!outgoingDirectedEdges.containsKey(other)) {
					outgoingDirectedEdges.put(other, new HashSet<Edge>());
				}
				outgoingDirectedEdges.get(other).add(e);
			} else if (e.getEndpoints().getSecond() == this) {
				if (!incomingDirectedEdges.containsKey(other)) {
					incomingDirectedEdges.put(other, new HashSet<Edge>());
				}
				incomingDirectedEdges.get(other).add(e);
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
	 * Get the map of undirected edges sharing this node as an endpoint.
	 * The map's key is the endpoint of the edge which is not this node.
	 * The value of the map is a set of edges.
	 * 
	 * @return The set of connected undirected edges.
	 */
	public Map<Node, Set<Edge>> getUndirectedEdges() {
		return undirectedEdges;
	}
	
	/**
	 * Get the map of directed edges leaving this node.
	 * The map's key is the endpoint of the edge which is not this node.
	 * The value of the map is a set of edges.
	 * 
	 * @return The set of outgoing directed edges.
	 */
	public Map<Node, Set<Edge>> getOutgoingDirectedEdges() {
		return outgoingDirectedEdges;
	}
	
	/**
	 * Get the map of directed edges entering this node.
	 * The map's key is the endpoint of the edge which is not this node.
	 * The value of the map is a set of edges.
	 * 
	 * @return The set of incoming directed edges.
	 */
	public Map<Node, Set<Edge>> getIncomingDirectedEdges() {
		return incomingDirectedEdges;
	}
	
	/**
	 * Get the set of nodes directly connected to this node via an edge. If we want, we
	 * may choose to disregard neighbors linked only by a directed edge pointing toward
	 * this node. 
	 * 
	 * @param followDirected true if we want to disregard neighbors with only directed edges
	 *                       toward this node, false otherwise.
	 * @return The set of neighbor nodes.
	 */
	public Set<Node> getNeighbors(boolean followDirected) {
		Set<Node> neighbors = new HashSet<>();
		for (Set<Edge> edgeSet : undirectedEdges.values()) {
			for (Edge e : edgeSet) {
				Node n = e.getOtherEndpoint(this);
				if (!neighbors.contains(n)) {
					neighbors.add(n);
				}
			}
		}
		
		for (Set<Edge> edgeSet : outgoingDirectedEdges.values()) {
			for (Edge e : edgeSet) {
				Node n = e.getOtherEndpoint(this);
				if (!neighbors.contains(n)) {
					neighbors.add(n);
				}
			}
		}
		
		if (!followDirected) {
			for (Set<Edge> edgeSet : incomingDirectedEdges.values()) {
				for (Edge e : edgeSet) {
					Node n = e.getOtherEndpoint(this);
					if (!neighbors.contains(n)) {
						neighbors.add(n);
					}
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
