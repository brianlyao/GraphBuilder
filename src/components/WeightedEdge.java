package components;

import components.display.EdgeData;

import context.GraphBuilderContext;

/**
 * An edge with a "weight" value. A weighted graph is composed of entirely weighted
 * edges using the same type of weight T.
 * 
 * @author Brian
 *
 * @param <T> The data type used for the weight value.
 */
public class WeightedEdge<T> extends Edge {

	private Comparable<T> weight;
	
	/** Create a new weighted edge. 
	 * 
	 * @param n1       A node endpoint.
	 * @param n2       Another node endpoint. We draw this edge from c1 to c2.
	 * @param data     The data object associated with this Edge.
	 * @param directed Whether this edge is directed.
	 * @param ctxt     The context (graph) this edge exists in.
	 * @param id       The id this node is assigned.
	 * @param weight   The weight value of this edge.
	 */
	public WeightedEdge(Node n1, Node n2, EdgeData data, boolean directed, GraphBuilderContext ctxt, int id, Comparable<T> weight) {
		super(n1, n2, data, directed, ctxt, id);
		this.weight = weight;
	}
	
	/**
	 * Create a new weighted edge with a default (null) weight.
	 * 
	 * @param n1       A node endpoint.
	 * @param n2       Another node endpoint. We draw this edge from c1 to c2.
	 * @param data     The data object associated with this Edge.
	 * @param directed Whether this edge is directed.
	 * @param ctxt     The context (graph) this edge exists in.
	 * @param id       The id this node is assigned.
	 */
	public WeightedEdge(Node n1, Node n2, EdgeData data, boolean directed, GraphBuilderContext ctxt, int id) {
		this(n1, n2, data, directed, ctxt, id, null);
	}
	
	/**
	 * Get the weight of this edge.
	 * 
	 * @return The Comparable<T> weight.
	 */
	public Comparable<T> getWeight() {
		return weight;
	}
	
	/**
	 * Set the weight of this edge.
	 * 
	 * @param newWeight The new weight value.
	 */
	public void setWeight(Comparable<T> newWeight) {
		weight = newWeight;
	}
	
}
