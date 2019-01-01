package algorithms;

import graph.Graph;
import graph.components.Edge;
import graph.components.Node;
import graph.path.Path;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import util.GraphUtils;

import java.util.*;

/**
 * An implementation of Dijkstra's algorithm.
 *
 * @author Brian Yao
 */
public final class Dijkstra {

	/**
	 * An instance is a item containing the necessary information for
	 * Dijkstra's algorithm.
	 *
	 * @author Brian Yao
	 */
	private static final class DijkstraItem {

		@Getter @Setter @NonNull
		private Double priority;
		private Node currentNode;
		private Node previousNode;
		private Edge fromPreviousNode;

		/**
		 * @param priority    The initial priority of this item.
		 * @param currentNode The node this item encapsulates.
		 */
		private DijkstraItem(Double priority, Node currentNode) {
			if (priority == null || currentNode == null) {
				throw new IllegalArgumentException("Contents of DijkstraItem cannot be null.");
			}
			this.priority = priority;
			this.currentNode = currentNode;
			this.previousNode = null;
			this.fromPreviousNode = null;
		}

		/**
		 * Set previous node and edge.
		 *
		 * @param newNode   New previous node.
		 * @param toNewNode New previous edge.
		 */
		public void setPrevious(Node newNode, Edge toNewNode) {
			previousNode = newNode;
			fromPreviousNode = toNewNode;
		}

	}

	/**
	 * An implementation of Dijkstra's algorithm, which computes the shortest
	 * path from the starting node to the destination node. This is only
	 * guaranteed to yield the correct shortest path on graphs with edges that
	 * have only non-negative numeric weights.
	 *
	 * @param graph       The graph to search for the path in.
	 * @param start       The start of our path.
	 * @param destination The destination of our path.
	 * @return The shortest path from start to destination in graph, or null if
	 *         no connecting path exists.
	 */
	public static Path execute(Graph graph, Node start, Node destination) {
		Set<Node> reachableFromStart = DFS.explore(start, true);
		validateDijkstraInput(graph, start, destination);

		if (!reachableFromStart.contains(destination)) {
			// If there is no connecting path from start to destination
			return null;
		}

		if (start == destination) {
			// If the start and end are the same node
			return new Path(start);
		}

		// Keep track of which item contains which node
		Map<Node, DijkstraItem> nodeToItem = new HashMap<>();

		Comparator<DijkstraItem> comparator = Comparator.comparingDouble(DijkstraItem::getPriority);
		PriorityQueue<DijkstraItem> priorityQueue = new PriorityQueue<>(comparator);

		// Populate the priority queue
		DijkstraItem startItem = new DijkstraItem(0., start);
		priorityQueue.add(startItem);
		nodeToItem.put(start, startItem);
		for (Node node : reachableFromStart) {
			if (node != start) {
				DijkstraItem nodeItem = new DijkstraItem(Double.POSITIVE_INFINITY, node);
				priorityQueue.add(nodeItem);
				nodeToItem.put(node, nodeItem);
			}
		}

		// Visit neighbors until we visit the destination node
		while (!priorityQueue.isEmpty()) {
			DijkstraItem currentItem = priorityQueue.poll();
			Node currentNode = currentItem.currentNode;

			// Terminate the search
			if (currentNode == destination) {
				break;
			}

			// Calculate tentative distance for each neighbor
			for (Node neighbor : currentNode.getNeighbors(true)) {
				Edge minEdge = GraphUtils.minWeightEdge(currentNode, neighbor, true);
				double minEdgeWeight = minEdge.getNumericWeight();

				// Check if tentative priority is lower than existing priority
				// for this neighbor. If so, set the priority to be the new lower one
				DijkstraItem neighborItem = nodeToItem.get(neighbor);
				double newPriority = currentItem.priority + minEdgeWeight;
				if (newPriority < neighborItem.priority) {
					neighborItem.setPriority(newPriority);
					neighborItem.setPrevious(currentNode, minEdge);

					// Re-add the item to update its priority
					priorityQueue.remove(neighborItem);
					priorityQueue.add(neighborItem);
				}
			}
		}

		// Construct the shortest path
		Path shortestPath = new Path(destination);
		Node current = destination;
		while (current != start) {
			DijkstraItem currItem = nodeToItem.get(current);
			shortestPath.prependNode(currItem.previousNode, currItem.fromPreviousNode);
			current = currItem.previousNode;
		}

		return shortestPath;
	}

	/**
	 * Helper function for validating the input of Dijkstra's algorithm.
	 *
	 * @param graph       Input graph.
	 * @param start       Input start node.
	 * @param destination Input destination node.
	 */
	private static void validateDijkstraInput(Graph graph, Node start, Node destination) {
		if (!graph.containsNode(start) || !graph.containsNode(destination)) {
			throw new IllegalArgumentException("Start and destination nodes must belong to the provided graph.");
		}

		graph.getEdges().values().forEach(edgeList -> edgeList.forEach(edge -> {
			if (edge.getNumericWeight() < 0.) {
				throw new IllegalArgumentException("Cannot use Dijkstra's algorithm on a graph with negative " +
													   "weights. Use Bellman-Ford instead.");
			}
		}));
	}

}
