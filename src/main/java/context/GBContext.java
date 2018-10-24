package context;

import actions.ReversibleAction;
import clipboard.Clipboard;
import graph.components.Edge;
import graph.components.Node;
import graph.components.gb.GBComponent;
import graph.components.gb.GBEdge;
import graph.components.gb.GBGraph;
import graph.components.gb.GBNode;
import lombok.Getter;
import lombok.Setter;
import structures.UOPair;
import ui.Editor;
import ui.GBFrame;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A collection of fields necessary to keep track of the program's state. Any time
 * a new file is created or we open another file, we are switching the context.
 *
 * @author Brian Yao
 */
public class GBContext {

	private GBFrame gui; // The GBFrame using this context

	@Getter
	private GBGraph graph;

	@Getter
	private Map<Integer, GBComponent> idMap; // A mapping from id to graph component

	@Getter
	private Stack<ReversibleAction> actionHistory; // A collection of the most recent reversible actions
	@Getter
	private Stack<ReversibleAction> undoHistory; // A collection of the most recent undos

	@Getter
	private Clipboard clipboard;

	@Getter
	private boolean unsaved; // If the current graph is unsaved

	@Getter @Setter
	private File currentlyLoadedFile;

	private int actionIdOnLastSave;

	private int idPool;

	/**
	 * @param graphConstraints The constraints on the graph this context manages.
	 */
	public GBContext(int graphConstraints) {
		// Initialize graph data structure
		graph = new GBGraph(this, graphConstraints);

		idMap = new HashMap<>();

		// Initialize empty clipboard
		clipboard = new Clipboard(this);

		// Initialize action histories
		actionHistory = new Stack<>();
		undoHistory = new Stack<>();
		actionIdOnLastSave = -1;

		// The id pool
		idPool = 0;
	}

	/**
	 * Set the GBFrame object providing an interface for this context.
	 *
	 * @param g The GBFrame for this context.
	 */
	public void setGUI(GBFrame g) {
		gui = g;
	}

	/**
	 * Add a graph component to the graph.
	 *
	 * @param gc The graph component to add.
	 */
	public void add(GBComponent gc) {
		if (gc instanceof GBNode) {
			addNode((GBNode) gc);
		} else if (gc instanceof GBEdge) {
			addEdge((GBEdge) gc);
		}
	}

	/**
	 * Add all the provided graph components to the graph.
	 *
	 * @param components The graph components to add.
	 */
	public void addAll(Collection<? extends GBComponent> components) {
		components.forEach(this::add);
	}

	/**
	 * Remove the specified graph component.
	 *
	 * @param gc The component to remove.
	 */
	public void remove(GBComponent gc) {
		if (gc instanceof GBNode) {
			removeNode((GBNode) gc);
		} else if (gc instanceof GBEdge) {
			removeEdge((GBEdge) gc);
		}
	}

	/**
	 * Remove the specified graph components.
	 *
	 * @param components The collection of graph components to remove.
	 */
	public void removeAll(Collection<? extends GBComponent> components) {
		components.forEach(this::remove);
	}

	/**
	 * Adds the specified node to the graph.
	 *
	 * @param n The node to add to the graph.
	 */
	public void addNode(GBNode n) {
		graph.addNode(n.getNode());
		idMap.put(n.getId(), n);

		// Add to the editor panel
		if (gui != null) {
			Editor editor = gui.getEditor();
			editor.add(n.getNodePanel());
			editor.repaint();
			editor.revalidate();
		}
	}

	/**
	 * Add all the provided nodes to the graph.
	 *
	 * @param nodes The nodes to add.
	 */
	public void addNodes(Collection<? extends GBNode> nodes) {
		nodes.forEach(this::addNode);
	}

	/**
	 * Removes the specified node from the graph.
	 *
	 * @param n The node to remove.
	 * @return The map of edges that was removed as a result of this node's removal.
	 */
	public Map<UOPair<GBNode>, List<GBEdge>> removeNode(GBNode n) {
		// Remove the node from the set of all nodes in this context
		Map<UOPair<GBNode>, List<GBEdge>> removedEdges = toGbEdges(graph.removeNode(n.getNode()));

		if (gui != null) {
			Editor editor = gui.getEditor();

			// If this node was the "base point" for an edge, reset the base point
			if (editor.getEdgeBasePoint() == n) {
				editor.clearEdgeBasePoint();
			}

			// Remove this node and removed edges from selections
			editor.removeSelection(n);
			removedEdges.values().forEach(editor::removeSelections);

			// Update GBFrame appearance and button states
			this.getGUI().getMainMenuBar().updateWithSelection();

			// Revalidate the editor panel after removing the panel
			editor.remove(n.getNodePanel());
			editor.repaint();
			editor.revalidate();
		}

		return removedEdges;
	}

	/**
	 * Add an edge to the graph. Insert the edge at the provided position.
	 *
	 * @param e        The edge we want to add to the graph.
	 * @param position The position (from the left) of the edge relative to other edges sharing e's endpoints.
	 */
	public void addEdge(GBEdge e, int position) {
		idMap.put(e.getId(), e);
		graph.addEdge(e.getEdge(), position);
	}

	/**
	 * Add and edge to the graph.
	 *
	 * @param e The edge to add.
	 */
	public void addEdge(GBEdge e) {
		idMap.put(e.getId(), e);
		graph.addEdge(e.getEdge());
	}

	/**
	 * Add all provided edges to the graph.
	 *
	 * @param edges The edges to add.
	 */
	public void addEdges(Collection<? extends GBEdge> edges) {
		edges.forEach(this::addEdge);
	}

	/**
	 * Remove an edge from the graph.
	 *
	 * @param gbe The edge we want to remove from the graph.
	 * @return The position of the edge before it was removed.
	 */
	public int removeEdge(GBEdge gbe) {
		int index = graph.removeEdge(gbe.getEdge());

		// Remove the edge from selections
		this.getGUI().getEditor().removeSelection(gbe);
		this.getGUI().getMainMenuBar().updateWithSelection();

		return index;
	}

	/**
	 * Remove all provided edges from the graph.
	 *
	 * @param edges The edges to remove.
	 */
	public void removeEdges(Collection<? extends GBEdge> edges) {
		edges.forEach(this::removeEdge);
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
		if ((actionHistory.isEmpty() && actionIdOnLastSave < 0) ||
			actionHistory.peek().actionId() == actionIdOnLastSave) {
			setAsSaved();
		} else {
			setAsUnsaved();
		}
		gui.getMainMenuBar().setUndoEnabled(!actionHistory.isEmpty());
		gui.getMainMenuBar().setRedoEnabled(!undoHistory.isEmpty());
	}

	/**
	 * Get the GBFrame corresponding to this context.
	 *
	 * @return The GBFrame corresponding to this context.
	 */
	public GBFrame getGUI() {
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
	 * @param id The value of the next id.
	 */
	public void setNextId(int id) {
		idPool = id;
	}

	/**
	 * Checks if the graph in this context is stored on disk.
	 *
	 * @return true if the graph is stored in a file, and false otherwise.
	 */
	public boolean existsOnDisk() {
		return currentlyLoadedFile != null;
	}

	/**
	 * Mark the graph in this context as saved, which changes the GBFrame's appearance slightly.
	 */
	public void setAsSaved() {
		unsaved = false;
		if (gui != null && gui.getTitle().endsWith("*")) {
			gui.setTitle(gui.getTitle().substring(0, gui.getTitle().length() - 1));
		}
	}

	/**
	 * Mark the graph in this context as unsaved, which changes the GBFrame's appearance slightly.
	 */
	public void setAsUnsaved() {
		unsaved = true;
		if (!gui.getTitle().endsWith("*")) {
			gui.setTitle(gui.getTitle() + "*");
		}
	}

	/**
	 * Set the value of the last action ID before the last save occurred.
	 *
	 * @param id The new value.
	 */
	public void setActionIdOnLastSave(int id) {
		actionIdOnLastSave = id;
	}

	/**
	 * Get a component given its ID.
	 *
	 * @param id The ID to look up.
	 * @return The GBComponent associated with that ID, or null if nonexistent.
	 */
	public GBComponent getFromId(int id) {
		return idMap.get(id);
	}

	/**
	 * Get the set of nodes associated with this context.
	 *
	 * @return The set of GBNodes in this context.
	 */
	public Set<GBNode> getGbNodes() {
		return toGbNodes(graph.getNodes());
	}

	/**
	 * Get the map of edges associated with this context.
	 *
	 * @return The map of GBEdges in this context.
	 */
	public Map<UOPair<GBNode>, List<GBEdge>> getGbEdges() {
		return toGbEdges(graph.getEdges());
	}

	/**
	 * Given a pair of nodes, get all edges with those nodes as endpoints in
	 * this context.
	 *
	 * @param nodes The endpoints.
	 * @return All edges in this context with those endpoints.
	 */
	public List<GBEdge> getEdgesBetweenNodes(UOPair<GBNode> nodes) {
		return this.getGbEdges().get(nodes);
	}

	/**
	 * Converts a set of nodes to GBNodes, assuming the nodes are associated
	 * with a context.
	 *
	 * @param nodes The set of plain nodes to convert.
	 * @return The set of corresponding GBNodes.
	 */
	private Set<GBNode> toGbNodes(Set<Node> nodes) {
		return nodes.stream().map(Node::getGbNode).collect(Collectors.toSet());
	}

	/**
	 * Converts a map of edges to GBEdges, assuming the edges are associated
	 * with a context.
	 *
	 * @param edges The set of plain edges to convert.
	 * @return The set of corresponding GBNodes.
	 */
	private Map<UOPair<GBNode>, List<GBEdge>> toGbEdges(Map<UOPair<Node>, List<Edge>> edges) {
		Map<UOPair<GBNode>, List<GBEdge>> gbEdgeMap = new HashMap<>();
		for (Map.Entry<UOPair<Node>, List<Edge>> edgeEntry : edges.entrySet()) {
			UOPair<Node> oldPair = edgeEntry.getKey();
			UOPair<GBNode> gbPair = new UOPair<>(oldPair.getFirst().getGbNode(), oldPair.getSecond().getGbNode());
			List<GBEdge> gbEdges = edgeEntry.getValue().stream().map(Edge::getGbEdge).collect(Collectors.toList());

			// Fill map
			gbEdgeMap.put(gbPair, gbEdges);
		}

		return gbEdgeMap;
	}

}
