package structures;

import graph.components.Edge;
import graph.components.Node;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ui.Editor;
import util.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for the EditorData class.
 *
 * @author Brian Yao
 */
public class EditorDataTest {

	@Mock
	private Editor mockedEditor;

	private Pair<GBNode[], GBEdge[]> addTestData(EditorData editorData) {
		GBNode[] n = TestUtils.newGbNodes(10, 0);
		GBEdge[] e = TestUtils.newGbEdges(new int[][] {{1, 2}, {4, 5}, {8, 9}}, TestUtils.booleans(3, false),
										  n, n.length);

		editorData.addSelection(n[0]);
		editorData.addSelection(e[0]);
		editorData.addHighlight(n[3]);
		editorData.addHighlight(e[1]);
		editorData.setEdgeBasePoint(n[6]);
		editorData.setPathBasePoint(n[7]);
		editorData.setPreviewEdge(e[2]);

		return new Pair<>(n, e);
	}

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testInitialization() {
		EditorData editorData = new EditorData(mockedEditor);

		assertNotNull(editorData.getSelectedNodes());
		assertNotNull(editorData.getSelectedEdges());
		assertNotNull(editorData.getHighlightedNodes());
		assertNotNull(editorData.getHighlightedEdges());
		assertNotNull(editorData.getNodePanelPositionMap());
		assertNotNull(editorData.getLastMousePoint());
	}

	@Test
	public void testAddMethods() {
		EditorData editorData = new EditorData(mockedEditor);
		Pair<GBNode[], GBEdge[]> components = addTestData(editorData);
		GBNode[] n = components.getValue0();
		GBEdge[] e = components.getValue1();

		UOPair<GBNode> key1 = new UOPair<>(n[1], n[2]);
		UOPair<GBNode> key2 = new UOPair<>(n[4], n[5]);

		assertTrue(editorData.getSelectedNodes().contains(n[0]));
		assertTrue(editorData.getSelectedEdges().containsKey(key1));
		assertTrue(editorData.getSelectedEdges().get(key1).contains(e[0]));
		assertTrue(editorData.getHighlightedNodes().contains(n[3]));
		assertTrue(editorData.getHighlightedEdges().containsKey(key2));
		assertTrue(editorData.getHighlightedEdges().get(key2).contains(e[1]));
		assertEquals(n[6], editorData.getEdgeBasePoint());
		assertEquals(n[7], editorData.getPathBasePoint());
		assertEquals(e[2], editorData.getPreviewEdge());

		assertTrue(editorData.isSelected(n[0]));
		assertTrue(editorData.isSelected(e[0]));
		assertTrue(editorData.isHighlighted(n[3]));
		assertTrue(editorData.isHighlighted(e[1]));
	}

	@Test
	public void testRemoveMethods() {
		EditorData editorData = new EditorData(mockedEditor);
		Pair<GBNode[], GBEdge[]> components = addTestData(editorData);
		GBNode[] n = components.getValue0();
		GBEdge[] e = components.getValue1();

		UOPair<GBNode> key1 = new UOPair<>(n[1], n[2]);
		UOPair<GBNode> key2 = new UOPair<>(n[4], n[5]);

		editorData.removeSelection(n[0]);
		assertFalse(editorData.getSelectedNodes().contains(n[0]));
		assertFalse(editorData.isSelected(n[0]));

		editorData.removeSelection(e[0]);
		assertFalse(editorData.getSelectedEdges().containsKey(key1));
		assertTrue(editorData.selectionsEmpty());

		editorData.removeAllHighlights();
		assertFalse(editorData.getHighlightedNodes().contains(n[3]));
		assertFalse(editorData.getHighlightedEdges().containsKey(key2));
		assertTrue(editorData.highlightsEmpty());

		editorData.clearEdgeBasePoint();
		assertNull(editorData.getEdgeBasePoint());

		editorData.clearPathBasePoint();
		assertNull(editorData.getPathBasePoint());

		editorData.clearPreviewEdge();
		assertNull(editorData.getPreviewEdge());
	}

}
