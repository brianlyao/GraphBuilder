package algorithms;

import graph.Graph;
import graph.GraphConstraint;
import graph.components.Edge;
import graph.components.Node;
import org.junit.Test;
import util.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DijkstraTest {

	@Test
	public void testSmallSimpleUnweightedUndirected() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(12, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {1, 3}, {2, 4}, {3, 4}, {2, 5}, {5, 6}, {4, 7},
			{3, 8}, {4, 8}, {8, 9}, {8, 10}, {7, 11}}, TestUtils.filledBooleanArray(13, false), n, n.length);
		
		TestUtils.addNodes(graph, n);
		assertTrue(TestUtils.addEdges(graph, e[0], e[1], e[2], e[3], e[4], e[5], e[6], e[7], e[8], e[9], e[10],
									  e[11], e[12]));

		String n0n6path = "Path[0-(12)->1-(13)->2-(17)->5-(18)->6]";
		assertEquals(n0n6path, String.valueOf(PathAlgorithms.dijkstra(graph, n[0], n[6])));

		String n0n9path = "Path[0-(12)->1-(14)->3-(20)->8-(22)->9]";
		assertEquals(n0n9path, String.valueOf(PathAlgorithms.dijkstra(graph, n[0], n[9])));

		String n11n6path = "Path[11-(24)->7-(19)->4-(15)->2-(17)->5-(18)->6]";
		assertEquals(n11n6path, String.valueOf(PathAlgorithms.dijkstra(graph, n[11], n[6]).toString()));

		Edge e25 = new Edge(n[6], n[8], false);
		assertTrue(TestUtils.addEdges(graph, e25));
		String n11n6path2 = "Path[11-(24)->7-(19)->4-(21)->8-(25)->6]";
		assertEquals(n11n6path2, String.valueOf(PathAlgorithms.dijkstra(graph, n[11], n[6]).toString()));

		graph.removeEdge(e[1]);
		String n1n6path = "Path[1-(14)->3-(20)->8-(25)->6]";
		assertEquals(n1n6path, String.valueOf(PathAlgorithms.dijkstra(graph, n[1], n[6]).toString()));
	}

}
