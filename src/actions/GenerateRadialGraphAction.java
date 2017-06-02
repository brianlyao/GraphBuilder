package actions;

import graph.Graph;
import graph.GraphDisplayFactory;

import java.awt.event.ActionEvent;

import context.GraphBuilderContext;

/**
 * An action which represents adding components from one graph to another. In
 * particular, the graph we are adding is typically generated and lacks any
 * display data. The nodes are automatically arranged in a radially symmetric
 * manner.
 * 
 * @author Brian Yao
 */
public class GenerateRadialGraphAction extends ReversibleAction {

	private static final long serialVersionUID = 8152429205865815337L;
	
	private Graph generatedGraph;
	
	/**
	 * Creates an action for adding a graph to the provided context. It is
	 * expected that the context contains an associated GUI object.
	 * 
	 * @param ctxt The context to add a generated graph to.
	 * @param graph The graph to add.
	 */
	public GenerateRadialGraphAction(GraphBuilderContext ctxt, Graph graph) {
		super(ctxt);
		generatedGraph = graph;
		if (graph.getNodes().iterator().next().getNodePanel() == null) {
			GraphDisplayFactory.generateRadialNodePanels(ctxt.getGUI(), graph);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.getContext().addAll(generatedGraph);
	}

	@Override
	public void undo() {
		this.getContext().removeAll(generatedGraph);
	}

}
