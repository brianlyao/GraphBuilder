package components.display;

import graph.Graph;
import graph.GraphConstraint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import components.Edge;
import components.Node;
import components.SelfEdge;
import components.SimpleEdge;
import context.GraphBuilderContext;
import preferences.Preferences;
import actions.MoveNodesAction;
import actions.PlaceEdgeAction;
import structures.OrderedPair;
import structures.UnorderedNodePair;
import tool.Tool;
import ui.Editor;
import ui.menus.NodeRightClickMenu;
import util.CoordinateUtils;

/**
 * The visual panel which represents a node on the editor panel.
 * 
 * @author Brian
 */
public class NodePanel extends JPanel {
	
	private static final long serialVersionUID = 2475149319858394032L;
	
	// Constants for maintaining a smooth appearance
	private static final int BORDER_THICKNESS = 2;
	private static final int SELECTED_BORDER_THICKNESS = 1;
	public static final int DEFAULT_RADIUS = 30;
	private static final int PADDING = 1;

	// The node this panel visualizes
	private Node node;
	
	// The editor in which this node is displayed
	private GraphBuilderContext context;
	
	private int x; // Location on editor panel
	private int y;
	private int radius; // Radius in pixels
	private String text; // Text displayed
	private Color fillColor; // Fill color
	private Color borderColor; // Border color
	private Color textColor; // Text color

	private Point clickPoint; // Coordinate of mouse click relative to the top left corner of its bounding box
	
	private boolean hovering; // True when the mouse is hovering over the circle
	
	/**
	 * Copy constructor.
	 * 
	 * @param np The node panel to copy.
	 */
	public NodePanel(NodePanel np, Node newNode) {
		this(np.x, np.y, np.radius, new String(np.text), new Color(np.fillColor.getRGB()), new Color(np.borderColor.getRGB()), new Color(np.textColor.getRGB()), np.context, newNode);
	}
	
	/**
	 * Creates a node with the specified location, radius, text,
	 * color, border color, text color, select color, and id.
	 * 
	 * @param x    The x-coordinate of the circle's top left corner.
	 * @param y    The y-coordinate of the circle's top left corner.
	 * @param r    The radius of the circle in pixels.
	 * @param txt  The text displayed on the circle.
	 * @param c    The fill color of the circle.
	 * @param lc   The border color of the circle.
	 * @param tc   The color of the circle's text.
	 * @param ctxt The context (graph) this node is a part of.
	 * @param n    The node this panel belongs to.
	 */
	public NodePanel(int x, int y, int r, String txt, Color c, Color lc, Color tc, GraphBuilderContext ctxt, Node n) {
		this.x = x;
		this.y = y;
		radius = r;
		text = txt;
		fillColor = c;
		borderColor = lc;
		textColor = tc;
		context = ctxt;
		node = n;
		
		hovering = false;
		
		clickPoint = new Point();
		
		setOpaque(false);
		
		// Listen for mouse events
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean contains = NodePanel.this.containsPoint(clickPoint);
				if (contains && SwingUtilities.isRightMouseButton(e)) {
					NodeRightClickMenu.show(NodePanel.this, e.getX(), e.getY());
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// Store the point we clicked on relative to the upper left corner of the panel
				clickPoint = e.getPoint();
				NodePanel sinkPanel = NodePanel.this;
				Node sink = sinkPanel.node;
				boolean contains = sinkPanel.containsPoint(clickPoint);
				
				// Display the right click menu if the node is right clicked
				Editor editor = context.getGUI().getEditor();
				if (contains && SwingUtilities.isLeftMouseButton(e)) {
					Tool current = editor.getGUI().getCurrentTool();
					if (current == Tool.SELECT) {
						// If this node was clicked while the select tool is held
						if ((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK || (e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK) {
							// If the user was holding down the control or shift keys, add or remove
							// this node from the set of selections
							if (sink.getSelected()) {
								editor.removeSelection(sink);
								editor.removeNodePanelEntry(sinkPanel);
							} else {
								editor.addSelection(sink);
								editor.addNodePanelEntry(sinkPanel);
							}
						} else if (!sink.getSelected()) {
							// Otherwise, if this node is not already selected, remove all existing
							// selections and select this node
							editor.removeAllSelections();
							editor.repaint();
							
							// Add this node as a selection
							editor.addSelection(sink);
							editor.addNodePanelEntry(sinkPanel);
						}
						editor.getGUI().getMainMenuBar().updateWithSelection();
						repaint(); // Redraw this node panel
					} else if (current == Tool.EDGE || current == Tool.DIRECTED_EDGE) {
						// If we left click on the node with an edge tool
						if (editor.getEdgeBasePoint() == null) {
							// If the base point is not set, set this node as the base point
							editor.setEdgeBasePoint(node);
						} else {
							// If the base point is set, draw a new edge
							Node source = editor.getEdgeBasePoint();
							NodePanel sourcePanel = source.getNodePanel();
							Color currentLineColor = editor.getGUI().getEdgeOptionsBar().getCurrentLineColor();
							int currentLineWeight = editor.getGUI().getEdgeOptionsBar().getCurrentLineWeight();
							boolean directed = current == Tool.DIRECTED_EDGE;
							Edge newEdge;
							
							// Check if the edge should be a self-edge
							if (sink == source) {
								double angle = getSelfEdgeOffsetAngle(NodePanel.this, e.getPoint());
								newEdge = new SelfEdge(sink, currentLineColor, currentLineWeight, Edge.DEFAULT_TEXT, angle, directed, editor.getContext(), editor.getContext().getNextIDAndInc());
							} else {
								newEdge = new SimpleEdge(sink, source, currentLineColor, currentLineWeight, Edge.DEFAULT_TEXT, directed, editor.getContext(), editor.getContext().getNextIDAndInc());
							}
							
							// Compute the position of this edge (only matters if it is a simple edge)
							List<Edge> pairEdges = editor.getContext().getEdgeMap().get(new UnorderedNodePair(sink, source)); 
							int edgePosition = getEdgePosition(sinkPanel, sourcePanel, e.getPoint(), pairEdges);
							
							// Add the new edge between the chosen nodes if no constraints are violated
							Graph currentGraph = editor.getContext().getGraph();
							boolean violatesLoops = !currentGraph.hasConstraint(GraphConstraint.LOOPS_ALLOWED) && newEdge instanceof SelfEdge;
							boolean violatesSimple = currentGraph.hasConstraint(GraphConstraint.SIMPLE) && pairEdges != null;
							if (!violatesLoops && !violatesSimple) {
								PlaceEdgeAction placeAction = new PlaceEdgeAction(editor.getContext(), newEdge, edgePosition);
								placeAction.actionPerformed(null);
								editor.getContext().pushReversibleAction(placeAction, true, false);
							}
							
							// Reset base point, now that the edge has been placed
							editor.setEdgeBasePoint(null); 
							
							// Remove any existing preview edge object
							editor.clearPreviewEdge();
							
							// Repaint to immediately draw the new edge
							editor.repaint();
						}
					}
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// A mapping from the moved panels to their original position (before the move)
				Editor editor = context.getGUI().getEditor();
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
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				hovering = false;
				
				// Remove any existing preview edge object
				Editor editor = context.getGUI().getEditor();
				editor.clearPreviewEdge();
				editor.repaint();
			}
			
		});
		
		// Listen for dragging and hovering mouse events
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseDragged(MouseEvent e) {
				Editor editor = context.getGUI().getEditor();
				if (editor.getGUI().getCurrentTool() == Tool.SELECT && containsPoint(clickPoint) && SwingUtilities.isLeftMouseButton(e)) {
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
						newPoint = new Point(thisPanel.x + dragPoint.x - clickPoint.x, thisPanel.y + dragPoint.y - clickPoint.y);
					}
					
					// Keep newPoint in the bounds of the editor
					CoordinateUtils.enforceBoundaries(newPoint, 0, editor.getWidth() - 2 * thisPanel.radius, 0, editor.getHeight() - 2 * thisPanel.radius);
					
					// Compute the net change in position for this node
					int changeX = newPoint.x - thisPanel.x;
					int changeY = newPoint.y - thisPanel.y;
					
					// Set the new coordinates of the current node panel
					thisPanel.setCoords(newPoint);
					
					// Iterate through all selected nodes, and move them the same amount, bypassing
					// grid snap to maintain the structure
					Node thisPanelNode = thisPanel.getNode();
					for (Node selectedNode : editor.getSelections().getKey()) {
						if (selectedNode != thisPanelNode) {
							// Compute the new coordinates of the selected nodes, and update them
							NodePanel selectionNodePanel = selectedNode.getNodePanel();
							int selectionRadius = selectionNodePanel.getRadius();
							Point newSelectionPoint = new Point(selectionNodePanel.getXCoord() + changeX, selectionNodePanel.getYCoord() + changeY);
							CoordinateUtils.enforceBoundaries(newSelectionPoint, 0, editor.getWidth() - 2 * selectionRadius, 0, editor.getHeight() - 2 * selectionRadius);
							selectionNodePanel.setCoords(newSelectionPoint);
						}
					}
					
					// Redraw the editor to update the position of the panel(s)
					editor.repaint();
				}
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				Editor editor = context.getGUI().getEditor();
				if (containsPoint(e.getPoint())) {
					hovering = true;
					
					// Draw preview edges correctly
					if (editor.getEdgeBasePoint() != null) {
						NodePanel ebpPanel = editor.getEdgeBasePoint().getNodePanel();
						Tool ctool = editor.getGUI().getCurrentTool();
						
						if ((ctool == Tool.EDGE || ctool == Tool.DIRECTED_EDGE) && ebpPanel != null) {
							Color previewColor = (Color) Preferences.EDGE_PREVIEW_COLOR.getData();
							int weight = editor.getGUI().getEdgeOptionsBar().getCurrentLineWeight();
							boolean directed = ctool == Tool.DIRECTED_EDGE;
							if (ebpPanel != NodePanel.this) {
								// Compute the preview for a simple edge
								List<Edge> existingEdges = editor.getContext().getEdgeMap().get(new UnorderedNodePair(ebpPanel.node, node));
								
								// Get the position of this edge within the existing edges
								int edgePosition = getEdgePosition(NodePanel.this, ebpPanel, e.getPoint(), existingEdges);
								
								// Create edge and set it as the preview
								SimpleEdge preview = new SimpleEdge(node, ebpPanel.node, previewColor, weight, Edge.DEFAULT_TEXT, directed, editor.getContext(), -1);
								editor.setPreviewEdge(preview, edgePosition, true);
							} else {
								// Compute the preview for a self edge
								double angle = getSelfEdgeOffsetAngle(NodePanel.this, e.getPoint());
								SelfEdge preview = new SelfEdge(node, previewColor, weight, Edge.DEFAULT_TEXT, angle,  directed, editor.getContext(), -1);
								editor.setPreviewEdge(preview, -1, true);
							}
							editor.repaint(); // Repaint editor to update preview edge
						}
					}
				} else {
					hovering = false;
					
					// Check for movement within this Node's panel but not within the node itself
					editor.setLastMousePoint(NodePanel.this.x + e.getPoint().x, NodePanel.this.y + e.getPoint().y);
					
					// Remove any existing preview edge object
					editor.setPreviewEdge(null, -1, false);
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
	 * @param to               The node the edge is flowing to.
	 * @param from             The node the edge is flowing from.
	 * @param mouseEvent       The point corresponding to the cursor's position on the node.
	 * @param numExistingEdges The number of edges which already exist between to and from.
	 * @return The integer index of the edge's newly determined position.
	 */
	private static int getEdgePosition(NodePanel to, NodePanel from, Point mouseEvent, List<Edge> existingEdges) {
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
			double spreadAngle = (double) Preferences.EDGE_SPREAD_ANGLE.getData();
			double boundAngle = (spreadAngle * (numExistingEdges - 1)) / 2.0;
			if (angleFromCenters > boundAngle) {
				edgePosition = numExistingEdges;
			} else if(angleFromCenters < -boundAngle) {
				edgePosition = 0;
			} else {
				edgePosition = (int) ((angleFromCenters + boundAngle) / spreadAngle) + 1;
			}
			
			// Indices are one-way; we might have to use the "opposite index" depending
			// on the direction the edge is being drawn from
			Edge first = existingEdges.get(0);
			if (numExistingEdges == 1 && to != from && first instanceof SimpleEdge) {
				if (to == first.getEndpoints().getSecond().getNodePanel()) {
					edgePosition = numExistingEdges - edgePosition;
				}
			} else if (to != from && first instanceof SimpleEdge) {
				Point2D.Double control = ((SimpleEdge) existingEdges.get(0)).getData().getBezierPoints()[1];
				double centerToControlDist = Point.distance(control.x, control.y, toCenter.x, toCenter.y);
				double centerToControlX = (control.x - toCenter.x) / centerToControlDist;
				double centerToControlY = (control.y - toCenter.y) / centerToControlDist;
				if (unitAwayX * centerToControlY - unitAwayY * centerToControlX > 0) {
					edgePosition = numExistingEdges - edgePosition;
				}
			}
		}
		return edgePosition;
	}
	
	/**
	 * Helper method which finds the angle of the cursor relative to some default vector.
	 * 
	 * @param n          The node in which the mouse is moving. 
	 * @param mouseEvent The point at which the cursor is located.
	 * @return           The angle (in radians) of the cursor's position relative to the default vector.
	 */
	private static double getSelfEdgeOffsetAngle(NodePanel n, Point mouseEvent) {
		final double defaultX = 1; // Default unit vector for an angle of 0
		final double defaultY = 0;
		double distCenter = Point.distance(mouseEvent.x, mouseEvent.y, n.radius, n.radius);
		double diffCenterX = (mouseEvent.x - n.radius) / distCenter;
		double diffCenterY = (mouseEvent.y - n.radius) / distCenter;
		double angle = Math.acos(diffCenterX * defaultX + diffCenterY * defaultY);
		if(mouseEvent.y < n.radius)
			angle *= -1;
		return angle;
	}
	
	/**
	 * Checks whether the Point object lies within the node's visual boundaries.
	 * 
	 * @param p The point we want to check.
	 * @return true if p is within this node's circle.
	 */
	public boolean containsPoint(Point p) {
		return radius >= Math.sqrt((p.x - radius) * (p.x - radius) + (p.y - radius) * (p.y - radius));
	}
	
	/**
	 * Get the node this panel visualizes.
	 * 
	 * @return The corresponding node.
	 */
	public Node getNode() {
		return node;
	}
	
	/** 
	 * Get the node panel's fill color.
	 * 
	 * @return The node panel's fill color.
	 */
	public Color getFillColor() {
		return fillColor;
	}
	
	/** 
	 * Get the node panel's border color.
	 * 
	 * @return The node panel's border color.
	 */
	public Color getBorderColor() {
		return borderColor;
	}
	
	/** 
	 * Get the node panel's text color.
	 * 
	 * @return The node panel's text color.
	 */
	public Color getTextColor() {
		return textColor;
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
	 * Set the coordinates of the panel's center.
	 * 
	 * @param newCenter The new center point.
	 */
	public void setCenter(Point newCenter) {
		x = newCenter.x - radius;
		y = newCenter.y - radius;
	}
	
	/**
	 * Set the coordinates of the panel's center.
	 * 
	 * @param cx The x-coordinate of the new center.
	 * @param cy The y-coordinate of the new center.
	 */
	public void setCenter(int cx, int cy) {
		x = cx - radius;
		y = cy - radius;
	}
	
	/**
	 * Get the x-coordinate of the panel's lower right corner.
	 * 
	 * @return The integer x-coordinate.
	 */
	public int getLowerRightX() {
		return x + 2 * radius;
	}
	
	/**
	 * Get the y-coordinate of the panel's lower right corner.
	 * 
	 * @return The integer y-coordinate.
	 */
	public int getLowerRightY() {
		return y + 2 * radius;
	}
	
	/**
	 * Get the radius of the drawn node.
	 * 
	 * @return The integer radius of the node's visual circle representation.
	 */
	public int getRadius() {
		return radius;
	}
	
	/**
	 * Set the new value of the radius of this node.
	 * 
	 * @param r The new integer radius of the node's visual circle representation.
	 */
	public void setRadius(int r) {
		radius = r;
	}
	
	/**
	 * Get the center of the panel's circle.
	 * 
	 * @return The point for the center of the panel's circle. 
	 */
	public Point getCenter() {
		return new Point(x + radius, y + radius);
	}
	
	/**
	 * Get the text displayed on this node panel.
	 * 
	 * @return The text on this panel.
	 */
	public String getText() {
		return text;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(2*(radius + PADDING + SELECTED_BORDER_THICKNESS), 2*(radius + PADDING + SELECTED_BORDER_THICKNESS));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Ellipse2D.Double circle = new Ellipse2D.Double(PADDING, PADDING, 2*radius, 2*radius);
		g2d.setStroke(new BasicStroke(BORDER_THICKNESS));
		g2d.setColor(fillColor);
		
		// Draw the circle, border, and text
		g2d.fill(circle);
		if (borderColor != null) {
			g2d.setColor(borderColor);
			g2d.draw(circle);
		}
		if (textColor != null && text != null) {
			g2d.setColor(textColor);
			Rectangle bounds = g2d.getFontMetrics().getStringBounds(text, g2d).getBounds();
			g2d.drawString(text, radius - bounds.width/2, radius + bounds.height/2);
		}
		
		// Compute the rectangular bounds of the node
		Rectangle bounds = circle.getBounds();
		bounds.x -= SELECTED_BORDER_THICKNESS;
		bounds.y -= SELECTED_BORDER_THICKNESS;
		bounds.width += 2*SELECTED_BORDER_THICKNESS;
		bounds.height += 2*SELECTED_BORDER_THICKNESS;
		if (node.getSelected()) {
			// Draw the selection "box" if the node is selected
			g2d.setStroke(new BasicStroke(SELECTED_BORDER_THICKNESS));
			g2d.setColor((Color) Preferences.SELECTION_COLOR.getData());
			g2d.draw(bounds);
		} else if (hovering) {
			// Draw various "hover" visual effects
			Editor editor = context.getGUI().getEditor();
			Tool current = editor.getGUI().getCurrentTool();
			if (current == Tool.EDGE){
				g2d.setStroke(new BasicStroke(SELECTED_BORDER_THICKNESS));
				if(editor.getEdgeBasePoint() == null)
					g2d.setColor((Color) Preferences.LINE_START_COLOR.getData());
				else
					g2d.setColor((Color) Preferences.LINE_END_COLOR.getData());
				g2d.draw(bounds);
			} else if (current == Tool.DIRECTED_EDGE) {
				g2d.setStroke(new BasicStroke(SELECTED_BORDER_THICKNESS));
				if(editor.getEdgeBasePoint() == null)
					g2d.setColor((Color) Preferences.ARROW_START_COLOR.getData());
				else
					g2d.setColor((Color) Preferences.ARROW_END_COLOR.getData());
				g2d.draw(bounds);
			}
		}
	}
	
	@Override
	public String toString() {
		return String.format("NodePanel[x=%d, y=%d, txt=%s]", x, y, text);
	}
	
}
