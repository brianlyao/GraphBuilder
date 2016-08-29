package context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import components.Edge;
import components.Node;

import actions.Action;
import actions.ReversibleAction;
import uielements.GUI;

/** A collection of fields necessary to keep track of the program's state. */
public class GraphBuilderContext {
	
	private GUI gui;
	
	private HashSet<Node> nodes; // The set of all nodes
	private HashSet<Edge> edges; // The set of all edges
	private HashMap<Node, HashMap<Node, ArrayList<Edge>>> edgeMap = new HashMap<>(); // A mapping from a pair of nodes to the edges between them
	
	private Stack<ReversibleAction> actionHistory; // A collection of the most recent reversible actions
	private Stack<ReversibleAction> undoHistory; // A collection of the most recent undos
	
	/**
	 * @param g The GUI corresponding to this context.
	 */
	public GraphBuilderContext(GUI g) {
		gui = g;
		
		// Initialize graph data structure
		nodes = new HashSet<>(); //Set of all nodes in the editor
		edges = new HashSet<>();
		edgeMap = new HashMap<>();
		
		// Initialize action histories
		actionHistory = new Stack<>();
		undoHistory = new Stack<>();
	}
	
	/**
	 * Get the GUI corresponding to this context.
	 * 
	 * @return The GUI corresponding to this context.
	 */
	public GUI getGUI() {
		return gui;
	}
	
	/**
	 * Push an action onto the action history without performing it.
	 * 
	 * @param a The action to push onto the history.
	 */
	public void pushAction(ReversibleAction a) {
		if(a == null)
			return;
		actionHistory.push(a);
	}
	
	/**
	 * Perform a particular action. If it is reversible, keep track of it in the action history.
	 * 
	 * @param a The action to perform.
	 */
	public void doAction(Action a) {
		if(a == null)
			return;
		a.perform();
		if(a instanceof ReversibleAction)
			actionHistory.push((ReversibleAction) a);
	}
	
	/**
	 * Undo the last action in the history. If the history is empty, this has no effect.
	 */
	public void undoAction() {
		if(!actionHistory.isEmpty()) {
			ReversibleAction a = actionHistory.pop();
			a.undo();
			undoHistory.push(a);
		}
	}
	
	/**
	 * Redo the last action which was undone. If no action has been undone, this has no effect.
	 */
	public void redoAction() {
		if(!undoHistory.isEmpty())
			doAction(undoHistory.pop());
	}
	
	/**
	 * Get the set of all nodes in the graph.
	 * 
	 * @return A HashSet of all nodes in the graph.
	 */
	public HashSet<Node> getNodes() {
		return nodes;
	}
	
	/**
	 * Get the set of all edges in the graph.
	 * 
	 * @return A HashSet of all edges in the graph.
	 */
	public HashSet<Edge> getEdges() {
		return edges;
	}
	
	/**
	 * Get the edge map, a mapping from a pair of nodes to a list of edges.
	 * 
	 * @return The edge map.
	 */
	public HashMap<Node, HashMap<Node, ArrayList<Edge>>> getEdgeMap() {
		return edgeMap;
	}

}
