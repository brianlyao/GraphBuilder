package actions.edit;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import structures.UnorderedNodePair;
import ui.Editor;
import util.ClipboardUtils;
import components.Edge;
import components.GraphComponent;
import components.Node;
import context.GraphBuilderContext;
import actions.ReversibleAction;

/**
 * A duplicate action on one or multiple graph components, which does
 * not add duplicated contents to the clipboard.
 * 
 * @author Brian
 */
public class Duplicate extends ReversibleAction {

	private static final long serialVersionUID = -3041348863766011160L;
	
	private static final int DUPLICATE_OFFSET_X = 30;
	private static final int DUPLICATE_OFFSET_Y = 30;

	private HashSet<Node> nodesToDuplicate;
	private HashMap<UnorderedNodePair, ArrayList<Edge>> edgesToDuplicate;
	private HashSet<Node> duplicatedNodes;
	private HashMap<UnorderedNodePair, ArrayList<Edge>> duplicatedEdges;
	
	private HashMap<Node, Node> oldToNew;
	
	private HashSet<GraphComponent> previousSelections;
	
	private int maxX;
	private int maxY;
	
	/**
	 * @param ctxt The context in which this action occurs.
	 * @param full Whether this is a full subgraph duplicate.
	 */
	public Duplicate(GraphBuilderContext ctxt, boolean full) {
		super(ctxt);
		Pair<HashSet<Node>, HashMap<UnorderedNodePair, ArrayList<Edge>>> pair = ClipboardUtils.separateSelections(this.getContext());
		nodesToDuplicate = pair.getValue0();
		edgesToDuplicate = pair.getValue1();
		
		// Copy nodes and edges
		Triplet<HashSet<Node>, HashMap<Node, Node>, Point> copyNodes = ClipboardUtils.copyNodes(nodesToDuplicate);
		oldToNew = copyNodes.getValue1();
		duplicatedNodes = copyNodes.getValue0();
		if (full) {
			HashMap<UnorderedNodePair, ArrayList<Edge>> subEdgeMap = ClipboardUtils.getSubEdgeMap(this.getContext(), pair.getValue0());
			duplicatedEdges = ClipboardUtils.copyEdges(subEdgeMap, oldToNew);
		} else {
			duplicatedEdges = ClipboardUtils.copyEdges(edgesToDuplicate, oldToNew);
		}
		
		maxX = copyNodes.getValue2().x;
		maxY = copyNodes.getValue2().y;
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
		previousSelections = new HashSet<>(editor.getSelections());
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
		
		for (ArrayList<Edge> edgeList : duplicatedEdges.values()) {
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
		for (GraphComponent gc : previousSelections) {
			editor.addSelection(gc);
		}
		
		editor.repaint();
	}
	
}