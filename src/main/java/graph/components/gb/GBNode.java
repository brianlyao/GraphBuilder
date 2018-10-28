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
	private NodePanel nodePanel;

	@Getter
	private Node node;

	/**
	 * Initialize a new GBNode.
	 *
	 * @param node      The node to attach a context to.
	 * @param context   The context this belongs to.
	 * @param nodePanel The panel for displaying on the editor.
	 */
	public GBNode(Node node, GBContext context, NodePanel nodePanel) {
		super(context);
		this.node = node;
		this.node.setGbNode(this);
		this.nodePanel = nodePanel;
		this.nodePanel.setGbNode(this);
	}

	/**
	 * Copy constructor. Uses the same context as the given GBNode, but the
	 * underlying Node is new.
	 *
	 * @param gbNode The node whose context to copy.
	 */
	public GBNode(GBNode gbNode) {
		this(new Node(), gbNode.getContext(), new NodePanel(gbNode.nodePanel));
	}

	@Override
	public String toString() {
		Point coords = nodePanel.getCoords();
		return String.format("GBNode[x=%d,y=%d,r=%d,text=%s,id=%d]", coords.x, coords.y,
							 nodePanel.getRadius(), nodePanel.getText() == null ? "" : nodePanel.getText(), getId());
	}

	@Override
	public String toStorageString() {
		Point coords = nodePanel.getCoords();
		return String.format("%s%d,%d,%d,%d,%s,%d,%d,%d", FileUtils.NODE_PREFIX, getId(), coords.x,
							 coords.y, nodePanel.getRadius(), nodePanel.getText(),
							 nodePanel.getFillColor().getRGB(), nodePanel.getBorderColor().getRGB(),
							 nodePanel.getTextColor().getRGB());
	}

}
