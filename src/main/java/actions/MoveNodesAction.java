package actions;

import context.GBContext;
import graph.components.display.NodePanel;
import structures.OrderedPair;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;

/**
 * An instance is a movement of one or more node(s) on the editor workspace.
 *
 * @author Brian Yao
 */
public class MoveNodesAction extends ReversibleAction {

	private static final long serialVersionUID = -844830261170043610L;

	// Mapping node panels to its previous and current positions
	private Map<NodePanel, OrderedPair<Point>> movementMap;

	/**
	 * @param ctxt        The context this action belongs in.
	 * @param movementMap The map from node panel to starting and ending points.
	 */
	public MoveNodesAction(GBContext ctxt, Map<NodePanel, OrderedPair<Point>> movementMap) {
		super(ctxt);
		this.movementMap = movementMap;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (Map.Entry<NodePanel, OrderedPair<Point>> entry : movementMap.entrySet())
			entry.getKey().setCoords(entry.getValue().getSecond());
		this.getContext().getGUI().getEditor().repaint();
	}

	@Override
	public void undo() {
		for (Map.Entry<NodePanel, OrderedPair<Point>> entry : movementMap.entrySet())
			entry.getKey().setCoords(entry.getValue().getFirst());
		this.getContext().getGUI().getEditor().repaint();
	}

}
