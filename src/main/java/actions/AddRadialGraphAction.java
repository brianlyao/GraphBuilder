package actions;

import graph.GraphDisplayFactory;
import graph.components.gb.GBEdge;
import graph.components.gb.GBGraph;
import graph.components.gb.GBNode;
import org.javatuples.Pair;

import java.awt.event.ActionEvent;
import java.util.Set;

/**
 * An action which represents adding components from one graph to another. In
 * particular, the graph we are adding is typically generated and lacks any
 * display data. The nodes are automatically arranged in a radially symmetric
 * manner.
 *
 * @author Brian Yao
 */
public class AddRadialGraphAction extends ReversibleAction {

	private static final long serialVersionUID = 8152429205865815337L;

	private Pair<Set<GBNode>, Set<GBEdge>> generatedComponents;

	/**
	 * Creates an action for adding a graph to the provided context. It is
	 * expected that the context contains an associated GBFrame object.
	 *
	 * @param graph The graph to add.
	 */
	public AddRadialGraphAction(GBGraph graph) {
		super(graph.getContext());
		generatedComponents = GraphDisplayFactory.generateRadialNodePanels(graph.getContext().getGUI(), graph);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.getContext().addNodes(generatedComponents.getValue0());
		this.getContext().addEdges(generatedComponents.getValue1());
	}

	@Override
	public void undo() {
		this.getContext().removeAll(generatedComponents.getValue0());
	}

}
