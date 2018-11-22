package graph.components.gb;

import context.GBContext;
import graph.components.Edge;
import graph.components.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import structures.UOPair;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * JUnit test cases for testing the GBEdge class.
 *
 * @author Brian Yao
 */
public class GBEdgeTest {

	@Mock
	private GBContext mockedContext;
	@Mock
	private GBContext anotherMockedContext;

	private GBNode[] gbNodes;
	private Node[] nodes;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);

		GBContext[] mockContexts = {mockedContext, mockedContext, anotherMockedContext};
		nodes = new Node[3];
		gbNodes = new GBNode[3];
		IntStream.range(0, nodes.length).forEach(i -> {
			nodes[i] = Mockito.mock(Node.class);
			gbNodes[i] = Mockito.mock(GBNode.class);
			when(gbNodes[i].getNode()).thenReturn(nodes[i]);
			when(nodes[i].getGbNode()).thenReturn(gbNodes[i]);
			when(gbNodes[i].getContext()).thenReturn(mockContexts[i]);
		});
	}

	@Test
	public void testInitialization() {
		GBEdge gbEdge1 = new GBEdge(gbNodes[0], gbNodes[1], true);

		assertEquals(gbEdge1, gbEdge1.getEdge().getGbEdge());
		assertEquals(gbNodes[0], gbEdge1.getFirstEnd());
		assertEquals(gbNodes[1], gbEdge1.getSecondEnd());
		assertEquals(nodes[0], gbEdge1.getEdge().getFirstEnd());
		assertEquals(nodes[1], gbEdge1.getEdge().getSecondEnd());
		assertTrue(gbEdge1.isDirected());

		assertEquals(GBEdge.DEFAULT_COLOR, gbEdge1.getColor());
		assertEquals(GBEdge.DEFAULT_WEIGHT, gbEdge1.getWeight());

		Edge edge = new Edge(nodes[0], nodes[1], true);
		GBEdge gbEdge2 = new GBEdge(edge);

		assertEquals(edge, gbEdge2.getEdge());
		assertEquals(gbNodes[0], gbEdge2.getFirstEnd());
		assertEquals(gbNodes[1], gbEdge2.getSecondEnd());
		assertTrue(gbEdge2.isDirected());

		assertThrows(IllegalArgumentException.class, () -> new GBEdge(gbNodes[0], gbNodes[2], false));
		assertThrows(IllegalArgumentException.class, () -> new GBEdge(new Edge(nodes[1], nodes[2], true)));
	}

	@Test
	public void testInstanceMethods() {
		GBEdge gbEdge = new GBEdge(gbNodes[0], gbNodes[1], true);

		assertEquals(new UOPair<>(gbNodes[0], gbNodes[1]), gbEdge.getUoEndpoints());
	}

}
