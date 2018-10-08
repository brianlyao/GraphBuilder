package clipboard;

import context.GBContext;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import lombok.Getter;
import structures.OrderedPair;
import structures.UOPair;
import util.CoordinateUtils;
import util.StructureUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A clipboard object storing a single set of nodes and edges. These
 * can be copied using copy and cut commands.
 *
 * @author Brian
 */
public class Clipboard {

	@Getter
	private GBContext context;
	@Getter
	private boolean empty;

	// Clipboard contents
	private Set<GBNode> copiedNodes;
	private Map<UOPair<GBNode>, List<GBEdge>> copiedEdges;

	@Getter
	private Map<GBNode, OrderedPair<Integer>> posFromCenterOfMass;

	private int numTimesPasted;

	/**
	 * @param ctxt The context this clipboard belongs to.
	 */
	public Clipboard(GBContext ctxt) {
		context = ctxt;
		empty = true;
	}

	/**
	 * Get the set of nodes this clipboard holds.
	 *
	 * @return The copied nodes.
	 */
	public Set<GBNode> getNodes() {
		return copiedNodes;
	}

	/**
	 * Get the map of edges this clipboard holds.
	 *
	 * @return The copied edges.
	 */
	public Map<UOPair<GBNode>, List<GBEdge>> getEdges() {
		return copiedEdges;
	}

	/**
	 * Set the contents of this clipboard.
	 *
	 * @param copiedNodes The copied nodes.
	 * @param copiedEdges The copied edges.
	 */
	public void setContents(Set<GBNode> copiedNodes, Map<UOPair<GBNode>, List<GBEdge>> copiedEdges) {
		if (copiedNodes == null || copiedEdges == null || copiedNodes.isEmpty()) {
			throw new IllegalArgumentException("Cannot set clipboard contents to empty. Use clear() to empty the clipboard.");
		}

		this.copiedNodes = new HashSet<>(copiedNodes);
		this.copiedEdges = new HashMap<>(StructureUtils.shallowCopy(copiedEdges));
		empty = false;
		numTimesPasted = 0;

		Point centerOfMass = CoordinateUtils.centerOfMass(copiedNodes);
		posFromCenterOfMass = copiedNodes.stream().collect(Collectors.toMap(
			Function.identity(),
			node -> new OrderedPair<>(node.getNodePanel().getXCoord() - centerOfMass.x,
									  node.getNodePanel().getYCoord() - centerOfMass.y)
		));
	}

	/**
	 * Clear the contents of the clipboard.
	 */
	public void clear() {
		copiedNodes = null;
		copiedEdges = null;
		posFromCenterOfMass = null;
		empty = true;
		numTimesPasted = 0;
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
		if (increment) {
			numTimesPasted++;
		} else {
			numTimesPasted--;
		}
	}

	/**
	 * Get the number of times the currently stored objects have been pasted.
	 *
	 * @return The integer count.
	 */
	public int getTimesPasted() {
		return numTimesPasted;
	}

}
