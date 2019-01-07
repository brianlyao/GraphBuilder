package graph.components.gb;

import context.GBContext;
import graph.Graph;
import lombok.Getter;

/**
 * This is precisely a graph but with a context. Note that the components are
 * not GBComponents; conversion from GraphComponent to GBComponent (and vice
 * versa) is handled in the GBComponent and GraphComponent subclasses.
 *
 * @see graph.Graph
 */
public class GBGraph extends Graph {

	@Getter
	private GBContext context;

	/**
	 * Create a new GBGraph. The underlying graph is also new, with the given
	 * constraints.
	 *
	 * @param context     The context this belongs to.
	 * @param constraints The graph constraints.
	 */
	public GBGraph(GBContext context, int constraints) {
		super(constraints);
		this.context = context;
	}

	/**
	 * Essentially a copy constructor. Adds all components of the
	 * graph
	 *
	 * @param context The context this copy of the graph belongs to.
	 * @param graph   The graph to copy.
	 */
	public GBGraph(GBContext context, Graph graph) {
		super(graph.getConstraints());
		this.addNodes(graph.getNodes());
		this.addEdges(graph.getEdgeSet());
		this.context = context;
	}

}
