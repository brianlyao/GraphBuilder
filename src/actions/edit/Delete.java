package actions.edit;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	private Set<Node> deletedNodes;
	private Set<Edge> deletedEdges;
	private Map<UnorderedNodePair, List<Edge>> originalEdgeMap;
	private Map<UnorderedNodePair, List<Edge>> deletedEdgeMap;
	
	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public Delete(GraphBuilderContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
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
