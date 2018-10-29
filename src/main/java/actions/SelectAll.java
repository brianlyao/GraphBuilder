package actions;

import context.GBContext;
import structures.EditorData;
import ui.Editor;

import java.awt.event.ActionEvent;

/**
 * An action for selecting all components in the context.
 *
 * @author Brian Yao
 */
public class SelectAll extends SimpleAction {

	/**
	 * @param context The context in which to select all components.
	 */
	public SelectAll(GBContext context) {
		super(context);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Editor editor = this.getContext().getGUI().getEditor();
		EditorData editorData = editor.getData();
		editorData.addSelections(this.getContext().getGbNodes());
		this.getContext().getGbEdges().values().forEach(editorData::addSelections);
		editor.repaint();
	}

}
