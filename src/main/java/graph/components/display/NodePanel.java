package graph.components.display;

import actions.MoveNodesAction;
import actions.PlaceEdgeAction;
import graph.Graph;
import graph.GraphConstraint;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import lombok.Getter;
import lombok.Setter;
import preferences.Preferences;
import structures.OrderedPair;
import structures.UOPair;
import tool.Tool;
import ui.Editor;
import ui.menus.NodeRightClickMenu;
import util.CoordinateUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The visual panel which represents a node on the editor panel.
 *
 * @author Brian Yao
 */
public class NodePanel extends JPanel {

	private static final long serialVersionUID = 2475149319858394032L;

	// Constants for maintaining a smooth appearance
	private static final int BORDER_THICKNESS = 2;
	private static final int SELECTED_BORDER_THICKNESS = 1;
	private static final int PADDING = 1;

	// The node this panel visualizes
	@Getter @Setter
	private GBNode gbNode;

	// Location (of the panel's top left corner) on editor panel. The origin
	// is at the top left of the editor panel.
	private int x;
	private int y;

	@Getter
	private int radius; // Radius in pixels
	@Getter @Setter
	private String text;
	@Getter @Setter
	private Color fillColor;
	@Getter @Setter
	private Color borderColor;
	@Getter @Setter
	private Color textColor;

	// Fields for temporarily storing state
	private Point clickPoint; // Coordinate of mouse click relative to the top left corner of its bounding box
	private boolean hovering; // True when the mouse is hovering within the circle

	/**
	 * Copy constructor. By default, the panel is put into the
	 * same context as the original.
	 *
	 * @param np The node panel to copy.
	 */
	public NodePanel(NodePanel np) {
		this(np.x, np.y, np.radius);
		this.text = np.text;
		this.fillColor = new Color(np.fillColor.getRGB());
		this.borderColor = new Color(np.borderColor.getRGB());
		this.textColor = new Color(np.textColor.getRGB());
	}

	/**
	 * Creates a node with the specified location, radius, text,
	 * color, border color, text color, select color, and id.
	 *
	 * @param x The x-coordinate of the circle's top left corner.
	 * @param y The y-coordinate of the circle's top left corner.
	 * @param r The radius of the circle in pixels.
	 */
	public NodePanel(int x, int y, int r) {
		this.x = x;
		this.y = y;
		this.radius = r;

		this.text = GBNode.DEFAULT_TEXT;
		this.fillColor = GBNode.DEFAULT_FILL_COLOR;
		this.borderColor = GBNode.DEFAULT_BORDER_COLOR;
		this.textColor = GBNode.DEFAULT_TEXT_COLOR;

		this.hovering = false;
		this.clickPoint = new Point();

		this.setOpaque(false);
		this.setVisible(true);

		// Listen for mouse events
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					Editor editor = gbNode.getContext().getGUI().getEditor();
					if (NodePanel.this.containsPoint(clickPoint) && editor.getEdgeBasePoint() == null) {
						NodeRightClickMenu.show(NodePanel.this, e.getX(), e.getY());
					}
					editor.clearEdgeBasePoint();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// Store the point we clicked on relative to the upper left corner of the panel
				clickPoint = e.getPoint();
				NodePanel sinkPanel = NodePanel.this;
				GBNode sink = sinkPanel.gbNode;
				boolean contains = sinkPanel.containsPoint(clickPoint);

				// Display the right click menu if the node is right clicked
				Editor editor = gbNode.getContext().getGUI().getEditor();
				if (SwingUtilities.isLeftMouseButton(e)) {
					Tool tool = editor.getGUI().getCurrentTool();
					if (!editor.highlightsEmpty()) {
						// Remove all highlights if clicked anywhere on this panel
						editor.removeAllHighlights();
					} else if (contains && tool == Tool.SELECT) {
						// If this node was clicked while the select tool is held
						if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK ||
							(e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
							// If the user was holding down the control or shift keys, add or remove
							// this node from the set of selections
							if (editor.isSelected(sink)) {
								editor.removeSelection(sink);
								editor.removeNodePanelEntry(sinkPanel);
							} else {
								editor.addSelection(sink);
								editor.addNodePanelEntry(sinkPanel);
							}
						} else if (!sink.isSelected()) {
							// Otherwise, if this node is not already selected, remove all existing
							// selections and select this node
							editor.removeAllSelections();
							for (GBNode wasSelected : editor.getSelections().getValue0()) {
								editor.removeNodePanelEntry(wasSelected.getNodePanel());
							}
							editor.repaint();

							// Add this node as a selection
							editor.addSelection(sink);
							editor.addNodePanelEntry(sinkPanel);
						} else {
							// If this node is already selected, we don't change the selections
							for (GBNode selectedNode : editor.getSelections().getValue0()) {
								editor.addNodePanelEntry(selectedNode.getNodePanel());
							}
						}
						editor.getGUI().getMainMenuBar().updateWithSelection();
						repaint(); // Redraw this node panel
					} else if (contains && tool == Tool.EDGE || tool == Tool.DIRECTED_EDGE) {
						// If we left click on the node with an edge tool
						if (editor.getEdgeBasePoint() == null) {
							// If the base point is not set, set this node as the base point
							editor.setEdgeBasePoint(gbNode);
						} else {
							// If the base point is set, draw a new edge
							GBNode source = editor.getEdgeBasePoint();
							NodePanel sourcePanel = source.getNodePanel();
							Color currentLineColor = editor.getGUI().getEdgeOptionsBar().getLineColor();
							int currentLineWeight = editor.getGUI().getEdgeOptionsBar().getCurrentLineWeight();
							boolean directed = tool == Tool.DIRECTED_EDGE;

							// Check if the edge should be a self-edge, and initialize accordingly
							GBEdge newEdge;
							if (sink.getNode() == source.getNode()) {
								newEdge = new GBEdge(sink, sink, directed);
								double angle = getSelfEdgeOffsetAngle(NodePanel.this, e.getPoint());
								newEdge.setAngle(angle);
							} else {
								newEdge = new GBEdge(source, sink, directed);
							}

							// Set the new edge's appearance
							newEdge.setColor(currentLineColor);
							newEdge.setWeight(currentLineWeight);

							// Compute the position of this edge (only matters if it is a simple edge)
							List<GBEdge> pairEdges = editor.getContext().getGbEdges().get(new UOPair<>(sink, source));
							int edgePosition = getEdgePosition(sourcePanel, sinkPanel, e.getPoint(), pairEdges);

							// Add the new edge between the chosen nodes if no constraints are violated
							Graph currentGraph = editor.getContext().getGraph();
							boolean violatesLoops = !currentGraph.hasConstraint(GraphConstraint.MULTIGRAPH) &&
								newEdge.isSelfEdge();
							boolean violatesSimple = currentGraph.hasConstraint(GraphConstraint.SIMPLE) &&
								pairEdges != null;
							if (!violatesLoops && !violatesSimple) {
								PlaceEdgeAction placeAction = new PlaceEdgeAction(editor.getContext(),
																				  newEdge, edgePosition);
								placeAction.perform();
								editor.getContext().pushReversibleAction(placeAction, true, false);
							}

							// Reset base point, now that the edge has been placed
							editor.clearEdgeBasePoint();

							// Remove any existing preview edge object
							editor.clearPreviewEdge();

							// Repaint to immediately draw the new edge
							editor.repaint();
							editor.revalidate();
						}
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// A mapping from the moved panels to their original position (before the move)
				Editor editor = gbNode.getContext().getGUI().getEditor();
				HashMap<NodePanel, OrderedPair<Point>> movementMap = new HashMap<>();
				Point originalPoint;
				Point currentPoint;

				for (Map.Entry<NodePanel, Point> npEntry : editor.getNodePanelPositionMap().entrySet()) {
					originalPoint = npEntry.getValue();
					currentPoint = npEntry.getKey().getCoords();

					// Only add this node panel to movement map if it moved (start and end points are different)
					if (!originalPoint.equals(currentPoint)) {
						OrderedPair<Point> movement = new OrderedPair<>(originalPoint, currentPoint);
						movementMap.put(npEntry.getKey(), movement);
					}
				}

				// If the map is not empty, push a move node action
				if (!movementMap.isEmpty()) {
					MoveNodesAction moveAction = new MoveNodesAction(editor.getContext(), movementMap);
					editor.getContext().pushReversibleAction(moveAction, true, false);

					// The movement is complete, so we clear the node panel position map
					editor.getNodePanelPositionMap().clear();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (containsPoint(e.getPoint())) {
					hovering = true;

					// Repaint to clear preview edge artifacts
					Editor editor = gbNode.getContext().getGUI().getEditor();
					editor.setDrawPreviewUsingObject(true);
					editor.repaint();
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				hovering = false;

				// Remove any existing preview edge object
				Editor editor = gbNode.getContext().getGUI().getEditor();
				editor.clearPreviewEdge();
				editor.setDrawPreviewUsingObject(false);
				editor.repaint();
			}

		});

		// Listen for dragging and hovering mouse events
		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				Editor editor = gbNode.getContext().getGUI().getEditor();
				if (editor.getGUI().getCurrentTool() == Tool.SELECT &&
					containsPoint(clickPoint) && SwingUtilities.isLeftMouseButton(e)) {
					// If the panel is dragged using left click and the select tool
					NodePanel thisPanel = NodePanel.this;
					Point dragPoint = e.getPoint();

					// The new point of this node on the editor
					Point newPoint;
					if (editor.getGUI().getGridSettingsDialog().getSnapToGrid()) {
						// Enforce grid snap
						Point mouseCenter = new Point(thisPanel.x + dragPoint.x, thisPanel.y + dragPoint.y);

						newPoint = CoordinateUtils.closestGridPoint(editor.getGUI(), mouseCenter);
						newPoint.x -= radius;
						newPoint.y -= radius;
					} else {
						// Drag the node as normal
						newPoint = new Point(thisPanel.x + dragPoint.x - clickPoint.x,
											 thisPanel.y + dragPoint.y - clickPoint.y);
					}

					// Keep newPoint in the bounds of the editor
					CoordinateUtils.enforceBoundaries(newPoint, 0, editor.getWidth() - 2 * thisPanel.radius, 0,
													  editor.getHeight() - 2 * thisPanel.radius);

					// Compute the net change in position for this node
					int changeX = newPoint.x - thisPanel.x;
					int changeY = newPoint.y - thisPanel.y;

					// Set the new coordinates of the current node panel
					thisPanel.setCoords(newPoint);

					// Iterate through all selected nodes, and move them the same amount, bypassing
					// grid snap to maintain the structure
					GBNode thisPanelNode = thisPanel.gbNode;
					for (GBNode selectedNode : editor.getSelections().getValue0()) {
						if (selectedNode != thisPanelNode) {
							// Compute the new coordinates of the selected nodes, and update them
							NodePanel selectionNodePanel = selectedNode.getNodePanel();
							int selectionRadius = selectionNodePanel.getRadius();
							Point newSelectionPoint = new Point(selectionNodePanel.getXCoord() + changeX,
																selectionNodePanel.getYCoord() + changeY);
							CoordinateUtils.enforceBoundaries(newSelectionPoint, 0,
															  editor.getWidth() - 2 * selectionRadius, 0,
															  editor.getHeight() - 2 * selectionRadius);
							selectionNodePanel.setCoords(newSelectionPoint);
						}
					}

					// Redraw the editor to update the position of the panel(s)
					editor.repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				Editor editor = gbNode.getContext().getGUI().getEditor();
				if (containsPoint(e.getPoint())) {
					hovering = true;

					// Preview edges (if applicable) should be drawn using a preview edge object
					editor.setDrawPreviewUsingObject(true);

					// Draw preview edges
					if (editor.getEdgeBasePoint() != null) {
						NodePanel ebpPanel = editor.getEdgeBasePoint().getNodePanel();
						Tool ctool = editor.getGUI().getCurrentTool();

						if ((ctool == Tool.EDGE || ctool == Tool.DIRECTED_EDGE) && ebpPanel != null) {
							Color previewColor = Preferences.PREVIEW_COLOR;
							int weight = editor.getGUI().getEdgeOptionsBar().getCurrentLineWeight();
							boolean directed = ctool == Tool.DIRECTED_EDGE;

							// Create edge and set it as the preview
							GBEdge preview = new GBEdge(ebpPanel.gbNode, gbNode, directed);
							preview.setColor(previewColor);
							preview.setWeight(weight);

							Graph currentGraph = gbNode.getContext().getGraph();
							boolean violatesSimple = currentGraph.hasConstraint(GraphConstraint.SIMPLE) &&
								gbNode.getContext().getEdgesBetweenNodes(preview.getUoEndpoints()) != null;
							boolean violatesLoops = !currentGraph.hasConstraint(GraphConstraint.MULTIGRAPH) &&
								preview.isSelfEdge();
							if (!preview.isSelfEdge() && !violatesSimple) {
								// Compute preview if edge is not a self edge
								List<GBEdge> existingEdges = editor.getContext().getGbEdges()
									.get(new UOPair<>(ebpPanel.gbNode, gbNode));

								// Get the position of this edge within the existing edges
								int edgePosition = getEdgePosition(ebpPanel, NodePanel.this,
																   e.getPoint(), existingEdges);

								editor.setPreviewEdge(preview, edgePosition);
							} else if (preview.isSelfEdge() && !violatesLoops) {
								// Draw preview self edge
								double angle = getSelfEdgeOffsetAngle(NodePanel.this, e.getPoint());
								preview.setAngle(angle);
								editor.setPreviewEdge(preview, -1);
							} else {
								// No preview edge to be drawn
								editor.clearPreviewEdge();
							}

							editor.repaint(); // Repaint editor to update preview edge
						}
					}
				} else {
					hovering = false;

					// Check for movement within this Node's panel but not within the node itself
					editor.setLastMousePoint(NodePanel.this.x + e.getPoint().x, NodePanel.this.y + e.getPoint().y);

					// Remove any existing preview edge object
					editor.clearPreviewEdge();

					// Preview edges should not be drawn using a preview edge object since the mouse is not hovering
					// over a second endpoint
					editor.setDrawPreviewUsingObject(false);

					editor.repaint();
				}

				repaint(); // Redraw things like bounding boxes
			}

		});
	}

	/**
	 * Helper method for obtaining the "position" of an edge given the pair of node endpoints
	 * and the position of the mouse on this node's panel.
	 *
	 * @param to            The node the edge is flowing to.
	 * @param from          The node the edge is flowing from.
	 * @param mouseEvent    The point corresponding to the cursor's position on the node.
	 * @param existingEdges List of edges which already exist between to and from.
	 * @return The integer index of the edge's newly determined position.
	 */
	private static int getEdgePosition(NodePanel from, NodePanel to, Point mouseEvent, List<GBEdge> existingEdges) {
		int edgePosition = 0;
		int numExistingEdges = existingEdges == null ? 0 : existingEdges.size();

		if (numExistingEdges > 0) {
			// If there are already edges between the two nodes
			Point fromCenter = from.getCenter();
			Point toCenter = to.getCenter();
			Point mouseEditor = new Point(mouseEvent.x + to.x, mouseEvent.y + to.y);
			double distCenters = Point.distance(fromCenter.x, fromCenter.y, toCenter.x, toCenter.y);
			double unitAwayX = (fromCenter.x - toCenter.x) / distCenters;
			double unitAwayY = (fromCenter.y - toCenter.y) / distCenters;
			double distCenterMouse = Point.distance(toCenter.x, toCenter.y, mouseEditor.x, mouseEditor.y);
			double unitToMouseX = (mouseEditor.x - toCenter.x) / distCenterMouse;
			double unitToMouseY = (mouseEditor.y - toCenter.y) / distCenterMouse;
			double angleFromCenters = Math.acos(unitAwayX * unitToMouseX + unitAwayY * unitToMouseY);

			// Check if the mouse is to the "right" of the vector pointing to the base point
			// Compute the cross product's magnitude; if it is negative, it is to the "right"
			if (unitAwayX * unitToMouseY - unitAwayY * unitToMouseX < 0) {
				angleFromCenters *= -1;
			}

			// Compute the angle over which the edges are spread
			double spreadAngle = Preferences.EDGE_SPREAD_ANGLE;
			double boundAngle = (spreadAngle * (numExistingEdges - 1)) / 2.0;
			if (angleFromCenters > boundAngle) {
				edgePosition = numExistingEdges;
			} else if (angleFromCenters < - boundAngle) {
				edgePosition = 0;
			} else {
				edgePosition = (int) ((angleFromCenters + boundAngle) / spreadAngle) + 1;
			}

			// Indices are one-way; we might have to use the "opposite index" depending
			// on the direction the edge is being drawn from
			GBEdge firstEdge = existingEdges.get(0);
			if (numExistingEdges == 1 && to != from && !firstEdge.isSelfEdge()) {
				if (to == firstEdge.getSecondEnd().getNodePanel()) {
					edgePosition = numExistingEdges - edgePosition;
				}
			} else if (to != from && !firstEdge.isSelfEdge()) {
				Point2D.Double control = firstEdge.getBezierPoints()[1];
				double centerToControlDist = Point.distance(control.x, control.y, toCenter.x, toCenter.y);
				double centerToControlX = (control.x - toCenter.x) / centerToControlDist;
				double centerToControlY = (control.y - toCenter.y) / centerToControlDist;
				if (unitAwayX * centerToControlY - unitAwayY * centerToControlX > 0) {
					edgePosition = numExistingEdges - edgePosition;
				}
			}
		}

		// Special case: the math above flips the correct position if there is
		// exactly one existing edge, so flip it back here
		if (numExistingEdges == 1) {
			edgePosition = 1 - edgePosition;
		}

		return edgePosition;
	}

	/**
	 * Helper method which finds the angle of the cursor relative to some default vector.
	 *
	 * @param n          The node in which the mouse is moving.
	 * @param mouseEvent The point at which the cursor is located.
	 * @return The angle (in radians) of the cursor's position relative to the default vector.
	 */
	private static double getSelfEdgeOffsetAngle(NodePanel n, Point mouseEvent) {
		final double defaultX = 1; // Default unit vector for an angle of 0
		final double defaultY = 0;
		double distCenter = Point.distance(mouseEvent.x, mouseEvent.y, n.radius, n.radius);
		double diffCenterX = (mouseEvent.x - n.radius) / distCenter;
		double diffCenterY = (mouseEvent.y - n.radius) / distCenter;
		double angle = Math.acos(diffCenterX * defaultX + diffCenterY * defaultY);
		if (mouseEvent.y < n.radius) {
			angle *= -1;
		}
		return angle;
	}

	/**
	 * Checks whether the Point object lies within the node's visual boundaries.
	 *
	 * @param p The point (on this panel) we want to check.
	 * @return true if p is within this node's circle.
	 */
	public boolean containsPoint(Point p) {
		return radius >= Math.sqrt((p.x - radius) * (p.x - radius) + (p.y - radius) * (p.y - radius));
	}

	/**
	 * Get the x coordinate of this panel's upper left corner (relative to the editor).
	 *
	 * @return The integer x-coordinate.
	 */
	public int getXCoord() {
		return x;
	}

	/**
	 * Get the y coordinate of this panel's upper left corner (relative to the editor).
	 *
	 * @return The integer y-coordinate.
	 */
	public int getYCoord() {
		return y;
	}

	/**
	 * Get the coordinates of the node's upper left corner.
	 *
	 * @return The point correspoinding to the upper left corner of this panel's bounding box.
	 */
	public Point getCoords() {
		return new Point(x, y);
	}

	/**
	 * Set the coordinates of the panel's upper left corner.
	 *
	 * @param p The point which will become the panel's upper left corner.
	 */
	public void setCoords(Point p) {
		x = p.x;
		y = p.y;
	}

	/**
	 * Set the coordinates of the panel's upper left corner.
	 *
	 * @param x The new x-coordinate of the panels' upper left corner.
	 * @param y The new y-coordinate of the panels' upper left corner.
	 */
	public void setCoords(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets this panel's absolute position on the parent container (in this
	 * case, the parent container is the Editor panel).
	 */
	public void enforceLocation() {
		this.setLocation(x, y);
	}

	/**
	 * Get the center of the panel's circle.
	 *
	 * @return The point for the center of the panel's circle.
	 */
	public Point getCenter() {
		return new Point(x + radius, y + radius);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(2 * (radius + PADDING + SELECTED_BORDER_THICKNESS),
							 2 * (radius + PADDING + SELECTED_BORDER_THICKNESS));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setStroke(new BasicStroke(BORDER_THICKNESS));
		g2d.setColor(fillColor);

		// The circle which depicts this node
		Ellipse2D.Double circle = new Ellipse2D.Double(PADDING, PADDING, 2 * radius, 2 * radius);

		Color trueFillColor = fillColor;
		Color trueBorderColor = borderColor;

		if (gbNode.isHighlighted()) {
			trueFillColor = Preferences.HIGHLIGHT_COLOR;
			trueBorderColor = Preferences.HIGHLIGHT_COLOR;
		} else if (gbNode.isSelected()) {
			trueFillColor = Preferences.SELECTION_COLOR;
			trueBorderColor = Preferences.SELECTION_COLOR;
		}

		if (hovering) {
			// Set colors for "hover" visual effects
			Editor editor = gbNode.getContext().getGUI().getEditor();
			Tool current = gbNode.getContext().getGUI().getCurrentTool();
			if (current == Tool.EDGE || current == Tool.DIRECTED_EDGE) {
				trueBorderColor = editor.getEdgeBasePoint() == null ? Preferences.EDGE_BASE_POINT_COLOR :
					Preferences.EDGE_SECOND_POINT_COLOR;
			}
		}

		// Draw the circle, border, and text
		g2d.setColor(trueFillColor);
		g2d.fill(circle);
		g2d.setColor(trueBorderColor);
		g2d.draw(circle);
		if (text != null && !text.isEmpty()) {
			g2d.setColor(textColor);
			Rectangle stringBounds = g2d.getFontMetrics().getStringBounds(text, g2d).getBounds();
			g2d.drawString(text, radius - stringBounds.width / 2, radius + stringBounds.height / 2);
		}
	}

	@Override
	public String toString() {
		return String.format("NP[%d, %d]", x, y);
	}

}
