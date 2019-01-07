package graph.components;

import graph.components.gb.GBNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An instance represents a node component of a graph.
 *
 * @author Brian Yao
 */
@NoArgsConstructor
public class Node extends GraphComponent {

	// The GBNode associated with this node (if any).
	@Getter @Setter
	private GBNode gbNode;

	/**
	 * Creates a node with the given component ID.
	 *
	 * @param id The ID of this node.
	 */
	public Node(int id) {
		this.setId(id);
	}

	@Override
	public String toString() {
		return String.format("{%d}", this.getId());
	}

}
