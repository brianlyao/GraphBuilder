package actions.edit;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

import actions.SimpleAction;
import structures.UnorderedNodePair;
import ui.Editor;
import util.ClipboardUtils;
import components.Edge;
import components.Node;
import context.GraphBuilderContext;

/**
 * A copy action on graph components (stores a copy of all selected components in the Clipboard).
 * 
 * @author Brian
 */
public class Copy extends SimpleAction {
	
	private static final long serialVersionUID = 3116865860340575301L;
	
	private boolean full;
	
	/**
	 * @param ctxt The context this action occurs in.
	 * @param full Whether this is a full subgraph copy or not.
	 */
	public Copy(GraphBuilderContext ctxt, boolean full) {
		super(ctxt);
		this.full = full;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Editor editor = this.getContext().getGUI().getEditor();
		Set<Node> nodes = editor.getSelections().getKey();
		Map<UnorderedNodePair, List<Edge>> edges;
		if (full) {
			edges = ClipboardUtils.getSubEdgeMap(this.getContext(), nodes);
		} else {
			edges = editor.getSelections().getValue();
		}
		this.getContext().getClipboard().setContents(nodes, edges);
		this.getContext().getGUI().getMainMenuBar().updateWithCopy();
	}
	
}
