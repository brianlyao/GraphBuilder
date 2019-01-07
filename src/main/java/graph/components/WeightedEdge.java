package graph.components;

import lombok.Getter;
import lombok.Setter;

/**
 * An edge with a numeric "weight" value. This weight is stored as a double.
 *
 * @author Brian Yao
 */
public class WeightedEdge extends Edge {

	@Getter @Setter
	private double weight;

	/**
	 * Create a new weighted edge.
	 *
	 * @param n1       The first node endpoint.
	 * @param n2       The second node endpoint.
	 * @param directed Whether this edge is directed.
	 * @param weight   The weight value of this edge.
	 */
	public WeightedEdge(Node n1, Node n2, boolean directed, double weight) {
		super(n1, n2, directed);
		this.weight = weight;
	}

	/**
	 * Create a new weighted edge with the given ID.
	 *
	 * @param n1       The first node endpoint.
	 * @param n2       The second node endpoint.
	 * @param directed Whether this edge is directed.
	 * @param weight   The weight value of this edge.
	 */
	public WeightedEdge(int id, Node n1, Node n2, boolean directed, double weight) {
		this(n1, n2, directed, weight);
		this.setId(id);
	}

}
