package graph.path;

import graph.components.Edge;
import graph.components.Node;
import org.javatuples.Pair;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator for iterating over a path.
 *
 * @author Brian Yao
 */
public class PathIterator implements Iterator<Pair<Node, Edge>> {

	private Iterator<Node> nodeIterator;
	private Iterator<Edge> edgeIterator;

	private boolean delay;

	/**
	 * Construct a path iterator for the following path. This provides two
	 * ways of iterating over a path; either each iteration gives a node and
	 * its preceding (entering) edge or a node and its following (exiting)
	 * edge. This behavior is determined by the outgoingEdges parameter, which
	 * is set to false for the former behavior and true for the latter.
	 *
	 * If outgoingEdges is false, then the first pair will contain the first
	 * node and a null edge. If it is true, then the last pair will contain
	 * the last node and a null edge.
	 *
	 * @param path          The path to iterate over.
	 * @param outgoingEdges True if we want to iterate over nodes and their
	 *                      incoming edges in the path, false if we want the
	 *                      outgoing edges instead.
	 * @see Path
	 */
	PathIterator(Path path, boolean outgoingEdges) {
		this.nodeIterator = path.getNodes().iterator();
		this.edgeIterator = path.getEdges().iterator();

		delay = !outgoingEdges;
	}

	@Override
	public Pair<Node, Edge> next() {
		if (!this.hasNext()) {
			throw new NoSuchElementException("No remaining items in the pathIterator.");
		}

		Edge nextEdge = (edgeIterator.hasNext() && !delay) ? edgeIterator.next() : null;
		delay = false;
		return new Pair<>(nodeIterator.next(), nextEdge);
	}

	@Override
	public boolean hasNext() {
		return nodeIterator.hasNext();
	}

}
