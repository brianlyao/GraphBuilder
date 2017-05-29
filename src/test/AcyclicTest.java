package test;

import static org.junit.Assert.*;

import graph.Graph;
import graph.GraphConstraint;

import org.junit.Test;

import algorithms.CycleAlgorithms;
import components.Edge;
import components.Node;
import components.SelfEdge;
import components.SimpleEdge;
import context.GraphBuilderContext;

/**
 * A JUnit test case for testing the algorithms inside the CycleAlgorithms
 * class.
 * 
 * @author Brian Yao
 */
public class AcyclicTest {

	/**
	 * Add the specified nodes to this graph.
	 * 
	 * @param nodes An array of nodes to add.
	 */
	private static void addNodes(Graph graph, Node...nodes) {
		for (Node n : nodes) {
			graph.addNode(n);
		}
	}
	
	/**
	 * Add the specified edges to the graph. Using this method implies no
	 * additional data needs to be provided for any of the edges being
	 * added.
	 * 
	 * @param edges The array of edges to add.
	 * @return true iff every edge was successfully added.
	 */
	private static boolean addEdges(Graph graph, Edge...edges) {
		boolean added = true;
		for (Edge e : edges) {
			added = added && graph.addEdge(e);
		}
		return added;
	}
	
	@Test
	public void emptyGraphTest() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.UNDIRECTED;

		Graph graph = new Graph(constraints);
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
	}
	
	@Test
	public void nullGraphTest() {
		final int numNodes = 10;
		int id = 0;
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.UNDIRECTED;
		GraphBuilderContext context = new GraphBuilderContext(constraints);

		Graph graph = new Graph(constraints);
		for (int i = 0 ; i < numNodes ; i++) {
			graph.addNode(new Node(context, id++));
		}
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
	}
	
	@Test
	public void selfEdgeTest() {
		int id = 0;
		int constraints = GraphConstraint.MULTIGRAPH | GraphConstraint.UNWEIGHTED | GraphConstraint.MIXED;
		GraphBuilderContext context = new GraphBuilderContext(constraints);

		Graph graph = new Graph(constraints);
		Node n = new Node(context, id++);
		Edge e = new SelfEdge(n, null, false, context, id++);
		graph.addNode(n);
		assertTrue(graph.addEdge(e));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e);
		Edge e1 = new SelfEdge(n, null, true, context, id++);
		assertTrue(graph.addEdge(e1));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
	}
	
	@Test
	public void simpleUndirectedSmallGraphTest() {
		int id = 0;
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.UNDIRECTED;
		GraphBuilderContext context = new GraphBuilderContext(constraints);

		Graph graph = new Graph(constraints);
		Node n0 = new Node(context, id++);
		Node n1 = new Node(context, id++);
		Node n2 = new Node(context, id++);
		Edge e1 = new SimpleEdge(n0, n1, null, false, context, id++);
		Edge e2 = new SimpleEdge(n1, n2, null, false, context, id++);
		Edge e3 = new SimpleEdge(n0, n2, null, false, context, id++);
		addNodes(graph, n0, n1, n2);
		assertTrue(addEdges(graph, e1, e2, e3));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e3);
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
	}
	
	@Test
	public void simpleDirectedSmallGraphTest() {
		int id = 0;
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.DIRECTED;
		GraphBuilderContext context = new GraphBuilderContext(constraints);

		Graph graph = new Graph(constraints);
		Node n0 = new Node(context, id++);
		Node n1 = new Node(context, id++);
		Node n2 = new Node(context, id++);
		Edge e1 = new SimpleEdge(n0, n1, null, true, context, id++);
		Edge e2 = new SimpleEdge(n1, n2, null, true, context, id++);
		Edge e3 = new SimpleEdge(n2, n0, null, true, context, id++);
		addNodes(graph, n0, n1, n2);
		assertTrue(addEdges(graph, e1, e2, e3));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e3);
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
		
		Edge e4 = new SimpleEdge(n0, n2, null, true, context, id++);
		assertTrue(graph.addEdge(e4));
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e2);
		Edge e5 = new SimpleEdge(n2, n1, null, true, context, id++);
		assertTrue(graph.addEdge(e5));
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e1);
		Edge e6 = new SimpleEdge(n1, n0, null, true, context, id++);
		assertTrue(graph.addEdge(e6));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
	}
	
	@Test
	public void simpleMixedSmallGraphTest() {
		int id = 0;
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.DIRECTED | GraphConstraint.UNDIRECTED;
		GraphBuilderContext context = new GraphBuilderContext(constraints);

		Graph graph = new Graph(constraints);
		Node n0 = new Node(context, id++);
		Node n1 = new Node(context, id++);
		Node n2 = new Node(context, id++);
		Node n3 = new Node(context, id++);
		Edge e1 = new SimpleEdge(n0, n1, null, true, context, id++);
		Edge e2 = new SimpleEdge(n2, n1, null, true, context, id++);
		Edge e3 = new SimpleEdge(n2, n3, null, false, context, id++);
		Edge e4 = new SimpleEdge(n0, n3, null, false, context, id++);
		
		addNodes(graph, n0, n1, n2, n3);
		assertTrue(addEdges(graph, e1, e2, e3, e4));
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
		
		Edge e5 = new SimpleEdge(n0, n2, null, false, context, id++);
		assertTrue(graph.addEdge(e5));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e5);
		Edge e6 = new SimpleEdge(n0, n2, null, true, context, id++);
		assertTrue(graph.addEdge(e6));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e2);
		graph.removeEdge(e6);
		Edge e7 = new SimpleEdge(n1, n2, null, false, context, id++);
		assertTrue(graph.addEdge(e7));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e1);
		graph.removeEdge(e7);
		Edge e8 = new SimpleEdge(n1, n0, null, true, context, id++);
		assertTrue(graph.addEdge(e2));
		assertTrue(graph.addEdge(e8));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e3);
		Edge e9 = new SimpleEdge(n2, n3, null, true, context, id++);
		assertTrue(graph.addEdge(e9));
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
	}
	
	@Test
	public void simpleUndirectedMediumGraphTest() {
		int id = 0;
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.UNDIRECTED;
		GraphBuilderContext context = new GraphBuilderContext(constraints);

		Graph graph = new Graph(constraints);
		Node n0 = new Node(context, id++);
		Node n1 = new Node(context, id++);
		Node n2 = new Node(context, id++);
		Node n3 = new Node(context, id++);
		Node n4 = new Node(context, id++);
		Node n5 = new Node(context, id++);
		Node n6 = new Node(context, id++);
		Node n7 = new Node(context, id++);
		Node n8 = new Node(context, id++);
		Node n9 = new Node(context, id++);
		Node n10 = new Node(context, id++);
		Edge e0 = new SimpleEdge(n0, n1, null, false, context, id++);
		Edge e1 = new SimpleEdge(n1, n2, null, false, context, id++);
		Edge e2 = new SimpleEdge(n2, n3, null, false, context, id++);
		Edge e3 = new SimpleEdge(n1, n5, null, false, context, id++);
		Edge e4 = new SimpleEdge(n4, n5, null, false, context, id++);
		Edge e5 = new SimpleEdge(n5, n8, null, false, context, id++);
		Edge e6 = new SimpleEdge(n5, n6, null, false, context, id++);
		Edge e7 = new SimpleEdge(n6, n9, null, false, context, id++);
		Edge e8 = new SimpleEdge(n6, n7, null, false, context, id++);
		Edge e9 = new SimpleEdge(n7, n10, null, false, context, id++);
		
		addNodes(graph, n0, n1, n2, n3, n4, n5, n6, n7, n8, n9, n10);
		assertTrue(addEdges(graph, e0, e1, e2, e3, e4, e5, e6, e7, e8, e9));
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
		
		Edge e10 = new SimpleEdge(n3, n10, null, false, context, id++);
		assertTrue(graph.addEdge(e10));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e6);
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
		
		Edge e11 = new SimpleEdge(n8, n9, null, false, context, id++);
		assertTrue(graph.addEdge(e11));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e1);
		graph.removeEdge(e3);
		graph.removeEdge(e8);
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
		
		Edge e12 = new SimpleEdge(n2, n7, null, false, context, id++);
		assertTrue(graph.addEdge(e12));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e12);
		Edge e13 = new SimpleEdge(n4, n8, null, false, context, id++);
		assertTrue(graph.addEdge(e13));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
	}
	
	@Test
	public void simpleDirectedMediumGraphTest() {
		int id = 0;
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.DIRECTED;
		GraphBuilderContext context = new GraphBuilderContext(constraints);

		Graph graph = new Graph(constraints);
		Node n0 = new Node(context, id++);
		Node n1 = new Node(context, id++);
		Node n2 = new Node(context, id++);
		Node n3 = new Node(context, id++);
		Node n4 = new Node(context, id++);
		Node n5 = new Node(context, id++);
		Node n6 = new Node(context, id++);
		Node n7 = new Node(context, id++);
		Node n8 = new Node(context, id++);
		Node n9 = new Node(context, id++);
		Node n10 = new Node(context, id++);
		Edge e0 = new SimpleEdge(n0, n1, null, true, context, id++);
		Edge e1 = new SimpleEdge(n1, n2, null, true, context, id++);
		Edge e2 = new SimpleEdge(n2, n3, null, true, context, id++);
		Edge e3 = new SimpleEdge(n5, n1, null, true, context, id++);
		Edge e4 = new SimpleEdge(n4, n5, null, true, context, id++);
		Edge e5 = new SimpleEdge(n5, n8, null, true, context, id++);
		Edge e6 = new SimpleEdge(n6, n5, null, true, context, id++);
		Edge e7 = new SimpleEdge(n6, n9, null, true, context, id++);
		Edge e8 = new SimpleEdge(n6, n7, null, true, context, id++);
		Edge e9 = new SimpleEdge(n7, n10, null, true, context, id++);
		
		addNodes(graph, n0, n1, n2, n3, n4, n5, n6, n7, n8, n9, n10);
		assertTrue(addEdges(graph, e0, e1, e2, e3, e4, e5, e6, e7, e8, e9));
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
		
		Edge e10 = new SimpleEdge(n2, n6, null, true, context, id++);
		assertTrue(graph.addEdge(e10));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e10);
		Edge e11 = new SimpleEdge(n6, n2, null, true, context, id++);
		assertTrue(graph.addEdge(e11));
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e11);
		Edge e12 = new SimpleEdge(n3, n10, null, true, context, id++);
		assertTrue(graph.addEdge(e12));
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
		
		Edge e13 = new SimpleEdge(n10, n4, null, true, context, id++);
		assertTrue(graph.addEdge(e13));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
	}
	
	@Test
	public void simpleMixedMediumGraphTest() {
		int id = 0;
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNWEIGHTED | GraphConstraint.MIXED;
		GraphBuilderContext context = new GraphBuilderContext(constraints);

		Graph graph = new Graph(constraints);
		Node n0 = new Node(context, id++);
		Node n1 = new Node(context, id++);
		Node n2 = new Node(context, id++);
		Node n3 = new Node(context, id++);
		Node n4 = new Node(context, id++);
		Node n5 = new Node(context, id++);
		Node n6 = new Node(context, id++);
		Node n7 = new Node(context, id++);
		Node n8 = new Node(context, id++);
		Node n9 = new Node(context, id++);
		Node n10 = new Node(context, id++);
		Node n11 = new Node(context, id++);
		Edge e0 = new SimpleEdge(n1, n0, null, true, context, id++);
		Edge e1 = new SimpleEdge(n1, n2, null, false, context, id++);
		Edge e2 = new SimpleEdge(n3, n2, null, true, context, id++);
		Edge e3 = new SimpleEdge(n4, n3, null, true, context, id++);
		Edge e4 = new SimpleEdge(n5, n4, null, true, context, id++);
		Edge e5 = new SimpleEdge(n5, n6, null, false, context, id++);
		Edge e6 = new SimpleEdge(n6, n7, null, true, context, id++);
		Edge e7 = new SimpleEdge(n5, n0, null, true, context, id++);
		Edge e8 = new SimpleEdge(n3, n8, null, true, context, id++);
		Edge e9 = new SimpleEdge(n2, n11, null, false, context, id++);
		Edge e10 = new SimpleEdge(n8, n11, null, false, context, id++);
		Edge e11 = new SimpleEdge(n1, n9, null, false, context, id++);
		Edge e12 = new SimpleEdge(n5, n7, null, true, context, id++);
		Edge e13 = new SimpleEdge(n9, n10, null, true, context, id++);
		Edge e14 = new SimpleEdge(n11, n10, null, true, context, id++);
		
		addNodes(graph, n0, n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11);
		assertTrue(addEdges(graph, e0, e1, e2, e3, e4, e5, e6, e7, e8, e9,
				e10, e11, e12, e13, e14));
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e6);
		Edge e15 = new SimpleEdge(n7, n6, null, true, context, id++);
		assertTrue(graph.addEdge(e15));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e5);
		Edge e16 = new SimpleEdge(n5, n6, null, true, context, id++);
		assertTrue(graph.addEdge(e16));
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e14);
		Edge e17 = new SimpleEdge(n10, n11, null, true, context, id++);
		assertTrue(graph.addEdge(e17));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e17);
		graph.removeEdge(e7);
		Edge e18 = new SimpleEdge(n0, n5, null, false, context, id++);
		assertTrue(graph.addEdge(e14));
		assertTrue(graph.addEdge(e18));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e1);
		graph.removeEdge(e9);
		graph.removeEdge(e13);
		Edge e19 = new SimpleEdge(n11, n2, null, true, context, id++);
		Edge e20 = new SimpleEdge(n10, n9, null, true, context, id++);
		assertTrue(addEdges(graph, e19, e20));
		
		assertFalse(CycleAlgorithms.isAcyclic(graph));
		
		graph.removeEdge(e3);
		Edge e21 = new SimpleEdge(n3, n4, null, true, context, id++);
		assertTrue(graph.addEdge(e21));
		
		assertTrue(CycleAlgorithms.isAcyclic(graph));
	}

}
