package actions.edit;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.javatuples.Quartet;

import actions.ReversibleAction;
import structures.UnorderedNodePair;
import util.ClipboardUtils;
import components.Edge;
import components.Node;
import context.GraphBuilderContext;

/**
 * A delete action on graph components (remove all selected components)
 * 
 * @author Brian
 */
public class Delete extends ReversibleAction {

	private static final long serialVersionUID = -3043275811020293526L;
	
	private HashSet<Node> deletedNodes;
	private HashSet<Edge> deletedEdges;
	private HashMap<UnorderedNodePair, ArrayList<Edge>> originalEdgeMap;
	private HashMap<UnorderedNodePair, ArrayList<Edge>> deletedEdgeMap;
	
	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public Delete(GraphBuilderContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Quartet<HashSet<Node>, HashSet<Edge>, HashMap<UnorderedNodePair, ArrayList<Edge>>,
				HashMap<UnorderedNodePair, ArrayList<Edge>>> quartet = ClipboardUtils.deleteSelections(this.getContext());
		deletedNodes = quartet.getValue0();
		deletedEdges = quartet.getValue1();
		originalEdgeMap = quartet.getValue2();
		deletedEdgeMap = quartet.getValue3();
		
		this.getContext().getGUI().getEditor().repaint();
	}

	@Override
	public void undo() {
		ClipboardUtils.undoDeleteComponents(this.getContext(), deletedNodes, deletedEdges, originalEdgeMap, deletedEdgeMap);
	}
	
}
