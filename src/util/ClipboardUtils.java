package util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Quartet;
import org.javatuples.Triplet;

import components.Edge;
import components.Node;
import components.SelfEdge;
import components.SimpleEdge;
import context.GraphBuilderContext;
import structures.UnorderedNodePair;
import ui.Editor;

/**
 * Utility class for clipboard (copy, paste, delete, etc.) procedures.
 * 
 * @author Brian
 */
public class ClipboardUtils {
	
	/**
	 * Delete the selected graph components, and return the necessary data for undoing the deletion.
	 * 
	 * @param ctxt The context whose selections to delete.
	 * @return A Quartet (4-tuple) of data: set of deleted nodes, set of deleted edges, map of edges
	 *         which were removed individually, and a map of edges that were removed as a result of
	 *         nodes being removed.
	 */
	public static Quartet<Set<Node>, Set<Edge>, Map<UnorderedNodePair, List<Edge>>,
			Map<UnorderedNodePair, List<Edge>>> deleteSelections(GraphBuilderContext ctxt) {
		Editor editor = ctxt.getGUI().getEditor();
		
		Set<Node> deletedNodes = new HashSet<>();
		Set<Edge> deletedEdges = new HashSet<>();
		Map<UnorderedNodePair, List<Edge>> originalEdgeMap = new HashMap<>();
		Map<UnorderedNodePair, List<Edge>> deletedEdgeMap = new HashMap<>();
		
		// Get the selected nodes
		deletedNodes.addAll(editor.getSelections().getValue0());
		
		// Delete the selected nodes
		for (Node n : deletedNodes) {
			Map<UnorderedNodePair, List<Edge>> removedFromNode = ctxt.removeNode(n);
			deletedEdgeMap.putAll(removedFromNode);
		}
		
		// Get the selected edges
		for (Map.Entry<UnorderedNodePair, List<Edge>> edgeEntry : editor.getSelections().getValue1().entrySet()) {
			UnorderedNodePair tempPair = edgeEntry.getKey();
			if (deletedEdgeMap.get(tempPair) == null) {
				// If the edge we are deleting is not in the edge map of deleted edges, we delete it
				deletedEdges.addAll(edgeEntry.getValue());
				
				// Fill the edge map of deleted edges
				ArrayList<Edge> originalEdges = new ArrayList<>(ctxt.getEdgeMap().get(tempPair));
				originalEdgeMap.put(tempPair, originalEdges);
			}	
		}
		
		// Delete the selected edges
		for (Edge e : deletedEdges) {
			ctxt.removeEdge(e);
		}
		
		return new Quartet<Set<Node>, Set<Edge>, Map<UnorderedNodePair, List<Edge>>,
				Map<UnorderedNodePair, List<Edge>>>(deletedNodes, deletedEdges, originalEdgeMap, deletedEdgeMap);
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
	public static void undoDeleteComponents(GraphBuilderContext ctxt, Set<Node> deletedNodes, Set<Edge> deletedEdges, Map<UnorderedNodePair, List<Edge>> originalEdgeMap, Map<UnorderedNodePair, List<Edge>> deletedEdgeMap) {
		Editor editor = ctxt.getGUI().getEditor();
		
		// Re-add the deleted nodes
		for (Node n : deletedNodes) {
			ctxt.addNode(n);
		}
		
		// Re-select deleted nodes
		editor.addSelections(deletedNodes);
		
		// Add all edges deleted as a result of the nodes being removed
		ctxt.getEdgeMap().putAll(deletedEdgeMap);
		
		// Add all edges deleted individually
		for (Map.Entry<UnorderedNodePair, List<Edge>> ogEntry : originalEdgeMap.entrySet()) {
			List<Edge> currentEdgeList = ctxt.getEdgeMap().get(ogEntry.getKey());
			if (currentEdgeList == null) {
				currentEdgeList = new ArrayList<Edge>();
			} else {
				currentEdgeList.clear();
			}
			currentEdgeList.addAll(ogEntry.getValue());
			ctxt.getEdgeMap().put(ogEntry.getKey(), currentEdgeList);
		}
		
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
	public static Map<UnorderedNodePair, List<Edge>> getSubEdgeMap(GraphBuilderContext ctxt, Set<Node> nodes) {
		Map<UnorderedNodePair, List<Edge>> edgeMap = ctxt.getEdgeMap();
		Map<UnorderedNodePair, List<Edge>> subEdgeMap = new HashMap<>();
		for (Map.Entry<UnorderedNodePair, List<Edge>> emEntry : edgeMap.entrySet()) {
			UnorderedNodePair pairKey = emEntry.getKey();
			if (nodes.contains(pairKey.getFirst()) && nodes.contains(pairKey.getSecond())) {
				subEdgeMap.put(pairKey, emEntry.getValue());
			}
		}
		return subEdgeMap;
	}
	
	/**
	 * Create copies of the provided nodes.
	 * 
	 * @param oldNodes The nodes we want to make copies of.
	 * @return A triplet of: the set of new nodes, a mapping from the old nodes to their new copies,
	 *         and a point signifying the bottom right corner of the bounding box of the old nodes.
	 */
	public static Triplet<Set<Node>, Map<Node, Node>, Point> copyNodes(Collection<Node> oldNodes) {
		int maxX = 0;
		int maxY = 0;
		Set<Node> newNodes = new HashSet<>();
		Map<Node, Node> oldToNew = new HashMap<>();
		for (Node n : oldNodes) {
			Node newNode = new Node(n);
			newNodes.add(newNode);
			oldToNew.put(n, newNode);
			
			maxX = Math.max(maxX, n.getNodePanel().getXCoord() + 2 * n.getNodePanel().getRadius());
			maxY = Math.max(maxY, n.getNodePanel().getYCoord() + 2 * n.getNodePanel().getRadius());
		}
		
		return new Triplet<Set<Node>, Map<Node, Node>, Point>(newNodes, oldToNew, new Point(maxX, maxY));
	}

	/**
	 * Create copies of the provided edges.
	 * 
	 * @param oldEdges The edges we want to make copies of.
	 * @param oldToNew The mapping from old nodes to their new copies (as returned by copyNodes).
	 * @return The new copied edges.
	 */
	public static Map<UnorderedNodePair, List<Edge>> copyEdges(Map<UnorderedNodePair, List<Edge>> oldEdges, Map<Node, Node> oldToNew) {
		Map<UnorderedNodePair, List<Edge>> newEdges = new HashMap<>();
		for (Map.Entry<UnorderedNodePair, List<Edge>> emEntry : oldEdges.entrySet()) {
			UnorderedNodePair oldPair = emEntry.getKey();
			ArrayList<Edge> forThisPair = new ArrayList<>();
			
			// Iterate through all edges associated with the "old" pair of nodes
			for (Edge e : emEntry.getValue()) {
				if (e instanceof SimpleEdge) {
					SimpleEdge tempSimp = (SimpleEdge) e;
					Node oldFirstEnd = tempSimp.getEndpoints().getFirst();
					Node oldSecondEnd = tempSimp.getEndpoints().getSecond();
					forThisPair.add(new SimpleEdge(tempSimp, oldToNew.get(oldFirstEnd), oldToNew.get(oldSecondEnd)));
				} else if (e instanceof SelfEdge) {
					SelfEdge tempSelf = (SelfEdge) e;
					Node oldEnd = tempSelf.getEndpoints().getFirst();
					forThisPair.add(new SelfEdge(tempSelf, oldToNew.get(oldEnd)));
				}
			}
			
			// Add new edges to new node's data
			Node newEnd1 = oldToNew.get(oldPair.getFirst());
			Node newEnd2 = oldToNew.get(oldPair.getSecond());
			for (Edge newEdge : forThisPair) {
				newEnd1.addEdge(newEdge);
				newEnd2.addEdge(newEdge);
			}
			
			UnorderedNodePair newPair = new UnorderedNodePair(newEnd1, newEnd2);
			newEdges.put(newPair, forThisPair);
		}
		return newEdges;
	}
	
}
