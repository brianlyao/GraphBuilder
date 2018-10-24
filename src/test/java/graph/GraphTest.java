package graph;

import graph.components.Edge;
import graph.components.Node;
import org.junit.jupiter.api.Test;
import structures.UOPair;
import util.TestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for testing the Graph class.
 *
 * @author Brian Yao
 */
public class GraphTest {

	@Test
	public void testGraphConstraints() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED;
		Graph graph = new Graph(constraints);

		assertTrue(graph.getConstraints() == constraints);
		assertTrue(graph.hasConstraint(GraphConstraint.SIMPLE));
		assertTrue(graph.hasConstraint(GraphConstraint.UNDIRECTED));
		assertFalse(graph.hasConstraint(GraphConstraint.UNWEIGHTED));
		assertFalse(graph.hasConstraint(GraphConstraint.DIRECTED));

		graph.addConstraint(GraphConstraint.UNWEIGHTED);

		assertTrue(graph.hasConstraint(GraphConstraint.UNWEIGHTED));

		graph.removeConstraint(GraphConstraint.DIRECTED);

		assertFalse(graph.hasConstraint(GraphConstraint.DIRECTED));
	}

	@Test
	public void testInitializationState() {
		Graph graph = new Graph(0);

		assertTrue(graph.isEmpty());
		assertTrue(graph.getNodes().isEmpty());
		assertTrue(graph.getEdges().isEmpty());
	}

	@Test
	public void testAddNode() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
		Graph graph = new Graph(constraints);

		Node node = new Node();

		graph.addNode(node);

		assertTrue(graph.containsNode(node));

		assertThrows(IllegalArgumentException.class, () -> graph.addNode(node));
	}

	@Test
	public void testAddEdge() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(5, 0);
		graph.addNodes(n);

		Edge edge = new Edge(n[4], n[1], false);

		graph.addEdge(edge);

		assertTrue(graph.containsEdge(edge));
		assertTrue(n[1].hasEdge(edge));
		assertTrue(n[4].hasEdge(edge));

		assertThrows(IllegalArgumentException.class, () -> graph.addEdge(edge));
	}

	@Test
	public void testRemoveNode() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(5, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 2}, {0, 3}, {1, 2}, {1, 3}},
									  TestUtils.booleans(4, false), n, 0);
		graph.addNodes(n);
		graph.addEdges(e);

		Map<UOPair<Node>, List<Edge>> removedEdges = graph.removeNode(n[0]);
		assertFalse(graph.containsNode(n[0]));
		assertFalse(graph.containsEdge(e[0]));
		assertFalse(graph.containsEdge(e[1]));

		assertTrue(removedEdges.get(e[0].getUoEndpoints()).contains(e[0]));
		assertTrue(removedEdges.get(e[1].getUoEndpoints()).contains(e[1]));

		assertThrows(IllegalArgumentException.class, () -> graph.removeNode(n[0]));

		assertTrue(graph.removeNode(n[4]).isEmpty());
	}

	@Test
	public void testRemoveEdge() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(5, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 2}, {0, 3}, {1, 2}, {3, 4}},
									  TestUtils.booleans(4, false), n, 0);
		graph.addNodes(n);
		graph.addEdges(e);

		int index = graph.removeEdge(e[1]);

		assertFalse(graph.containsEdge(e[1]));
		assertFalse(e[1].getFirstEnd().hasEdge(e[1]));
		assertFalse(e[1].getSecondEnd().hasEdge(e[1]));
		assertEquals(0, index);
		assertFalse(graph.getEdges().containsKey(e[1].getUoEndpoints()));

		assertThrows(IllegalArgumentException.class, () -> graph.removeEdge(e[1]));
	}

	@Test
	public void testAddEdgeMultigraph() {
		int constraints = GraphConstraint.MULTIGRAPH | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(4, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {0, 1}, {0, 1}, {0, 1}, {1, 3}},
									  TestUtils.booleans(5, false), n, 0);
		graph.addNodes(n);
		graph.addEdge(e[0], 0);
		graph.addEdge(e[1], 0);
		graph.addEdge(e[2], 1);
		graph.addEdge(e[3]);

		UOPair<Node> ends = new UOPair<>(n[0], n[1]);
		List<Edge> edges = graph.getEdges().get(ends);
		assertTrue(graph.containsEdge(e[0]));
		assertTrue(graph.containsEdge(e[1]));
		assertTrue(graph.containsEdge(e[2]));
		assertTrue(graph.containsEdge(e[3]));
		assertEquals(e[0], edges.get(2));
		assertEquals(e[1], edges.get(0));
		assertEquals(e[2], edges.get(1));
		assertEquals(e[3], edges.get(3));
		assertEquals(4, graph.getEdges().get(ends).size());

		assertThrows(IndexOutOfBoundsException.class, () -> graph.addEdge(e[4], -1));
		assertThrows(IndexOutOfBoundsException.class, () -> graph.addEdge(e[4], 5));
	}

	@Test
	public void testRemoveEdgeMultigraph() {
		int constraints = GraphConstraint.MULTIGRAPH | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(3, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {0, 1}, {0, 1}, {0, 1}},
									  TestUtils.booleans(4, false), n, 0);
		graph.addNodes(n);
		graph.addEdges(e);

		int index = graph.removeEdge(e[2]);
		assertFalse(graph.containsEdge(e[2]));
		assertFalse(n[0].hasEdge(e[2]));
		assertFalse(n[1].hasEdge(e[2]));
		assertEquals(2, index);

		UOPair<Node> ends = new UOPair<>(n[0], n[1]);
		List<Edge> edges = graph.getEdges().get(ends);
		assertEquals(e[0], edges.get(0));
		assertEquals(e[1], edges.get(1));
		assertEquals(e[3], edges.get(2));
		assertEquals(3, edges.size());
	}

	@Test
	public void testInducedSubgraph() {
		int constraints = GraphConstraint.MULTIGRAPH | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(6, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {0, 3}, {1, 4}, {1, 3}, {1, 3}, {3, 4}, {2, 4}},
									  TestUtils.booleans(7, false), n, 0);
		graph.addNodes(n);
		graph.addEdges(e);

		Set<Node> subset = new HashSet<>(Arrays.asList(n[0], n[1], n[3], n[5]));
		Graph subgraph = graph.inducedSubgraph(subset);
		assertEquals(constraints, subgraph.getConstraints());
		assertEquals(subset, subgraph.getNodes());
		assertEquals(3, subgraph.getEdges().size());

		UOPair<Node> ends1 = new UOPair<>(n[0], n[1]);
		UOPair<Node> ends2 = new UOPair<>(n[0], n[3]);
		UOPair<Node> ends3 = new UOPair<>(n[1], n[3]);
		assertEquals(e[0], subgraph.getEdges().get(ends1).get(0));
		assertEquals(e[1], subgraph.getEdges().get(ends2).get(0));
		assertEquals(e[3], subgraph.getEdges().get(ends3).get(0));
		assertEquals(e[4], subgraph.getEdges().get(ends3).get(1));
		assertEquals(2, subgraph.getEdges().get(ends3).size());
	}

	@Test
	public void testSimpleConstraintViolations() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
		Graph graph = new Graph(constraints);
		Node[] n = TestUtils.newNodes(2, 0);
		Edge edge = new Edge(n[0], n[1], false);
		Edge edgeDupe = new Edge(n[0], n[1], false);
		Edge selfEdge = new Edge(n[0], n[0], false);

		graph.addEdge(edge);

		assertThrows(IllegalArgumentException.class, () -> graph.addEdge(edgeDupe));
		assertThrows(IllegalArgumentException.class, () -> graph.addEdge(selfEdge));
	}

}
