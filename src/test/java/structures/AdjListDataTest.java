package structures;

import graph.components.Edge;
import graph.components.Node;
import org.junit.jupiter.api.Test;
import util.TestUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for the AdjListData class.
 *
 * @author Brian Yao
 */
public class AdjListDataTest {

	@Test
	public void testAddEdge() {
		Node node = new Node();
		AdjListData data = new AdjListData(node);
		Node[] other = TestUtils.newNodes(3, 0);
		AdjListData data1 = new AdjListData(other[0]);
		AdjListData data2 = new AdjListData(other[1]);
		AdjListData data3 = new AdjListData(other[2]);

		Edge selfEdge = new Edge(node, node, false);
		Edge undirectedEdge = new Edge(node, other[0], false);
		Edge directedOut = new Edge(node, other[1], true);
		Edge directedIn = new Edge(other[2], node, true);

		// Test self edge
		assertTrue(data.getSelfEdges().isEmpty());

		data.addEdge(selfEdge);

		assertTrue(data.getSelfEdges().contains(selfEdge));

		// Test undirected edge
		assertTrue(data.getUndirectedEdges().isEmpty());
		assertTrue(data1.getUndirectedEdges().isEmpty());

		data.addEdge(undirectedEdge);
		data1.addEdge(undirectedEdge);

		assertTrue(data.getUndirectedEdges().containsKey(other[0]));
		assertTrue(data.getUndirectedEdges().get(other[0]).contains(undirectedEdge));
		assertTrue(data1.getUndirectedEdges().containsKey(node));
		assertTrue(data1.getUndirectedEdges().get(node).contains(undirectedEdge));

		// Test outgoing directed edge
		assertTrue(data.getOutgoingDirectedEdges().isEmpty());
		assertTrue(data2.getIncomingDirectedEdges().isEmpty());

		data.addEdge(directedOut);
		data2.addEdge(directedOut);

		assertTrue(data.getOutgoingDirectedEdges().containsKey(other[1]));
		assertTrue(data.getOutgoingDirectedEdges().get(other[1]).contains(directedOut));
		assertTrue(data2.getIncomingDirectedEdges().containsKey(node));
		assertTrue(data2.getIncomingDirectedEdges().get(node).contains(directedOut));

		// Test incoming directed edge
		assertTrue(data.getIncomingDirectedEdges().isEmpty());
		assertTrue(data3.getOutgoingDirectedEdges().isEmpty());

		data.addEdge(directedIn);
		data3.addEdge(directedIn);

		assertTrue(data.getIncomingDirectedEdges().containsKey(other[2]));
		assertTrue(data.getIncomingDirectedEdges().get(other[2]).contains(directedIn));
		assertTrue(data3.getOutgoingDirectedEdges().containsKey(node));
		assertTrue(data3.getOutgoingDirectedEdges().get(node).contains(directedIn));

		// Test error cases
		Node foo = new Node();
		Node bar = new Node();
		Edge baz = new Edge(foo, bar, false);
		assertThrows(IllegalArgumentException.class, () -> data.addEdge(baz));
		assertThrows(IllegalArgumentException.class, () -> data.addEdge(selfEdge));
	}

	@Test
	public void testRemoveEdge() {
		Node node = new Node();
		AdjListData data = new AdjListData(node);
		Node[] other = TestUtils.newNodes(3, 0);

		Edge selfEdge = new Edge(node, node, false);
		Edge undirectedEdge = new Edge(node, other[0], false);
		Edge directedOut = new Edge(node, other[1], true);
		Edge directedIn = new Edge(other[2], node, true);

		data.addEdge(selfEdge);
		data.addEdge(undirectedEdge);
		data.addEdge(directedOut);
		data.addEdge(directedIn);

		data.removeEdge(selfEdge);
		data.removeEdge(undirectedEdge);
		data.removeEdge(directedOut);
		data.removeEdge(directedIn);

		assertTrue(data.getSelfEdges().isEmpty());
		assertTrue(data.getUndirectedEdges().isEmpty());
		assertTrue(data.getOutgoingDirectedEdges().isEmpty());
		assertTrue(data.getIncomingDirectedEdges().isEmpty());

		// Test error cases
		Node foo = new Node();
		Node bar = new Node();
		Edge baz = new Edge(foo, bar, false);
		assertThrows(IllegalArgumentException.class, () -> data.removeEdge(baz));
		assertThrows(IllegalArgumentException.class, () -> data.removeEdge(directedIn));
	}

	@Test
	public void testHasEdge() {
		Node node = new Node();
		AdjListData data = new AdjListData(node);
		Node[] other = TestUtils.newNodes(3, 0);
		AdjListData data1 = new AdjListData(other[0]);
		AdjListData data2 = new AdjListData(other[1]);
		AdjListData data3 = new AdjListData(other[2]);

		Edge selfEdge = new Edge(node, node, false);
		Edge undirectedEdge = new Edge(node, other[0], false);
		Edge directedOut = new Edge(node, other[1], true);
		Edge directedIn = new Edge(other[2], node, true);

		data.addEdge(selfEdge);
		data.addEdge(undirectedEdge);
		data.addEdge(directedOut);
		data.addEdge(directedIn);
		data1.addEdge(undirectedEdge);
		data2.addEdge(directedOut);
		data3.addEdge(directedIn);

		assertTrue(data.hasEdge(selfEdge));
		assertTrue(data.hasEdge(undirectedEdge));
		assertTrue(data.hasEdge(directedOut));
		assertTrue(data.hasEdge(directedIn));
		assertTrue(data1.hasEdge(undirectedEdge));
		assertTrue(data2.hasEdge(directedOut));
		assertTrue(data3.hasEdge(directedIn));
		assertFalse(data1.hasEdge(directedIn));
		assertFalse(data2.hasEdge(undirectedEdge));
		assertFalse(data3.hasEdge(directedOut));
	}

	@Test
	public void testGetNeighboringEdges() {
		Node[] n = TestUtils.newNodes(6, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {0, 2}, {4, 0}, {3, 0}},
									  new boolean[] {false, true, false, true}, n, 0);

		Map<Node, AdjListData> data = new HashMap<>();
		Arrays.stream(n).forEach(node -> data.put(node, new AdjListData(node)));
		for (Edge edge : e) {
			data.get(edge.getFirstEnd()).addEdge(edge);
			data.get(edge.getSecondEnd()).addEdge(edge);
		}

		Map<Node, Set<Edge>> n0edges1 = data.get(n[0]).getNeighboringEdges(false);
		assertEquals(4, n0edges1.size());
		assertFalse(n0edges1.containsKey(n[0]));
		assertTrue(n0edges1.containsKey(n[1]));
		assertTrue(n0edges1.containsKey(n[2]));
		assertTrue(n0edges1.containsKey(n[3]));
		assertTrue(n0edges1.containsKey(n[4]));
		assertFalse(n0edges1.containsKey(n[5]));
		assertTrue(n0edges1.get(n[1]).contains(e[0]));
		assertTrue(n0edges1.get(n[2]).contains(e[1]));
		assertTrue(n0edges1.get(n[3]).contains(e[3]));
		assertTrue(n0edges1.get(n[4]).contains(e[2]));

		Map<Node, Set<Edge>> n0edges2 = data.get(n[0]).getNeighboringEdges(true);
		assertEquals(3, n0edges2.size());
		assertFalse(n0edges2.containsKey(n[0]));
		assertTrue(n0edges2.containsKey(n[1]));
		assertTrue(n0edges2.containsKey(n[2]));
		assertFalse(n0edges2.containsKey(n[3]));
		assertTrue(n0edges2.containsKey(n[4]));
		assertFalse(n0edges2.containsKey(n[5]));
		assertTrue(n0edges2.get(n[1]).contains(e[0]));
		assertTrue(n0edges2.get(n[2]).contains(e[1]));
		assertTrue(n0edges2.get(n[4]).contains(e[2]));
	}

	@Test
	public void testGetEdgesToNeighbor() {
		Node[] n = TestUtils.newNodes(6, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 0}, {0, 1}, {0, 2}, {4, 0}, {3, 0}},
									  new boolean[] {false, true, true, true, false, true}, n, 0);

		Map<Node, AdjListData> data = new HashMap<>();
		Arrays.stream(n).forEach(node -> data.put(node, new AdjListData(node)));
		for (Edge edge : e) {
			data.get(edge.getFirstEnd()).addEdge(edge);
			data.get(edge.getSecondEnd()).addEdge(edge);
		}

		Set<Edge> n1edges1 = data.get(n[0]).getEdgesToNeighbor(n[1], false);
		assertEquals(3, n1edges1.size());
		assertTrue(n1edges1.contains(e[0]));
		assertTrue(n1edges1.contains(e[1]));
		assertTrue(n1edges1.contains(e[2]));
		Set<Edge> n3edges1 = data.get(n[0]).getEdgesToNeighbor(n[3], false);
		assertEquals(1, n3edges1.size());
		assertTrue(n3edges1.contains(e[5]));

		Set<Edge> n1edges2 = data.get(n[0]).getEdgesToNeighbor(n[1], true);
		assertEquals(2, n1edges2.size());
		assertTrue(n1edges2.contains(e[0]));
		assertTrue(n1edges2.contains(e[2]));
		Set<Edge> n3edges2 = data.get(n[0]).getEdgesToNeighbor(n[3], true);
		assertTrue(n3edges2.isEmpty());

		assertTrue(data.get(n[0]).getEdgesToNeighbor(n[2], true).contains(e[3]));
		assertTrue(data.get(n[0]).getEdgesToNeighbor(n[4], true).contains(e[4]));
		assertTrue(data.get(n[0]).getEdgesToNeighbor(n[5], true).isEmpty());
	}

	@Test
	public void testGetNeighbors() {
		Node[] n = TestUtils.newNodes(6, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{1, 0}, {1, 0}, {0, 2}, {3, 0}, {4, 0}, {5, 0}},
									  new boolean[] {true, true, true, true, true, false}, n, 0);
		Map<Node, AdjListData> data = new HashMap<>();
		Arrays.stream(n).forEach(node -> data.put(node, new AdjListData(node)));
		for (Edge edge : e) {
			data.get(edge.getFirstEnd()).addEdge(edge);
			data.get(edge.getSecondEnd()).addEdge(edge);
		}

		Set<Node> n0nodes1 = data.get(n[0]).getNeighbors(false);
		assertEquals(5, n0nodes1.size());
		assertTrue(n0nodes1.contains(n[1]));
		assertTrue(n0nodes1.contains(n[2]));
		assertTrue(n0nodes1.contains(n[3]));
		assertTrue(n0nodes1.contains(n[4]));
		assertTrue(n0nodes1.contains(n[5]));

		Set<Node> n0nodes2 = data.get(n[0]).getNeighbors(true);
		assertEquals(2, n0nodes2.size());
		assertTrue(n0nodes2.contains(n[2]));
		assertTrue(n0nodes2.contains(n[5]));
	}

}
