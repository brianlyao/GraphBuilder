package util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

import components.Edge;
import components.GraphComponent;
import components.Node;
import components.SelfEdge;
import components.SimpleEdge;
import context.GraphBuilderContext;
import structures.OrderedPair;
import structures.UnorderedNodePair;
import ui.Editor;

/**
 * Utility class for clipboard (copy, paste, delete, etc.) procedures.
 * 
 * @author Brian
 */
public class ClipboardUtils {

	/**
	 * Separates the current selections into nodes and edges.
	 * 
	 * @param ctxt The context (whose selections) we are copying.
	 * @return A pair containing the set of copied nodes and the map (from pairs of nodes to lists of edges).
	 */
	public static Pair<HashSet<Node>, HashMap<UnorderedNodePair, ArrayList<Edge>>> separateSelections(GraphBuilderContext ctxt) {
		HashSet<Node> copiedNodes = new HashSet<>();
		HashMap<UnorderedNodePair, ArrayList<Edge>> copiedEdges = new HashMap<>();
		
		// Determine the set of selected nodes
		for (GraphComponent gc : ctxt.getGUI().getEditor().getSelections())
			if (gc instanceof Node)
				copiedNodes.add((Node) gc);
		
		// Determine the set of selected edges. Each edge must have both endpoints in the set of
		// copied nodes
		HashSet<Edge> totalEdges = new HashSet<>();
		if (!copiedNodes.isEmpty()) {
			for (GraphComponent gc : ctxt.getGUI().getEditor().getSelections()) {
				if (gc instanceof Edge) {
					Edge tempEdge = (Edge) gc;
					OrderedPair<Node> tempep = tempEdge.getEndpoints();
					if (copiedNodes.contains(tempep.getFirst()) && copiedNodes.contains(tempep.getSecond()))
						totalEdges.add(tempEdge); 
				}
			}
		}
		
		// Sort the edges into a node-pair to edge mapping
		for (Edge e : totalEdges) {
			UnorderedNodePair tempPair = new UnorderedNodePair(e);
			// If this pair has not yet been added to the copied edges, add it
			if (copiedEdges.get(tempPair) == null) {
				ArrayList<Edge> newEdgeList = new ArrayList<>();
				for (Edge inThisPair : ctxt.getEdgeMap().get(tempPair))
					if (totalEdges.contains(inThisPair))
						newEdgeList.add(inThisPair);
				copiedEdges.put(tempPair, newEdgeList);
			}
		}
		
		// Update the menu bar
		ctxt.getGUI().getMainMenuBar().updateWithCopy();
		
		return new Pair<HashSet<Node>, HashMap<UnorderedNodePair, ArrayList<Edge>>>(copiedNodes, copiedEdges);
	}
	
	/**
	 * Delete the selected graph components, and return the necessary data for undoing the deletion.
	 * 
	 * @param ctxt The context whose selections to delete.
	 * @return A Quartet (4-tuple) of data: set of deleted nodes, set of deleted edges, map of edges
	 *         which were removed individually, and a map of edges that were removed as a result of
	 *         nodes being removed.
	 */
	public static Quartet<HashSet<Node>, HashSet<Edge>, HashMap<UnorderedNodePair, ArrayList<Edge>>,
			HashMap<UnorderedNodePair, ArrayList<Edge>>> deleteSelections(GraphBuilderContext ctxt) {
		Editor editor = ctxt.getGUI().getEditor();
		HashSet<GraphComponent> selections = editor.getSelections();
		
		HashSet<Node> deletedNodes = new HashSet<>();
		HashSet<Edge> deletedEdges = new HashSet<>();
		HashMap<UnorderedNodePair, ArrayList<Edge>> originalEdgeMap = new HashMap<>();
		HashMap<UnorderedNodePair, ArrayList<Edge>> deletedEdgeMap = new HashMap<>();
		
		// Get the selected nodes
		for (GraphComponent gc : selections)
			if (gc instanceof Node)
				deletedNodes.add((Node) gc);
		
		// Delete the selected nodes
		for (Node n : deletedNodes) {
			HashMap<UnorderedNodePair, ArrayList<Edge>> removedFromNode = ctxt.removeNode(n);
			deletedEdgeMap.putAll(removedFromNode);
		}
		
		// Get the selected edges
		for (GraphComponent gc : selections) {
			if (gc instanceof Edge) {
				Edge tempEdge = (Edge) gc;
				UnorderedNodePair tempPair = new UnorderedNodePair(tempEdge);
				 
				if (deletedEdgeMap.get(tempPair) == null) {
					// If the edge we are deleting is not in the edge map, we delete it
					deletedEdges.add(tempEdge);
					
					// Fill the edge map of deleted edges
					ArrayList<Edge> originalEdges = new ArrayList<>();
					originalEdges.addAll(ctxt.getEdgeMap().get(tempPair));
					originalEdgeMap.put(tempPair, originalEdges);
				}	
			}
		}
		
		// Delete the selected edges
		for (Edge e : deletedEdges) {
			ctxt.removeEdge(e);
		}
		
		return new Quartet<HashSet<Node>, HashSet<Edge>, HashMap<UnorderedNodePair, ArrayList<Edge>>,
				HashMap<UnorderedNodePair, ArrayList<Edge>>>(deletedNodes, deletedEdges, originalEdgeMap, deletedEdgeMap);
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
	public static void undoDeleteComponents(GraphBuilderContext ctxt, HashSet<Node> deletedNodes, HashSet<Edge> deletedEdges, HashMap<UnorderedNodePair, ArrayList<Edge>> originalEdgeMap, HashMap<UnorderedNodePair, ArrayList<Edge>> deletedEdgeMap) {
		Editor editor = ctxt.getGUI().getEditor();
		
		// Re-add the deleted nodes
		for (Node n : deletedNodes) {
			ctxt.addNode(n);
		}
		
		// Re-select deleted nodes
		editor.addSelections(deletedNodes);
		
		// Add all edges deleted as a result of the nodes being removed
		ctxt.getEdgeMap().putAll(deletedEdgeMap);
		for (ArrayList<Edge> edgeList : deletedEdgeMap.values()) {
			ctxt.getEdges().addAll(edgeList);
		}
		
		// Add all edges deleted individually
		for (Map.Entry<UnorderedNodePair, ArrayList<Edge>> ogEntry : originalEdgeMap.entrySet()) {
			ArrayList<Edge> ogEdgeList = ctxt.getEdgeMap().get(ogEntry.getKey());
			ogEdgeList.clear();
			ogEdgeList.addAll(ogEntry.getValue());
		}
		
		//Re-select individually deleted edges
		ctxt.getEdges().addAll(deletedEdges);
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
	public static HashMap<UnorderedNodePair, ArrayList<Edge>> getSubEdgeMap(GraphBuilderContext ctxt, HashSet<Node> nodes) {
		HashMap<UnorderedNodePair, ArrayList<Edge>> edgeMap = ctxt.getEdgeMap();
		HashMap<UnorderedNodePair, ArrayList<Edge>> subEdgeMap = new HashMap<>();
		for (Map.Entry<UnorderedNodePair, ArrayList<Edge>> emEntry : edgeMap.entrySet()) {
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
	public static Triplet<HashSet<Node>, HashMap<Node, Node>, Point> copyNodes(Collection<Node> oldNodes) {
		int maxX = 0;
		int maxY = 0;
		HashSet<Node> newNodes = new HashSet<>();
		HashMap<Node, Node> oldToNew = new HashMap<>();
		for (Node n : oldNodes) {
			Node newNode = new Node(n);
			newNodes.add(newNode);
			oldToNew.put(n, newNode);
			
			maxX = Math.max(maxX, n.getNodePanel().getXCoord() + 2 * n.getNodePanel().getRadius());
			maxY = Math.max(maxY, n.getNodePanel().getYCoord() + 2 * n.getNodePanel().getRadius());
		}
		
		return new Triplet<HashSet<Node>, HashMap<Node, Node>, Point>(newNodes, oldToNew, new Point(maxX, maxY));
	}

	/**
	 * Create copies of the provided edges.
	 * 
	 * @param oldEdges The edges we want to make copies of.
	 * @param oldToNew The mapping from old nodes to their new copies (as returned by copyNodes).
	 * @return The new copied edges.
	 */
	public static HashMap<UnorderedNodePair, ArrayList<Edge>> copyEdges(HashMap<UnorderedNodePair, ArrayList<Edge>> oldEdges, HashMap<Node, Node> oldToNew) {
		HashMap<UnorderedNodePair, ArrayList<Edge>> newEdges = new HashMap<>();
		for (Map.Entry<UnorderedNodePair, ArrayList<Edge>> emEntry : oldEdges.entrySet()) {
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
			
			UnorderedNodePair newPair = new UnorderedNodePair(oldToNew.get(oldPair.getFirst()), oldToNew.get(oldPair.getSecond()));
			newEdges.put(newPair, forThisPair);
		}
		return newEdges;
	}
	
}
