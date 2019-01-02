package graph.path;

import graph.components.Edge;
import graph.components.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * An instance is a graph cycle: a path which starts and ends at the same node.
 *
 * @author Brian Yao
 */
public final class Cycle extends Path {

	/**
	 * Attempts to initialize a cycle using the given path, which is assumed
	 * to be a cycle.
	 *
	 * @param cycle The cycle path.
	 * @throws IllegalArgumentException if the given path is not a cycle.
	 */
	public Cycle(Path cycle) {
		super(cycle);

		if (!cycle.isCycle()) {
			throw new IllegalArgumentException("Provided path is not a cycle.");
		}
	}

	/**
	 * Create a cycle with the given nodes and edges. The number of nodes must
	 * equal the number of edges. The last edge should point connect the last
	 * node to the first.
	 *
	 * @param nodes The nodes in this cycle.
	 * @param edges The edges in this cycle.
	 */
	public Cycle(List<Node> nodes, List<Edge> edges) {
		this(validateAndCreateCycle(nodes, edges));
	}

	/**
	 * Private helper for validating input of a cycle, and constructing the
	 * underlying path.
	 *
	 * @param nodes The nodes in this cycle.
	 * @param edges The edges in this cycle.
	 * @return A path representing this cycle.
	 */
	private static Path validateAndCreateCycle(List<Node> nodes, List<Edge> edges) {
		if (nodes.size() != edges.size()) {
			throw new IllegalArgumentException("A cycle must have the same number of edges as nodes.");
		}

		List<Node> nodesCopy = new ArrayList<>(nodes);
		nodesCopy.add(nodes.get(0));

		return new Path(nodesCopy, edges);
	}

	/**
	 * Create a new cycle with one node and no edges.
	 *
	 * @param node The node to place in the cycle.
	 */
	public Cycle(Node node) {
		super(node);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Cycle)) {
			return false;
		}

		Cycle other = (Cycle) o;
		if (this.edgeLength() != other.edgeLength()) {
			return false;
		}

		// Find where the first node of this cycle is in the other cycle
		Node firstNode = this.getNodes().getFirst();
		int indexOfFirst = other.getNodes().indexOf(firstNode);
		if (indexOfFirst < 0) {
			return false;
		}

		// Compare the order of nodes and edges
		LinkedList<Node> reorderedNodes = new LinkedList<>();
		reorderedNodes.addAll(other.getNodes().subList(indexOfFirst, other.getNodes().size() - 1));
		reorderedNodes.addAll(other.getNodes().subList(0, indexOfFirst));
		reorderedNodes.add(reorderedNodes.getFirst());
		LinkedList<Edge> reorderedEdges = new LinkedList<>();
		reorderedEdges.addAll(other.getEdges().subList(indexOfFirst, other.edgeLength()));
		reorderedEdges.addAll(other.getEdges().subList(0, indexOfFirst));

		return this.getNodes().equals(reorderedNodes) && this.getEdges().equals(reorderedEdges);
	}

}
