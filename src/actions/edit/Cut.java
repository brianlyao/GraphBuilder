package actions.edit;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	private Set<Node> deletedNodes;
	private Set<Edge> deletedEdges;
	private Map<UnorderedNodePair, List<Edge>> originalEdgeMap;
	private Map<UnorderedNodePair, List<Edge>> deletedEdgeMap;
	
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
		Pair<Set<Node>, Map<UnorderedNodePair, List<Edge>>> pair = ClipboardUtils.separateSelections(this.getContext());
		Map<UnorderedNodePair, List<Edge>> edges;
		if (full) {
			edges = ClipboardUtils.getSubEdgeMap(this.getContext(), pair.getValue0());
		} else {
			edges = pair.getValue1();
		}
		this.getContext().getClipboard().setContents(pair.getValue0(), edges);
		this.getContext().getGUI().getMainMenuBar().updateWithCopy();
		
		// Delete selections
		Quartet<Set<Node>, Set<Edge>, Map<UnorderedNodePair, List<Edge>>,
				Map<UnorderedNodePair, List<Edge>>> quartet = ClipboardUtils.deleteSelections(this.getContext());
		deletedNodes = quartet.getValue0();
		deletedEdges = quartet.getValue1();
		originalEdgeMap = quartet.getValue2();
		deletedEdgeMap = quartet.getValue3();
		
		this.getContext().getGUI().getEditor().repaint();
	}

	@Override
	public void undo() {
		ClipboardUtils.undoDeleteComponents(this.getContext(), deletedNodes, deletedEdges, originalEdgeMap, deletedEdgeMap);
		this.getContext().getGUI().getEditor().repaint();
	}
	
}
