package algorithms;

import graph.Graph;
import graph.GraphConstraint;
import graph.components.Edge;
import graph.components.Node;
import graph.path.Cycle;
import graph.path.Path;
import structures.UOPair;
import util.GraphUtils;
import util.StructureUtils;

import java.util.*;

/**
 * Implementation of algorithms for finding cycles and determining acyclicity.
 *
 * @author Brian Yao
 */
public final class Cycles {

	/**
	 * Check whether a particular graph is acyclic (contains no cycle). This is
	 * applicable to undirected, directed, and mixed graphs. The definition of
	 * acyclic for a mixed graph is: if there is no way to change undirected
	 * edges into directed edges such that there is a directed cycle formed as
	 * a result (and there are no undirected or directed cycles), then the
	 * graph is acyclic.
	 *
	 * @param graph The graph to search in for cycles.
	 * @return true iff the graph contains no cycles.
	 */
	public static boolean isAcyclic(Graph graph) {
		return findCycle(graph) == null;
	}

	/**
	 * Finds an arbitrary graph cycle and returns it as a Cycle object.
	 * If no cycle exists in the graph, return null.
	 *
	 * @param graph The graph to find a cycle in.
	 * @return a cycle contained in the given graph, or null if none exist.
	 */
	public static Cycle findCycle(Graph graph) {
		Cycle potential2Cycle = Cycles.findSmallCycle(graph);
		if (potential2Cycle != null) {
			return potential2Cycle;
		}

		if (graph.hasConstraint(GraphConstraint.MIXED)) {
			return findCycleMixed(graph);
		} else {
			return findCycleNotMixed(graph);
		}
	}

	/**
	 * If the provided graph is a multigraph, search for 2-cycles (cycles
	 * containing only two nodes; note that this is impossible for simple
	 * graphs). Also searches for 1-cycles (self loop edges). Returns a cycle
	 * of length <= 2 if one exists, or null otherwise.
	 *
	 * @param graph The graph to search for 1 and 2-cycles.
	 * @return a 1-cycle or 2-cycle if one exists, or null if none exists.
	 */
	private static Cycle findSmallCycle(Graph graph) {
		if (graph.hasConstraint(GraphConstraint.MULTIGRAPH)) {
			for (Node node : graph.getNodes()) {
				Set<Edge> selfEdges = graph.getAdjListOf(node).getSelfEdges();
				if (!selfEdges.isEmpty()) {
					return new Cycle(List.of(node), List.of(StructureUtils.arbitraryElement(selfEdges)));
				}
			}

			UOPair<Node> ends = null;
			for (Map.Entry<UOPair<Node>, List<Edge>> edgeEntry : graph.getEdges().entrySet()) {
				Node firstEnd = edgeEntry.getKey().getFirst();
				int undirected = 0;
				int outgoingDirected = 0;
				int incomingDirected = 0;

				for (Edge edge : edgeEntry.getValue()) {
					if (!edge.isDirected() &&
						(++undirected > 1 || outgoingDirected > 0 || incomingDirected > 0)) {
						// Undirected or mixed 2-cycle
						ends = edgeEntry.getKey();
						break;
					} else if (edge.isDirected()) {
						if (edge.getFirstEnd() == firstEnd) {
							outgoingDirected++;
						} else {
							incomingDirected++;
						}

						if (incomingDirected > 0 && outgoingDirected > 0) {
							// Directed 2-cycle
							ends = edgeEntry.getKey();
							break;
						}
					}
				}

				if (ends != null) {
					break;
				}
			}

			if (ends != null) {
				// Construct 2-cycle
				Node first = ends.getFirst();
				Node second = ends.getSecond();

				Edge toSecond = GraphUtils.arbitraryEdge(graph, first, second, true);

				// Avoid using the same undirected edge in both directions
				Set<Edge> toFirst = graph.getAdjListOf(second).getEdgesToNeighbor(first, true);
				toFirst.remove(toSecond);

				return new Cycle(List.of(first, second), List.of(toSecond, StructureUtils.arbitraryElement(toFirst)));
			}
		}

		// If the graph is not a multigraph, 2-cycles and 1-cycles cannot exist
		return null;
	}

	/**
	 * Searches the graph for any cycle. Returns null if the graph is acyclic.
	 * This procedure works specifically for undirected and directed graphs.
	 *
	 * @param graph The undirected or directed graph to search for a cycle.
	 * @return the first cycle found, or null if none exists.
	 */
	private static Cycle findCycleNotMixed(Graph graph) {
		Set<Node> unvisited = new HashSet<>(graph.getNodes());
		Set<Node> visited = new HashSet<>();
		Set<Node> visiting = new HashSet<>();
		Map<Node, Node> parents = new HashMap<>();

		// Perform a depth-first search on every connected component
		while (!unvisited.isEmpty()) {
			Node start = StructureUtils.arbitraryElement(unvisited);
			parents.put(start, null);

			// Perform depth first search
			Cycle potentialCycle = visitNotMixed(graph, start, unvisited, visiting, visited, parents);
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
	 * whose length is <= 2) since this is done when findSmallCycle is called
	 * on this graph.
	 *
	 * @param graph     The graph containing the node being visited
	 * @param n         The node being visited.
	 * @param unvisited The set of unvisited nodes.
	 * @param visiting  The set of nodes being visited.
	 * @param visited   The set of nodes which are fully visited.
	 * @param parents   The map from a node to its "parent" node from which
	 *                  the node was visited.
	 * @return The first cycle found, or null if no cycle was found.
	 */
	private static Cycle visitNotMixed(Graph graph, Node n, Set<Node> unvisited, Set<Node> visiting,
									   Set<Node> visited, Map<Node, Node> parents) {
		unvisited.remove(n);
		visiting.add(n);
		for (Node neighbor : graph.getAdjListOf(n).getNeighbors(true)) {
			if (!parents.containsKey(neighbor)) {
				parents.put(neighbor, n);
			}

			if (visiting.contains(neighbor) && parents.get(n) != neighbor) {
				// Encountered a cycle; compute cycle using parents map
				Path cycle = new Path(neighbor);
				Node currentNode = neighbor;
				Node prevNode = n;
				do {
					Edge edgeToCurrent = GraphUtils.arbitraryEdge(graph, prevNode, currentNode, true);
					cycle.prependNode(prevNode, edgeToCurrent);
					currentNode = prevNode;
					prevNode = parents.get(currentNode);
				} while (currentNode != neighbor);
				return new Cycle(cycle);
			}

			if (!visiting.contains(neighbor) && !visited.contains(neighbor)) {
				// Only visit neighbor if it's unvisited
				Cycle possibleCycle = visitNotMixed(graph, neighbor, unvisited, visiting, visited, parents);
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
	 * @return A cycle in the graph, or null if none exists.
	 */
	private static Cycle findCycleMixed(Graph graph) {
		Set<Node> relevantNodes = new HashSet<>(graph.getNodes());
		Map<UOPair<Node>, Node> redirected = new HashMap<>();

		// Remove irrelevant nodes until there are none left
		while (removeIrrelevantNodes(graph, relevantNodes, redirected));
		if (relevantNodes.isEmpty()) {
			// No cycle exists
			return null;
		} else {
			// Backtrack through the graph until we obtain a cycle
			Node start = StructureUtils.arbitraryElement(relevantNodes);
			Set<Node> traversed = new HashSet<>();
			Map<Node, Edge> parentEdges = new HashMap<>();
			return visitMixed(graph, start, relevantNodes, traversed, parentEdges);
		}
	}

	/**
	 * A recursive helper function used to carry out something similar to
	 * DFS. This is used specifically for mixed graphs. This will not check
	 * for "small" cycles (any cycles whose length are <= 2).
	 *
	 * @param graph         The graph containing the node being visited.
	 * @param n             The node to visit.
	 * @param relevantNodes The set of relevant nodes (from findCycleMixed).
	 * @param traversed     The set of all nodes already traversed.
	 * @param parentEdges   The mapping from nodes to the edge leading to it.
	 * @return The first cycle found as a Path object, or null if no cycle was found.
	 */
	private static Cycle visitMixed(Graph graph, Node n, Set<Node> relevantNodes,
								   Set<Node> traversed, Map<Node, Edge> parentEdges) {
		if (!traversed.contains(n)) {
			// Compute the set of edges we consider traversing
			Set<Edge> relevantEdges = new HashSet<>();
			for (Map.Entry<Node, Set<Edge>> incEntry : graph.getAdjListOf(n).getIncomingDirectedEdges().entrySet()) {
				Node incNeighbor = incEntry.getKey();
				if (relevantNodes.contains(incNeighbor)) {
					Edge incEdge = StructureUtils.arbitraryElement(incEntry.getValue());
					if (incEdge != null) {
						relevantEdges.add(incEdge);
					}
				}
			}
			for (Map.Entry<Node, Set<Edge>> undEntry : graph.getAdjListOf(n).getUndirectedEdges().entrySet()) {
				Node undNeighbor = undEntry.getKey();
				Edge parentEdge = parentEdges.get(n);
				boolean diffParent = parentEdge == null ||
					parentEdge.getOtherEndpoint(n) != undEntry.getKey();
				if (relevantNodes.contains(undNeighbor) && diffParent) {
					Edge undEdge = StructureUtils.arbitraryElement(undEntry.getValue());
					if (undEdge != null) {
						relevantEdges.add(undEdge);
					}
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
				Cycle result = visitMixed(graph, otherEnd, relevantNodes, traversed, parentEdges);
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
			return new Cycle(cycle);
		}
	}

	/**
	 * A procedure used for testing a mixed graph for acyclicity. We perform
	 * the following steps until none can be performed.
	 *
	 * - If there is a node with no incoming directed edges or incident
	 *   undirected edges, mark it irrelevant by removing it from the set of
	 *   relevant nodes.
	 * - If there is a node N with exactly one neighbor with whom it shares an
	 *   undirected edge, direct these undirected edges to point to N.
	 *
	 * @param graph         The graph to remove irrelevant nodes from.
	 * @param relevantNodes The set of relevant nodes.
	 * @param redirected    A mapping representing which edges are redirected.
	 * @return false iff no modifications to the graph were made.
	 */
	private static boolean removeIrrelevantNodes(Graph graph, Set<Node> relevantNodes,
												 Map<UOPair<Node>, Node> redirected) {
		Map<UOPair<Node>, Node> tempRedirected = new HashMap<>();
		Set<Node> tempIrrelevantNodes = new HashSet<>();
		for (Node current : relevantNodes) {
			// Check if any edges have been redirected toward current node, in
			// which case there ARE incoming edges. At the same time, check if
			// all incident undirected edges have been redirected, such that
			// there are actually not any incident undirected edges.
			boolean noneRedirected = true;
			boolean allRedirected = true;
			Set<Node> undirectedNeighbors = new HashSet<>();
			for (Node undirectedNeighbor : graph.getAdjListOf(current).getUndirectedEdges().keySet()) {
				if (relevantNodes.contains(undirectedNeighbor)) {
					UOPair<Node> key = new UOPair<>(current, undirectedNeighbor);
					if (!redirected.containsKey(key)) {
						// The undirected edges with this neighbor have not
						// been redirected
						undirectedNeighbors.add(undirectedNeighbor);

						allRedirected = false;
					} else if (redirected.get(key) == current) {
						noneRedirected = false;
					}
				}
			}

			// Check if there exist incoming edges from nodes which are still
			// relevant
			boolean noDirectedIncoming = true;
			for (Node incomingNeighbor : graph.getAdjListOf(current).getIncomingDirectedEdges().keySet()) {
				if (relevantNodes.contains(incomingNeighbor)) {
					noDirectedIncoming = false;
					break;
				}
			}

			// Define conditions for no incoming directed edges and no incident
			// undirected edges
			boolean noIncoming = noneRedirected && noDirectedIncoming;
			boolean noUndirected = allRedirected;

			// Check if all incident undirected edges have been redirected, in
			// which case there are no neighboring undirected edges
			if (noUndirected && noIncoming) {
				// If no undirected edges or incoming directed edges, this node
				// is irrelevant; slate it for removal from relevant nodes
				tempIrrelevantNodes.add(current);
			} else if (noIncoming && undirectedNeighbors.size() == 1) {
				// There is exactly one neighbor with which the current node
				// has undirected edges
				Node redirectedNeighbor = StructureUtils.arbitraryElement(undirectedNeighbors);

				// Reorient the undirected edges to be an incoming edge, since
				// this is the only way for this node to be in a cycle
				UOPair<Node> newKey = new UOPair<>(current, redirectedNeighbor);
				tempRedirected.put(newKey, current);
			}
		}

		// Update the state of the graph (if there are changes to be made)
		relevantNodes.removeAll(tempIrrelevantNodes);
		redirected.putAll(tempRedirected);

		// Return true if we marked any nodes as irrelevant or redirected any
		// edges; false otherwise
		return !tempIrrelevantNodes.isEmpty() && !tempRedirected.isEmpty();
	}

}