package algorithms;

import graph.Graph;
import graph.GraphConstraint;
import graph.components.Edge;
import graph.components.Node;
import org.junit.jupiter.api.Test;
import util.TestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for Kruskal's algorithm.
 *
 * @author Brian Yao
 */
public class KruskalTest {

	@Test
	public void testEmptyGraph() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
		Graph graph = new Graph(constraints);
		Graph spanningTree = Kruskal.execute(graph);
		assertTrue(spanningTree.isEmpty());
		assertEquals(graph, spanningTree);
	}

	@Test
	public void testNullGraph() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
		Node[] n = TestUtils.newNodes(6, 0);
		Graph graph = new Graph(constraints);
		graph.addNodes(n);

		Graph spanningTree = Kruskal.execute(graph);

		assertTrue(spanningTree.getEdgeSet().isEmpty());
		assertEquals(graph, spanningTree);
	}

	@Test
	public void testDisconnected() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(8, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {0, 3}, {1, 3}, {2, 5}, {2, 7}, {4, 6}},
									  TestUtils.booleans(6, false), n, n.length);
		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		assertNotEquals(graph.getNodes(), BFS.explore(graph, n[0], false));

		Graph spanningTree = Kruskal.execute(graph);

		assertTrue(Cycles.isAcyclic(spanningTree));
		assertEquals(graph.getNodes(), spanningTree.getNodes());
		assertNotEquals(spanningTree.getNodes(), BFS.explore(spanningTree, n[0], false));
		assertEquals(Set.of(n[0], n[1], n[3]), BFS.explore(spanningTree, n[1], false));
		assertEquals(Set.of(n[2], n[5], n[7]), BFS.explore(spanningTree, n[2], false));
		assertEquals(Set.of(n[4], n[6]), BFS.explore(spanningTree, n[6], false));
	}

	@Test
	public void testTreeInput() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
		Graph tree = new Graph(constraints);
		Node[] n = TestUtils.newNodes(8, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {0, 3}, {0, 6}, {1, 2}, {2, 5}, {2, 7}, {4, 6}},
									  TestUtils.booleans(7, false), n, n.length);
		tree.addNodes(n);
		assertTrue(tree.addEdges(e));

		assertEquals(tree.getNodes(), BFS.explore(tree, n[0], false));
		assertTrue(Cycles.isAcyclic(tree));

		Graph spanningTree = Kruskal.execute(tree);
		assertEquals(tree, spanningTree);
	}

	@Test
	public void testUnweighted() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(9, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {0, 2}, {0, 7}, {1, 3}, {1, 5}, {1, 6}, {1, 8}, {2, 3},
			{2, 7}, {3, 5}, {3, 7}, {4, 6}, {5, 6}, {5, 7}, {7, 8}}, TestUtils.booleans(15, false), n, n.length);
		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		Graph spanningTree = Kruskal.execute(graph);

		assertTrue(Cycles.isAcyclic(spanningTree));
		assertEquals(graph.getNodes(), spanningTree.getNodes());
		assertEquals(spanningTree.getNodes(), BFS.explore(spanningTree, n[0], false));
	}

	@Test
	public void testWeighted() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.WEIGHTED;
		testComplete5(constraints, false);
	}

	@Test
	public void testWeightedDirected() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.DIRECTED | GraphConstraint.WEIGHTED;
		testComplete5(constraints, true);
	}

	/**
	 * Test case with a weighted complete graph with 5 nodes.
	 *
	 * @param constraints graph constraints.
	 * @param directed    true iff all edges are directed.
	 */
	private static void testComplete5(int constraints, boolean directed) {
		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(5, 0);
		Edge[] e = TestUtils.newWeightedEdges(new int[][] {{0, 1}, {0, 2}, {0, 3}, {0, 4}, {1, 2}, {1, 3}, {1, 4},
			{2, 3}, {2, 4}, {3, 4}}, TestUtils.booleans(10, directed), new double[] {2, 2, 1, 4, 5, 3, 3, 2, 4,
			8}, n, n.length);
		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		Graph spanningTree = Kruskal.execute(graph);

		assertTrue(Cycles.isAcyclic(spanningTree));
		assertEquals(graph.getNodes(), spanningTree.getNodes());
		assertEquals(spanningTree.getNodes(), BFS.explore(spanningTree, n[0], false));

		Graph expectedTree = new Graph(constraints);
		expectedTree.addNodes(n);
		expectedTree.addEdges(e[0], e[2], e[6], e[7]);

		assertEquals(expectedTree, spanningTree);
	}

}
