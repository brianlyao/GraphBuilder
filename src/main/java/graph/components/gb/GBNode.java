package graph.components.gb;

import context.GBContext;
import graph.components.Node;
import graph.components.display.NodePanel;
import lombok.Getter;
import lombok.Setter;
import util.FileUtils;

import java.awt.*;

/**
 * A GBNode is a "GraphBuilder Node". It represents a node in a graph which is
 * attached to GraphBuilder context (see class context.GBContext for details).
 * This is intended to abstract GraphBuilder functionality away from the Graph
 * data structure.
 */
public class GBNode extends GBComponent {

	public static final String DEFAULT_TEXT = "";
	public static final Color DEFAULT_FILL_COLOR = Color.WHITE;
	public static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
	public static final Color DEFAULT_BORDER_COLOR = Color.BLACK;

	// The JPanel containing this node's visual appearance
	@Getter @Setter
	private NodePanel panel;

	@Getter
	private Node node;

	/**
	 * Construct a new GBNode with the given node.
	 *
	 * @param node      The node to attach a context to.
	 * @param context   The context this belongs to.
	 * @param panel The panel for displaying on the editor.
	 */
	public GBNode(Node node, GBContext context, NodePanel panel) {
		super(context);
		this.node = node;
		this.node.setGbNode(this);
		this.panel = panel;
		this.panel.setGbData(this);
	}

	/**
	 * Copy constructor. Uses the same context as the given GBNode, but the
	 * underlying Node is new.
	 *
	 * @param id     The ID of the new underlying node.
	 * @param gbNode The node whose context to copy.
	 */
	public GBNode(int id, GBNode gbNode) {
		this(new Node(id), gbNode.getContext(), new NodePanel(gbNode.panel));
	}

	@Override
	public String toString() {
		return String.format("GBNode[%s:(%d,%d)]", this.getId(), panel.getXCoord(), panel.getYCoord());
	}

	@Override
	public int getId() {
		return node.getId();
	}

	@Override
	public String toStorageString() {
		Point coords = panel.getCoords();
		return String.format("%s%d,%d,%d,%d,%s,%d,%d,%d", FileUtils.NODE_PREFIX, this.getId(), coords.x,
							 coords.y, panel.getRadius(), panel.getText(), panel.getFillColor().getRGB(),
							 panel.getBorderColor().getRGB(), panel.getTextColor().getRGB());
	}

}
