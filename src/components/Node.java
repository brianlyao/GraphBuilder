package components;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;

import context.GraphBuilderContext;
import actions.MoveNodeAction;
import actions.PlaceEdgeAction;
import preferences.Preferences;
import tool.Tool;
import ui.Editor;
import ui.menus.NodeRightClickMenu;

/** An instance represents a node (visually represented by a circle) placed on the editor panel. */
public class Node extends GraphComponent {

	/** An instance is an unordered pair of Nodes. */
	public static class Pair {
		
		private final Node node1;
		private final Node node2;
		
		public Pair(Node n1, Node n2) {
			node1 = n1;
			node2 = n2;
		}
		
		public Pair(Edge e) {
			Node[] ends = e.getEndpoints();
			node1 = ends[0];
			node2 = ends[1];
		}
		
		/**
		 * Check if this pair has the specified node.
		 * 
		 * @param n The node we want to compare with the pair's contents.
		 * @return true if n is contained in this pair, false otherwise.
		 */
		public boolean hasNode(Node n) {
			return n == node1 || n == node2;
		}
		
		public Node getFirst() {
			return node1;
		}
		
		public Node getSecond() {
			return node2;
		}
		
		/**
		 * Checks if the the two nodes in both pairs are identical to each other.
		 */
		public boolean equals(Object o) {
			if(o == null)
				return false;
			Pair other = (Pair) o;
			return (node1 == other.node1 && node2 == other.node2) || (node1 == other.node2 && node2 == other.node1);
		}
		
		public int hashCode() {
			int code1 = node1 == null ? 1 : node1.hashCode();
			int code2 = node2 == null ? 1 : node2.hashCode();
			return code1 * code2;
		}
		
	}
	
	private static final long serialVersionUID = 7543278908176323314L;
	
	// Constants for maintaining a smooth appearance
	private static final int BORDER_THICKNESS = 2;
	private static final int SELECTED_BORDER_THICKNESS = 1;
	private static final int DEFAULT_RADIUS = 30;
	private static final int PADDING = 1;
	
	private Editor editor; // The editor in which this node is displayed
	
	private int x; // Location on editor panel
	private int y;
	private int radius; // Radius in pixels
	private String text; // Text displayed
	private Color fillColor; // Fill color
	private Color borderColor; // Border color
	private Color textColor; // Text color

	private Point clickPoint; // Coordinate of mouse click relative to the top left corner of its bounding box
	private boolean hovering;
	
	// Coordinate of node's bounding box's top left corner relative to editor origin when node is clicked
	private Point editorLocationOnClick;
	
	private HashSet<Edge> edges;
		
	/**
	 * Creates a circle at the top left corner with a radius of 30 pixels.
	 * 
	 * @param ctxt The context (graph) this node is a part of.
	 * @param id   The id this node is assigned.
	 */
	public Node(GraphBuilderContext ctxt, int id){
		this(0, 0, DEFAULT_RADIUS, ctxt, id);
	}
	
	/** 
	 * Creates a node with the specified location and radius. 
	 * 
	 * @param x    The x-coordinate of the circle's top left corner.
	 * @param y    The y-coordinate of the circle's top left corner.
	 * @param r    The radius of the circle in pixels.
	 * @param ctxt The context (graph) this node is a part of.
	 * @param id   The id this node is assigned.
	 */
	public Node(int x, int y, int r, GraphBuilderContext ctxt, int id) {
		this(x, y, r, null, ctxt, id);
	}
	
	/** 
	 * Creates a node with the specified location, radius, and color. 
	 * 
	 * @param x    The x-coordinate of the circle's top left corner.
	 * @param y    The y-coordinate of the circle's top left corner.
	 * @param r    The radius of the circle in pixels. 
	 * @param c    The fill color of the circle.
	 * @param ctxt The context (graph) this node is a part of.
	 * @param id   The id this node is assigned.
	 */
	public Node(int x, int y, int r, Color c, GraphBuilderContext ctxt, int id) {
		this(x, y, r, null, c, null, null, ctxt, id);
	}

	/** 
	 * Creates a node with the specified location, radius, color, and border color. 
	 * 
	 * @param x    The x-coordinate of the circle's top left corner.
	 * @param y    The y-coordinate of the circle's top left corner.
	 * @param r    The radius of the circle in pixels. 
	 * @param c    The fill color of the circle.
	 * @param lc   The border color of the circle.
	 * @param ctxt The context (graph) this node is a part of.
	 * @param id   The id this node is assigned.
	 */
	public Node(int x, int y, int r, Color c, Color lc, GraphBuilderContext ctxt, int id) {
		this(x, y, r, null, c, lc, null, ctxt, id);
	}
	
	/**
	 * Creates a node with the specified location, radius, text, color, and text color. 
	 * 
	 * @param x    The x-coordinate of the circle's top left corner.
	 * @param y    The y-coordinate of the circle's top left corner.
	 * @param r    The radius of the circle in pixels.
	 * @param txt  The text displayed on the circle.
	 * @param c    The fill color of the circle.
	 * @param tc   The color of the circle's text.
	 * @param ctxt The context (graph) this node is a part of.
	 * @param id   The id this node is assigned.
	 */
	public Node(int x, int y, int r, String txt, Color c, Color tc, GraphBuilderContext ctxt, int id) {
		this(x, y, r, txt, c, null, tc, ctxt, id);
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
	 * @param id   The id this node is assigned.
	 */
	public Node(int x, int y, int r, String txt, Color c, Color lc, Color tc, GraphBuilderContext ctxt, int id) {
		super(ctxt, id);
		this.x = x;
		this.y = y;
		radius = r;
		text = txt;
		fillColor = c;
		borderColor = lc;
		textColor = tc;
		edges = new HashSet<>();
		editor = ctxt.getGUI().getEditor();
		
		clickPoint = new Point(-1, -1); // Initialize to a bad value
		editorLocationOnClick = new Point(-1, -1);
		hovering = false;
		
		setSelected(false); // This component should be deselected on creation
		setOpaque(false);
		
		// Listen for mouse events
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				clickPoint = e.getPoint();
				editorLocationOnClick.setLocation(Node.this.x, Node.this.y);
				Node sink = (Node) e.getSource();
				boolean contains = sink.containsPoint(clickPoint);
				
				// Display the right click menu if the node is right clicked
				if(contains && e.getButton() == MouseEvent.BUTTON3) {
					NodeRightClickMenu.show(Node.this, e.getX(), e.getY());
				} else if(contains && e.getButton() == MouseEvent.BUTTON1) {
					Tool current = editor.getGUI().getCurrentTool();
					if(current == Tool.SELECT) {
						GraphComponent previous = editor.getSelection();
						sink.setSelected(true);
						if(previous != null && previous != sink) {
							previous.setSelected(false);
							previous.repaint();
							editor.setSelection(sink);
						}
						repaint();
						editor.setSelection(sink); 
					} else if(current == Tool.EDGE || current == Tool.DIRECTED_EDGE) {
						if(editor.getEdgeBasePoint() == null) {
							editor.setEdgeBasePoint(Node.this);
						} else {
							Node source = editor.getEdgeBasePoint();
							Color currentLineColor = editor.getGUI().getEdgeOptionsBar().getCurrentLineColor();
							int currentLineWeight = editor.getGUI().getEdgeOptionsBar().getCurrentLineWeight();
							boolean directed = current == Tool.DIRECTED_EDGE;
							Edge newEdge;
							
							// Check if the edge should be a self-edge
							if(sink == source) {
								double angle = getSelfEdgeOffsetAngle(Node.this, e.getPoint());
								newEdge = new SelfEdge(sink, currentLineColor, currentLineWeight, angle, directed, getContext(), getContext().getNextIDAndInc());
							} else {
								newEdge = new SimpleEdge(sink, source, currentLineColor, currentLineWeight, directed, getContext(), getContext().getNextIDAndInc());
							}
							
							// Compute the position of this edge (only matters if it is a simple edge)
							// Add the new edge at the given position
							ArrayList<Edge> pairEdges = getContext().getEdgeMap().get(new Node.Pair(sink, source)); 
							int edgePosition = getEdgePosition(sink, source, e.getPoint(), pairEdges);
							new PlaceEdgeAction(getContext(), newEdge, edgePosition).actionPerformed(null);
							editor.setEdgeBasePoint(null); 
							
							// Remove any existing preview edge object
							editor.setPreviewEdge(null, -1, false);
							
							editor.repaint();
						}
					}
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				Point locationOnEditor = new Point(Node.this.x, Node.this.y);
				if(!editorLocationOnClick.equals(locationOnEditor)) {
					getContext().getActionHistory().push(new MoveNodeAction(getContext(), Node.this, editorLocationOnClick, locationOnEditor));
					getContext().updateSaveState();
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if(containsPoint(e.getPoint()))
					hovering = true;
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				hovering = false;
				
				// Remove any existing preview edge object
				editor.setPreviewEdge(null, -1, false);
				editor.repaint();
			}
			
		});
		
		// Listen for dragging and hovering mouse events
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(editor.getGUI().getCurrentTool() == Tool.SELECT && containsPoint(clickPoint)) {
					Node c = (Node) e.getSource();
					Point dragPoint = e.getPoint();
					Point newPoint = new Point(Math.max(0, c.x + dragPoint.x - clickPoint.x),
											   Math.max(0, c.y + dragPoint.y - clickPoint.y));
					setCoords(newPoint);
					editor.repaint();
				}
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if(containsPoint(e.getPoint())) {
					hovering = true;
					
					// Draw preview edges correctly
					Node ebp = editor.getEdgeBasePoint();
					Tool ctool = editor.getGUI().getCurrentTool();
					if((ctool == Tool.EDGE || ctool == Tool.DIRECTED_EDGE) && ebp != null) {
						Color previewColor = (Color) Preferences.EDGE_PREVIEW_COLOR.getData();
						int weight = editor.getGUI().getEdgeOptionsBar().getCurrentLineWeight();
						boolean directed = ctool == Tool.DIRECTED_EDGE;
						if(ebp != Node.this) {
							// Compute the preview for a simple edge
							ArrayList<Edge> existingEdges = getContext().getEdgeMap().get(new Node.Pair(ebp, Node.this));
							
							// Get the position of this edge within the existing edges
							int edgePosition = getEdgePosition(Node.this, ebp, e.getPoint(), existingEdges);
							
							// Create edge and set it as the preview
							SimpleEdge preview = new SimpleEdge(Node.this, ebp, previewColor, weight, directed, getContext(), -1);
							editor.setPreviewEdge(preview, edgePosition, true);
						} else {
							// Compute the preview for a self edge
							double angle = getSelfEdgeOffsetAngle(Node.this, e.getPoint());
							SelfEdge preview = new SelfEdge(Node.this, previewColor, weight, angle, directed, getContext(), -1);
							editor.setPreviewEdge(preview, -1, true);
						}
						editor.repaint(); // Repaint editor to update preview edge
					}
				} else {
					hovering = false;
					
					// Check for movement within this Node's panel but not within the node itself
					editor.setLastMousePoint(Node.this.x + e.getPoint().x, Node.this.y + e.getPoint().y);
					
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
	private static int getEdgePosition(Node to, Node from, Point mouseEvent, ArrayList<Edge> existingEdges) {
		int edgePosition = 0;
		int numExistingEdges = existingEdges == null ? 0 : existingEdges.size();
		if(numExistingEdges > 0) {
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
			if(unitAwayX * unitToMouseY - unitAwayY * unitToMouseX < 0)
				angleFromCenters *= -1;
			
			// Compute the angle over which the edges are spread
			double spreadAngle = (double) Preferences.EDGE_SPREAD_ANGLE.getData();
			double boundAngle = (spreadAngle * (numExistingEdges - 1)) / 2.0;
			if(angleFromCenters > boundAngle)
				edgePosition = numExistingEdges;
			else if(angleFromCenters < -boundAngle)
				edgePosition = 0;
			else
				edgePosition = (int) ((angleFromCenters + boundAngle) / spreadAngle) + 1;
			
			// Indices are one-way; we might have to use the "opposite index depending
			// on the direction the edge is being drawn from
			Edge first = existingEdges.get(0);
			if(numExistingEdges == 1 && to != from && first instanceof SimpleEdge) {
				if(to == first.getEndpoints()[1])
					edgePosition = numExistingEdges - edgePosition;
			} else if(to != from && first instanceof SimpleEdge) {
				Point2D.Double control = ((SimpleEdge) existingEdges.get(0)).getBezierPoints()[1];
				double centerToControlDist = Point.distance(control.x, control.y, toCenter.x, toCenter.y);
				double centerToControlX = (control.x - toCenter.x) / centerToControlDist;
				double centerToControlY = (control.y - toCenter.y) / centerToControlDist;
				if(unitAwayX * centerToControlY - unitAwayY * centerToControlX > 0)
					edgePosition = numExistingEdges - edgePosition;
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
	private static double getSelfEdgeOffsetAngle(Node n, Point mouseEvent) {
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
	
	public void addEdge(Edge l) {
		edges.add(l);
	}
	
	public HashSet<Edge> getEdges() {
		return edges;
	}
	
	/** 
	 * Get the node's fill color.
	 * 
	 * @return The node's fill color.
	 */
	public Color getFillColor() {
		return fillColor;
	}
	
	/**
	 * Get the coordinates of the node's upper left corner.
	 * 
	 * @return The point correspoinding to the upper left corner of this node's bounding box.
	 */
	public Point getCoords() {
		return new Point(x, y);
	}
	
	/**
	 * Set the coordinates of the node's upper left corner.
	 * 
	 * @param p The point which will become the node's upper left corner.
	 */
	public void setCoords(Point p) {
		x = p.x;
		y = p.y;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public void setRadius(int r) {
		radius = r;
	}
	
	/**
	 * Get the center of the node's circle.
	 * 
	 * @return The point for the center of the node's circle. 
	 */
	public Point getCenter() {
		return new Point(x + radius, y + radius);
	}
	
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
		g2d.fill(circle);
		if(borderColor != null) {
			g2d.setColor(borderColor);
			g2d.draw(circle);
		}
		if(textColor != null && text != null) {
			g2d.setColor(textColor);
			Rectangle bounds = g2d.getFontMetrics().getStringBounds(text, g2d).getBounds();
			g2d.drawString(text, radius - bounds.width/2, radius + bounds.height/2);
		}
		Rectangle bounds = circle.getBounds();
		bounds.x -= SELECTED_BORDER_THICKNESS;
		bounds.y -= SELECTED_BORDER_THICKNESS;
		bounds.width += 2*SELECTED_BORDER_THICKNESS;
		bounds.height += 2*SELECTED_BORDER_THICKNESS;
		boolean contains = containsPoint(clickPoint);
		if(getSelected() && contains) {
			g2d.setStroke(new BasicStroke(SELECTED_BORDER_THICKNESS));
			g2d.setColor((Color) Preferences.SELECTION_COLOR.getData());
			g2d.draw(bounds);
		} else if(hovering) {
			Tool current = editor.getGUI().getCurrentTool();
			if(current == Tool.EDGE){
				g2d.setStroke(new BasicStroke(SELECTED_BORDER_THICKNESS));
				if(editor.getEdgeBasePoint() == null)
					g2d.setColor((Color) Preferences.LINE_START_COLOR.getData());
				else
					g2d.setColor((Color) Preferences.LINE_END_COLOR.getData());
				g2d.draw(bounds);
			} else if(current == Tool.DIRECTED_EDGE) {
				g2d.setStroke(new BasicStroke(SELECTED_BORDER_THICKNESS));
				if(editor.getEdgeBasePoint() == null)
					g2d.setColor((Color) Preferences.ARROW_START_COLOR.getData());
				else
					g2d.setColor((Color) Preferences.ARROW_END_COLOR.getData());
				g2d.draw(bounds);
			}
		}
	}
	
	public String toString() {
		return String.format("Node[x=%d,y=%d,r=%d,text=%s,id=%d]", x, y, radius, text == null ? "" : text, getID());
	}
	
	public String toStorageString() {
		return String.format("N:%d,%d,%d,%d,%s,%d,%d,%d", getID(), x, y, radius,
				text, fillColor.getRGB(), borderColor.getRGB(), textColor.getRGB());
	}
	
}
