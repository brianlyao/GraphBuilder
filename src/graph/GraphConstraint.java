package graph;

/**
 * A class of constants used to describe graph constraints.
 * 
 * @author Brian
 */
public final class GraphConstraint {

	public static final int EDGE_BEHAVIOR_MASK = 0b11;
	public static final int UNDIRECTED = 0b1;
	public static final int DIRECTED = 0b10;
	public static final int MIXED = 0b11;
	
	public static final int GRAPH_TYPE_MASK = 0b11000;
	public static final int SIMPLE = 0b1000;
	public static final int MULTIGRAPH = 0b10000;
	
	public static final int WEIGHT_MASK = 0b11100000;
	public static final int UNWEIGHTED = 0b100000;
	public static final int INTEGER_WEIGHTED = 0b1000000;
	public static final int DOUBLE_WEIGHTED = 0b10000000;
	
}
