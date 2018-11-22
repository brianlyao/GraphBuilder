package graph.components.display;

import graph.components.gb.GBNode;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for testing the NodePanel class.
 *
 * @author Brian Yao
 */
public class NodePanelTest {

	@Test
	public void testInitialization() {
		NodePanel np = new NodePanel(31, 74, 50);

		assertEquals(31, np.getXCoord());
		assertEquals(74, np.getYCoord());
		assertEquals(50, np.getRadius());

		assertNull(np.getGbNode());
		assertNotEquals(0, np.getMouseListeners().length);
		assertNotEquals(0, np.getMouseMotionListeners().length);

		assertEquals(GBNode.DEFAULT_FILL_COLOR, np.getFillColor());
		assertEquals(GBNode.DEFAULT_BORDER_COLOR, np.getBorderColor());
		assertEquals(GBNode.DEFAULT_TEXT_COLOR, np.getTextColor());
	}

	@Test
	public void testCopyConstructor() {
		NodePanel np = new NodePanel(31, 74, 50);
		np.setFillColor(Color.BLUE);
		np.setBorderColor(Color.GREEN);
		np.setTextColor(Color.MAGENTA);

		NodePanel copy = new NodePanel(np);

		assertEquals(31, copy.getXCoord());
		assertEquals(74, copy.getYCoord());
		assertEquals(50, copy.getRadius());

		assertNull(copy.getGbNode());
		assertNotEquals(0, copy.getMouseListeners().length);
		assertNotEquals(0, copy.getMouseMotionListeners().length);

		assertEquals(Color.BLUE, copy.getFillColor());
		assertEquals(Color.GREEN, copy.getBorderColor());
		assertEquals(Color.MAGENTA, copy.getTextColor());
	}

	@Test
	public void testInstanceMethods() {
		NodePanel np = new NodePanel(444, 265, 25);

		assertEquals(new Point(469, 290), np.getCenter());
	}

}
