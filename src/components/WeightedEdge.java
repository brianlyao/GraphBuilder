package components;

import components.display.EdgeData;

import context.GraphBuilderContext;

/**
 * An edge with a numeric "weight" value. This weight is stored as a double.
 * 
 * @author Brian Yao
 */
public class WeightedEdge extends Edge {

	private double weight;
	
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
	public WeightedEdge(Node n1, Node n2, EdgeData data, boolean directed, GraphBuilderContext ctxt, int id, double weight) {
		super(n1, n2, data, directed, ctxt, id);
		this.weight = weight;
	}
	
	/**
	 * Get the weight of this edge.
	 * 
	 * @return The weight.
	 */
	public double getWeight() {
		return weight;
	}
	
	/**
	 * Set the numerical weight of this edge.
	 * 
	 * @param newWeight The new weight value.
	 */
	public void setWeight(double newWeight) {
		weight = newWeight;
	}
	
}
