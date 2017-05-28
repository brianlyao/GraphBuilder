package graph;

import java.util.LinkedList;
import java.util.ListIterator;

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
	
	public static final Path NULL = new Path();

	// For n nodes, there are n-1 corresponding edges
	private LinkedList<Node> nodes;
	private LinkedList<Edge> edges;
	
	/**
	 * Default constructor. Constructs an empty path.
	 */
	public Path() {
		nodes = new LinkedList<Node>();
		edges = new LinkedList<Edge>();
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
		nodes.addLast(nextNode);
		edges.addLast(toNextNode);
	}
	
	/**
	 * Prepend a node (and edge) to the "start" of this path.
	 * 
	 * @param prevNode     The node to prepend.
	 * @param fromPrevNode The edge from the prepended node.
	 */
	public void prependNode(Node prevNode, Edge fromPrevNode) {
		nodes.addFirst(prevNode);
		edges.addFirst(fromPrevNode);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[");
		if (nodes.size() > 0) {
			builder.append(nodes.getFirst().getID());
			ListIterator<Node> nodeIterator = nodes.listIterator(1);
			ListIterator<Edge> edgeIterator = edges.listIterator();
			while (nodeIterator.hasNext()) {
				Node nextNode = nodeIterator.next();
				Edge nextEdge = edgeIterator.next();
				builder.append(String.format("-(%d)->%d", nextEdge.getID(), nextNode.getID()));
			}
		}
		builder.append(']');
		return builder.toString();
	}
	
}
