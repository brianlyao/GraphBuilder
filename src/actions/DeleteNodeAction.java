package actions;

import java.awt.event.ActionEvent;
import java.util.HashSet;

import components.Edge;
import components.Node;
import context.GraphBuilderContext;

/** An instance represents the action of deleting an existing node from the editor panel. */
public class DeleteNodeAction extends ReversibleAction {

	private static final long serialVersionUID = -745312635068655475L;
	
	private Node node;
	
	private HashSet<Edge> removedEdges; // The set of edges that was removed due to the node's removal
	
	/**
	 * @param ctxt The context in which this action occurs.
	 * @param n    The node which was deleted.
	 */
	public DeleteNodeAction(GraphBuilderContext ctxt, Node n) {
		super(ctxt);
		node = n;
		
		removedEdges = new HashSet<>();
		for(Edge e : ctxt.getEdges())
			if(e.hasEndpoint(n))
				removedEdges.add(e);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		removedEdges = getContext().removeNode(node);
		addSelfToHistory();
		getContext().updateSaveState();
	}
	
	@Override
	public void undo() {
		getContext().addNode(node);
		if(removedEdges != null)
			for(Edge e : removedEdges)
				getContext().addEdge(e);
		addSelfToUndoHistory();
		getContext().updateSaveState();
	}
	
}
