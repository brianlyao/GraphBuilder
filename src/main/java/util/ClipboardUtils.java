package util;

import context.GBContext;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import structures.UOPair;
import ui.Editor;

import java.awt.*;
import java.util.*;
import java.util.List;

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
		Editor editor = ctxt.getGUI().getEditor();

		Set<GBNode> deletedNodes = new HashSet<>(editor.getSelections().getValue0());
		Set<GBEdge> deletedEdges = new HashSet<>();
		Map<UOPair<GBNode>, List<GBEdge>> originalEdgeMap = new HashMap<>();
		Map<UOPair<GBNode>, List<GBEdge>> deletedEdgeMap = new HashMap<>();

		// Delete the selected nodes
		for (GBNode n : deletedNodes) {
			Map<UOPair<GBNode>, List<GBEdge>> removedFromNode = ctxt.removeNode(n);
			deletedEdgeMap.putAll(removedFromNode);
		}

		// Get the selected edges
		for (Map.Entry<UOPair<GBNode>, List<GBEdge>> edgeEntry : editor.getSelections().getValue1().entrySet()) {
			UOPair<GBNode> tempPair = edgeEntry.getKey();
			if (deletedEdgeMap.get(tempPair) == null) {
				// If the edge we are deleting is not in the edge map of deleted edges, we delete it
				deletedEdges.addAll(edgeEntry.getValue());

				// Fill the edge map of deleted edges
				List<GBEdge> originalEdges = new ArrayList<>(ctxt.getGbEdges().get(tempPair));
				originalEdgeMap.put(tempPair, originalEdges);
			}
		}

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
		Editor editor = ctxt.getGUI().getEditor();

		// Re-add the deleted nodes
		ctxt.addNodes(deletedNodes);

		// Add all edges deleted as a result of the nodes being removed
		deletedEdgeMap.values().forEach(ctxt::addEdges);

		// Add all edges deleted individually (restore original edges)
		Map<UOPair<GBNode>, List<GBEdge>> gbEdges = ctxt.getGbEdges();
		for (Map.Entry<UOPair<GBNode>, List<GBEdge>> ogEntry : originalEdgeMap.entrySet()) {
			List<GBEdge> currentEdgeList = gbEdges.get(ogEntry.getKey());

			// If there are edges between these nodes, clear them before adding
			// the original list of edges
			if (currentEdgeList != null) {
				ctxt.removeEdges(currentEdgeList);
			}
			ctxt.addEdges(ogEntry.getValue());
		}

		// Re-select deleted nodes
		editor.addSelections(deletedNodes);
		// Re-select individually deleted edges
		editor.addSelections(deletedEdges);
	}

	/**
	 * Given a set of nodes, obtain the subset of edges whose endpoints both lie in the
	 * provided set of nodes.
	 *
	 * @param ctxt  The context whose relevant graph we are looking at.
	 * @param nodes A set of nodes.
	 * @return The map of edges.
	 */
	public static Map<UOPair<GBNode>, List<GBEdge>> getSubEdgeMap(GBContext ctxt, Set<GBNode> nodes) {
		Map<UOPair<GBNode>, List<GBEdge>> edgeMap = ctxt.getGbEdges();
		Map<UOPair<GBNode>, List<GBEdge>> subEdgeMap = new HashMap<>();
		for (Map.Entry<UOPair<GBNode>, List<GBEdge>> emEntry : edgeMap.entrySet()) {
			UOPair<GBNode> pairKey = emEntry.getKey();
			if (nodes.contains(pairKey.getFirst()) && nodes.contains(pairKey.getSecond())) {
				subEdgeMap.put(pairKey, emEntry.getValue());
			}
		}
		return subEdgeMap;
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
			maxX = Math.max(maxX, n.getNodePanel().getXCoord() + 2 * n.getNodePanel().getRadius());
			maxY = Math.max(maxY, n.getNodePanel().getYCoord() + 2 * n.getNodePanel().getRadius());
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
			GBNode newNode = new GBNode(n);
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
	 * @param oldToNew The mapping from old nodes to their new copies (as returned by copyNodes).
	 * @return The new copied edges.
	 */
	public static Map<UOPair<GBNode>, List<GBEdge>> copyEdges(Map<UOPair<GBNode>, List<GBEdge>> oldEdges,
															  Map<GBNode, GBNode> oldToNew) {
		Map<UOPair<GBNode>, List<GBEdge>> newEdges = new HashMap<>();
		for (Map.Entry<UOPair<GBNode>, List<GBEdge>> emEntry : oldEdges.entrySet()) {
			List<GBEdge> forThisPair = new ArrayList<>();

			// Iterate through all edges associated with the "old" pair of nodes
			for (GBEdge e : emEntry.getValue()) {
				GBNode oldFirstEnd = e.getFirstEnd();
				GBNode oldSecondEnd = e.getSecondEnd();
				forThisPair.add(new GBEdge(oldToNew.get(oldFirstEnd), oldToNew.get(oldSecondEnd), e.isDirected()));
			}

			GBNode newEnd1 = oldToNew.get(emEntry.getKey().getFirst());
			GBNode newEnd2 = oldToNew.get(emEntry.getKey().getSecond());
			UOPair<GBNode> newPair = new UOPair<>(newEnd1, newEnd2);
			newEdges.put(newPair, forThisPair);
		}

		return newEdges;
	}

}