package ui;

import actions.PlaceNodeAction;
import context.GBContext;
import graph.Graph;
import graph.GraphConstraint;
import graph.components.Node;
import graph.components.display.NodePanel;
import graph.components.gb.GBComponent;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import lombok.Getter;
import lombok.Setter;
import math.Complex;
import math.CubicFormula;
import org.javatuples.Pair;
import preferences.Preferences;
import structures.UOPair;
import tool.Tool;
import ui.menus.EditorRightClickMenu;
import util.CoordinateUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.*;
import java.util.List;

/**
 * The main panel on which the user draws graphs.
 *
 * @author Brian Yao
 */
public class Editor extends JPanel {

	private static final long serialVersionUID = 7327691115632869820L;

	private static final Dimension DEFAULT_DIMENSION = new Dimension(2048, 2048);

	private GBFrame gui; // The GBFrame this editor is placed in

	private Point lastMousePoint; // Keep track of the last mouse position

	private Point closestEdgeSelectPoint; // Used for the edge select tool
	private GBEdge closestEdge;

	@Getter @Setter
	private GBNode edgeBasePoint; // The first node that's selected when drawing an edge

	private Set<GBNode> selectedNodes;
	private Map<UOPair<GBNode>, List<GBEdge>> selectedEdges;

	private Set<GBNode> highlightedNodes;
	private Map<UOPair<GBNode>, List<GBEdge>> highlightedEdges;

	@Getter
	private Map<NodePanel, Point> nodePanelPositionMap; // Map from panel to upper left corner position on editor

	private GBEdge previewEdge;
	private int previewEdgeIndex;
	@Setter
	private boolean drawPreviewUsingObject; // If true, the preview edge should be the object previewEdge

	/**
	 * Constructor for an editor panel.
	 *
	 * @param g The GBFrame object we want our editor placed in.
	 */
	public Editor(GBFrame g) {
		super();

		gui = g;
		lastMousePoint = new Point(0, 0);

		selectedNodes = new HashSet<>();
		selectedEdges = new HashMap<>();
		highlightedNodes = new HashSet<>();
		highlightedEdges = new HashMap<>();
		nodePanelPositionMap = new HashMap<>();

		// Initialize the panel with default settings...
		setBackground(Color.WHITE);
		setPreferredSize(DEFAULT_DIMENSION);

		// Listen for mouse events
		addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent evt) {
				if (SwingUtilities.isRightMouseButton(evt) && edgeBasePoint == null) {
					EditorRightClickMenu.show(Editor.this, evt.getX(), evt.getY());
				}
			}

			@Override
			public void mouseEntered(MouseEvent evt) {
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent evt) {
				repaint();
			}

			@Override
			public void mousePressed(MouseEvent evt) {
				Tool currentTool = gui.getCurrentTool();
				boolean shouldDeselect = currentTool == Tool.SELECT ||
					((currentTool == Tool.EDGE || currentTool == Tool.DIRECTED_EDGE) && edgeBasePoint == null);
				if (shouldDeselect) {
					if (!Editor.this.highlightsEmpty()) {
						// Clicking the canvas will remove all highlights
						Editor.this.removeAllHighlights();
						Editor.this.repaint();
					} else if ((evt.getModifiersEx() & (InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) == 0 &&
						SwingUtilities.isLeftMouseButton(evt)) {
						// Clicking the canvas will deselect all selections
						Editor.this.removeAllSelections();
						Editor.this.repaint();
						gui.getMainMenuBar().updateWithSelection();
					}
				} else if (currentTool == Tool.NODE && SwingUtilities.isLeftMouseButton(evt)) {
					// If user left clicks the canvas with the node tool, add a new node
					Color[] colors = gui.getNodeOptionsBar().getCurrentCircleColors();
					int currentRadius = gui.getNodeOptionsBar().getCurrentRadius();

					// If grid snap is enabled, the location of placement is different
					Point placed;
					if (gui.getGridSettingsDialog().getSnapToGrid()) {
						// Compute "snapped" placement position
						placed = CoordinateUtils.closestGridPoint(gui, lastMousePoint);
						placed.x -= currentRadius;
						placed.y -= currentRadius;
					} else {
						// Use regular placement position (at cursor)
						placed = new Point(Math.max(0, lastMousePoint.x - currentRadius),
										   Math.max(0, lastMousePoint.y - currentRadius));
					}
					// By default, the new node has no text
					NodePanel newPanel = new NodePanel(placed.x, placed.y, currentRadius);
					newPanel.setFillColor(colors[0]);
					newPanel.setBorderColor(colors[1]);
					newPanel.setTextColor(colors[2]);

					// The new node being added to the graph
					GBNode newNode = new GBNode(new Node(), getContext(), newPanel);

					// Perform the action for placing a node
					PlaceNodeAction placeAction = new PlaceNodeAction(getContext(), newNode);
					placeAction.perform();
					gui.getContext().pushReversibleAction(placeAction, true, false);
				}

				if (currentTool == Tool.EDGE_SELECT && closestEdge != null && SwingUtilities.isLeftMouseButton(evt)) {
					// Select the edge
					if ((evt.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != InputEvent.CTRL_DOWN_MASK &&
						(evt.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != InputEvent.SHIFT_DOWN_MASK) {
						// De-select all else if shift and control are not held
						removeAllSelections();
					}
					addSelection(closestEdge);
					gui.getMainMenuBar().updateWithSelection();
					repaint(); // To draw the selected look
				}

				if ((currentTool == Tool.EDGE || currentTool == Tool.DIRECTED_EDGE) &&
					edgeBasePoint != null && SwingUtilities.isRightMouseButton(evt)) {
					// Reset base point to "cancel" edge placement using right click
					clearEdgeBasePoint();
					repaint(); // To "un"-draw the preview
				}
			}

			@Override
			public void mouseReleased(MouseEvent evt) {}

		});

		// Listen for dragging and hovering mouse events
		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent evt) {
				Tool current = gui.getCurrentTool();
				if (current == Tool.PAN) {
					Point currentPoint = evt.getPoint();
					int changeX = currentPoint.x - lastMousePoint.x;
					int changeY = currentPoint.y - lastMousePoint.y;
					JScrollPane sp = gui.getScrollPane();
					JScrollBar horiz = sp.getHorizontalScrollBar();
					JScrollBar vert = sp.getVerticalScrollBar();
					double multiplier = Preferences.PAN_SENSITIVITY;
					int newHorizVal = (int) Math.min(horiz.getMaximum(),
													 Math.max(horiz.getMinimum(),
															  horiz.getValue() - multiplier * changeX));
					int newVertVal = (int) Math.min(vert.getMaximum(),
													Math.max(vert.getMinimum(),
															 vert.getValue() - multiplier * changeY));
					horiz.setValue(newHorizVal);
					vert.setValue(newVertVal);
				}
				lastMousePoint = evt.getPoint();
				repaint();
			}

			@Override
			public void mouseMoved(MouseEvent evt) {
				lastMousePoint = evt.getPoint();
				Tool currentTool = gui.getCurrentTool();

				if (currentTool == Tool.EDGE_SELECT) {
					findClosestEdge();
				}

				// Repaint whenever the mouse moves
				repaint();
			}

		});
	}

	/**
	 * Get the context of the GBFrame this editor is on.
	 *
	 * @return The context of the GBFrame this editor is on.
	 */
	public GBContext getContext() {
		return gui.getContext();
	}

	/**
	 * Clears all state held by the editor, like the set of selections, the nodes
	 * currently displayed, the preview edge, etc.
	 */
	public void clearState() {
		selectedNodes.clear();
		selectedEdges.clear();
		nodePanelPositionMap.clear();
		this.clearEdgeBasePoint();
		this.clearPreviewEdge();

		// Remove all node panels
		this.removeAll();
	}

	/**
	 * Get the GBFrame this editor is on.
	 *
	 * @return The GBFrame instance this editor is on.
	 */
	public GBFrame getGUI() {
		return gui;
	}

	/**
	 * Sets the last mouse point; we need this if the mouse is not on the Editor panel.
	 *
	 * @param x The x coordinate of the new position.
	 * @param y The y coordinate of the new position.
	 */
	public void setLastMousePoint(int x, int y) {
		if (lastMousePoint != null) {
			lastMousePoint.setLocation(x, y);
		} else {
			lastMousePoint = new Point(x, y);
		}
	}

	/**
	 * Add a new entry to the mapping from node panel to position.
	 *
	 * @param np The node panel we want to add.
	 */
	public void addNodePanelEntry(NodePanel np) {
		nodePanelPositionMap.put(np, np.getCoords());
	}

	/**
	 * Remove an entry from the mapping from node panel to position.
	 *
	 * @param np The node panel whose entry to remove.
	 */
	public void removeNodePanelEntry(NodePanel np) {
		nodePanelPositionMap.remove(np);
	}

	/**
	 * Check if a component is selected.
	 *
	 * @param gc The component to check.
	 * @return true iff the component is selected in this Editor.
	 */
	public boolean isSelected(GBComponent gc) {
		return componentBelongsTo(gc, selectedNodes, selectedEdges);
	}

	/**
	 * Add the given component to the set of selections.
	 *
	 * @param gc The selected component.
	 */
	public void addSelection(GBComponent gc) {
		addComponentTo(gc, selectedNodes, selectedEdges, this.getContext());
	}

	/**
	 * Add the given components to the set of selections.
	 *
	 * @param components The selected components.
	 */
	public void addSelections(Iterable<? extends GBComponent> components) {
		components.forEach(this::addSelection);
	}

	/**
	 * Remove the given component from the set of selections.
	 *
	 * @param gc The deselected component.
	 */
	public void removeSelection(GBComponent gc) {
		removeComponentFrom(gc, selectedNodes, selectedEdges);
	}

	/**
	 * Remove the given components from the set of selections.
	 *
	 * @param components The deselected components.
	 */
	public void removeSelections(Iterable<? extends GBComponent> components) {
		components.forEach(this::removeSelection);
	}

	/**
	 * Clear all selections.
	 */
	public void removeAllSelections() {
		selectedNodes.clear();
		selectedEdges.clear();
	}

	/**
	 * @return true iff no graph components are selected.
	 */
	public boolean selectionsEmpty() {
		return selectedNodes.isEmpty() && selectedEdges.isEmpty();
	}

	/**
	 * Check if a component is highlighted.
	 *
	 * @param gc The component to check.
	 * @return true iff the component is highlighted.
	 */
	public boolean isHighlighted(GBComponent gc) {
		return componentBelongsTo(gc, highlightedNodes, highlightedEdges);
	}

	/**
	 * Add the given component to the set of highlighted components.
	 *
	 * @param gc The highlighted component.
	 */
	public void addHighlight(GBComponent gc) {
		addComponentTo(gc, highlightedNodes, highlightedEdges, this.getContext());
	}

	/**
	 * Add the given components to the set of highlighted components.
	 *
	 * @param components The highlighted components.
	 */
	public void addHighlights(Iterable<? extends GBComponent> components) {
		components.forEach(this::addHighlight);
	}

	/**
	 * Clear all highlighted components.
	 */
	public void removeAllHighlights() {
		highlightedNodes.clear();
		highlightedEdges.clear();
	}

	/**
	 * @return true iff no graph components are highlighted.
	 */
	public boolean highlightsEmpty() {
		return highlightedNodes.isEmpty() && highlightedEdges.isEmpty();
	}

	/**
	 * Get the set of all selected edges.
	 *
	 * @return The set of all selected edges.
	 */
	public Set<GBEdge> getSelectedEdges() {
		Set<GBEdge> edgeSet = new HashSet<>();
		for (List<GBEdge> edgeList : selectedEdges.values()) {
			edgeSet.addAll(edgeList);
		}
		return edgeSet;
	}

	/**
	 * Get the set of all selected components. The node and
	 * edge selections are wrapped together as a Pair.
	 *
	 * @return The Pair, containing a set of selected nodes and map of selected edges.
	 */
	public Pair<Set<GBNode>, Map<UOPair<GBNode>, List<GBEdge>>> getSelections() {
		return new Pair<>(selectedNodes, selectedEdges);
	}

	/**
	 * Clear the edge base point (set it to null).
	 */
	public void clearEdgeBasePoint() {
		this.setEdgeBasePoint(null);
	}

	/**
	 * Set the editor's preview edge and the preview edge's position
	 *
	 * @param edge     The edge object representing the preview edge.
	 * @param position The position of the preview edge relative to the existing edges between the
	 *                 the same endpoints the preview edge has.
	 */
	public void setPreviewEdge(GBEdge edge, int position) {
		previewEdge = edge;
		previewEdgeIndex = position;
	}

	/**
	 * Clear the preview edge object (set it to null).
	 */
	public void clearPreviewEdge() {
		this.setPreviewEdge(null, -1);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// Explicitly set the position of the nodes; this allows the pane to be scrollable while
		// retaining the position of the circles relative to the top left corner of the editor panel
		this.getContext().getGbNodes().forEach(node -> node.getNodePanel().enforceLocation());

		// Set anti-aliasing on for smoother appearance
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw grid lines if they are enabled
		if (gui.getGridSettingsDialog().getShowGrid()) {
			int level = gui.getGridSettingsDialog().getGridLevel();
			g2d.setStroke(new BasicStroke(1));
			g2d.setColor(gui.getGridSettingsDialog().getGridColor());
			for (int xi = level ; xi < this.getWidth() ; xi += level) {
				g2d.drawLine(xi, 0, xi, this.getHeight());
			}
			for (int yi = level ; yi < this.getHeight() ; yi += level) {
				g2d.drawLine(0, yi, this.getWidth(), yi);
			}
		}

		// Draw all edges by iterating through pairs of nodes
		Map<UOPair<GBNode>, List<GBEdge>> edgeMap = gui.getContext().getGbEdges();

		// Check if the preview edge's endpoint pair exists in the edge map
		// If not, then we need to draw it separately
		Graph currentGraph = this.getContext().getGraph();
		boolean violatesLoops = !currentGraph.hasConstraint(GraphConstraint.MULTIGRAPH) &&
			previewEdge != null && previewEdge.isSelfEdge();
		if (previewEdge != null) {
			UOPair<GBNode> previewPair = previewEdge.getUoEndpoints();
			if (!edgeMap.containsKey(previewPair) && !violatesLoops) {
				List<GBEdge> previewList = Collections.singletonList(previewEdge);
				drawEdgesBetweenNodePair(g2d, previewPair, previewList, previewEdge == null);
			}
		}

		// Iterate through the pairs of nodes in the edge map, and draw the edges between them
		// If there is a preview edge, we "add" it (not directly) to the list of existing edges
		// before we draw the list of edges
		edgeMap.keySet().forEach(pair -> {
			List<GBEdge> toDrawEdges;
			List<GBEdge> pairEdges = edgeMap.get(pair);
			if (previewEdge != null && pair.equals(previewEdge.getUoEndpoints())) {
				// Check if drawing preview edge would violate a constraint
				UOPair<Node> nodePair = pair.map(GBNode::getNode);
				boolean violatesSimple = currentGraph.hasConstraint(GraphConstraint.SIMPLE) &&
					currentGraph.getEdges().containsKey(nodePair);
				if (!violatesLoops && !violatesSimple) {
					toDrawEdges = new ArrayList<>(pairEdges);
					int newIndex = previewEdgeIndex < 0 || previewEdgeIndex > pairEdges.size() ?
						pairEdges.size() : previewEdgeIndex;
					toDrawEdges.add(newIndex, previewEdge);
				} else {
					toDrawEdges = pairEdges;
				}
			} else {
				toDrawEdges = pairEdges;
			}

			// Draw edges for this pair of nodes
			drawEdgesBetweenNodePair(g2d, pair, toDrawEdges, previewEdge == null);
		});

		g2d.setStroke(new BasicStroke());

		// Draw tool-specific graphics
		Tool ctool = gui.getCurrentTool();
		if (ctool == Tool.NODE) {
			// Draw preview for the Node tool
			g2d.setColor(Preferences.PREVIEW_COLOR);
			int currentRadius = gui.getNodeOptionsBar().getCurrentRadius();

			// If snap to grid is enabled, draw the preview differently
			Ellipse2D.Double preview;
			if (gui.getGridSettingsDialog().getSnapToGrid()) {
				// Draw the "snapped" preview circle
				Point closestGridPoint = CoordinateUtils.closestGridPoint(gui, lastMousePoint);
				preview = new Ellipse2D.Double(closestGridPoint.x - currentRadius,
											   closestGridPoint.y - currentRadius,
											   2 * currentRadius, 2 * currentRadius);
			} else {
				// Draw the normal preview circle (at the cursor location)
				preview = new Ellipse2D.Double(lastMousePoint.x - currentRadius,
											   lastMousePoint.y - currentRadius,
											   2 * currentRadius, 2 * currentRadius);
			}

			g2d.draw(preview);
		} else if (ctool == Tool.EDGE_SELECT) {
			if (closestEdgeSelectPoint != null) {
				// Draw the edge select line to closest edge
				g2d.setColor(Preferences.EDGE_SELECT_PREVIEW_COLOR);
				g2d.drawLine(closestEdgeSelectPoint.x, closestEdgeSelectPoint.y, lastMousePoint.x, lastMousePoint.y);
			}
		} else if (ctool == Tool.EDGE || ctool == Tool.DIRECTED_EDGE) {
			if (edgeBasePoint != null && !drawPreviewUsingObject) {
				// Draw preview edge when not hovering over a node
				int weight = gui.getEdgeOptionsBar().getCurrentLineWeight();
				g2d.setStroke(new BasicStroke(weight));
				g2d.setColor(Preferences.PREVIEW_COLOR);
				Point center = edgeBasePoint.getNodePanel().getLocation();
				center.x += edgeBasePoint.getNodePanel().getRadius();
				center.y += edgeBasePoint.getNodePanel().getRadius();
				g2d.drawLine(center.x, center.y, lastMousePoint.x, lastMousePoint.y);
				if (ctool == Tool.DIRECTED_EDGE) {
					double dist = Point.distance(lastMousePoint.x, lastMousePoint.y, center.x, center.y);
					double unitX = (lastMousePoint.x - center.x) / dist;
					double unitY = (lastMousePoint.y - center.y) / dist;
					drawArrowTip(g2d, unitX, unitY, lastMousePoint, weight);
				}
			}
		}
	}

	/**
	 * A helper method for drawing the edges between a pair of nodes.
	 *
	 * @param g2d       The Graphics2D object we want to draw with.
	 * @param nodePair  The pair of nodes we are drawing edges between.
	 * @param edges     The list of edges we need to draw between c1 and c2.
	 * @param noPreview True if there is no preview object.
	 */
	private static void drawEdgesBetweenNodePair(Graphics2D g2d, UOPair<GBNode> nodePair,
												 List<GBEdge> edges, boolean noPreview) {
		// Draw edges between nodes c1 and c2; if edges.size() > 1, draw them as quadratic bezier curves
		double lowerAngle = (1 - edges.size()) * Preferences.EDGE_SPREAD_ANGLE / 2.0;
		int count = 0; // Keep count of how many edges have been drawn so far

		GBNode n1 = nodePair.getFirst();

		// Draw the edges
		for (int i = 0 ; i < edges.size() ; i++) {
			GBEdge e = edges.get(i);

			// Get the visual properties of this edge
			int weight = e.getWeight();
			g2d.setStroke(new BasicStroke(weight));
			g2d.setColor(trueEdgeColor(e));

			// Split cases by the type of edge
			if (e.isSelfEdge()) {
				// Draw this self edge (looks like a loop)
				double offsetAngle = e.getAngle();

				Point nodeCenter = n1.getNodePanel().getCenter();

				// The radius of n1's circle
				int n1r = n1.getNodePanel().getRadius();

				double centralAngle = Preferences.SELF_EDGE_SUBTENDED_ANGLE;
				double edgeAngle = Preferences.SELF_EDGE_ARC_ANGLE;

				double edgeRadius = Math.sin(centralAngle / 2) * n1r / Math.sin(edgeAngle / 2);
				double unitX = Math.cos(offsetAngle);
				double unitY = Math.sin(offsetAngle);
				double centralDist = edgeRadius * Math.cos(edgeAngle / 2) + n1r * Math.cos(centralAngle / 2);
				double edgeCenterX = centralDist * unitX + nodeCenter.x;
				double edgeCenterY = centralDist * unitY + nodeCenter.y;
				g2d.drawOval((int) (edgeCenterX - edgeRadius), (int) (edgeCenterY - edgeRadius),
							 (int) (2 * edgeRadius), (int) (2 * edgeRadius));

				// If the edge is directed, draw the arrow tip
				if (e.isDirected()) {
					double toEndX = n1r * unitX * Math.cos(centralAngle / 2) -
						n1r * unitY * Math.sin(centralAngle / 2);
					double toEndY = n1r * unitX * Math.sin(centralAngle / 2) +
						n1r * unitY * Math.cos(centralAngle / 2);

					drawArrowTip(g2d, -toEndX, -toEndY,
								 new Point((int) toEndX + nodeCenter.x, (int) toEndY + nodeCenter.y), weight);
				}

				e.setArcCenter(new Point2D.Double(edgeCenterX, edgeCenterY));
				e.setRadius(edgeRadius);
				count++;
			} else {
				// The edge is either a line or bezier curve; either way, they get drawn the same way
				double initAngle = lowerAngle + count * Preferences.EDGE_SPREAD_ANGLE;
				if (e.getFirstEnd() != n1) {
					initAngle *= -1;
				}
				Point p1 = e.getFirstEnd().getNodePanel().getCenter();
				Point p2 = e.getSecondEnd().getNodePanel().getCenter();

				// Reciprocal of distance between the centers of the nodes
				double dist = 1.0 / Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));

				// Get the radii of both ends
				int p1r = e.getFirstEnd().getNodePanel().getRadius();
				int p2r = e.getSecondEnd().getNodePanel().getRadius();

				// Compute components of vectors pointing from one node to the other
				// The length of the vectors is the radius of the node they point from
				double radiusVectorX1 = p1r * dist * (p2.x - p1.x);
				double radiusVectorY1 = p1r * dist * (p2.y - p1.y);
				double radiusVectorX2 = p2r * dist * (p1.x - p2.x);
				double radiusVectorY2 = p2r * dist * (p1.y - p2.y);

				if (edges.size() % 2 == 1 && i == edges.size() / 2) {
					// If the index corresponds to the "center" edge, it is drawn as a straight line.

					// Draw the line
					double linep1X = radiusVectorX1 + p1.x;
					double linep1Y = radiusVectorY1 + p1.y;
					double linep2X = radiusVectorX2 + p2.x;
					double linep2Y = radiusVectorY2 + p2.y;
					g2d.drawLine((int) linep2X, (int) linep2Y, (int) linep1X, (int) linep1Y);

					// Set the control point to a negative value to indicate it does not exist
					if (noPreview) {
						e.setBezierPoints(linep1X, linep1Y, -1.0, -1.0, linep2X, linep2Y);
					}

					// If this edge is directed, draw the arrow
					if (e.isDirected()) {
						Point tip = new Point((int) radiusVectorX2 + p2.x, (int) radiusVectorY2 + p2.y);
						drawArrowTip(g2d, -radiusVectorX2, -radiusVectorY2, tip, weight);
					}
				} else {
					// Otherwise, the edges are drawn as quadratic bezier curves

					// Rotate the radius vectors by an angle; this is how the curved edges are separated
					double circ1X = radiusVectorX1 * Math.cos(initAngle) + radiusVectorY1 * Math.sin(initAngle);
					double circ1Y = radiusVectorY1 * Math.cos(initAngle) - radiusVectorX1 * Math.sin(initAngle);
					double circ2X = radiusVectorX2 * Math.cos(initAngle) - radiusVectorY2 * Math.sin(initAngle);
					double circ2Y = radiusVectorX2 * Math.sin(initAngle) + radiusVectorY2 * Math.cos(initAngle);

					if (e.isDirected()) {
						// Draw the triangular tip of the arrow if the edge is directed
						Point tip = new Point((int) circ2X + p2.x, (int) circ2Y + p2.y);
						drawArrowTip(g2d, -circ2X, -circ2Y, tip, weight);
					}

					// Compute slopes of the rotated "radius vectors"
					double slope1 = circ1Y / circ1X;
					double slope2 = circ2Y / circ2X;

					// Set the "base point" of these vectors
					circ1X += p1.x;
					circ1Y += p1.y;
					circ2X += p2.x;
					circ2Y += p2.y;

					// Compute the intersection of the two radius vectors (the control point for quadratic beziers)
					double controlX = (slope2 * circ2X - slope1 * circ1X + circ1Y - circ2Y) / (slope2 - slope1);
					double controlY = slope1 * (controlX - circ1X) + circ1Y;

					// Draw the curve
					QuadCurve2D curve = new QuadCurve2D.Double(circ1X, circ1Y, controlX, controlY, circ2X, circ2Y);
					g2d.draw(curve);

					// Only set the points if we are not also drawing a preview edge
					if (noPreview) {
						e.setBezierPoints(circ1X, circ1Y, controlX, controlY, circ2X, circ2Y);
					}
				}
				count++;
			}
		}
	}

	/**
	 * Finds the edge closest to the cursor position, and sets the closestEdge
	 * and closestEdgeSelectPoint fields.
	 */
	private void findClosestEdge() {
		// Compute the line and distance to the closest edge
		Point2D.Double clickd = new Point2D.Double(lastMousePoint.x, lastMousePoint.y); // Move point

		// Iteratively updated variables for determining the closest edge
		closestEdgeSelectPoint = null;
		closestEdge = null;
		double minDistance = Double.MAX_VALUE;

		// Get the edges in the graph
		Map<UOPair<GBNode>, List<GBEdge>> em = gui.getContext().getGbEdges();

		// Iterate through the edges
		for (UOPair<GBNode> endpoints : em.keySet()) {
			// Get set of edges between first and second
			List<GBEdge> betweenTwo = em.get(endpoints);

			// Iterate through edges between the nodes "first" and "second"
			// For each edge, find the closest distance from the current mouse position to the edge
			// The Edge Select tool requires this to find the edge closest to the mouse
			for (GBEdge edge : betweenTwo) {
				double closestDist;
				Point closestPoint;
				if (!edge.isSelfEdge()) {
					Point2D.Double[] bcp = edge.getBezierPoints();
					if (bcp[1].x < 0 && bcp[1].y < 0) {
						// Only the endpoints of the line are stored...
						Point2D.Double c1 = bcp[0];
						Point2D.Double c2 = bcp[2];

						// Only 3 candidates: both endpoints and the intersection of the edge and the
						// line perpendicular to the edge which passes through the cursor
						List<Point2D> candidatePoints = new ArrayList<>();
						candidatePoints.add(c1);
						candidatePoints.add(c2);

						// Find the intersection point
						Point2D intersection = null;
						if (c2.x != c1.x && c2.y != c1.y) {
							// Find closest point to line using a parametric line
							Point2D.Double c1click = new Point2D.Double(clickd.x - c1.x, clickd.y - c1.y);
							Point2D.Double c1c2 = new Point2D.Double(c2.x - c1.x, c2.y - c1.y);
							double dot = c1click.x * c1c2.x + c1click.y * c1c2.y;
							double t = dot / (c1c2.x * c1c2.x + c1c2.y * c1c2.y);

							// Only a candidate if the intersection lies on the line segment
							if (t > 0 && t < 1) {
								intersection = new Point2D.Double(c1.x + c1c2.x * t, c1.y + c1c2.y * t);
							}
						} else if (c2.x == c1.x) {
							// If the line happens to be vertical
							intersection = new Point((int) c1.x, lastMousePoint.y);

						} else {
							// If the line happens to be horizontal
							intersection = new Point(lastMousePoint.x, (int) c1.y);
						}

						if (intersection != null) {
							candidatePoints.add(intersection);
						}

						// Determine the closest point among the candidates
						Point2D closestCandidate = Collections.min(candidatePoints,
																   Comparator.comparingDouble(p -> p.distance(clickd)));
						closestDist = closestCandidate.distance(clickd);
						closestPoint = new Point((int) closestCandidate.getX(), (int) closestCandidate.getY());
					} else {
						// To get the closest point on a bezier curve... solve a cubic
						double a = bcp[0].x;
						double b = bcp[0].y;
						double c = bcp[1].x;
						double d = bcp[1].y;
						double e = bcp[2].x;
						double f = bcp[2].y;
						double x = clickd.x;
						double y = clickd.y;

						// The coefficients of the cubic
						double n1 = (a - 2 * c + e) * (a - 2 * c + e) + (b - 2 * d + f) * (b - 2 * d + f);
						double n2 = -3 * ((a - c) * (a - 2 * c + e) + (b - d) * (b - 2 * d + f));
						double n3 = a * (3 * a - x + e) - 2 * c * (3 * a - c - x) - e * x +
							b * (3 * b - y + f) - 2 * d * (3 * b - d - y) - f * y;
						double n4 = (c - a) * (a - x) + (d - b) * (b - y);

						// Compute the roots of the equation n1x^3 + n2x^2 + n3x + n4 = 0
						Complex[] roots = CubicFormula.getRoots(n1, n2, n3, n4);

						// Temporary holder of the points corresponding to the REAL roots of the cubic
						List<Point2D> candidatePoints = new ArrayList<>();

						// Determine which roots are real; use these values of t to determine
						// the candidate "closest" points
						for (Complex rt : roots) {
							if (rt.isReal() && rt.getReal() >= 0 && rt.getReal() <= 1) {
								candidatePoints.add(getBezierPoint(bcp, rt.getReal()));
							}
						}

						// Make sure to include the endpoints of the bezier curve
						candidatePoints.add(bcp[0]);
						candidatePoints.add(bcp[2]);

						// Determine, out of the candidate points, which is the closest
						Point2D closestCandidate = Collections.min(candidatePoints,
																   Comparator.comparingDouble(p -> p.distance(clickd)));
						closestDist = closestCandidate.distance(clickd);
						closestPoint = new Point((int) closestCandidate.getX(), (int) closestCandidate.getY());
					}
				} else {
					// Case where edge is a self edge
					Point2D.Double cen = edge.getArcCenter();

					// Determine closest point on the loop to the cursor
					double distcen = Point.distance(cen.x, cen.y, clickd.x, clickd.y);
					double closex = (clickd.x - cen.x) * edge.getRadius() / distcen + cen.x;
					double closey = (clickd.y - cen.y) * edge.getRadius() / distcen + cen.y;
					closestPoint = new Point((int) closex, (int) closey);
					closestDist = Math.abs(distcen - edge.getRadius());
				}

				// Update the closest edge; once the distance to each edge is computed,
				// closestEdge will contain the closest edge
				if (closestDist < minDistance) {
					minDistance = closestDist;
					closestEdge = edge;
					closestEdgeSelectPoint = closestPoint;
				}
			}
		}
	}

	/**
	 * Check if the given component is currently in the editor's metadata.
	 *
	 * @param gc    The component to check for inclusion.
	 * @param nodes Node metadata.
	 * @param edges Edge metadata.
	 * @return true iff the given component is included in the given metadata.
	 */
	private static boolean componentBelongsTo(GBComponent gc, Set<GBNode> nodes,
											  Map<UOPair<GBNode>, List<GBEdge>> edges) {
		if (gc instanceof GBNode) {
			return nodes.contains(gc);
		} else {
			GBEdge edge = (GBEdge) gc;
			List<GBEdge> selectedList = edges.get(edge.getUoEndpoints());
			return selectedList != null && selectedList.contains(edge);
		}
	}

	/**
	 * Add the given graph component to the editor metadata.
	 *
	 * @param gc The component to add.
	 * @param nodes Node metadata.
	 * @param edges Edge metadata.
	 */
	private static void addComponentTo(GBComponent gc, Set<GBNode> nodes, Map<UOPair<GBNode>, List<GBEdge>> edges,
									   GBContext context) {
		if (gc instanceof GBNode) {
			// Node case
			nodes.add((GBNode) gc);
		} else {
			// Edge case
			GBEdge selectedEdge = (GBEdge) gc;
			UOPair<GBNode> key = selectedEdge.getUoEndpoints();
			if (edges.containsKey(key)) {
				// Insert this edge into the right position
				List<GBEdge> totalList = context.getGbEdges().get(key);
				List<GBEdge> selectedList = edges.get(key);
				int selectionIndex = 0;
				for (GBEdge existingEdge : totalList) {
					if (selectedEdge == existingEdge) {
						selectedList.add(selectionIndex, selectedEdge);
						break;
					} else if (selectedList.contains(existingEdge)) {
						selectionIndex++;
					}
				}
			} else {
				// Create new entry
				List<GBEdge> newList = new ArrayList<>();
				newList.add(selectedEdge);
				edges.put(key, newList);
			}
		}
	}

	/**
	 * Remove the given graph component from the editor metadata.
	 *
	 * @param gc The component to remove.
	 * @param nodes Node metadata.
	 * @param edges Edge metadata.
	 */
	private static void removeComponentFrom(GBComponent gc, Set<GBNode> nodes,
											Map<UOPair<GBNode>, List<GBEdge>> edges) {
		if (gc instanceof GBNode) {
			// Deselect node
			nodes.remove(gc);
		} else {
			// Deselect edge
			GBEdge selectedEdge = (GBEdge) gc;
			UOPair<GBNode> key = selectedEdge.getUoEndpoints();
			List<GBEdge> selectedList = edges.get(key);
			if (selectedList != null) {
				selectedList.remove(selectedEdge);
				if (selectedList.isEmpty()) {
					edges.remove(key);
				}
			}
		}
	}

	/**
	 * Get the color that will be used to draw the edge.
	 *
	 * @param edge The edge to get the color of.
	 * @return the color of this edge.
	 */
	private static Color trueEdgeColor(GBEdge edge) {
		if (edge.isSelected()) {
			return Preferences.SELECTION_COLOR;
		} else if (edge.isHighlighted()) {
			return Preferences.HIGHLIGHT_COLOR;
		} else {
			return edge.getColor();
		}
	}

	/**
	 * A helper method for drawing the tip of a directed edge (a triangle) given the
	 * direction in which the edge is pointing (unit vector), the location of the arrow's tip,
	 * and the weight (thickness) of the edge.
	 *
	 * @param g2d     The Graphics2D object which will draw the tip.
	 * @param vectorX The x component of a vector in the direction the edge is pointing.
	 * @param vectorY The y component of a vector in the direction the edge is pointing.
	 * @param tip     The Point object representing the coordinates of the edge's tip.
	 * @param weight  The weight of the edge.
	 */
	private static void drawArrowTip(Graphics2D g2d, double vectorX, double vectorY, Point tip, int weight) {
		double scale = Preferences.ARROW_TIP_SCALE_FACTOR;
		double halfScale = scale / 2;

		double mag = Math.sqrt(vectorX * vectorX + vectorY * vectorY);
		double unitVectorX = vectorX / mag;
		double unitVectorY = vectorY / mag;
		double leftCornerX = tip.x - scale * weight * unitVectorX + halfScale * weight * unitVectorY;
		double leftCornerY = tip.y - scale * weight * unitVectorY - halfScale * weight * unitVectorX;
		double rightCornerX = leftCornerX - scale * weight * unitVectorY;
		double rightCornerY = leftCornerY + scale * weight * unitVectorX;
		g2d.fillPolygon(new int[] {tip.x, (int) leftCornerX, (int) rightCornerX},
						new int[] {tip.y, (int) leftCornerY, (int) rightCornerY}, 3);
	}

	/**
	 * A helper method for obtaining the point on the given quadratic bezier at the parameter 0 <= t <= 1.
	 *
	 * @param coords The array of Points which define the quadratic bezier.
	 * @param t      The parameter t where 0 <= t <= 1.
	 * @return The Point on the bezier corresponding to the given value of t.
	 */
	private static Point2D.Double getBezierPoint(Point2D.Double[] coords, double t) {
		return new Point2D.Double(
			(1 - t) * (1 - t) * coords[0].x + 2 * (1 - t) * t * coords[1].x + t * t * coords[2].x,
			(1 - t) * (1 - t) * coords[0].y + 2 * (1 - t) * t * coords[1].y + t * t * coords[2].y
		);
	}

}
