package algorithms;

import graph.Graph;
import graph.GraphConstraint;
import graph.components.Edge;
import graph.components.Node;
import org.junit.jupiter.api.Test;
import util.ShortestPathTemplates;
import util.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for Dijkstra's algorithm.
 *
 * @author Brian Yao
 */
public class DijkstraTest {

	@Test
	public void testInvalidInput() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(3, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {0, 2}}, new boolean[] {false, false}, n, n.length);
		Node z = new Node();

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		assertThrows(IllegalArgumentException.class, () -> Dijkstra.execute(graph, n[0], z));
		assertThrows(IllegalArgumentException.class, () -> Dijkstra.execute(graph, z, n[1]));
	}

	@Test
	public void testNegativeWeights() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.WEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(3, 0);
		Edge[] e = TestUtils.newWeightedEdges(new int[][] {{0, 1}, {1, 2}, {2, 0}}, TestUtils.booleans(3, true),
											  new double[] {2, 2.5, -1}, n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		assertThrows(IllegalArgumentException.class, () -> Dijkstra.execute(graph, n[0], n[1]));
		assertThrows(IllegalArgumentException.class, () -> Dijkstra.execute(graph, n[1], n[2]));
		assertThrows(IllegalArgumentException.class, () -> Dijkstra.execute(graph, n[2], n[0]));
	}

	@Test
	public void testNoConnectingPath() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(5, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {1, 3}, {0, 2}},
									  TestUtils.booleans(4, false), n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		assertNull(Dijkstra.execute(graph, n[0], n[4]));
		assertNull(Dijkstra.execute(graph, n[4], n[0]));
	}

	@Test
	public void testSameStartAndDest() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(5, 0);

		graph.addNodes(n);

		assertEquals("Path[2]", String.valueOf(Dijkstra.execute(graph, n[2], n[2])));
	}

	@Test
	public void testSSUU() {
		ShortestPathTemplates.testSSUU(Dijkstra::execute);
	}

	@Test
	public void testSSUD() {
		ShortestPathTemplates.testSSUD(Dijkstra::execute);
	}

	@Test
	public void testSSUM() {
		ShortestPathTemplates.testSSUM(Dijkstra::execute);
	}

	@Test
	public void testMSUU() {
		ShortestPathTemplates.testMSUU(Dijkstra::execute);
	}

	@Test
	public void testMSUD() {
		ShortestPathTemplates.testMSUD(Dijkstra::execute);
	}

	@Test
	public void testMSUM() {
		ShortestPathTemplates.testMSUM(Dijkstra::execute);
	}

}
