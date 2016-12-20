package actions.edit;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.javatuples.Pair;
import org.javatuples.Quartet;

import actions.ReversibleAction;
import structures.UnorderedNodePair;
import util.ClipboardUtils;
import components.Edge;
import components.Node;
import context.GraphBuilderContext;

/**
 * A cut action on graph components (copy and then delete all selected components).
 * 
 * @author Brian
 */
public class Cut extends ReversibleAction {

	private static final long serialVersionUID = 4469871982980822405L;
	
	private HashSet<Node> deletedNodes;
	private HashSet<Edge> deletedEdges;
	private HashMap<UnorderedNodePair, ArrayList<Edge>> originalEdgeMap;
	private HashMap<UnorderedNodePair, ArrayList<Edge>> deletedEdgeMap;
	
	private boolean full;
	
	/**
	 * @param ctxt The context in which this action occurs.
	 * @param full Whether this cut performs a full subgraph copy or not.
	 */
	public Cut(GraphBuilderContext ctxt, boolean full) {
		super(ctxt);
		this.full = full;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Copy selections to clipboard
		Pair<HashSet<Node>, HashMap<UnorderedNodePair, ArrayList<Edge>>> pair = ClipboardUtils.separateSelections(this.getContext());
		HashMap<UnorderedNodePair, ArrayList<Edge>> edges;
		if (full) {
			edges = ClipboardUtils.getSubEdgeMap(this.getContext(), pair.getValue0());
		} else {
			edges = pair.getValue1();
		}
		this.getContext().getClipboard().setContents(pair.getValue0(), edges);
		this.getContext().getGUI().getMainMenuBar().updateWithCopy();
		
		// Delete selections
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
