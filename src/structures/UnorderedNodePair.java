package structures;

import components.Edge;
import components.Node;

/**
 * An instance is an unordered pair of 2 nodes.
 * 
 * @author Brian
 */
public class UnorderedNodePair {
		
	private final Node node1;
	private final Node node2;

	/**
	 * @param n1 The first node.
	 * @param n2 The second node.
	 */
	public UnorderedNodePair(Node n1, Node n2) {
		node1 = n1;
		node2 = n2;
	}

	/**
	 * @param e The edge whose endpoints we want to put in this pair.
	 */
	public UnorderedNodePair(Edge e) {
		OrderedPair<Node> ends = e.getEndpoints();
		node1 = ends.getFirst();
		node2 = ends.getSecond();
	}

	/**
	 * Check if this pair has the specified node.
	 * 
	 * @param n The node we want to compare with the pair's contents.
	 * @return true if n is contained in this pair, false otherwise.
	 */
	public boolean hasNode(Node n) {
		return n == node1 || n == node2;
	}

	/**
	 * Get the "first" node.
	 * 
	 * @return the first node in the pair.
	 */
	public Node getFirst() {
		return node1;
	}

	/**
	 * Get the "second" node.
	 * 
	 * @return the second node in the pair.
	 */
	public Node getSecond() {
		return node2;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		UnorderedNodePair other = (UnorderedNodePair) o;
		return (node1 == other.node1 && node2 == other.node2) || (node1 == other.node2 && node2 == other.node1);
	}

	@Override
	public int hashCode() {
		int code1 = node1 == null ? 1 : node1.hashCode();
		int code2 = node2 == null ? 1 : node2.hashCode();
		return code1 * code2;
	}

}
