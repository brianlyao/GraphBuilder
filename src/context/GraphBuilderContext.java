package context;

import graph.Graph;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import clipboard.Clipboard;
import components.Edge;
import components.GraphComponent;
import components.Node;
import actions.ReversibleAction;
import structures.UnorderedNodePair;
import ui.Editor;
import ui.GUI;

/**
 * A collection of fields necessary to keep track of the program's state. Any time
 * a new file is created or we open another file, we are switching the context.
 * 
 * @author Brian
 */
public class GraphBuilderContext {
	
	private GUI gui; // The GUI using this context
	
	private Graph graph;
	
	private HashMap<Integer, GraphComponent> idMap; // A mapping from id to graph component
	
	private Stack<ReversibleAction> actionHistory; // A collection of the most recent reversible actions
	private Stack<ReversibleAction> undoHistory; // A collection of the most recent undos
	
	private Clipboard clipboard;
	
	private boolean unsaved; // If the current graph is unsaved

	private File currentlyLoaded;
	
	private int actionIdOnLastSave;
	
	private int idPool;
	
	/**
	 * @param graphConstraints The constraints on the graph this context manages.
	 */
	public GraphBuilderContext(int graphConstraints) {
		// Initialize graph data structure
		graph = new Graph(graphConstraints);
		
		idMap = new HashMap<Integer, GraphComponent>();
		
		// Initialize empty clipboard
		clipboard = new Clipboard(this);
		
		// Initialize action histories
		actionHistory = new Stack<ReversibleAction>();
		undoHistory = new Stack<ReversibleAction>();
		actionIdOnLastSave = -1;
		
		// The idpool
		idPool = 0;
	}
	
	/**
	 * Set the GUI object providing an interface for this context.
	 * 
	 * @param g The GUI for this context.
	 */
	public void setGUI(GUI g) {
		gui = g;
	}
	
	/**
	 * Remove the specified graph component.
	 * 
	 * @param gc The component to remove.
	 */
	public void remove(GraphComponent gc) {
		if (gc instanceof Node) {
			removeNode((Node) gc); 
		} else if (gc instanceof Edge) {
			removeEdge((Edge) gc);
		}
	}
	
	/**
	 * Remove the specified graph components.
	 * 
	 * @param components The collection of graph components to remove.
	 */
	public void removeAll(Collection<? extends GraphComponent> components) {
		for (GraphComponent gc : components) {
			remove(gc);
		}
	}
	
	/**
	 * Adds the specified node to the graph.
	 * 
	 * @param n The node to add to the graph.
	 */
	public void addNode(Node n) {
		graph.addNode(n);
		
		// Add to the editor panel
		if (gui != null) {
			Editor editor = gui.getEditor();
			editor.add(n.getNodePanel());
			editor.repaint();
			editor.revalidate();
		}
	}
	
	/**
	 * Removes the specified node from the graph.
	 * 
	 * @param n The node to remove.
	 * @return The map of edges that was removed as a result of this node's removal.
	 */
	public Map<UnorderedNodePair, List<Edge>> removeNode(Node n) {
		// Remove the node from the set of all nodes in this context
		Map<UnorderedNodePair, List<Edge>> removedSubEdgeMap = graph.removeNode(n);
		
		if (gui != null) {
			Editor editor = gui.getEditor();
			
			// If this node was the "base point" for an edge, reset the base point
			if (editor.getEdgeBasePoint() == n) {
				editor.setEdgeBasePoint(null);
			}
			
			// Remove this node and removed edges from selections
			editor.removeSelection(n);
			for (List<Edge> removedEdgeList : removedSubEdgeMap.values()) {
				editor.removeSelections(removedEdgeList);
			}
			this.getGUI().getMainMenuBar().updateWithSelection();
			
			// Revalidate the editor panel after removing the panel
			editor.remove(n.getNodePanel());
			editor.repaint();
			editor.revalidate();
		}
		
		return removedSubEdgeMap;
	}
	
	/** 
	 * Add an edge to the graph.
	 * 
	 * @param e        The edge we want to add to add to the graph.
	 * @param position The position (from the left) of the edge relative to other edges sharing e's endpoints.
	 */
	public void addEdge(Edge e, int position) {
		UnorderedNodePair endsp = new UnorderedNodePair(e);
		
		// Add edge to the endpoints' data
		endsp.getFirst().addEdge(e);
		endsp.getSecond().addEdge(e);
		
		// Find the list of edges to add this one to, or create it if it doesn't exist
		Map<UnorderedNodePair, List<Edge>> edgeMap = graph.getEdges();
		List<Edge> addTo = edgeMap.get(endsp);
		if (addTo == null) {
			graph.getEdges().put(endsp, new ArrayList<Edge>());
			addTo = edgeMap.get(endsp);
		}
		
		// If the position index is out of bounds, just add it to the end
		if (position < 0 || position > addTo.size()) {
			addTo.add(e);
		} else {
			addTo.add(position, e);
		}
	}
	
	/** 
	 * Remove an edge from the graph.
	 * 
	 * @param e The edge we want to add to add to the graph.
	 * @return The position of the edge before it was removed.
	 */
	public int removeEdge(Edge e) {
		int index = graph.removeEdge(e);
		
		// Remove the edge from selections
		this.getGUI().getEditor().removeSelection(e);
		this.getGUI().getMainMenuBar().updateWithSelection();
		
		return index;
	}
	
	/**
	 * Adds all of the components in the provided graph to this context. The
	 * components are assumed to already point toward this context.
	 * 
	 * @param graph The components to add to this context.
	 */
	public void addAll(Graph graph) {
		for (Node node : graph.getNodes()) {
			addNode(node);
		}
		
		for (Map.Entry<UnorderedNodePair, List<Edge>> edgeEntry : graph.getEdges().entrySet()) {
			Node first = edgeEntry.getKey().getFirst();
			Node second = edgeEntry.getKey().getSecond();
			for (Edge inList : edgeEntry.getValue()) {
				first.addEdge(inList);
				second.addEdge(inList);
			}
			
			this.graph.getEdges().put(edgeEntry.getKey(), edgeEntry.getValue());
		}
	}
	
	/**
	 * Removes all of the components in the provided graph to this context.
	 * 
	 * @param graph The components to add to this context.
	 */
	public void removeAll(Graph graph) {
		for (Node inGraph : graph.getNodes()) {
			removeNode(inGraph);
		}
	}
	
	/**
	 * Add a reversible action to the top of the action history stack.
	 * 
	 * @param action           The action to push.
	 * @param affectsSaveState Whether the action affects the save state
	 *                         (will making this action cause the file to be unsaved?).
	 * @param redo             Whether the pushed action is a redo of some undone action.
	 */
	public void pushReversibleAction(ReversibleAction action, boolean affectsSaveState, boolean redo) {
		actionHistory.push(action);
		if (affectsSaveState) {
			updateSaveState();
		}
		if (!redo) {
			undoHistory.clear();
		}
	}
	
	/**
	 * Add an reversible action to the top of the undo history stack.
	 * 
	 * @param action           The action to push.
	 * @param affectsSaveState Whether the action affects the save state
	 *                         (will making this action cause the file to be unsaved?).
	 */
	public void pushReversibleUndoAction(ReversibleAction action, boolean affectsSaveState) {
		undoHistory.push(action);
		if (affectsSaveState) {
			updateSaveState();
		}
	}
	
	/**
	 * Updates the "saved" state of the current graph.
	 */
	public void updateSaveState() { 
		if ((actionHistory.isEmpty() && actionIdOnLastSave < 0) || actionHistory.peek().actionId() == actionIdOnLastSave) {
			setAsSaved();
		} else {
			setAsUnsaved();
		}
		gui.getMainMenuBar().setUndoEnabled(!actionHistory.isEmpty());
		gui.getMainMenuBar().setRedoEnabled(!undoHistory.isEmpty());
	}
	
	/**
	 * Get the clipboard of the current context.
	 * 
	 * @return The clipboard object of this context.
	 */
	public Clipboard getClipboard() {
		return clipboard;
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
	public int getNextIdAndInc() {
		return idPool++;
	}
	
	/**
	 * Get the next id without modifying the id pool.
	 * 
	 * @return The next graph component id.
	 */
	public int getNextId() {
		return idPool;
	}
	
	/**
	 * Set the id pool (used when loading a file).
	 * 
	 * @param nid The value of the next id.
	 */
	public void setNextId(int nid) {
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
	 * @return A set of all nodes in the graph.
	 */
	public Set<Node> getNodes() {
		return graph.getNodes();
	}
	
	/**
	 * Get the set of all edges in the graph.
	 * 
	 * @return A set of all edges in the graph.
	 */
	public Set<Edge> getEdgeSet() {
		return graph.edgeSet();
	}
	
	/**
	 * Get the ID map (mapping from id to graph component).
	 * 
	 * @return The ID map.
	 */
	public Map<Integer, GraphComponent> getIdMap() {
		return idMap;
	}
	
	/**
	 * Get the edge map, a mapping from a pair of nodes to a list of edges.
	 * 
	 * @return The edge map.
	 */
	public Map<UnorderedNodePair, List<Edge>> getEdgeMap() {
		return graph.getEdges();
	}

	/**
	 * Get the graph in this context.
	 * 
	 * @return The Graph object with the graph of this context.
	 */
	public Graph getGraph() {
		return graph;
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
	
	/**
	 * Mark the graph in this context as saved, which changes the GUI's appearance slightly.
	 */
	public void setAsSaved() {
		unsaved = false;
		if (gui != null && gui.getTitle().endsWith("*")) {
			gui.setTitle(gui.getTitle().substring(0, gui.getTitle().length() - 1));
		}
	}
	
	/**
	 * Mark the graph in this context as unsaved, which changes the GUI's appearance slightly.
	 */
	public void setAsUnsaved() {
		unsaved = true;
		if (!gui.getTitle().endsWith("*"))
			gui.setTitle(gui.getTitle() + "*");
	}
	
	/**
	 * Set the value of the last action ID before the last save occurred.
	 * 
	 * @param id The new value.
	 */
	public void setActionIdOnLastSave(int id) {
		actionIdOnLastSave = id;
	}

}
