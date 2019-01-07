package util;

import context.GBContext;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import structures.EditorData;
import structures.UOPair;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for clipboard (copy, paste, delete, etc.) procedures.
 *
 * @author Brian Yao
 */
public class ClipboardUtils {

	/**
	 * Delete the selected graph components, and return the necessary data for undoing the deletion.
	 *
	 * @param ctxt The context whose selections to delete.
	 * @return A Quartet (4-tuple) of data: set of deleted nodes, set of deleted edges, map of edges
	 * which were removed individually, and a map of edges that were removed as a result of
	 * nodes being removed.
	 */
	public static Quartet<Set<GBNode>, Set<GBEdge>, Map<UOPair<GBNode>, List<GBEdge>>,
		Map<UOPair<GBNode>, List<GBEdge>>> deleteSelections(GBContext ctxt) {
		EditorData editorData = ctxt.getGUI().getEditor().getData();

		Set<GBNode> deletedNodes = new HashSet<>(editorData.getSelectedNodes());
		Set<GBEdge> deletedEdges = new HashSet<>();
		Map<UOPair<GBNode>, List<GBEdge>> originalEdgeMap = new HashMap<>();
		Map<UOPair<GBNode>, List<GBEdge>> deletedEdgeMap = new HashMap<>();

		// Delete the selected nodes
		for (GBNode n : deletedNodes) {
			Map<UOPair<GBNode>, List<GBEdge>> removedFromNode = ctxt.removeNode(n);
			deletedEdgeMap.putAll(removedFromNode);
		}

		// Get the selected edges
		editorData.getSelectedEdges().forEach((key, edges) -> {
			if (deletedEdgeMap.get(key) == null) {
				// If the edges we are deleting is not in the edge map of
				// deleted edges, remove them
				deletedEdges.addAll(edges);

				// Fill data remembering the original edges
				originalEdgeMap.put(key, new ArrayList<>(ctxt.getGbEdges().get(key)));
			}
		});

		// Delete the selected edges
		ctxt.removeEdges(deletedEdges);

		return new Quartet<>(deletedNodes, deletedEdges, originalEdgeMap, deletedEdgeMap);
	}

	/**
	 * Undo a particular deletion, given the data returned by deleteSelections().
	 *
	 * @param ctxt            The context in which the deletion occurred.
	 * @param deletedNodes    Set of deleted nodes.
	 * @param deletedEdges    Set of deleted edges.
	 * @param originalEdgeMap Map of node pairs to edges; contains the original lists of edges before
	 *                        removing edges which were deleted individually.
	 * @param deletedEdgeMap  Map of node pairs to edges; contains the edges removed as a result of
	 *                        either endpoint of each edge being removed.
	 */
	public static void undoDeleteComponents(GBContext ctxt, Set<GBNode> deletedNodes, Set<GBEdge> deletedEdges,
											Map<UOPair<GBNode>, List<GBEdge>> originalEdgeMap,
											Map<UOPair<GBNode>, List<GBEdge>> deletedEdgeMap) {
		// Re-add the deleted nodes
		ctxt.addNodes(deletedNodes);

		// Add all edges deleted as a result of the nodes being removed
		deletedEdgeMap.values().forEach(ctxt::addEdges);

		// Add all edges deleted individually (restore original edges)
		Map<UOPair<GBNode>, List<GBEdge>> gbEdges = ctxt.getGbEdges();
		originalEdgeMap.forEach((key, edges) -> {
			List<GBEdge> currentEdgeList = gbEdges.get(key);

			// If there are edges between these nodes, clear them before adding
			// the original list of edges
			if (currentEdgeList != null) {
				ctxt.removeEdges(currentEdgeList);
			}
			ctxt.addEdges(edges);
		});

		// Re-select deleted nodes and individually deleted edges
		EditorData editorData = ctxt.getGUI().getEditor().getData();
		editorData.addSelections(deletedNodes);
		editorData.addSelections(deletedEdges);
	}

	/**
	 * Given a set of nodes, obtain the subset of edges whose endpoints both lie in the
	 * provided set of nodes.
	 *
	 * @param ctxt  The context whose graph we are looking at.
	 * @param nodes A set of nodes.
	 * @return The map of edges.
	 */
	public static Map<UOPair<GBNode>, List<GBEdge>> getSubEdgeMap(GBContext ctxt, Set<GBNode> nodes) {
		Map<UOPair<GBNode>, List<GBEdge>> edges = ctxt.getGbEdges();
		return ctxt.getGbEdges().keySet().stream()
			.filter(pair -> nodes.contains(pair.getFirst()) && nodes.contains(pair.getSecond()))
			.collect(Collectors.toMap(Function.identity(), edges::get));
	}

	/**
	 * Given a collection of nodes, find the lower left corner of the bounding
	 * box containing the nodes' visual panels.
	 *
	 * @param nodes The collection of nodes.
	 * @return a Point representing the lower right corner of the bounding box.
	 */
	public static Point lowerRightCorner(Collection<GBNode> nodes) {
		int maxX = 0;
		int maxY = 0;
		for (GBNode n : nodes) {
			maxX = Math.max(maxX, n.getPanel().getXCoord() + 2 * n.getPanel().getRadius());
			maxY = Math.max(maxY, n.getPanel().getYCoord() + 2 * n.getPanel().getRadius());
		}

		return new Point(maxX, maxY);
	}

	/**
	 * Create copies of the provided nodes. The copies will belong to the same
	 * context as the originals.
	 *
	 * @param oldNodes The nodes we want to make copies of.
	 * @return A pair of: the set of new nodes, and a mapping from the old nodes
	 * to their new copies.
	 */
	public static Pair<Set<GBNode>, Map<GBNode, GBNode>> copyNodes(Collection<GBNode> oldNodes) {
		Set<GBNode> newNodes = new HashSet<>();
		Map<GBNode, GBNode> oldToNew = new HashMap<>();
		for (GBNode n : oldNodes) {
			GBNode newNode = new GBNode(n.getContext().getNextIdAndInc(), n);
			newNodes.add(newNode);
			oldToNew.put(n, newNode);
		}

		return new Pair<>(newNodes, oldToNew);
	}

	/**
	 * Create copies of the provided edges. The copies will belong to the same
	 * context as the originals.
	 *
	 * @param oldEdges The edges we want to make copies of.
	 * @param oldToNew The mapping from old nodes to their new copies
	 *                 (as returned by copyNodes).
	 * @return The new copied edges.
	 */
	public static Map<UOPair<GBNode>, List<GBEdge>> copyEdges(Map<UOPair<GBNode>, List<GBEdge>> oldEdges,
															  Map<GBNode, GBNode> oldToNew) {
		Map<UOPair<GBNode>, List<GBEdge>> newEdges = new HashMap<>();
		for (Map.Entry<UOPair<GBNode>, List<GBEdge>> emEntry : oldEdges.entrySet()) {
			List<GBEdge> forThisPair = new ArrayList<>();

			// Iterate through all edges associated with the "old" pair of nodes
			for (GBEdge e : emEntry.getValue()) {
				forThisPair.add(new GBEdge(e.getContext().getNextIdAndInc(), oldToNew.get(e.getFirstEnd()),
										   oldToNew.get(e.getSecondEnd()), e.isDirected()));
			}

			GBNode newEnd1 = oldToNew.get(emEntry.getKey().getFirst());
			GBNode newEnd2 = oldToNew.get(emEntry.getKey().getSecond());
			UOPair<GBNode> newPair = new UOPair<>(newEnd1, newEnd2);
			newEdges.put(newPair, forThisPair);
		}

		return newEdges;
	}

}
