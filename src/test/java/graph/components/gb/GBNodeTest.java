package graph.components.gb;

import context.GBContext;
import graph.components.Node;
import graph.components.display.NodePanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ui.Editor;
import ui.GBFrame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

/**
 * JUnit test cases for testing the GBNode class.
 *
 * @author Brian Yao
 */
public class GBNodeTest {

	@Mock
	private GBContext mockedContext;
	@Mock
	private GBFrame mockedGui;
	@Mock
	private Editor mockedEditor;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(mockedGui.getContext()).thenReturn(mockedContext);
		when(mockedContext.getGUI()).thenReturn(mockedGui);
		when(mockedGui.getEditor()).thenReturn(mockedEditor);
		when(mockedEditor.getGUI()).thenReturn(mockedGui);
	}

	@Test
	public void testInitialization() {
		NodePanel np = new NodePanel(34, 56, 78);
		Node node = new Node();
		GBNode gbNode = new GBNode(node, mockedContext, np);

		assertEquals(node, gbNode.getNode());
		assertEquals(np, gbNode.getPanel());

		// Initialization of GBNode should set some NodePanel data
		assertEquals(gbNode, np.getGbNode());
	}

	@Test
	public void testCopyConstructor() {
		NodePanel np = new NodePanel(34, 56, 78);
		Node node = new Node();
		GBNode gbNode = new GBNode(node, mockedContext, np);

		GBNode copy = new GBNode(0, gbNode);

		assertNotEquals(node, copy.getNode());
		assertEquals(mockedContext, copy.getContext());
		assertNotEquals(np, copy.getPanel());
	}

}
