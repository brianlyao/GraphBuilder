package algorithms;

import graph.Graph;
import graph.GraphConstraint;
import graph.components.Edge;
import graph.components.Node;
import org.junit.Test;
import util.TestUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A JUnit test case for testing the algorithms inside the CycleAlgorithms
 * class.
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
		final int numNodes = 10;
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.UNDIRECTED;

		Graph graph = new Graph(constraints);
		for (int i = 0; i < numNodes; i++) {
			graph.addNode(new Node());
		}

		assertTrue(CycleAlgorithms.isAcyclic(graph));
	}

	@Test
	public void selfEdgeTest() {
		int constraints = GraphConstraint.MULTIGRAPH | GraphConstraint.UNWEIGHTED | GraphConstraint.MIXED;

		Graph graph = new Graph(constraints);
		Node n = new Node();
		Edge e = new Edge(n, n, false);
		graph.addNode(n);
		assertTrue(graph.addEdge(e));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e);
		Edge e1 = new Edge(n, n, true);
		assertTrue(graph.addEdge(e1));

		assertFalse(CycleAlgorithms.isAcyclic(graph));
	}

	@Test
	public void simpleUndirectedSmallGraphTest() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.UNDIRECTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(3, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {0, 2}},
									  TestUtils.filledBooleanArray(3, false), n, n.length);

		TestUtils.addNodes(graph, n);
		assertTrue(TestUtils.addEdges(graph, e));

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
									  TestUtils.filledBooleanArray(3, true), n, n.length);

		TestUtils.addNodes(graph, n);
		assertTrue(TestUtils.addEdges(graph, e[0], e[1], e[2]));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[2]);

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		Edge e4 = new Edge(n[0], n[2], true);
		assertTrue(graph.addEdge(e4));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[1]);
		Edge e5 = new Edge(n[2], n[1], true);
		assertTrue(graph.addEdge(e5));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[0]);
		Edge e6 = new Edge(n[1], n[0], true);
		assertTrue(graph.addEdge(e6));

		assertFalse(CycleAlgorithms.isAcyclic(graph));
	}

	@Test
	public void simpleMixedSmallGraphTest() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED |
			GraphConstraint.DIRECTED | GraphConstraint.UNDIRECTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(4, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {2, 1}, {2, 3}, {0, 3}},
									  new boolean[] {true, true, false, false}, n, n.length);

		TestUtils.addNodes(graph, n);
		assertTrue(TestUtils.addEdges(graph, e));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		Edge e5 = new Edge(n[0], n[2], false);
		assertTrue(graph.addEdge(e5));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e5);
		Edge e6 = new Edge(n[0], n[2], true);
		assertTrue(graph.addEdge(e6));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[1]);
		graph.removeEdge(e6);
		Edge e7 = new Edge(n[1], n[2], false);
		assertTrue(graph.addEdge(e7));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[0]);
		graph.removeEdge(e7);
		Edge e8 = new Edge(n[1], n[0], true);
		assertTrue(graph.addEdge(e[1]));
		assertTrue(graph.addEdge(e8));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[2]);
		Edge e9 = new Edge(n[2], n[3], true);
		assertTrue(graph.addEdge(e9));

		assertTrue(CycleAlgorithms.isAcyclic(graph));
	}

	@Test
	public void simpleUndirectedMediumGraphTest() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.UNDIRECTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(11, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {1, 5}, {4, 5}, {5, 8}, {5, 6}, {6, 9},
										  {6, 7}, {7, 10}}, TestUtils.filledBooleanArray(10, false), n, n.length);

		TestUtils.addNodes(graph, n);
		assertTrue(TestUtils.addEdges(graph, e));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		Edge e10 = new Edge(n[3], n[10], false);
		assertTrue(graph.addEdge(e10));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[6]);

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		Edge e11 = new Edge(n[8], n[9], false);
		assertTrue(graph.addEdge(e11));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[1]);
		graph.removeEdge(e[3]);
		graph.removeEdge(e[8]);

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		Edge e12 = new Edge(n[2], n[7], false);
		assertTrue(graph.addEdge(e12));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e12);
		Edge e13 = new Edge(n[4], n[8], false);
		assertTrue(graph.addEdge(e13));

		assertFalse(CycleAlgorithms.isAcyclic(graph));
	}

	@Test
	public void simpleDirectedMediumGraphTest() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.DIRECTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(11, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {5, 1}, {4, 5}, {5, 8}, {6, 5}, {6, 9},
										  {6, 7}, {7, 10}}, TestUtils.filledBooleanArray(10, true), n, n.length);

		TestUtils.addNodes(graph, n);
		assertTrue(TestUtils.addEdges(graph, e));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		Edge e10 = new Edge(n[2], n[6], true);
		assertTrue(graph.addEdge(e10));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e10);
		Edge e11 = new Edge(n[6], n[2], true);
		assertTrue(graph.addEdge(e11));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e11);
		Edge e12 = new Edge(n[3], n[10], true);
		assertTrue(graph.addEdge(e12));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		Edge e13 = new Edge(n[10], n[4], true);
		assertTrue(graph.addEdge(e13));

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

		TestUtils.addNodes(graph, n);
		assertTrue(TestUtils.addEdges(graph, e));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[6]);
		Edge e15 = new Edge(n[7], n[6], true);
		assertTrue(graph.addEdge(e15));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[5]);
		Edge e16 = new Edge(n[5], n[6], true);
		assertTrue(graph.addEdge(e16));

		assertTrue(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[14]);
		Edge e17 = new Edge(n[10], n[11], true);
		assertTrue(graph.addEdge(e17));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e17);
		graph.removeEdge(e[7]);
		Edge e18 = new Edge(n[0], n[5], false);
		assertTrue(graph.addEdge(e[14]));
		assertTrue(graph.addEdge(e18));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[1]);
		graph.removeEdge(e[9]);
		graph.removeEdge(e[13]);
		Edge e19 = new Edge(n[11], n[2], true);
		Edge e20 = new Edge(n[10], n[9], true);
		assertTrue(TestUtils.addEdges(graph, e19, e20));

		assertFalse(CycleAlgorithms.isAcyclic(graph));

		graph.removeEdge(e[3]);
		Edge e21 = new Edge(n[3], n[4], true);
		assertTrue(graph.addEdge(e21));

		assertTrue(CycleAlgorithms.isAcyclic(graph));
	}

}
