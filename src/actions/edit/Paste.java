package actions.edit;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.awt.Point;

import org.javatuples.Triplet;

import actions.ReversibleAction;
import structures.UnorderedNodePair;
import ui.Editor;
import util.ClipboardUtils;
import util.CoordinateUtils;
import clipboard.Clipboard;
import components.Edge;
import components.Node;
import context.GraphBuilderContext;

/**
 * An instance is a paste of one or many graph components.
 * 
 * @author Brian
 */
public class Paste extends ReversibleAction {
	
	private static final long serialVersionUID = -3846799653570593379L;
	
	private static final int PASTE_OFFSET_X = 30;
	private static final int PASTE_OFFSET_Y = 30;
	
	private Set<Node> previousSelectedNodes;
	private Set<Edge> previousSelectedEdges;
	
	private Set<Node> pastedNodes;
	private Map<UnorderedNodePair, List<Edge>> pastedEdges;
	
	private Map<Node, Node> oldToNew;
	
	private int maxX;
	private int maxY;
	private Point originalCenterOfMass;
	private Point specifiedCenterOfMass;
	
	/**
	 * @param ctxt          The context in which this action occurs.
	 * @param pasteLocation The provided location at which to paste the copied components.
	 */
	public Paste(GraphBuilderContext ctxt, Point pasteLocation) {
		this(ctxt);
		specifiedCenterOfMass = pasteLocation;
	}

	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public Paste(GraphBuilderContext ctxt) {
		super(ctxt);
		Clipboard currentClipboard = this.getContext().getClipboard();
		Triplet<Set<Node>, Map<Node, Node>, Point> copyNodes = ClipboardUtils.copyNodes(currentClipboard.getNodes());
		pastedNodes = copyNodes.getValue0();
		oldToNew = copyNodes.getValue1();
		maxX = copyNodes.getValue2().x;
		maxY = copyNodes.getValue2().y;
		originalCenterOfMass = CoordinateUtils.centerOfMass(currentClipboard.getNodes());
		pastedEdges = ClipboardUtils.copyEdges(currentClipboard.getEdges(), oldToNew);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Clipboard currentClipboard = this.getContext().getClipboard();

		// Get the location of paste "center of mass" point
		Point pastePoint;
		if (specifiedCenterOfMass != null) {
			// If paste point is explicitly specified (right click menu -> paste) 
			pastePoint = new Point(specifiedCenterOfMass);
		} else {
			// If paste point is implicit (ctrl + V)
			pastePoint = new Point(originalCenterOfMass);
			int offsetMultiplier = currentClipboard.getTimesPasted() + 1;
			pastePoint.x += PASTE_OFFSET_X * offsetMultiplier;
			pastePoint.y += PASTE_OFFSET_Y * offsetMultiplier;
		}
		
		// Make sure the bounding box of pasted nodes is "in bounds"
		Editor editor = this.getContext().getGUI().getEditor();
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
		previousSelectedNodes = new HashSet<Node>(editor.getSelections().getValue0());
		previousSelectedEdges = new HashSet<Edge>(editor.getSelectedEdges());
		editor.removeAllSelections();
		
		// Add the new nodes and edges to the context
		for (Node oldNode : currentClipboard.getNodes()) {
			Point oldLocation = oldNode.getNodePanel().getCoords();
			Node newNode = oldToNew.get(oldNode);
			newNode.getNodePanel().setCoords(oldLocation.x + diffX, oldLocation.y + diffY);
			this.getContext().addNode(newNode);
		}
		
		// Select pasted nodes
		editor.addSelections(pastedNodes);
		
		for (List<Edge> edgeList : pastedEdges.values()) {
			for (int i = 0 ; i < edgeList.size() ; i++) {
				this.getContext().addEdge(edgeList.get(i), i);
			}
			
			// Select pasted edges
			editor.addSelections(edgeList);
		}
		
		if (specifiedCenterOfMass == null) {
			currentClipboard.updateTimesPasted(true);
		}
		
		editor.repaint();
	}

	@Override
	public void undo() {
		// Remove pasted nodes (along with their edges)
		for (Node n : pastedNodes) {
			getContext().removeNode(n);
		}
		
		// Subtract one from times pasted if necessary
		if (specifiedCenterOfMass == null) {
			Clipboard currentClipboard = this.getContext().getClipboard();
			currentClipboard.updateTimesPasted(false);
		}
		
		// Reselect old selections
		Editor editor = this.getContext().getGUI().getEditor();
		editor.removeAllSelections();
		editor.addSelections(previousSelectedNodes);
		editor.addSelections(previousSelectedEdges);
		
		editor.repaint();
	}
	
}
