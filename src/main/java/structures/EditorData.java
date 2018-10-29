package structures;

import context.GBContext;
import graph.components.display.NodePanel;
import graph.components.gb.GBComponent;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import lombok.Getter;
import lombok.Setter;
import ui.Editor;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A class for holding data stored which is pertinent to the editor' state.
 *
 * @author Brian Yao
 */
public class EditorData {

	// The editor this data is associated with
	private Editor editor;

	// Keep track of the last mouse position
	@Getter @Setter
	private Point lastMousePoint;

	@Getter @Setter
	private GBNode edgeBasePoint; // The first node that's selected when drawing an edge

	@Getter @Setter
	private GBNode pathBasePoint; // The first node that's selected when computing a path

	@Getter
	private Set<GBNode> selectedNodes;
	@Getter
	private Map<UOPair<GBNode>, List<GBEdge>> selectedEdges;

	@Getter
	private Set<GBNode> highlightedNodes;
	@Getter
	private Map<UOPair<GBNode>, List<GBEdge>> highlightedEdges;

	@Getter
	private Map<NodePanel, Point> nodePanelPositionMap; // Map from panel to upper left corner position on editor

	@Getter @Setter
	private GBEdge previewEdge;
	@Getter @Setter
	private int previewEdgeIndex;
	@Getter @Setter
	private boolean previewUsingObject; // If true, the preview edge should be the object previewEdge

	// Data for the edge select tool
	@Getter @Setter
	private Point closestEdgePoint;
	@Getter @Setter
	private GBEdge closestEdge;

	/**
	 * Initialize editor data to be empty.
	 *
	 * @param editor The editor this data is associated with.
	 */
	public EditorData(Editor editor) {
		this.editor = editor;

		lastMousePoint = new Point();
		selectedNodes = new HashSet<>();
		selectedEdges = new HashMap<>();
		highlightedNodes = new HashSet<>();
		highlightedEdges = new HashMap<>();
		nodePanelPositionMap = new HashMap<>();
	}

	/**
	 * Empty all editor data.
	 */
	public void clear() {
		selectedNodes.clear();
		selectedEdges.clear();
		highlightedNodes.clear();
		highlightedEdges.clear();
		nodePanelPositionMap.clear();
		clearPreviewEdge();
		clearEdgeBasePoint();
		clearPathBasePoint();
	}

	/**
	 * Add a new entry to the mapping from node panel to position.
	 *
	 * @param np The node panel we want to add.
	 */
	public void addNodePanelEntry(NodePanel np) {
		nodePanelPositionMap.put(np, np.getCoords());
	}

	/**
	 * Remove an entry from the mapping from node panel to position.
	 *
	 * @param np The node panel whose entry to remove.
	 */
	public void removeNodePanelEntry(NodePanel np) {
		nodePanelPositionMap.remove(np);
	}

	/**
	 * Check if a component is selected.
	 *
	 * @param gc The component to check.
	 * @return true iff the component is selected in this Editor.
	 */
	public boolean isSelected(GBComponent gc) {
		return componentBelongsTo(gc, selectedNodes, selectedEdges);
	}

	/**
	 * Add the given component to the set of selections.
	 *
	 * @param gc The selected component.
	 */
	public void addSelection(GBComponent gc) {
		addComponentTo(gc, selectedNodes, selectedEdges, editor.getContext());
	}

	/**
	 * Add the given components to the set of selections.
	 *
	 * @param components The selected components.
	 */
	public void addSelections(Iterable<? extends GBComponent> components) {
		components.forEach(this::addSelection);
	}

	/**
	 * Remove the given component from the set of selections.
	 *
	 * @param gc The deselected component.
	 */
	public void removeSelection(GBComponent gc) {
		removeComponentFrom(gc, selectedNodes, selectedEdges);
	}

	/**
	 * Remove the given components from the set of selections.
	 *
	 * @param components The deselected components.
	 */
	public void removeSelections(Iterable<? extends GBComponent> components) {
		components.forEach(this::removeSelection);
	}

	/**
	 * Clear all selections.
	 */
	public void removeAllSelections() {
		selectedNodes.clear();
		selectedEdges.clear();
	}

	/**
	 * @return true iff no graph components are selected.
	 */
	public boolean selectionsEmpty() {
		return selectedNodes.isEmpty() && selectedEdges.isEmpty();
	}

	/**
	 * Check if a component is highlighted.
	 *
	 * @param gc The component to check.
	 * @return true iff the component is highlighted.
	 */
	public boolean isHighlighted(GBComponent gc) {
		return componentBelongsTo(gc, highlightedNodes, highlightedEdges);
	}

	/**
	 * Add the given component to the set of highlighted components.
	 *
	 * @param gc The highlighted component.
	 */
	public void addHighlight(GBComponent gc) {
		addComponentTo(gc, highlightedNodes, highlightedEdges, editor.getContext());
	}

	/**
	 * Add the given components to the set of highlighted components.
	 *
	 * @param components The highlighted components.
	 */
	public void addHighlights(Iterable<? extends GBComponent> components) {
		components.forEach(this::addHighlight);
	}

	/**
	 * Clear all highlighted components.
	 */
	public void removeAllHighlights() {
		highlightedNodes.clear();
		highlightedEdges.clear();
	}

	/**
	 * @return true iff no graph components are highlighted.
	 */
	public boolean highlightsEmpty() {
		return highlightedNodes.isEmpty() && highlightedEdges.isEmpty();
	}

	/**
	 * Sets the last mouse point; we need this if the mouse is not on the Editor panel.
	 *
	 * @param x The x coordinate of the new position.
	 * @param y The y coordinate of the new position.
	 */
	public void setLastMousePoint(int x, int y) {
		lastMousePoint.setLocation(x, y);
	}

	/**
	 * Clear the edge base point (set it to null).
	 */
	public void clearEdgeBasePoint() {
		this.setEdgeBasePoint(null);
	}

	/**
	 * Clear the path base point (set it to null).
	 */
	public void clearPathBasePoint() {
		this.setPathBasePoint(null);
	}

	/**
	 * Set the editor's preview edge and the preview edge's position
	 *
	 * @param edge     The edge object representing the preview edge.
	 * @param position The position of the preview edge relative to the existing edges between the
	 *                 the same endpoints the preview edge has.
	 */
	public void setPreviewEdge(GBEdge edge, int position) {
		previewEdge = edge;
		previewEdgeIndex = position;
	}

	/**
	 * Clear the preview edge object (set it to null).
	 */
	public void clearPreviewEdge() {
		this.setPreviewEdge(null, -1);
	}

	/**
	 * Check if the given component is currently in the editor's metadata.
	 *
	 * @param gc    The component to check for inclusion.
	 * @param nodes Node metadata.
	 * @param edges Edge metadata.
	 * @return true iff the given component is included in the given metadata.
	 */
	private static boolean componentBelongsTo(GBComponent gc, Set<GBNode> nodes,
											  Map<UOPair<GBNode>, List<GBEdge>> edges) {
		if (gc instanceof GBNode) {
			return nodes.contains(gc);
		} else {
			GBEdge edge = (GBEdge) gc;
			List<GBEdge> selectedList = edges.get(edge.getUoEndpoints());
			return selectedList != null && selectedList.contains(edge);
		}
	}

	/**
	 * Add the given graph component to the editor metadata.
	 *
	 * @param gc The component to add.
	 * @param nodes Node metadata.
	 * @param edges Edge metadata.
	 */
	private static void addComponentTo(GBComponent gc, Set<GBNode> nodes, Map<UOPair<GBNode>, List<GBEdge>> edges,
									   GBContext context) {
		if (gc instanceof GBNode) {
			// Node case
			nodes.add((GBNode) gc);
		} else {
			// Edge case
			GBEdge selectedEdge = (GBEdge) gc;
			UOPair<GBNode> key = selectedEdge.getUoEndpoints();
			if (edges.containsKey(key)) {
				// Insert this edge into the right position
				List<GBEdge> totalList = context.getGbEdges().get(key);
				List<GBEdge> selectedList = edges.get(key);
				int selectionIndex = 0;
				for (GBEdge existingEdge : totalList) {
					if (selectedEdge == existingEdge) {
						selectedList.add(selectionIndex, selectedEdge);
						break;
					} else if (selectedList.contains(existingEdge)) {
						selectionIndex++;
					}
				}
			} else {
				// Create new entry
				List<GBEdge> newList = new ArrayList<>();
				newList.add(selectedEdge);
				edges.put(key, newList);
			}
		}
	}

	/**
	 * Remove the given graph component from the editor metadata.
	 *
	 * @param gc The component to remove.
	 * @param nodes Node metadata.
	 * @param edges Edge metadata.
	 */
	private static void removeComponentFrom(GBComponent gc, Set<GBNode> nodes,
											Map<UOPair<GBNode>, List<GBEdge>> edges) {
		if (gc instanceof GBNode) {
			// Deselect node
			nodes.remove(gc);
		} else {
			// Deselect edge
			GBEdge selectedEdge = (GBEdge) gc;
			UOPair<GBNode> key = selectedEdge.getUoEndpoints();
			List<GBEdge> selectedList = edges.get(key);
			if (selectedList != null) {
				selectedList.remove(selectedEdge);
				if (selectedList.isEmpty()) {
					edges.remove(key);
				}
			}
		}
	}

}
