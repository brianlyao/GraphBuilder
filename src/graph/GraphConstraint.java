package graph;

/**
 * A class of constants used to describe graph constraints.
 * 
 * @author Brian Yao
 */
public final class GraphConstraint {

	public static final int EDGE_BEHAVIOR_MASK = 0b11;
	public static final int UNDIRECTED = 0b1;
	public static final int DIRECTED = 0b10;
	public static final int MIXED = 0b11;
	
	public static final int GRAPH_TYPE_MASK = 0b11000;
	public static final int SIMPLE = 0b1000;
	public static final int MULTIGRAPH = 0b10000;
	
	public static final int WEIGHT_MASK = 0b1100000;
	public static final int UNWEIGHTED = 0b100000;
	public static final int WEIGHTED = 0b1000000;
	
}
