package actions.edit;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;

import structures.UnorderedNodePair;
import ui.Editor;
import util.ClipboardUtils;
import util.StructureUtils;
import components.Edge;
import components.Node;
import context.GraphBuilderContext;
import actions.ReversibleAction;

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

	private Set<Node> nodesToDuplicate;
	private Map<UnorderedNodePair, List<Edge>> edgesToDuplicate;
	private Set<Node> duplicatedNodes;
	private Map<UnorderedNodePair, List<Edge>> duplicatedEdges;
	
	private Map<Node, Node> oldToNew;
	
	private Set<Node> previousSelectedNodes;
	private Set<Edge> previousSelectedEdges;
	
	private int maxX;
	private int maxY;
	
	/**
	 * @param ctxt The context in which this action occurs.
	 * @param full Whether this is a full subgraph duplicate.
	 */
	public Duplicate(GraphBuilderContext ctxt, boolean full) {
		super(ctxt);
		Editor editor = this.getContext().getGUI().getEditor();
		nodesToDuplicate = new HashSet<>(editor.getSelections().getValue0());
		edgesToDuplicate = StructureUtils.shallowCopy(editor.getSelections().getValue1());
		
		// Copy nodes and edges
		Pair<Set<Node>, Map<Node, Node>> copyNodes = ClipboardUtils.copyNodes(nodesToDuplicate);
		oldToNew = copyNodes.getValue1();
		duplicatedNodes = copyNodes.getValue0();
		if (full) {
			Map<UnorderedNodePair, List<Edge>> subEdgeMap = ClipboardUtils.getSubEdgeMap(this.getContext(), nodesToDuplicate);
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
		previousSelectedNodes = new HashSet<Node>(editor.getSelections().getValue0());
		previousSelectedEdges = new HashSet<Edge>(editor.getSelectedEdges());
		editor.removeAllSelections();
		
		// Add the new nodes and edges to the context
		for (Node oldNode : nodesToDuplicate) {
			Point oldLocation = oldNode.getNodePanel().getCoords();
			Node newNode = oldToNew.get(oldNode);
			newNode.getNodePanel().setCoords(oldLocation.x + diffX, oldLocation.y + diffY);
			this.getContext().addNode(newNode);
		}
		
		// Select new nodes
		editor.addSelections(duplicatedNodes);
		
		for (List<Edge> edgeList : duplicatedEdges.values()) {
			for (int i = 0 ; i < edgeList.size() ; i++) {
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
		for (Node n : duplicatedNodes) {
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
