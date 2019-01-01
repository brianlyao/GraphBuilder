package exception;

import graph.components.Edge;
import graph.components.Node;
import graph.path.Cycle;
import graph.path.Path;
import lombok.Getter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An exception pertaining to shortest path algorithms, indicating
 * that no shortest path exists due to the existence of a negative
 * cycle in the graph.
 *
 * @author Brian Yao
 */
public class NegativeCycleException extends RuntimeException {

	private Map<Node, Edge> next;
	private Node pathStart;

	@Getter
	private Set<Edge> negativeEdges;

	/**
	 * Initialize a new exception with negative cycle data.
	 *
	 * @param message   Exception message.
	 * @param next      Map from nodes to the edge connecting them to the
	 *                  next node in the shortest path.
	 * @param pathStart First node of the path.
	 */
	public NegativeCycleException(String message, Map<Node, Edge> next, Node pathStart) {
		super(message);
		this.next = next;
		this.pathStart = pathStart;
	}

	/**
	 * Initialize a new exception with negative undirected edge data.
	 *
	 * @param message       Exception message.
	 * @param negativeEdges Set of negative undirected edges.
	 */
	public NegativeCycleException(String message, Set<Edge> negativeEdges) {
		super(message);
		this.negativeEdges = negativeEdges;
	}

	/**
	 * Get the negative cycle which causes no shortest path to exist.
	 *
	 * @return A negative cycle which caused this exception.
	 */
	public Cycle getNegativeCycle() {
		Set<Node> seen = new HashSet<>();
		seen.add(pathStart);

		Path negativeCycle = new Path(pathStart);
		Node current = next.get(pathStart).getOtherEndpoint(pathStart);
		while (current != null) {
			negativeCycle.appendNode(current, next.get(current));
			if (seen.contains(current)) {
				break;
			}
			seen.add(current);
			current = next.get(current).getOtherEndpoint(current);
		}

		return new Cycle(negativeCycle);
	}

}
