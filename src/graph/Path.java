package graph;

import java.util.ArrayList;
import java.util.List;

import components.Edge;
import components.Node;

/**
 * An instance represents a path within a graph. A non-empty path contains at
 * least one node and at least zero edges. The number of edges is always one
 * less than the number of nodes.
 * 
 * @author Brian
 */
public final class Path {
	
	public static final Path DISCONNECTED = new Path();
	public static final Path WRONG_GRAPH = new Path();

	// For n nodes, there are n-1 corresponding edges
	private List<Node> nodes;
	private List<Edge> edges;
	
	/**
	 * Default constructor. Constructs an empty path.
	 */
	public Path() {
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge>();
	}
	
	/**
	 * Constructs a path with only a single a node and no edges.
	 * 
	 * @param start The initial node to put in the path.
	 */
	public Path(Node start) {
		this();
		nodes.add(start);
	}
	
	/**
	 * Append a node (and edge) to the "end" of this path.
	 * 
	 * @param nextNode   The node to append.
	 * @param toNextNode The edge to the appended node.
	 */
	public void appendNode(Node nextNode, Edge toNextNode) {
		nodes.add(nextNode);
		edges.add(toNextNode);
	}
	
	/**
	 * Prepend a node (and edge) to the "start" of this path.
	 * 
	 * @param prevNode     The node to prepend.
	 * @param fromPrevNode The edge from the prepended node.
	 */
	public void prependNode(Node prevNode, Edge fromPrevNode) {
		nodes.add(0, prevNode);
		edges.add(0, fromPrevNode);
	}
	
}
