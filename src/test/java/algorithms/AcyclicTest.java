package algorithms;

import graph.Graph;
import graph.GraphConstraint;
import graph.components.Edge;
import graph.components.Node;
import org.junit.jupiter.api.Test;
import util.TestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test cases for testing the acyclicity-testing algorithm.
 *
 * @author Brian Yao
 */
public class AcyclicTest {

	@Test
	public void emptyGraphTest() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.UNDIRECTED;

		Graph graph = new Graph(constraints);

		assertTrue(CycleAlgorithms.isAcyclic(graph));
	}

	@Test
	public void nullGraphTest() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.UNDIRECTED;

		Graph graph = new Graph(constraints);
		graph.addNodes(TestUtils.newNodes(10, 0));

		assertTrue(CycleAlgorithms.isAcyclic(graph));
	}

	@Test
	public void selfEdgeTest() {
		int constraints = GraphConstraint.MULTIGRAPH | GraphConstraint.UNWEIGHTED | GraphConstraint.MIXED;

		Graph graph = new Graph(constraints);
		Node n = new Node(0);
		Edge e = new Edge(1, n, n, false);
		graph.addNode(n);
		assertTrue(graph.addEdge(e));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e);
		Edge e1 = new Edge(2, n, n, true);
		assertTrue(graph.addEdge(e1));

		assertFalse(CycleAlgorithms.isAcyclic(graph));
	}

	@Test
	public void simpleUndirectedSmallGraphTest() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.UNDIRECTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(3, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {0, 2}},
									  TestUtils.booleans(3, false), n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[2]);

		assertTrue(CycleAlgorithms.isAcyclic(graph));
	}

	@Test
	public void simpleDirectedSmallGraphTest() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.DIRECTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(3, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {2, 0}},
									  TestUtils.booleans(3, true), n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[2]);

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		Edge e4 = new Edge(6, n[0], n[2], true);
		assertTrue(graph.addEdge(e4));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[1]);
		Edge e5 = new Edge(7, n[2], n[1], true);
		assertTrue(graph.addEdge(e5));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[0]);
		Edge e6 = new Edge(8, n[1], n[0], true);
		assertTrue(graph.addEdge(e6));

		assertFalse(CycleAlgorithms.isAcyclic(graph));
	}

	@Test
	public void simpleMixedSmallGraphTest() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.MIXED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(4, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {2, 1}, {2, 3}, {0, 3}},
									  new boolean[] {true, true, false, false}, n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		Edge e8 = new Edge(8, n[0], n[2], false);
		assertTrue(graph.addEdge(e8));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e8);
		Edge e9 = new Edge(9, n[0], n[2], true);
		assertTrue(graph.addEdge(e9));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[1]);
		graph.removeEdge(e9);
		Edge e10 = new Edge(10, n[1], n[2], false);
		assertTrue(graph.addEdge(e10));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[0]);
		graph.removeEdge(e10);
		Edge e11 = new Edge(11, n[1], n[0], true);
		assertTrue(graph.addEdge(e[1]));
		assertTrue(graph.addEdge(e11));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[2]);
		Edge e12 = new Edge(12, n[2], n[3], true);
		assertTrue(graph.addEdge(e12));

		assertTrue(CycleAlgorithms.isAcyclic(graph));
	}

	@Test
	public void simpleUndirectedMediumGraphTest() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.UNDIRECTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(11, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {1, 5}, {4, 5}, {5, 8}, {5, 6}, {6, 9},
			{6, 7}, {7, 10}}, TestUtils.booleans(10, false), n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		Edge e21 = new Edge(21, n[3], n[10], false);
		assertTrue(graph.addEdge(e21));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[6]);

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		Edge e22 = new Edge(22, n[8], n[9], false);
		assertTrue(graph.addEdge(e22));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[1]);
		graph.removeEdge(e[3]);
		graph.removeEdge(e[8]);

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		Edge e23 = new Edge(23, n[2], n[7], false);
		assertTrue(graph.addEdge(e23));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e23);
		Edge e24 = new Edge(24, n[4], n[8], false);
		assertTrue(graph.addEdge(e24));

		assertFalse(CycleAlgorithms.isAcyclic(graph));
	}

	@Test
	public void simpleDirectedMediumGraphTest() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.DIRECTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(11, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {5, 1}, {4, 5}, {5, 8}, {6, 5}, {6, 9},
			{6, 7}, {7, 10}}, TestUtils.booleans(10, true), n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		Edge e21 = new Edge(21, n[2], n[6], true);
		assertTrue(graph.addEdge(e21));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e21);
		Edge e22 = new Edge(22, n[6], n[2], true);
		assertTrue(graph.addEdge(e22));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e22);
		Edge e23 = new Edge(23, n[3], n[10], true);
		assertTrue(graph.addEdge(e23));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		Edge e24 = new Edge(24, n[10], n[4], true);
		assertTrue(graph.addEdge(e24));

		assertFalse(CycleAlgorithms.isAcyclic(graph));
	}

	@Test
	public void simpleMixedMediumGraphTest() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.MIXED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(12, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{1, 0}, {1, 2}, {3, 2}, {4, 3}, {5, 4}, {5, 6}, {6, 7}, {5, 0},
										  {3, 8}, {2, 11}, {8, 11}, {1, 9}, {5, 7}, {9, 10}, {11, 10}},
									  new boolean[] {true, false, true, true, true, false, true, true, true, false,
										  false, false, true, true, true}, n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[6]);
		Edge e27 = new Edge(27, n[7], n[6], true);
		assertTrue(graph.addEdge(e27));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[5]);
		Edge e28 = new Edge(28, n[5], n[6], true);
		assertTrue(graph.addEdge(e28));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[14]);
		Edge e29 = new Edge(29, n[10], n[11], true);
		assertTrue(graph.addEdge(e29));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e29);
		graph.removeEdge(e[7]);
		Edge e30 = new Edge(30, n[0], n[5], false);
		assertTrue(graph.addEdges(e[14], e30));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[1]);
		graph.removeEdge(e[9]);
		graph.removeEdge(e[13]);
		Edge e31 = new Edge(31, n[11], n[2], true);
		Edge e32 = new Edge(32, n[10], n[9], true);
		assertTrue(graph.addEdges(e31, e32));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[3]);
		Edge e33 = new Edge(33, n[3], n[4], true);
		assertTrue(graph.addEdge(e33));

		assertTrue(CycleAlgorithms.isAcyclic(graph));
	}

}