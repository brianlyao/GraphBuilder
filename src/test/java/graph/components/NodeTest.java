package graph.components;

import org.junit.jupiter.api.Test;
import util.TestUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for testing the Node class.
 *
 * @author Brian Yao
 */
public class NodeTest {

	@Test
	public void testInitialization() {
		Node node1 = new Node();

		assertNotNull(node1.getSelfEdges());
		assertNotNull(node1.getUndirectedEdges());
		assertNotNull(node1.getIncomingDirectedEdges());
		assertNotNull(node1.getOutgoingDirectedEdges());

		assertNull(node1.getGbNode());

		Node node2 = new Node(123);
		assertEquals(123, node2.getId());
	}

	@Test
	public void testAddEdge() {
		Node node = new Node();
		Node[] other = TestUtils.newNodes(3, 0);
		Edge selfEdge = new Edge(node, node, false);
		Edge undirectedEdge = new Edge(node, other[0], false);
		Edge directedOut = new Edge(node, other[1], true);
		Edge directedIn = new Edge(other[2], node, true);

		// Test self edge
		assertTrue(node.getSelfEdges().isEmpty());

		node.addEdge(selfEdge);

		assertTrue(node.getSelfEdges().contains(selfEdge));

		// Test undirected edge
		assertTrue(node.getUndirectedEdges().isEmpty());
		assertTrue(other[0].getUndirectedEdges().isEmpty());

		node.addEdge(undirectedEdge);
		other[0].addEdge(undirectedEdge);

		assertTrue(node.getUndirectedEdges().containsKey(other[0]));
		assertTrue(node.getUndirectedEdges().get(other[0]).contains(undirectedEdge));
		assertTrue(other[0].getUndirectedEdges().containsKey(node));
		assertTrue(other[0].getUndirectedEdges().get(node).contains(undirectedEdge));

		// Test outgoing directed edge
		assertTrue(node.getOutgoingDirectedEdges().isEmpty());
		assertTrue(other[1].getIncomingDirectedEdges().isEmpty());

		node.addEdge(directedOut);
		other[1].addEdge(directedOut);

		assertTrue(node.getOutgoingDirectedEdges().containsKey(other[1]));
		assertTrue(node.getOutgoingDirectedEdges().get(other[1]).contains(directedOut));
		assertTrue(other[1].getIncomingDirectedEdges().containsKey(node));
		assertTrue(other[1].getIncomingDirectedEdges().get(node).contains(directedOut));

		// Test incoming directed edge
		assertTrue(node.getIncomingDirectedEdges().isEmpty());
		assertTrue(other[2].getOutgoingDirectedEdges().isEmpty());

		node.addEdge(directedIn);
		other[2].addEdge(directedIn);

		assertTrue(node.getIncomingDirectedEdges().containsKey(other[2]));
		assertTrue(node.getIncomingDirectedEdges().get(other[2]).contains(directedIn));
		assertTrue(other[2].getOutgoingDirectedEdges().containsKey(node));
		assertTrue(other[2].getOutgoingDirectedEdges().get(node).contains(directedIn));

		// Test error cases
		Node foo = new Node();
		Node bar = new Node();
		Edge baz = new Edge(foo, bar, false);
		assertThrows(IllegalArgumentException.class, () -> node.addEdge(baz));
		assertThrows(IllegalArgumentException.class, () -> node.addEdge(selfEdge));
	}

	@Test
	public void testRemoveEdge() {
		Node node = new Node();
		Node[] other = TestUtils.newNodes(3, 0);
		Edge selfEdge = new Edge(node, node, false);
		Edge undirectedEdge = new Edge(node, other[0], false);
		Edge directedOut = new Edge(node, other[1], true);
		Edge directedIn = new Edge(other[2], node, true);

		node.addEdge(selfEdge);
		node.addEdge(undirectedEdge);
		node.addEdge(directedOut);
		node.addEdge(directedIn);

		node.removeEdge(selfEdge);
		node.removeEdge(undirectedEdge);
		node.removeEdge(directedOut);
		node.removeEdge(directedIn);

		assertTrue(node.getSelfEdges().isEmpty());
		assertTrue(node.getUndirectedEdges().isEmpty());
		assertTrue(node.getOutgoingDirectedEdges().isEmpty());
		assertTrue(node.getIncomingDirectedEdges().isEmpty());

		// Test error cases
		Node foo = new Node();
		Node bar = new Node();
		Edge baz = new Edge(foo, bar, false);
		assertThrows(IllegalArgumentException.class, () -> node.removeEdge(baz));
		assertThrows(IllegalArgumentException.class, () -> node.removeEdge(directedIn));
	}

	@Test
	public void testHasEdge() {
		Node node = new Node();
		Node[] other = TestUtils.newNodes(3, 0);
		Edge selfEdge = new Edge(node, node, false);
		Edge undirectedEdge = new Edge(node, other[0], false);
		Edge directedOut = new Edge(node, other[1], true);
		Edge directedIn = new Edge(other[2], node, true);

		node.addEdge(selfEdge);
		node.addEdge(undirectedEdge);
		node.addEdge(directedOut);
		node.addEdge(directedIn);
		other[0].addEdge(undirectedEdge);
		other[1].addEdge(directedOut);
		other[2].addEdge(directedIn);

		assertTrue(node.hasEdge(selfEdge));
		assertTrue(node.hasEdge(undirectedEdge));
		assertTrue(node.hasEdge(directedOut));
		assertTrue(node.hasEdge(directedIn));
		assertTrue(other[0].hasEdge(undirectedEdge));
		assertTrue(other[1].hasEdge(directedOut));
		assertTrue(other[2].hasEdge(directedIn));
		assertFalse(other[0].hasEdge(directedIn));
		assertFalse(other[1].hasEdge(undirectedEdge));
		assertFalse(other[2].hasEdge(directedOut));
	}

	@Test
	public void testGetNeighboringEdges() {
		Node[] n = TestUtils.newNodes(6, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {0, 2}, {4, 0}, {3, 0}},
									  new boolean[] {false, true, false, true}, n, 0);
		for (Edge edge : e) {
			edge.addSelfToNodeData();
		}

		Map<Node, Set<Edge>> n0edges1 = n[0].getNeighboringEdges(false);
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

		Map<Node, Set<Edge>> n0edges2 = n[0].getNeighboringEdges(true);
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
		for (Edge edge : e) {
			edge.addSelfToNodeData();
		}

		Set<Edge> n1edges1 = n[0].getEdgesToNeighbor(n[1], false);
		assertEquals(3, n1edges1.size());
		assertTrue(n1edges1.contains(e[0]));
		assertTrue(n1edges1.contains(e[1]));
		assertTrue(n1edges1.contains(e[2]));
		Set<Edge> n3edges1 = n[0].getEdgesToNeighbor(n[3], false);
		assertEquals(1, n3edges1.size());
		assertTrue(n3edges1.contains(e[5]));

		Set<Edge> n1edges2 = n[0].getEdgesToNeighbor(n[1], true);
		assertEquals(2, n1edges2.size());
		assertTrue(n1edges2.contains(e[0]));
		assertTrue(n1edges2.contains(e[2]));
		Set<Edge> n3edges2 = n[0].getEdgesToNeighbor(n[3], true);
		assertTrue(n3edges2.isEmpty());

		assertTrue(n[0].getEdgesToNeighbor(n[2], true).contains(e[3]));
		assertTrue(n[0].getEdgesToNeighbor(n[4], true).contains(e[4]));
		assertTrue(n[0].getEdgesToNeighbor(n[5], true).isEmpty());
	}

	@Test
	public void testGetNeighbors() {
		Node[] n = TestUtils.newNodes(6, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{1, 0}, {1, 0}, {0, 2}, {3, 0}, {4, 0}, {5, 0}},
									  new boolean[] {true, true, true, true, true, false}, n, 0);
		for (Edge edge : e) {
			edge.addSelfToNodeData();
		}

		Set<Node> n0nodes1 = n[0].getNeighbors(false);
		assertEquals(5, n0nodes1.size());
		assertTrue(n0nodes1.contains(n[1]));
		assertTrue(n0nodes1.contains(n[2]));
		assertTrue(n0nodes1.contains(n[3]));
		assertTrue(n0nodes1.contains(n[4]));
		assertTrue(n0nodes1.contains(n[5]));

		Set<Node> n0nodes2 = n[0].getNeighbors(true);
		assertEquals(2, n0nodes2.size());
		assertTrue(n0nodes2.contains(n[2]));
		assertTrue(n0nodes2.contains(n[5]));
	}

}
