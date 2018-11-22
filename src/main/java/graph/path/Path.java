package graph.path;

import graph.components.Edge;
import graph.components.Node;
import lombok.Getter;
import structures.UOPair;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.IntStream;

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
		nodes = new LinkedList<>(path.nodes);
		edges = new LinkedList<>(path.edges);
	}

	/**
	 * Construct a path with the given nodes and edges. The provided edges
	 * must connect the nodes in the order as provided in the list. There
	 * should be exactly one more node than edge provided, and any directed
	 * edge's sink node must have a higher index in the node list than its
	 * source node.
	 *
	 * @param nodeList The nodes in this path.
	 * @param edgeList The edges in this path.
	 */
	public Path(List<Node> nodeList, List<Edge> edgeList) {
		if (edgeList.size() + 1 != nodeList.size()) {
			throw new IllegalArgumentException(String.format("The number of nodes must be exactly one more than the " +
																 "number of edges. Provided: %d nodes, %d edges.",
															 nodeList.size(), edgeList.size()));
		}

		// Validate the input
		IntStream.range(0, edgeList.size()).forEach(i -> {
			Node node1 = nodeList.get(i);
			Node node2 = nodeList.get(i + 1);
			Edge edge = edgeList.get(i);
			if (!edge.getUoEndpoints().equals(new UOPair<>(node1, node2))) {
				throw new IllegalArgumentException(String.format("The edge joining %s and %s does not have the " +
																	 "nodes as endpoints: %s", node1, node2, edge));
			} else if (edge.isDirected() && edge.getFirstEnd() != node1) {
				throw new IllegalArgumentException(String.format("The directed edge joining %s and %s is backwards.",
																 node1, node2));
			}
		});

		nodes = new LinkedList<>(nodeList);
		edges = new LinkedList<>(edgeList);
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
	 * Returns the length (the number of edges) of this path.
	 *
	 * @return The number of edges in this path.
	 */
	public int edgeLength() {
		return edges.size();
	}

	/**
	 * @return true iff this path represents a cycle.
	 */
	public boolean isCycle() {
		return !edges.isEmpty() && nodes.getFirst() == nodes.getLast();
	}

	/**
	 * Get an iterator for this path. Each iterated item is a node and the
	 * edge from that node to the next node in the path. If the next edge is
	 * null and the next node is not, that item contains the last node in the
	 * path.
	 *
	 * @param incomingEdges Whether we want to iterate over nodes and their
	 *                      incoming edges (as opposed to outgoing edges).
	 * @return A path iterator for this path.
	 * @see PathIterator
	 */
	public PathIterator iterator(boolean incomingEdges) {
		return new PathIterator(this, incomingEdges);
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
		} else if (!toNextNode.hasEndpoint(nodes.getLast())) {
			throw new IllegalArgumentException("The provided edge must contain the path's current last node " +
												   "as an endpoint.");
		} else if (toNextNode.isDirected() && toNextNode.getSecondEnd() != nextNode) {
			throw new IllegalArgumentException("The provided directed edge must be directed toward " +
												   "the provided node.");
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
		} else if (!fromPrevNode.hasEndpoint(nodes.getFirst())) {
			throw new IllegalArgumentException("The provided edge must contain the path's current first node " +
												   "as an endpoint.");
		} else if (fromPrevNode.isDirected() && fromPrevNode.getFirstEnd() != prevNode) {
			throw new IllegalArgumentException("The provided directed edge must be directed away from " +
												   "the provided node.");
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
