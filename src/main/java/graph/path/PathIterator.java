package graph.path;

import graph.components.Edge;
import graph.components.Node;
import org.javatuples.Pair;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Iterator for iterating over a path.
 *
 * @author Brian Yao
 */
public class PathIterator implements Iterator<Pair<Node, Edge>> {

	private ListIterator<Node> nodeIterator;
	private ListIterator<Edge> edgeIterator;

	public PathIterator(Path path) {
		this.nodeIterator = path.getNodes().listIterator();
		this.edgeIterator = path.getEdges().listIterator();
	}

	@Override
	public Pair<Node, Edge> next() {
		if (!this.hasNext()) {
			throw new NoSuchElementException("No remaining items in the pathIterator.");
		}

		Edge nextEdge = edgeIterator.hasNext() ? edgeIterator.next() : null;
		return new Pair<>(nodeIterator.next(), nextEdge);
	}

	@Override
	public boolean hasNext() {
		return nodeIterator.hasNext();
	}

}
