package context;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import components.Edge;
import components.GraphComponent;
import components.Node;
import actions.ReversibleAction;
import ui.Editor;
import ui.GUI;

/** A collection of fields necessary to keep track of the program's state. */
public class GraphBuilderContext {
	
	private GUI gui;
	
	private HashSet<Node> nodes; // The set of all nodes
	private HashSet<Edge> edges; // The set of all edges
	private HashMap<Integer, GraphComponent> idMap;
	private HashMap<Node.Pair, ArrayList<Edge>> edgeMap; // A mapping from a pair of nodes to the edges between them
	
	private Stack<ReversibleAction> actionHistory; // A collection of the most recent reversible actions
	private Stack<ReversibleAction> undoHistory; // A collection of the most recent undos
	
	private boolean unsaved; // If the current graph is unsaved

	private File currentlyLoaded;
	
	private int actionIdOnLastSave;
	
	private int idPool;
	
	/**
	 * @param g The GUI corresponding to this context.
	 */
	public GraphBuilderContext(GUI g) {
		gui = g;
		
		// Initialize graph data structure
		nodes = new HashSet<>(); //Set of all nodes in the editor
		edges = new HashSet<>();
		idMap = new HashMap<>();
		edgeMap = new HashMap<>();
		
		// Initialize action histories
		actionHistory = new Stack<>();
		undoHistory = new Stack<>();
		actionIdOnLastSave = -1;
		
		// The idpool
		idPool = 0;
	}
	
	/**
	 * Remove the specified graph component.
	 * 
	 * @param gc The component to remove.
	 */
	public void remove(GraphComponent gc) {
		if (gc instanceof Node)
			removeNode((Node) gc); 
		else if(gc instanceof Edge)
			removeEdge((Edge) gc); 
	}
	
	/**
	 * Adds the specified node to the graph.
	 * 
	 * @param n The node to add to the graph.
	 */
	public void addNode(Node n) {
		Editor editor = gui.getEditor();
		nodes.add(n);
		editor.add(n.getNodePanel());
		editor.repaint();
		editor.revalidate();
	}
	
	/**
	 * Removes the specified node from the graph.
	 * 
	 * @param n The node to remove.
	 * @return The map of edges that was removed as a result of this node's removal.
	 */
	public HashMap<Node.Pair, ArrayList<Edge>> removeNode(Node n) {
		// Remove the node from the set of all nodes in this context
		nodes.remove(n);
		
		// Revalidate the editor panel
		Editor editor = gui.getEditor();
		editor.remove(n.getNodePanel());
		editor.repaint();
		editor.revalidate();
		
		Iterator<Map.Entry<Node.Pair, ArrayList<Edge>>> edgeMapIterator = edgeMap.entrySet().iterator();
		Iterator<Edge> lineit = edges.iterator();
		
		// Remove all edges neighboring the removed node
		while (lineit.hasNext())
			if (lineit.next().hasEndpoint(n))
				lineit.remove();
		
		// Remove all edges neighboring the removed node from edge map
		// Keep track of which edges are removed, and return it
		HashMap<Node.Pair, ArrayList<Edge>> removedSubEdgeMap = new HashMap<>();
		Map.Entry<Node.Pair, ArrayList<Edge>> temp;
		while (edgeMapIterator.hasNext()) {
			temp = edgeMapIterator.next();
			if(temp.getKey().hasNode(n)) {
				removedSubEdgeMap.put(temp.getKey(), temp.getValue());
				edgeMapIterator.remove();
			}
		}
		
		// If this node was the "base point" for an edge, reset the base point
		if (editor.getEdgeBasePoint() == n)
			editor.setEdgeBasePoint(null);
		
		return removedSubEdgeMap;
	}
	
	/** 
	 * Add an edge to the graph.
	 * 
	 * @param e        The edge we want to add to add to the graph.
	 * @param position The position (from the left) of the edge relative to other edges sharing e's endpoints.
	 */
	public void addEdge(Edge e, int position) {
		edges.add(e);
		
		Node[] ends = e.getEndpoints();
		Node.Pair endsp = new Node.Pair(ends[0], ends[1]);
		
		// Find the list of edges to add this one to, or create it if it doesn't exist
		ArrayList<Edge> addTo = edgeMap.get(endsp);
		if (addTo == null) {
			edgeMap.put(endsp, new ArrayList<Edge>());
			addTo = edgeMap.get(endsp);
		}
		
		// If the position index is out of bounds, just add it to the end
		if (position < 0 || position > addTo.size())
			addTo.add(e);
		else
			addTo.add(position, e);
	}
	
	/** 
	 * Remove an edge from the graph.
	 * 
	 * @param e The edge we want to add to add to the graph.
	 * @return The position of the edge before it was removed.
	 */
	public int removeEdge(Edge e) {
		edges.remove(e);
		
		Node[] ends = e.getEndpoints();
		Node.Pair endsp = new Node.Pair(ends[0], ends[1]);
		ArrayList<Edge> removeFrom = edgeMap.get(endsp);
		if (removeFrom == null)
			throw new IllegalArgumentException("The edge " + e + " is not in the graph, and cannot be removed.");
		int index = removeFrom.indexOf(e);
		removeFrom.remove(e);
		return index;
	}
	
	/**
	 * Add a reversible action to the top of the action history stack.
	 * 
	 * @param action           The action to push.
	 * @param affectsSaveState Whether the action affects the save state (will making this action cause the file to be unsaved?).
	 */
	public void pushReversibleAction(ReversibleAction action, boolean affectsSaveState) {
		actionHistory.push(action);
		if (affectsSaveState)
			updateSaveState();
	}
	
	/**
	 * Add an reversible action to the top of the undo history stack.
	 * 
	 * @param action           The action to push.
	 * @param affectsSaveState Whether the action affects the save state (will making this action cause the file to be unsaved?).
	 */
	public void pushReversibleUndoAction(ReversibleAction action, boolean affectsSaveState) {
		undoHistory.push(action);
		if (affectsSaveState)
			updateSaveState();
	}
	
	/**
	 * Updates the "saved" state of the current graph.
	 */
	public void updateSaveState() { 
		if ((actionHistory.isEmpty() && actionIdOnLastSave < 0) || actionHistory.peek().actionId() == actionIdOnLastSave)
			setAsSaved();
		else
			setAsUnsaved();
		gui.getMainMenuBar().setUndoEnabled(!actionHistory.isEmpty());
		gui.getMainMenuBar().setRedoEnabled(!undoHistory.isEmpty());
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
	 * Get the next id, and increment the id pool.
	 * 
	 * @return The next graph component id.
	 */
	public int getNextIDAndInc() {
		return idPool++;
	}
	
	/**
	 * Get the next id without modifying the id pool.
	 * 
	 * @return The next graph component id.
	 */
	public int getNextID() {
		return idPool;
	}
	
	/**
	 * Set the id pool (used when loading a file).
	 * 
	 * @param nid The value of the next id.
	 */
	public void setNextID(int nid) {
		idPool = nid;
	}
	
	/**
	 * Get the action history.
	 * 
	 * @return The Stack of reversible actions corresponding to the action history.
	 */
	public Stack<ReversibleAction> getActionHistory() {
		return actionHistory;
	}
	
	/**
	 * Get the undo history.
	 * 
	 * @return The Stack of reversible actions corresponding to the undo history.
	 */
	public Stack<ReversibleAction> getUndoHistory() {
		return undoHistory;
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
	 * Get the ID map (mapping from id to graph component).
	 * 
	 * @return The ID map.
	 */
	public HashMap<Integer, GraphComponent> getIdMap() {
		return idMap;
	}
	
	/**
	 * Get the edge map, a mapping from a pair of nodes to a list of edges.
	 * 
	 * @return The edge map.
	 */
	public HashMap<Node.Pair, ArrayList<Edge>> getEdgeMap() {
		return edgeMap;
	}

	/**
	 * Get the file which is currently loaded in this context.
	 * 
	 * @return The currently loaded file.
	 */
	public File getCurrentlyLoadedFile() {
		return currentlyLoaded;
	}
	
	/**
	 * Set the file which is loaded in this context.
	 * 
	 * @param f The to-be loaded file.
	 */
	public void setCurrentlyLoadedFile(File f) {
		currentlyLoaded = f;
	}
	
	/**
	 * Checks if the graph in this context is stored on disk.
	 * 
	 * @return true if the graph is stored in a file, and false otherwise.
	 */
	public boolean existsOnDisk() {
		return currentlyLoaded != null;
	}
	
	/**
	 * Checks if this graph is unsaved.
	 * 
	 * @return true if this graph is unsaved, and false otherwise.
	 */
	public boolean isUnsaved() {
		return unsaved;
	}
	
	public void setAsSaved() {
		unsaved = false;
		if (gui.getTitle().endsWith("*"))
			gui.setTitle(gui.getTitle().substring(0, gui.getTitle().length() - 1));
	}
	
	/**
	 * Mark the graph in this context as unsaved, which changes the GUI's appearance slightly.
	 */
	public void setAsUnsaved() {
		unsaved = true;
		if (!gui.getTitle().endsWith("*"))
			gui.setTitle(gui.getTitle() + "*");
	}
	
	public void setActionIdOnLastSave(int id) {
		actionIdOnLastSave = id;
	}

}
