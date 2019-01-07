package util;

import context.GBContext;
import graph.components.Edge;
import graph.components.Node;
import graph.components.WeightedEdge;
import graph.components.display.NodePanel;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import org.mockito.Mockito;

import java.util.Arrays;

/**
 * A class containing methods used in unit testing.
 *
 * @author Brian Yao
 */
public class TestUtils {

	/**
	 * Generate an array of nodes with the specified length.
	 *
	 * @param numNodes The number of nodes to create.
	 * @param startId  The starting ID; consecutive IDs will be assigned to
	 *                 subsequently generated nodes.
	 * @return the array of new nodes.
	 */
	public static Node[] newNodes(int numNodes, int startId) {
		Node[] nodes = new Node[numNodes];
		for (int i = 0; i < numNodes; i++) {
			nodes[i] = new Node(startId + i);
		}
		return nodes;
	}

	/**
	 * Generate an array of edges. The indexPairs parameter is an array of
	 * pairs (arrays of length 2) of indices. These indices are for identifying
	 * nodes in the provided array of nodes. Thus a pair of indices defines the
	 * endpoints of the edge being created. The directed array specifies
	 * whether the i-th generated edge should be directed or not.
	 *
	 * @param indexPairs An array of index pairs specifying node endpoints.
	 * @param directed   An array of booleans specifying whether an edge should
	 *                   be directed.
	 * @param nodes      The list of nodes from which endpoints are taken.
	 * @param startId    The starting ID; consecutive IDs will be assigned to
	 *                   subsequently generated edges.
	 * @return the array of new edges.
	 */
	public static Edge[] newEdges(int[][] indexPairs, boolean[] directed, Node[] nodes, int startId) {
		Edge[] edges = new Edge[indexPairs.length];
		for (int i = 0 ; i < edges.length ; i++) {
			edges[i] = new Edge(startId + i, nodes[indexPairs[i][0]], nodes[indexPairs[i][1]], directed[i]);
		}
		return edges;
	}

	/**
	 * Same as {@link TestUtils#newEdges(int[][], boolean[], Node[], int)},
	 * but generates weighted edges using the given array of weights.
	 *
	 * @param indexPairs An array of index pairs specifying node endpoints.
	 * @param directed   An array of booleans specifying whether an edge should
	 *                   be directed.
	 * @param weights    An array of numerical weights.
	 * @param nodes      The list of nodes from which endpoints are taken.
	 * @param startId    The starting ID; consecutive IDs will be assigned to
	 *                   subsequently generated edges.
	 * @return the array of new weighted edges.
	 */
	public static Edge[] newWeightedEdges(int[][] indexPairs, boolean[] directed, double[] weights,
										  Node[] nodes, int startId) {
		Edge[] edges = new Edge[indexPairs.length];
		for (int i = 0 ; i < edges.length ; i++) {
			edges[i] = new WeightedEdge(startId + i, nodes[indexPairs[i][0]], nodes[indexPairs[i][1]], directed[i],
										weights[i]);
		}
		return edges;
	}

	/**
	 * Generate an array of GBNodes with the specified length. They are all
	 * associated with the same mocked context. They each have their
	 * own mocked NodePanel.
	 *
	 * @param numNodes The number of GBNodes to generate.
	 * @param startId  The starting ID; consecutive IDs will be assigned to
	 *                 subsequently generated GBNodes.
	 * @return the array of GBNodes.
	 */
	public static GBNode[] newGbNodes(int numNodes, int startId) {
		GBContext context = Mockito.mock(GBContext.class);
		return Arrays.stream(newNodes(numNodes, startId)).map(
			node -> new GBNode(node, context, Mockito.mock(NodePanel.class))
		).toArray(GBNode[]::new);
	}

	/**
	 * Generate a list of GBEdges. The parameters are nearly identical to
	 * those of newEdges. They are all associated with the same mocked context.
	 *
	 * @param indexPairs An array of index pairs specifying GBNode endpoints.
	 * @param directed   An array of booleans specifying whether a GBEdge should
	 *                   be directed.
	 * @param gbNodes    The list of GBNodes from which endpoints are taken.
	 * @param startId    The starting ID; consecutive IDs will be assigned to
	 *                   subsequently generated GBEdges.
	 * @return the array of GBEdges.
	 * @see TestUtils#newEdges(int[][], boolean[], Node[], int)
	 */
	public static GBEdge[] newGbEdges(int[][] indexPairs, boolean[] directed, GBNode[] gbNodes, int startId) {
		GBEdge[] gbEdges = new GBEdge[indexPairs.length];
		for (int i = 0 ; i < gbEdges.length ; i++) {
			gbEdges[i] = new GBEdge(startId + i, gbNodes[indexPairs[i][0]], gbNodes[indexPairs[i][1]], directed[i]);
		}
		return gbEdges;
	}

	/**
	 * Create a boolean array of the specified length with all entries set
	 * to the specified value. Useful with the newEdges method.
	 *
	 * @param length The length of the array.
	 * @param value  The value to fill the array with.
	 * @return the filled array.
	 */
	public static boolean[] booleans(int length, boolean value) {
		boolean[] directed = new boolean[length];
		Arrays.fill(directed, value);
		return directed;
	}

}
