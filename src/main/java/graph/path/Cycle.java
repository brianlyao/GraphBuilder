package graph.path;

import graph.components.Node;

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
	 * Create a new cycle with one node and no edges.
	 *
	 * @param node The node to place in the cycle.
	 */
	public Cycle(Node node) {
		super(node);
	}

}
