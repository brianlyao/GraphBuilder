package clipboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import structures.UnorderedNodePair;
import components.Edge;
import components.Node;
import context.GraphBuilderContext;

/**
 * A clipboard object storing a single set of nodes and edges. These
 * can be copied using copy and cut commands.
 * 
 * @author Brian
 */
public class Clipboard {

	private GraphBuilderContext context;
	private boolean isEmpty;
	
	private HashSet<Node> copiedNodes;
	private HashMap<UnorderedNodePair, ArrayList<Edge>> copiedEdges;
	
	private int numTimesPasted;
	
	/**
	 * @param ctxt The context this clipboard belongs to.
	 */
	public Clipboard(GraphBuilderContext ctxt) {
		copiedNodes = new HashSet<>();
		copiedEdges = new HashMap<>();
		context = ctxt;
		isEmpty = true;
	}
	
	/**
	 * Get the set of nodes this clipboard holds.
	 * 
	 * @return The copied nodes.
	 */
	public HashSet<Node> getNodes() {
		return copiedNodes;
	}
	
	/**
	 * Get the map of edges this clipboard holds.
	 * 
	 * @return The copied edges.
	 */
	public HashMap<UnorderedNodePair, ArrayList<Edge>> getEdges() {
		return copiedEdges;
	}
	
	/**
	 * Set the contents of this clipboard.
	 * 
	 * @param copiedNodes The copied nodes.
	 * @param copiedEdges The copied edges.
	 */
	public void setContents(HashSet<Node> copiedNodes, HashMap<UnorderedNodePair, ArrayList<Edge>> copiedEdges) {
		if (copiedNodes == null || copiedEdges == null || copiedNodes.isEmpty())
			return;
		this.copiedNodes.clear();
		this.copiedEdges.clear();
		this.copiedNodes.addAll(copiedNodes);
		this.copiedEdges.putAll(copiedEdges);
		isEmpty = false;
		numTimesPasted = 0;
	}
	
	/**
	 * Clear the contents of the clipboard.
	 */
	public void clear() {
		copiedNodes.clear();
		copiedEdges.clear();
		isEmpty = true;
		numTimesPasted = 0;
	}
	
	/**
	 * Check if the clipboard is empty.
	 * 
	 * @return true iff the clipboard is empty.
	 */
	public boolean isEmpty() {
		return isEmpty;
	}
	
	/**
	 * Reset the number of the times the currently stored objects have been pasted.
	 */
	public void resetTimesPasted() {
		numTimesPasted = 0;
	}
	
	/**
	 * Update (by 1 or -1) the number of times the currently stored objects have been pasted.
	 * 
	 * @param increment true if we want to add 1, false if we want to subtract 1.
	 */
	public void updateTimesPasted(boolean increment) {
		if (increment)
			numTimesPasted++;
		else
			numTimesPasted--;
	}
	
	/**
	 * Get the number of times the currently stored objects have been pasted.
	 * 
	 * @return The integer count.
	 */
	public int getTimesPasted() {
		return numTimesPasted;
	}
	
	/**
	 * Get the context this clipboard belongs to.
	 * 
	 * @return The relevant context.
	 */
	public GraphBuilderContext getContext() {
		return context;
	}
	
}
