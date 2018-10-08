package actions.edit;

import actions.SimpleAction;
import context.GBContext;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import structures.UOPair;
import ui.Editor;
import util.ClipboardUtils;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public Copy(GBContext ctxt, boolean full) {
		super(ctxt);
		this.full = full;
		this.getContext().getClipboard().clear();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Editor editor = this.getContext().getGUI().getEditor();
		Set<GBNode> nodes = editor.getSelections().getValue0();
		Map<UOPair<GBNode>, List<GBEdge>> edges;
		if (full) {
			edges = ClipboardUtils.getSubEdgeMap(this.getContext(), nodes);
		} else {
			edges = editor.getSelections().getValue1();
		}
		this.getContext().getClipboard().setContents(nodes, edges);
		this.getContext().getGUI().getMainMenuBar().updateWithCopy();
	}

}
