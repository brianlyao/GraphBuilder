package actions.edit;

import actions.ReversibleAction;
import context.GBContext;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import org.javatuples.Quartet;
import structures.UOPair;
import util.ClipboardUtils;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A delete action on graph components (remove all selected components)
 *
 * @author Brian
 */
public class Delete extends ReversibleAction {

	private static final long serialVersionUID = -3043275811020293526L;

	private Set<GBNode> deletedNodes;
	private Set<GBEdge> deletedEdges;
	private Map<UOPair<GBNode>, List<GBEdge>> originalEdgeMap;
	private Map<UOPair<GBNode>, List<GBEdge>> deletedEdgeMap;

	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public Delete(GBContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Quartet<Set<GBNode>, Set<GBEdge>, Map<UOPair<GBNode>, List<GBEdge>>,
			Map<UOPair<GBNode>, List<GBEdge>>> quartet = ClipboardUtils.deleteSelections(this.getContext());
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
