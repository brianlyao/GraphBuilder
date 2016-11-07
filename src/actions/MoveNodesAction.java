package actions;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import components.display.NodePanel;
import context.GraphBuilderContext;
import structures.OrderedPair;

/**
 * An instance is a movement of one or more node(s) on the editor workspace.
 * 
 * @author Brian
 */
public class MoveNodesAction extends ReversibleAction {

	private static final long serialVersionUID = -844830261170043610L;
	
	private HashMap<NodePanel, OrderedPair<Point>> movementMap;
	
	/**
	 * @param ctxt        The context this action belongs in.
	 * @param movementMap The map from node panel to starting and ending points.
	 */
	public MoveNodesAction(GraphBuilderContext ctxt, HashMap<NodePanel, OrderedPair<Point>> movementMap) {
		super(ctxt);
		this.movementMap = movementMap;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (Map.Entry<NodePanel, OrderedPair<Point>> entry : movementMap.entrySet())
			entry.getKey().setCoords(entry.getValue().getSecond());
		getContext().getGUI().getEditor().repaint();
	}

	@Override
	public void undo() {
		for (Map.Entry<NodePanel, OrderedPair<Point>> entry : movementMap.entrySet())
			entry.getKey().setCoords(entry.getValue().getFirst());
		getContext().getGUI().getEditor().repaint();
	}
	
}
