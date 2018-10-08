package actions.edit;

import actions.ReversibleAction;
import context.GBContext;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import org.javatuples.Pair;
import structures.UOPair;
import ui.Editor;
import util.ClipboardUtils;
import util.StructureUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A duplicate action on one or multiple graph components, which does
 * not add duplicated contents to the clipboard.
 *
 * @author Brian Yao
 */
public class Duplicate extends ReversibleAction {

	private static final long serialVersionUID = -3041348863766011160L;

	private static final int DUPLICATE_OFFSET_X = 30;
	private static final int DUPLICATE_OFFSET_Y = 30;

	private Set<GBNode> nodesToDuplicate;
	private Map<UOPair<GBNode>, List<GBEdge>> edgesToDuplicate;
	private Set<GBNode> duplicatedNodes;
	private Map<UOPair<GBNode>, List<GBEdge>> duplicatedEdges;

	private Map<GBNode, GBNode> oldToNew;

	private Set<GBNode> previousSelectedNodes;
	private Set<GBEdge> previousSelectedEdges;

	private int maxX;
	private int maxY;

	/**
	 * @param ctxt The context in which this action occurs.
	 * @param full Whether this is a full subgraph duplicate.
	 */
	public Duplicate(GBContext ctxt, boolean full) {
		super(ctxt);
		Editor editor = this.getContext().getGUI().getEditor();
		nodesToDuplicate = new HashSet<>(editor.getSelections().getValue0());
		edgesToDuplicate = StructureUtils.shallowCopy(editor.getSelections().getValue1());

		// Copy nodes and edges
		Pair<Set<GBNode>, Map<GBNode, GBNode>> copyNodes = ClipboardUtils.copyNodes(nodesToDuplicate);
		oldToNew = copyNodes.getValue1();
		duplicatedNodes = copyNodes.getValue0();
		if (full) {
			Map<UOPair<GBNode>, List<GBEdge>> subEdgeMap = ClipboardUtils.getSubEdgeMap(this.getContext(), nodesToDuplicate);
			duplicatedEdges = ClipboardUtils.copyEdges(subEdgeMap, oldToNew);
		} else {
			duplicatedEdges = ClipboardUtils.copyEdges(edgesToDuplicate, oldToNew);
		}

		Point lowerRight = ClipboardUtils.lowerRightCorner(nodesToDuplicate);
		maxX = lowerRight.x;
		maxY = lowerRight.y;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Make sure the bounding box of pasted nodes is "in bounds"
		Editor editor = this.getContext().getGUI().getEditor();
		int editorMaxX = editor.getWidth();
		int editorMaxY = editor.getHeight();
		int diffX = DUPLICATE_OFFSET_X;
		int diffY = DUPLICATE_OFFSET_Y;
		if (maxX + DUPLICATE_OFFSET_X > editorMaxX) {
			diffX = editorMaxX - maxX;
		}
		if (maxY + DUPLICATE_OFFSET_Y > editorMaxY) {
			diffY = editorMaxY - maxY;
		}

		// Deselect all currently selected
		previousSelectedNodes = new HashSet<>(editor.getSelections().getValue0());
		previousSelectedEdges = new HashSet<>(editor.getSelectedEdges());
		editor.removeAllSelections();

		// Add the new nodes and edges to the context
		for (GBNode oldNode : nodesToDuplicate) {
			Point oldLocation = oldNode.getNodePanel().getCoords();
			GBNode newNode = oldToNew.get(oldNode);
			newNode.getNodePanel().setCoords(oldLocation.x + diffX, oldLocation.y + diffY);
			this.getContext().addNode(newNode);
		}

		// Select new nodes
		editor.addSelections(duplicatedNodes);

		for (List<GBEdge> edgeList : duplicatedEdges.values()) {
			for (int i = 0; i < edgeList.size(); i++) {
				this.getContext().addEdge(edgeList.get(i), i);
			}

			// Select new edges
			editor.addSelections(edgeList);
		}

		editor.repaint();
	}

	@Override
	public void undo() {
		// Remove pasted nodes (along with their edges)
		for (GBNode n : duplicatedNodes) {
			getContext().removeNode(n);
		}

		// Reselect old selections
		Editor editor = this.getContext().getGUI().getEditor();
		editor.removeAllSelections();
		editor.addSelections(previousSelectedNodes);
		editor.addSelections(previousSelectedEdges);

		editor.repaint();
	}

}
