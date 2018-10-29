package graph.path;

import graph.components.Edge;
import graph.components.Node;
import lombok.Getter;
import org.javatuples.Pair;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * An instance represents a path within a graph. A non-empty path contains at
 * least one node and at least zero edges. The number of edges is always one
 * less than the number of nodes.
 *
 * @author Brian Yao
 */
public class Path {

	// For n > 0 nodes, there are n - 1 edges in the path
	@Getter
	private LinkedList<Node> nodes;
	@Getter
	private LinkedList<Edge> edges;

	/**
	 * Default constructor. Constructs an empty path.
	 */
	private Path() {
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
	 * Copy constructor. The nodes and edges used in this path are exactly
	 * those used in the given path.
	 *
	 * @param path The path to copy.
	 */
	public Path(Path path) {
		nodes = path.nodes;
		edges = path.edges;
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
	 * @return true iff this path represents a cycle.
	 */
	public boolean isCycle() {
		return nodes.getFirst() == nodes.getLast();
	}

	/**
	 * Get an iterator for this path. Each iterated item is a node and the
	 * edge from that node to the next node in the path. If the next edge is
	 * null and the next node is not, that item contains the last node in the
	 * path.
	 *
	 * @return A path iterator for this path.
	 */
	public Iterator<Pair<Node, Edge>> iterator() {
		return new PathIterator(this);
	}

	/**
	 * Append a node (and edge) to the "end" of this path.
	 *
	 * @param nextNode   The node to append.
	 * @param toNextNode The edge to the appended node.
	 * @throws IllegalArgumentException if the provided edge does not contain
	 *         the provided node, or if the provided edge is directed and in
	 *         the wrong direction.
	 */
	public void appendNode(Node nextNode, Edge toNextNode) {
		if (!toNextNode.hasEndpoint(nextNode)) {
			throw new IllegalArgumentException("The provided edge must contain the provided node as an endpoint.");
		} else if (toNextNode.isDirected() && toNextNode.getSecondEnd() != nextNode) {
			throw new IllegalArgumentException("The provided directed edge must end in the provided node.");
		}

		nodes.addLast(nextNode);
		edges.addLast(toNextNode);
	}

	/**
	 * Prepend a node (and edge) to the "start" of this path.
	 *
	 * @param prevNode     The node to prepend.
	 * @param fromPrevNode The edge from the prepended node.
	 * @throws IllegalArgumentException if the provided edge does not contain
	 *         the provided node, or if the provided edge is directed and in
	 *         the wrong direction.
	 */
	public void prependNode(Node prevNode, Edge fromPrevNode) {
		if (!fromPrevNode.hasEndpoint(prevNode)) {
			throw new IllegalArgumentException("The provided edge must contain the provided node as an endpoint.");
		} else if (fromPrevNode.isDirected() && fromPrevNode.getFirstEnd() != prevNode) {
			throw new IllegalArgumentException("The provided directed edge must start in the provided node.");
		}

		nodes.addFirst(prevNode);
		edges.addFirst(fromPrevNode);
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
