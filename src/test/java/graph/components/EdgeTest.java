package graph.components;

import org.junit.jupiter.api.Test;
import structures.OrderedPair;
import structures.UOPair;
import util.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for testing the Edge class.
 *
 * @author Brian Yao
 */
public class EdgeTest {

	@Test
	public void testInitialization() {
		Node node1 = new Node();
		Node node2 = new Node();
		Edge edge = new Edge(node1, node2, false);

		assertEquals(node1, edge.getFirstEnd());
		assertEquals(node2, edge.getSecondEnd());
		assertFalse(edge.isDirected());
		assertEquals(new OrderedPair<>(node1, node2), edge.getEndpoints());

		assertNull(edge.getGbEdge());

		Edge edgeWithId = new Edge(123, node2, node1, true);

		assertEquals(123, edgeWithId.getId());
	}

	@Test
	public void testInstanceMethods() {
		Node node1 = new Node();
		Node node2 = new Node();
		Edge edge = new Edge(node1, node2, false);

		assertEquals(new UOPair<>(node1, node2), edge.getUoEndpoints());
		assertTrue(edge.hasEndpoint(node1));
		assertTrue(edge.hasEndpoint(node2));
		assertFalse(edge.isSelfEdge());

		assertEquals(node2, edge.getOtherEndpoint(node1));
		assertEquals(node1, edge.getOtherEndpoint(node2));

		Node node3 = new Node();
		assertFalse(edge.hasEndpoint(node3));
		assertThrows(IllegalArgumentException.class, () -> edge.getOtherEndpoint(node3));
	}

}
