package util;

import graph.Graph;
import graph.GraphConstraint;
import graph.components.Edge;
import graph.components.Node;
import graph.path.Path;
import util.TestUtils;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Templates for unit tests of shortest path algorithms. Each test
 * has a 4-5 letter identifier:
 * - S, M, or L for small, medium, or large graph sizes.
 * - S or M for simple graph or multigraph.
 * - U or W for unweighted or weighted.
 * - U, D, or M for undirected, directed, or mixed graphs.
 * - An optional additional N if the graph is weighted and has
 *   negative weights.
 *
 * Each of the algorithms are expected to take a graph, start, and destination
 * as input and return null if no connecting path exists.
 *
 * @author Brian Yao
 */
public class ShortestPathTemplates {

	/**
	 * Unit test for a small simple unweighted undirected graph.
	 *
	 * @param algorithm The shortest path algorithm.
	 */
	public static void testSSUU(Function<Graph, BiFunction<Node, Node, Path>> algorithm) {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(5, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 0}},
									  TestUtils.booleans(5, false), n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		assertEquals("Path[0-(5)->1-(6)->2]", String.valueOf(algorithm.apply(graph).apply(n[0], n[2])));
		assertEquals("Path[0-(9)->4-(8)->3]", String.valueOf(algorithm.apply(graph).apply(n[0], n[3])));

		Edge e10 = new Edge(10, n[0], n[2], false);
		assertTrue(graph.addEdge(e10));

		assertEquals("Path[0-(10)->2]", String.valueOf(algorithm.apply(graph).apply(n[0], n[2])));

		graph.removeEdge(e[0]);

		assertEquals("Path[0-(10)->2-(6)->1]", String.valueOf(algorithm.apply(graph).apply(n[0], n[1])));
	}

	/**
	 * Unit test for a small simple unweighted directed graph.
	 *
	 * @param algorithm The shortest path algorithm.
	 */
	public static void testSSUD(Function<Graph, BiFunction<Node, Node, Path>> algorithm) {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.DIRECTED | GraphConstraint.UNWEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(5, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {2, 1}, {2, 3}, {3, 4}, {4, 0}},
									  TestUtils.booleans(5, true), n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		assertNull(algorithm.apply(graph).apply(n[0], n[2]));
		assertEquals("Path[2-(7)->3-(8)->4-(9)->0]", String.valueOf(algorithm.apply(graph).apply(n[2], n[0])));

		Edge e10 = new Edge(10, n[3], n[0], true);
		assertTrue(graph.addEdge(e10));

		assertEquals("Path[2-(7)->3-(10)->0]", String.valueOf(algorithm.apply(graph).apply(n[2], n[0])));

		graph.removeEdge(e10);
		graph.removeEdge(e[3]);

		assertNull(algorithm.apply(graph).apply(n[2], n[0]));
	}

	/**
	 * Unit test for a small simple unweighted mixed graph.
	 *
	 * @param algorithm The shortest path algorithm.
	 */
	public static void testSSUM(Function<Graph, BiFunction<Node, Node, Path>> algorithm) {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.MIXED | GraphConstraint.UNWEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(5, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {4, 3}, {0, 4}, {3, 0}},
									  new boolean[] {false, true, false, true, true, false}, n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		assertEquals("Path[3-(7)->2]", String.valueOf(algorithm.apply(graph).apply(n[3], n[2])));
		assertEquals("Path[3-(10)->0-(5)->1]", String.valueOf(algorithm.apply(graph).apply(n[3], n[1])));
		assertEquals("Path[0-(10)->3]", String.valueOf(algorithm.apply(graph).apply(n[0], n[3])));

		graph.removeEdge(e[5]);

		assertEquals("Path[0-(9)->4-(8)->3]", String.valueOf(algorithm.apply(graph).apply(n[0], n[3])));
		assertNull(algorithm.apply(graph).apply(n[3], n[1]));
	}

	/**
	 * Unit test for a medium simple unweighted undirected graph.
	 *
	 * @param algorithm The shortest path algorithm.
	 */
	public static void testMSUU(Function<Graph, BiFunction<Node, Node, Path>> algorithm) {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(12, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {1, 3}, {2, 4}, {3, 4}, {2, 5}, {5, 6}, {4, 7},
			{3, 8}, {4, 8}, {8, 9}, {8, 10}, {7, 11}}, TestUtils.booleans(13, false), n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		String n0n6path = "Path[0-(12)->1-(13)->2-(17)->5-(18)->6]";
		assertEquals(n0n6path, String.valueOf(algorithm.apply(graph).apply(n[0], n[6])));

		String n0n9path = "Path[0-(12)->1-(14)->3-(20)->8-(22)->9]";
		assertEquals(n0n9path, String.valueOf(algorithm.apply(graph).apply(n[0], n[9])));

		String n11n6path = "Path[11-(24)->7-(19)->4-(15)->2-(17)->5-(18)->6]";
		assertEquals(n11n6path, String.valueOf(algorithm.apply(graph).apply(n[11], n[6])));

		Edge e25 = new Edge(25, n[6], n[8], false);
		assertTrue(graph.addEdge(e25));
		String n11n6path2 = "Path[11-(24)->7-(19)->4-(21)->8-(25)->6]";
		assertEquals(n11n6path2, String.valueOf(algorithm.apply(graph).apply(n[11], n[6])));

		graph.removeEdge(e[1]);
		String n1n6path = "Path[1-(14)->3-(20)->8-(25)->6]";
		assertEquals(n1n6path, String.valueOf(algorithm.apply(graph).apply(n[1], n[6])));
	}

	/**
	 * Unit test for a medium simple unweighted directed graph.
	 *
	 * @param algorithm The shortest path algorithm.
	 */
	public static void testMSUD(Function<Graph, BiFunction<Node, Node, Path>> algorithm) {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.DIRECTED | GraphConstraint.UNWEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(9, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {1, 3}, {2, 3}, {3, 0}, {3, 5}, {4, 0}, {5, 4},
			{5, 6}, {5, 7}, {6, 7}, {7, 2}, {7, 8}}, TestUtils.booleans(13, true), n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		String n0n8path = "Path[0-(9)->1-(11)->3-(14)->5-(18)->7-(21)->8]";
		assertEquals(n0n8path, String.valueOf(algorithm.apply(graph).apply(n[0], n[8])));

		String n7n6path = "Path[7-(20)->2-(12)->3-(14)->5-(17)->6]";
		assertEquals(n7n6path, String.valueOf(algorithm.apply(graph).apply(n[7], n[6])));

		String n6n0path = "Path[6-(19)->7-(20)->2-(12)->3-(13)->0]";
		assertEquals(n6n0path, String.valueOf(algorithm.apply(graph).apply(n[6], n[0])));

		Edge e22 = new Edge(22, n[8], n[1], true);
		Edge e23 = new Edge(23, n[0], n[3], true);
		graph.removeEdge(e[4]);
		assertTrue(graph.addEdges(e22, e23));

		String n0n8path2 = "Path[0-(23)->3-(14)->5-(18)->7-(21)->8]";
		assertEquals(n0n8path2, String.valueOf(algorithm.apply(graph).apply(n[0], n[8])));

		String n6n0path2 = "Path[6-(19)->7-(20)->2-(12)->3-(14)->5-(16)->4-(15)->0]";
		assertEquals(n6n0path2, String.valueOf(algorithm.apply(graph).apply(n[6], n[0])));

		String n6n1path = "Path[6-(19)->7-(21)->8-(22)->1]";
		assertEquals(n6n1path, String.valueOf(algorithm.apply(graph).apply(n[6], n[1])));
	}

	/**
	 * Unit test for a medium simple unweighted mixed graph.
	 *
	 * @param algorithm The shortest path algorithm.
	 */
	public static void testMSUM(Function<Graph, BiFunction<Node, Node, Path>> algorithm) {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.MIXED | GraphConstraint.UNWEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(12, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {0, 3}, {1, 4}, {2, 1}, {2, 3}, {2, 5}, {4, 5}, {4, 6},
			{5, 6}, {5, 11}, {7, 5}, {7, 8}, {7, 9}, {8, 9}, {10, 7}, {10, 11}, {11, 3}}, new boolean[] {true,
			true, true, true, false, false, false, true, false, false, true, false, false, true, true, false,
			true}, n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		String n6n1path = "Path[6-(20)->5-(17)->2-(15)->1]";
		assertEquals(n6n1path, String.valueOf(algorithm.apply(graph).apply(n[6], n[1])));

		graph.removeEdge(e[5]);

		String n6n1path2 = "Path[6-(20)->5-(21)->11-(28)->3-(16)->2-(15)->1]";
		assertEquals(n6n1path2, String.valueOf(algorithm.apply(graph).apply(n[6], n[1])));

		String n0n9path = "Path[0-(12)->1-(14)->4-(18)->5-(21)->11-(27)->10-(26)->7-(24)->9]";
		assertEquals(n0n9path, String.valueOf(algorithm.apply(graph).apply(n[0], n[9])));

		Edge e29 = new Edge(29, n[6], n[7], false);
		assertTrue(graph.addEdge(e29));

		String n0n9path2 = "Path[0-(12)->1-(14)->4-(19)->6-(29)->7-(24)->9]";
		assertEquals(n0n9path2, String.valueOf(algorithm.apply(graph).apply(n[0], n[9])));

		String n10n1path = "Path[10-(27)->11-(28)->3-(16)->2-(15)->1]";
		assertEquals(n10n1path, String.valueOf(algorithm.apply(graph).apply(n[10], n[1])));

		Edge e30 = new Edge(30, n[9], n[2], true);
		graph.removeEdge(e[16]);
		assertTrue(graph.addEdge(e30));

		String n10n1path2 = "Path[10-(26)->7-(24)->9-(30)->2-(15)->1]";
		assertEquals(n10n1path2, String.valueOf(algorithm.apply(graph).apply(n[10], n[1])));
	}

}
