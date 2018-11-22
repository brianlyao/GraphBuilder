package graph.components.gb;

import context.GBContext;
import graph.components.Edge;
import lombok.Getter;
import lombok.Setter;
import structures.OrderedPair;
import structures.UOPair;
import util.FileUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;

/**
 * A GBEdge is a "GraphBuilder Edge". It represents a edge in a graph which is
 * attached to GraphBuilder context (see class context.GBContext for details).
 * This is intended to abstract GraphBuilder functionality away from the Graph
 * data structure.
 */
public class GBEdge extends GBComponent {

	public static final Color DEFAULT_COLOR = Color.BLACK;
	public static final int DEFAULT_WEIGHT = 2;

	@Getter @Setter
	private Color color; // Edge color
	@Getter @Setter
	private int weight; // Weight (thickness) of the line
	@Getter @Setter
	private String text; // Text to display next to the edge

	// The following field is only used if this is not a self edge.
	// The edge is a quadratic bezier curve OR a linear bezier (in the latter
	// case, control is null). The points are start, control, end.
	@Getter
	private Point2D.Double[] bezierPoints;

	// The following three fields are only used if this is a self edge. They
	// contain the offset angle at which the edge is positioned, the center of
	// the self-edge's arc, and the radius of the arc.
	@Getter @Setter
	private double angle;
	@Getter @Setter
	private Point2D.Double arcCenter;
	@Getter @Setter
	private double radius;

	@Getter
	private OrderedPair<GBNode> endpoints;

	@Getter
	private Edge edge;

	/**
	 * "Default" private constructor. Initializes several instance fields.
	 *
	 * @param context The context this belongs to.
	 */
	private GBEdge(GBContext context) {
		super(context);
		this.color = DEFAULT_COLOR;
		this.weight = DEFAULT_WEIGHT;

		bezierPoints = new Point2D.Double[3];
		Arrays.fill(bezierPoints, new Point2D.Double());

		arcCenter = new Point2D.Double();
	}

	/**
	 * Create a new GBEdge. The underlying Edge is newly created, with the
	 * provided endpoints.
	 *
	 * @param node1    The first endpoint.
	 * @param node2    The second endpoint.
	 * @param directed Whether the edge is directed.
	 */
	public GBEdge(GBNode node1, GBNode node2, boolean directed) {
		this(node1.getContext());
		if (node1.getContext() != node2.getContext()) {
			throw new IllegalArgumentException("Edge endpoints must belong to the same context.");
		}
		this.edge = new Edge(node1.getNode(), node2.getNode(), directed);
		this.edge.setGbEdge(this);
		this.endpoints = new OrderedPair<>(node1, node2);
	}

	/**
	 * Create a new GBEdge. The underlying Edge is the provided edge. The
	 * node endpoints are assumed to have GBNodes associated with a
	 * context.
	 *
	 * @param edge The edge to attach a context to.
	 */
	public GBEdge(Edge edge) {
		this(edge.getFirstEnd().getGbNode().getContext());
		this.edge = edge;
		this.edge.setGbEdge(this);

		GBNode end1 = edge.getFirstEnd().getGbNode();
		GBNode end2 = edge.getSecondEnd().getGbNode();
		if (end1.getContext() != end2.getContext()) {
			throw new IllegalArgumentException("Edge endpoints must belong to the same context.");
		}
		this.endpoints = new OrderedPair<>(end1, end2);
	}

	/**
	 * @return true iff the underlying edge is directed.
	 */
	public boolean isDirected() {
		return edge.isDirected();
	}

	/**
	 * @return true iff the underlying edge is a self edge.
	 */
	public boolean isSelfEdge() {
		return edge.isSelfEdge();
	}

	/**
	 * @return the first node (source) endpoint of this edge.
	 */
	public GBNode getFirstEnd() {
		return endpoints.getFirst();
	}

	/**
	 * @return the second node (sink) endpoint of this edge.
	 */
	public GBNode getSecondEnd() {
		return endpoints.getSecond();
	}

	/**
	 * @return an unordered pair containing both node endpoints.
	 */
	public UOPair<GBNode> getUoEndpoints() {
		return new UOPair<>(endpoints.getFirst(), endpoints.getSecond());
	}

	/**
	 * Set the three points necessary for a bezier curve.
	 *
	 * @param startX The x-coordinate of the point at t = 0.
	 * @param startY The y-coordinate of the point at t = 0.
	 * @param contrX The x-coordinate of the control point.
	 * @param contrY The y-coordinate of the control point.
	 * @param endX   The x-coordinate of the point at t = 1.
	 * @param endY   The y-coordinate of the point at t = 1.
	 */
	public void setBezierPoints(double startX, double startY, double contrX, double contrY, double endX, double endY) {
		bezierPoints[0].setLocation(startX, startY);
		bezierPoints[1].setLocation(contrX, contrY);
		bezierPoints[2].setLocation(endX, endY);
	}

	@Override
	public String toString() {
		GBNode n1 = endpoints.getFirst();
		GBNode n2 = endpoints.getSecond();
		return String.format("GBEdge[id=%d,n1=%d,n2=%d,color=%s,"
								 + "weight=%d,directed=%s]", this.getId(), n1.getId(),
							 n2.getId(), color, weight, edge.isDirected());
	}

	@Override
	public String toStorageString() {
		GBNode n1 = endpoints.getFirst();
		GBNode n2 = endpoints.getSecond();
		return String.format("%s%d,%d,%d,%d,%d,%d,%s,%s", FileUtils.EDGE_PREFIX, getId(), n1.getId(),
							 n2.getId(), color.getRGB(), weight, edge.isDirected() ? 1 : 0, text, angle);
	}

}
