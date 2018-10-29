package actions.edit;

import actions.ReversibleAction;
import clipboard.Clipboard;
import context.GBContext;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import org.javatuples.Pair;
import structures.EditorData;
import structures.OrderedPair;
import structures.UOPair;
import ui.Editor;
import util.ClipboardUtils;
import util.CoordinateUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An instance is a paste of graph components.
 *
 * @author Brian Yao
 */
public class Paste extends ReversibleAction {

	private static final long serialVersionUID = -3846799653570593379L;

	private static final int PASTE_OFFSET_X = 30;
	private static final int PASTE_OFFSET_Y = 30;

	private Set<GBNode> previousSelectedNodes;
	private Set<GBEdge> previousSelectedEdges;

	private Set<GBNode> copiedNodes;
	private Set<GBNode> pastedNodes;
	private Map<UOPair<GBNode>, List<GBEdge>> pastedEdges;

	private Map<GBNode, GBNode> oldToNew;

	private int maxX;
	private int maxY;
	private Point originalCenterOfMass;
	private Point specifiedCenterOfMass;

	/**
	 * @param ctxt          The context in which this action occurs.
	 * @param pasteLocation The provided location at which to paste the copied components.
	 */
	public Paste(GBContext ctxt, Point pasteLocation) {
		this(ctxt);
		specifiedCenterOfMass = pasteLocation;
	}

	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public Paste(GBContext ctxt) {
		super(ctxt);

		Clipboard clipboard = this.getContext().getClipboard();
		Pair<Set<GBNode>, Map<GBNode, GBNode>> copyNodes = ClipboardUtils.copyNodes(clipboard.getNodes());

		copiedNodes = new HashSet<>(clipboard.getNodes());
		pastedNodes = new HashSet<>(copyNodes.getValue0());
		oldToNew = copyNodes.getValue1();
		pastedEdges = ClipboardUtils.copyEdges(clipboard.getEdges(), oldToNew);

		Point lowerRight = ClipboardUtils.lowerRightCorner(clipboard.getNodes());
		maxX = lowerRight.x;
		maxY = lowerRight.y;
		originalCenterOfMass = CoordinateUtils.centerOfMass(clipboard.getNodes());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Get the location of paste "center of mass" point
		Point pastePoint;
		if (specifiedCenterOfMass != null) {
			// If paste point is explicitly specified (right click menu -> paste) 
			pastePoint = new Point(specifiedCenterOfMass);
		} else {
			// If paste point is implicit (ctrl + V)
			pastePoint = new Point(originalCenterOfMass);
			int offsetMultiplier = this.getContext().getClipboard().getTimesPasted() + 1;
			pastePoint.x += PASTE_OFFSET_X * offsetMultiplier;
			pastePoint.y += PASTE_OFFSET_Y * offsetMultiplier;
		}

		// Make sure the bounding box of pasted nodes is "in bounds"
		Editor editor = this.getContext().getGUI().getEditor();
		EditorData editorData = editor.getData();
		int editorMaxX = editor.getWidth();
		int editorMaxY = editor.getHeight();
		int newMaxX = pastePoint.x + maxX - originalCenterOfMass.x;
		int newMaxY = pastePoint.y + maxY - originalCenterOfMass.y;
		newMaxX = Math.min(newMaxX, editorMaxX);
		newMaxY = Math.min(newMaxY, editorMaxY);

		int diffX = newMaxX - maxX;
		int diffY = newMaxY - maxY;
		pastePoint.x += diffX;
		pastePoint.y += diffY;

		// Deselect all currently selected
		previousSelectedNodes = new HashSet<>(editorData.getSelectedNodes());
		previousSelectedEdges = new HashSet<>();
		editorData.getSelectedEdges().values().forEach(previousSelectedEdges::addAll);
		editorData.removeAllSelections();

		// Add the new nodes and edges to the context
		Map<GBNode, OrderedPair<Integer>> relativePos = this.getContext().getClipboard().getPosFromCenterOfMass();
		for (GBNode oldNode : copiedNodes) {
			Point oldLocation = new Point(originalCenterOfMass.x + relativePos.get(oldNode).getFirst(),
										  originalCenterOfMass.y + relativePos.get(oldNode).getSecond());
			GBNode newNode = oldToNew.get(oldNode);
			newNode.getNodePanel().setCoords(oldLocation.x + diffX, oldLocation.y + diffY);
			this.getContext().addNode(newNode);
		}

		// Select pasted nodes
		editorData.addSelections(pastedNodes);

		// Add pasted edges
		for (List<GBEdge> edgeList : pastedEdges.values()) {
			this.getContext().addEdges(edgeList);

			// Select pasted edges
			editorData.addSelections(edgeList);
		}

		if (specifiedCenterOfMass == null) {
			this.getContext().getClipboard().updateTimesPasted(true);
		}

		editor.repaint();
	}

	@Override
	public void undo() {
		// Remove pasted nodes (along with their edges)
		for (GBNode n : pastedNodes) {
			getContext().removeNode(n);
		}

		// Subtract one from times pasted if necessary
		if (specifiedCenterOfMass == null) {
			this.getContext().getClipboard().updateTimesPasted(false);
		}

		// Reselect old selections
		Editor editor = this.getContext().getGUI().getEditor();
		editor.getData().removeAllSelections();
		editor.getData().addSelections(previousSelectedNodes);
		editor.getData().addSelections(previousSelectedEdges);

		editor.repaint();
	}

}
