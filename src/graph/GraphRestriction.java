package graph;

public class GraphRestriction {

	public static final int UNDIRECTED = 0b1;
	public static final int DIRECTED = 0b10;
	public static final int MULTIGRAPH = 0b100;
	public static final int SIMPLE = 0b1000;
	public static final int LOOPS_ALLOWED = 0b10000;
	
	public static final int UNWEIGHTED = 0;
	public static final int INTEGER_WEIGHTED = 1;
	public static final int DOUBLE_WEIGHTED = 0b10;
	
}
