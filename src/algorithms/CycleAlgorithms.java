package algorithms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import structures.UnorderedNodePair;
import util.StructureUtils;
import components.Edge;
import components.Node;
import components.SelfEdge;
import graph.Graph;
import graph.GraphConstraint;
import graph.Path;

/**
 * A class containing implementations of algorithms pertaining to cycles.
 * 
 * @author Brian Yao
 */
public class CycleAlgorithms {
	
	/**
	 * Check whether a particular graph is acyclic (contains no cycle). This is
	 * applicable to undirected, directed, and mixed graphs. The definition of
	 * acyclic for a mixed graph is: if there is no way to change undirected
	 * edges into directed edges such that there is a directed cycle formed as
	 * a result (and there are no undirected or directed edges), then the graph
	 * is acyclic.
	 * 
	 * @param graph The graph to search in for cycles.
	 * @return true iff the graph contains no cycles.
	 */
	public static boolean isAcyclic(Graph graph) {
		return findCycle(graph) == null;
	}
	
	/**
	 * Find a particular cycle and return it as a Path object. If no cycle
	 * exists, return null.
	 * 
	 * @param graph The graph to find a cycle in.
	 * @return A Path containing a cycle if one exists, or null otherwise.
	 */
	public static Path findCycle(Graph graph) {
		if (graph.hasConstraint(GraphConstraint.MIXED)) {
			return findCycleMixed(graph);
		} else {
			return findCycleNotMixed(graph);
		}
	}
	
	/**
	 * If the provided graph is a multigraph, search for 2-cycles (cycles
	 * containing only two nodes; note that this is impossible for simple
	 * graphs). Simultaneously searches for 1-cycles (self loop edges).
	 * Returns a cycle of length <= 2 if one exists, or null otherwise.
	 * 
	 * @param graph The graph to search for 1 and 2-cycles.
	 * @return A 1 or 2-cycle if one exists, or null if none exists.
	 */
	private static Path find2Cycle(Graph graph) {
		if (graph.hasConstraint(GraphConstraint.MULTIGRAPH)) {
			for (Map.Entry<UnorderedNodePair, List<Edge>> edgeEntry : graph.getEdges().entrySet()) {
				int undirected = 0;
				int outgoingDirected = 0;
				int incomingDirected = 0;
				
				for (Edge edge : edgeEntry.getValue()) {
					Node firstEnd = edgeEntry.getKey().getFirst();
					
					if (edge instanceof SelfEdge) {
						// Self-edge creates a cycle
						Path cycle = new Path(firstEnd);
						cycle.appendNode(firstEnd, edge);
						return cycle;
					} else if (!edge.isDirected()) {
						if (++undirected > 1) {
							// Undirected 2-cycle
							Node secondEnd = edge.getOtherEndpoint(firstEnd);
							Path cycle = new Path(firstEnd);
							Set<Edge> undirectedEdges = firstEnd.getUndirectedEdges().get(secondEnd);
							Iterator<Edge> undirectedIterator = undirectedEdges.iterator();
							cycle.appendNode(secondEnd, undirectedIterator.next());
							cycle.appendNode(firstEnd, undirectedIterator.next());
							return cycle;
						} else if (outgoingDirected > 0 || incomingDirected > 0) {
							// Mixed 2-cycle
							Node secondEnd = edge.getOtherEndpoint(firstEnd);
							Path cycle = new Path(firstEnd);
							Set<Edge> toSecond = outgoingDirected > 0 ?
									firstEnd.getOutgoingDirectedEdges().get(secondEnd) :
									firstEnd.getUndirectedEdges().get(secondEnd);
							Set<Edge> fromSecond = outgoingDirected > 0 ?
									firstEnd.getUndirectedEdges().get(secondEnd) :
									firstEnd.getIncomingDirectedEdges().get(secondEnd);
							cycle.appendNode(secondEnd, StructureUtils.arbitraryElement(toSecond));
							cycle.appendNode(firstEnd, StructureUtils.arbitraryElement(fromSecond));
							return cycle;
						}
					} else {
						// See if directed 2-cycle found
						boolean cycleFound = false;
						if (edge.getEndpoints().getFirst() == firstEnd) {
							outgoingDirected++;
							if (incomingDirected > 0) {
								cycleFound = true;
							}
						} else {
							incomingDirected++;
							if (outgoingDirected > 0) {
								cycleFound = true;
							}
						}
						
						if (cycleFound) {
							// Directed 2-cycle
							Node secondEnd = edge.getOtherEndpoint(firstEnd);
							Path cycle = new Path(firstEnd);
							Set<Edge> toSecond = firstEnd.getOutgoingDirectedEdges().get(secondEnd);
							Set<Edge> fromSecond = firstEnd.getIncomingDirectedEdges().get(secondEnd);
							Edge fromFirst = StructureUtils.arbitraryElement(toSecond);
							Edge toFirst = StructureUtils.arbitraryElement(fromSecond);
							cycle.appendNode(secondEnd, fromFirst);
							cycle.appendNode(firstEnd, toFirst);
							return cycle;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Searches the graph for any cycle. Returns null if the graph is acyclic.
	 * This procedure works specifically for undirected and directed graphs.
	 * 
	 * @param graph The undirected or directed graph to search for a cycle.
	 * @return A path containing a cycle in the graph, or null if none exists.
	 */
	private static Path findCycleNotMixed(Graph graph) {
		Path potential2Cycle = find2Cycle(graph);
		if (potential2Cycle != null) {
			return potential2Cycle;
		}
		
		// Begin search for cycles of length >= 3
		Set<Node> unvisited = new HashSet<>(graph.getNodes());
		Set<Node> visited = new HashSet<>();
		Set<Node> visiting = new HashSet<>();
		Map<Node, Node> parents = new HashMap<>();
		
		// Perform a depth-first search on every connected component
		while (!unvisited.isEmpty()) {
			Node start = StructureUtils.arbitraryElement(unvisited);
			parents.put(start, null);
			
			// Perform depth first search
			Path potentialCycle = visitNotMixed(start, unvisited, visiting, visited, parents);
			if (potentialCycle != null) {
				return potentialCycle;
			}
		}
		
		return null;
	}
	
	/**
	 * A recursive helper function used to carry out the DFS needed for the
	 * findCycleNotMixed algorithm. This is used specifically for undirected
	 * and directed graphs. This will not check for "small" cycles (any cycles
	 * whose length is <= 2) since this is done when find2Cycles() is called
	 * on this graph.
	 * 
	 * @param n            The node being visited.
	 * @param inMultigraph set to true iff the node belongs in a multigraph.
	 * @param unvisited    The set of unvisited nodes.
	 * @param visiting     The set of nodes being visited (neighbors being visited).
	 * @param visited      The set of nodes which are fully visited.
	 * @param parents      The map from a node to its "parent" node from which
	 *                     the node was visited.
	 * @return The first cycle found as a Path object, or null if no cycle was found.
	 */
	private static Path visitNotMixed(Node n, Set<Node> unvisited, Set<Node> visiting,
			Set<Node> visited, Map<Node, Node> parents) {
		unvisited.remove(n);
		visiting.add(n);
		for (Node neighbor : n.getNeighbors(true)) {
			if (!parents.containsKey(neighbor)) {
				parents.put(neighbor, n);
			}
			
			if (visiting.contains(neighbor) && parents.get(n) != neighbor) {
				// Encountered a cycle; compute cycle using parents map
				Path cycle = new Path(neighbor);
				Node currentNode = neighbor;
				Node prevNode = n;
				do {
					Set<Edge> prevToCurrent = prevNode.getNeighboringEdgesForNeighbor(currentNode, true);
					Edge edgeToCurrent = StructureUtils.arbitraryElement(prevToCurrent);
					cycle.prependNode(prevNode, edgeToCurrent);
					currentNode = prevNode;
					prevNode = parents.get(currentNode);
				} while (currentNode != neighbor);
				return cycle;
			}
			
			if (!visiting.contains(neighbor) && !visited.contains(neighbor)) {
				// Only visit neighbor if it's unvisited
				Path possibleCycle = visitNotMixed(neighbor, unvisited, visiting, visited, parents);
				if (possibleCycle != null) {
					// Encountered a cycle
					return possibleCycle;
				}
			}
		}
		
		// Move node from visiting to visited
		visiting.remove(n);
		visited.add(n);
		return null;
	}
	
	/**
	 * Searches the graph for any cycle. Returns null if the graph is acyclic.
	 * This procedure works specifically for mixed graphs.
	 * 
	 * @param graph The mixed graph to search for a cycle.
	 * @return A path containing a cycle in the graph, or null if none exists.
	 */
	private static Path findCycleMixed(Graph graph) {
		Path potential2Cycle = CycleAlgorithms.find2Cycle(graph);
		if (potential2Cycle != null) {
			return potential2Cycle;
		}
		
		// Look for larger cycles
		Set<Node> relevantNodes = new HashSet<>(graph.getNodes());
		Map<UnorderedNodePair, Node> redirected = new HashMap<>();
		
		// Remove irrelevant nodes
		while (removeIrrelevantNodes(relevantNodes, redirected));
		if (relevantNodes.isEmpty()) {
			// No cycle exists
			return null;
		} else {
			// Backtrack through the graph until we obtain a cycle
			Node start = StructureUtils.arbitraryElement(relevantNodes);
			Set<Node> traversed = new HashSet<>();
			Map<Node, Edge> parentEdges = new HashMap<>();
			return visitMixed(start, relevantNodes, traversed, parentEdges);
		}
	}
	
	/**
	 * A recursive helper function used to carry out something similar to
	 * DFS. This is used specifically for mixed graphs. This will not check
	 * for "small" cycles (any cycles whose length are <= 2) since this is
	 * done when find2Cycles() is called on this graph.
	 * 
	 * @param n             The node to visit.
	 * @param relevantNodes The set of relevant nodes (from findCycleMixed).
	 * @param traversed     The set of all nodes already traversed.
	 * @param parentEdges   The mapping from nodes to the edge leading to it.
	 * @return The first cycle found as a Path object, or null if no cycle was found.
	 */
	private static Path visitMixed(Node n, Set<Node> relevantNodes,
			Set<Node> traversed, Map<Node, Edge> parentEdges) {
		if (!traversed.contains(n)) {
			
			// Compute the set of edges we consider traversing
			Set<Edge> relevantEdges = new HashSet<>();
			for (Map.Entry<Node, Set<Edge>> incEntry : n.getIncomingDirectedEdges().entrySet()) {
				Node incNeighbor = incEntry.getKey();
				if (relevantNodes.contains(incNeighbor)) {
					Edge incEdge = StructureUtils.arbitraryElement(incEntry.getValue());
					relevantEdges.add(incEdge);
					continue;
				}
			}
			for (Map.Entry<Node, Set<Edge>> undEntry : n.getUndirectedEdges().entrySet()) {
				Node undNeighbor = undEntry.getKey();
				Edge parentEdge = parentEdges.get(n);
				boolean diffParent = parentEdge == null ||
						parentEdge.getOtherEndpoint(n) != undEntry.getKey();
				if (relevantNodes.contains(undNeighbor) && diffParent) {
					Edge undEdge = StructureUtils.arbitraryElement(undEntry.getValue());
					relevantEdges.add(undEdge);
				}
			}
			
			if (relevantEdges.isEmpty()) {
				// This path will not lead to a cycle
				return null;
			}
			
			// Only now add node to traversed, now that this path is promising
			traversed.add(n);
			for (Edge neighborEdge : relevantEdges) {
				Node otherEnd = neighborEdge.getOtherEndpoint(n);
				parentEdges.put(otherEnd, neighborEdge);
				Path result = visitMixed(otherEnd, relevantNodes, traversed, parentEdges);
				if (result != null) {
					// Found a cycle in recursion
					return result;
				} else {
					// No cycle found from traversing this neighbor
					traversed.remove(otherEnd);
				}
			}
			
			// If traversing neighbors did not lead to a cycle
			return null;
		} else {
			// Compute a cycle we have found
			Path cycle = new Path(n);
			Node currCycleNode = n;
			do {
				Edge nextEdge = parentEdges.get(currCycleNode);
				Node nextNode = nextEdge.getOtherEndpoint(currCycleNode);
				cycle.appendNode(nextNode, nextEdge);
				currCycleNode = nextNode;
			} while (currCycleNode != n);
			return cycle;
		}
	}
	
	/**
	 * A procedure used for testing a mixed graph for acyclicity. We perform
	 * the following steps: Iterate through all relevant nodes, and stop if
	 * one of the following occurs:
	 *  
	 * - If there is a node with no incoming edges, mark it irrelevant.
	 * - If there is a node N with exactly one neighbor with whom it shares an
	 *   undirected edge, direct these undirected edges to point to N.
	 * 
	 * @param relevantNodes The set of relevant nodes.
	 * @param redirected    A mapping representing which edges are redirected.
	 * @return false iff no modifications to the graph were made.
	 */
	private static boolean removeIrrelevantNodes(Set<Node> relevantNodes,
			Map<UnorderedNodePair, Node> redirected) {
		Iterator<Node> nodeIterator = relevantNodes.iterator();
		while (nodeIterator.hasNext()) {
			Node current = nodeIterator.next();
			
			boolean noIncoming = current.getIncomingDirectedEdges().isEmpty();
			boolean noUndirected = current.getUndirectedEdges().isEmpty();
			
			// Check if any edges have been redirected toward current node, in
			// which case there ARE incoming edges
			boolean noneRedirected = true;
			for (Node undirectedNeighbor : current.getUndirectedEdges().keySet()) {
				if (relevantNodes.contains(undirectedNeighbor)) {
					UnorderedNodePair key = new UnorderedNodePair(current, undirectedNeighbor);
					if (redirected.get(key) == current) {
						noneRedirected = false;
						break;
					}
				}
			}
			
			// Check if there exist incoming edges from nodes which are still
			// relevant
			boolean allIrrelevant = true;
			for (Node incomingNeighbor : current.getIncomingDirectedEdges().keySet()) {
				if (relevantNodes.contains(incomingNeighbor)) {
					allIrrelevant = false;
					break;
				}
			}
			noIncoming = noneRedirected && allIrrelevant;
			
			// Check if all incident undirected edges have been redirected, in
			// which case there are no neighboring undirected edges
			boolean allRedirected = true;
			for (Node redirectedNeighbor : current.getUndirectedEdges().keySet()) {
				if (relevantNodes.contains(redirectedNeighbor)) {
					// If only one entry, we check if all edges in the entry
					// are redirected
					UnorderedNodePair key = new UnorderedNodePair(current, redirectedNeighbor);
					if (redirected.get(key) == null) {
						allRedirected = false;
						break;
					}
				}
			}
			noUndirected = allRedirected;
			
			if (noIncoming && noUndirected) {
				// If no undirected edges or incoming directed edges, this node
				// cannot be a member of a cycle
				nodeIterator.remove();
				
				return true;
			} else if (noIncoming && current.getUndirectedEdges().size() == 1) {
				// Get the one undirected edge incident to the current node
				Node undirectedNeighbor = StructureUtils.arbitraryElement(current.getUndirectedEdges().keySet());
				
				// Reorient the undirected edges to be an incoming edge, since
				// this is the only way for this node to be in a cycle
				UnorderedNodePair newKey = new UnorderedNodePair(current, undirectedNeighbor);
				redirected.put(newKey, current);
				
				return true;
			}
		}
		
		return false;
	}
	
}
