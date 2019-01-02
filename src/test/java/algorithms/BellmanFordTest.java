package algorithms;

import exception.NegativeCycleException;
import graph.Graph;
import graph.GraphConstraint;
import graph.components.Edge;
import graph.components.Node;
import graph.path.Cycle;
import org.junit.jupiter.api.Test;
import util.ShortestPathTemplates;
import util.TestUtils;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for the Bellman-Ford algorithm.
 *
 * @author Brian Yao
 */
public class BellmanFordTest {

	@Test
	public void testInvalidInput() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(3, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {0, 2}}, new boolean[] {false, false}, n, n.length);
		Node z = new Node();

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		assertThrows(IllegalArgumentException.class, () -> BellmanFord.execute(graph, n[0], z));
		assertThrows(IllegalArgumentException.class, () -> BellmanFord.execute(graph, z, n[1]));
	}

	@Test
	public void testNegativeUndirectedEdges() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.MIXED | GraphConstraint.WEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(9, 0);
		Edge[] e = TestUtils.newWeightedEdges(new int[][] {{0, 1}, {0, 3}, {1, 2}, {2, 8}, {3, 5}, {4, 3}, {4, 6},
			{4, 7}, {5, 6}}, new boolean[] {true, true, false, false, false, true, true, false, false}, new double[]
			{2, 3, -1, 1, 1, 2, 3, -1, 5}, n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		NegativeCycleException nce = assertThrows(NegativeCycleException.class,
												  () -> BellmanFord.execute(graph, n[0], n[8]));

		Set<Edge> negEdges = nce.getNegativeEdges();
		assertNotNull(negEdges);
		assertEquals(Set.of(e[2]), negEdges);

		NegativeCycleException nce2 = assertThrows(NegativeCycleException.class,
												   () -> BellmanFord.execute(graph, n[4], n[5]));

		Set<Edge> negEdges2 = nce2.getNegativeEdges();
		assertNotNull(negEdges2);
		assertEquals(Set.of(e[7]), negEdges2);

		assertDoesNotThrow(() -> BellmanFord.execute(graph, n[0], n[6]));
	}

	@Test
	public void testNegativeCycle() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.DIRECTED | GraphConstraint.WEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(9, 0);
		Edge[] e = TestUtils.newWeightedEdges(new int[][] {{0, 2}, {2, 3}, {3, 4}, {3, 5}, {4, 2}, {4, 6}, {5, 7},
			{6, 1}, {6, 8}, {7, 6}, {8, 1}, {8, 5}}, TestUtils.booleans(12, true), new double[] {2, 3, -8, 1, 4, 2,
			1, 5, 1, -4, 6, 3}, n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		NegativeCycleException nce = assertThrows(NegativeCycleException.class,
												  () -> BellmanFord.execute(graph, n[0], n[6]));
		Cycle negCycle = nce.getNegativeCycle();

		assertTrue(negCycle.length() < 0.);
		assertEquals(new Cycle(List.of(n[2], n[3], n[4]), List.of(e[1], e[2], e[4])), negCycle);

		assertDoesNotThrow(() -> BellmanFord.execute(graph, n[8], n[1]));
	}

	@Test
	public void testNoConnectingPath() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(5, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {3, 4}}, TestUtils.booleans(3, false), n, n.length);

		graph.addNodes(n);
		assertTrue(graph.addEdges(e));

		assertNull(BellmanFord.execute(graph, n[0], n[3]));
		assertNull(BellmanFord.execute(graph, n[4], n[1]));
	}

	@Test
	public void testStartEqualsDest() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;

		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(5, 0);

		graph.addNodes(n);

		assertEquals("Path[2]", String.valueOf(BellmanFord.execute(graph, n[2], n[2])));
	}

	@Test
	public void testSSUU() {
		ShortestPathTemplates.testSSUU(graph -> (start, dest) -> BellmanFord.execute(graph, start, dest));
	}

	@Test
	public void testSSUD() {
		ShortestPathTemplates.testSSUD(graph -> (start, dest) -> BellmanFord.execute(graph, start, dest));
	}

	@Test
	public void testSSUM() {
		ShortestPathTemplates.testSSUM(graph -> (start, dest) -> BellmanFord.execute(graph, start, dest));
	}

	@Test
	public void testMSUU() {
		ShortestPathTemplates.testMSUU(graph -> (start, dest) -> BellmanFord.execute(graph, start, dest));
	}

	@Test
	public void testMSUD() {
		ShortestPathTemplates.testMSUD(graph -> (start, dest) -> BellmanFord.execute(graph, start, dest));
	}

	@Test
	public void testMSUM() {
		ShortestPathTemplates.testMSUM(graph -> (start, dest) -> BellmanFord.execute(graph, start, dest));
	}

}
