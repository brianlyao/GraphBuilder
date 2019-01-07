package graph.components;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * JUnit test cases for testing the Node class.
 *
 * @author Brian Yao
 */
public class NodeTest {

	@Test
	public void testInitialization() {
		Node node1 = new Node();
		assertNull(node1.getGbNode());

		Node node2 = new Node(123);
		assertEquals(123, node2.getId());
	}

}
