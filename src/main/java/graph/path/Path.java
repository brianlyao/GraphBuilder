package graph.path;

import graph.components.Edge;
import graph.components.Node;
import lombok.Getter;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * An instance represents a path within a graph. A non-empty path contains at
 * least one node and at least zero edges. The number of edges is always one
 * less than the number of nodes.
 *
 * @author Brian Yao
 */
public final class Path {

	// For n > 0 nodes, there are n - 1 edges in the path
	@Getter
	private LinkedList<Node> nodes;
	@Getter
	private LinkedList<Edge> edges;

	/**
	 * Default constructor. Constructs an empty path.
	 */
	public Path() {
		nodes = new LinkedList<>();
		edges = new LinkedList<>();
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
	 * Returns the length (based on edge weights) of this path. If the
	 * path contains only unweighted edges, the length is simply the number
	 * of edges in the path.
	 *
	 * @return the length of this path.
	 */
	public double length() {
		return edges.stream().mapToDouble(Edge::getNumericWeight).sum();
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

	/**
	 * @return the first node in this path.
	 */
	public Node getFirstNode() {
		return nodes.getFirst();
	}

	/**
	 * @return the last node in this path.
	 */
	public Node getLastNode() {
		return nodes.getLast();
	}

	/**
	 * Remove the last node (and edge) from this path.
	 */
	public void removeLast() {
		nodes.removeLast();
		edges.removeLast();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Path[");
		if (nodes.size() > 0) {
			builder.append(nodes.getFirst().getId());
			ListIterator<Node> nodeIterator = nodes.listIterator(1);
			ListIterator<Edge> edgeIterator = edges.listIterator();
			while (nodeIterator.hasNext() && edgeIterator.hasNext()) {
				Node nextNode = nodeIterator.next();
				Edge nextEdge = edgeIterator.next();
				builder.append(String.format("-(%d)->%d", nextEdge.getId(), nextNode.getId()));
			}
		}
		builder.append(']');
		return builder.toString();
	}

}
