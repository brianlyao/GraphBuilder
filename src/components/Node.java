package components;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.HashSet;

import javax.swing.*;

import actions.MoveNodeAction;
import preferences.Preferences;
import tool.Tool;
import uielements.Editor;

/** An instance represents a node (visually represented by a circle) placed on the editor panel. */
public class Node extends GraphComponent {

	private static final long serialVersionUID = 7543278908176323314L;
	
	// Constants for maintaining a smooth appearance
	private static final int BORDER_THICKNESS = 2;
	private static final int SELECTED_BORDER_THICKNESS = 1;
	private static final int DEFAULT_RADIUS = 30;
	private static final int PADDING = 1;
//	private static final int DISTANCE_PADDING = 5;
	
	private Editor editor; // The editor in which this node is displayed
	
	// Right click menu
	private JPopupMenu rightClickMenu;
	private JMenuItem properties;
	private JMenuItem copy;
	private JMenuItem delete;
	
	private int x; // Location on editor panel
	private int y;
	private int radius; // Radius in pixels
	private String text; // Text displayed
	private Color color; // Fill color
	private Color lineColor;
	private Color textColor;

	private Point clickPoint; // Coordinate of mouse click relative to the top left corner of its bounding box
	private boolean hovering;
	
	// Coordinate of node's bounding box's top left corner relative to editor origin when node is clicked
	private Point editorLocationOnClick;
	
	private HashSet<Edge> edges;
		
	/**
	 * Creates a circle at the top left corner with a radius of 30 pixels.
	 * 
	 * @param ed The editor panel this node is displayed on.
	 */
	public Node(Editor ed){
		this(0, 0, DEFAULT_RADIUS, ed);
	}
	
	/** 
	 * Creates a node with the specified location and radius. 
	 * @param x  The x-coordinate of the circle's top left corner.
	 * @param y  The y-coordinate of the circle's top left corner.
	 * @param r  The radius of the circle in pixels.
	 * @param ed The editor panel this node is displayed on.
	 */
	public Node(int x, int y, int r, Editor ed) {
		this(x, y, r, null, ed);
	}
	
	/** 
	 * Creates a node with the specified location, radius, and color. 
	 * @param x  The x-coordinate of the circle's top left corner.
	 * @param y  The y-coordinate of the circle's top left corner.
	 * @param r  The radius of the circle in pixels. 
	 * @param c  The fill color of the circle.
	 * @param ed The editor panel this node is displayed on.
	 */
	public Node(int x, int y, int r, Color c, Editor ed) {
		this(x, y, r, null, c, null, null, ed);
	}

	/** 
	 * Creates a node with the specified location, radius, color, and border color. 
	 * @param x  The x-coordinate of the circle's top left corner.
	 * @param y  The y-coordinate of the circle's top left corner.
	 * @param r  The radius of the circle in pixels. 
	 * @param c  The fill color of the circle.
	 * @param lc The border color of the circle.
	 * @param ed The editor panel this node is displayed on.
	 */
	public Node(int x, int y, int r, Color c, Color lc, Editor ed) {
		this(x, y, r, null, c, lc, null, ed);
	}
	
	/**
	 * Creates a node with the specified location, radius, text,
	 * color, and text color. 
	 * @param x   The x-coordinate of the circle's top left corner.
	 * @param y   The y-coordinate of the circle's top left corner.
	 * @param r   The radius of the circle in pixels.
	 * @param txt The text displayed on the circle.
	 * @param c   The fill color of the circle.
	 * @param tc  The color of the circle's text.
	 * @param ed  The editor panel this node is displayed on.
	 */
	public Node(int x, int y, int r, String txt, Color c, Color tc, Editor ed) {
		this(x, y, r, txt, c, null, tc, ed);
	}
	
	/**
	 * Creates a node with the specified location, radius, text,
	 * color, border color, text color, and select color.
	 * @param x   The x-coordinate of the circle's top left corner.
	 * @param y   The y-coordinate of the circle's top left corner.
	 * @param r   The radius of the circle in pixels.
	 * @param txt The text displayed on the circle.
	 * @param c   The fill color of the circle.
	 * @param lc  The border color of the circle.
	 * @param tc  The color of the circle's text.
	 * @param ed  The editor panel this node is displayed on.
	 */
	public Node(int x, int y, int r, String txt, Color c, Color lc, Color tc, Editor ed) {
		super();
		this.x = x;
		this.y = y;
		radius = r;
		text = txt;
		color = c;
		lineColor = lc;
		textColor = tc;
		editor = ed;
		edges = new HashSet<>();
		
		clickPoint = new Point(-1, -1); // Initialize to a bad value
		hovering = false;
		
		setSelected(false); // This component should be deselected on creation
		setLocation(x, y); // Set its location on the panel
		setOpaque(false);
		
		// Fill right click menu with items
		rightClickMenu = new JPopupMenu();
		
		properties = new JMenuItem("View/Edit Properties");
//		properties.addActionListener(new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				JFrame prop = new JFrame(String.format("Circle properties: \"%s\"", text));
//				//TODO
//			}
//		});
		copy = new JMenuItem("Copy");
		delete = new JMenuItem("Delete");
		delete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.removeNode(Node.this);
			}
			
		});
		rightClickMenu.add(properties);
		rightClickMenu.addSeparator();
		rightClickMenu.add(copy);
		rightClickMenu.addSeparator();
		rightClickMenu.add(delete);
		
		// Listen for mouse events
		addMouseListener(new MouseListener() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				clickPoint = e.getPoint();
				editorLocationOnClick = new Point(Node.this.x + e.getX(), Node.this.y + e.getY());
				Node source = (Node) e.getSource();
				boolean contains = source.containsPoint(clickPoint);
				
				// Display the right click menu if the node is right clicked
				if(contains && e.getButton() == MouseEvent.BUTTON3) {
					rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
				} else if(contains) {
					Tool current = editor.getGUI().getCurrentTool();
					if(current == Tool.SELECT) {
						GraphComponent previous = editor.getSelection();
						source.setSelected(true);
						if(previous != null && previous != source) {
							previous.setSelected(false);
							previous.repaint();
							editor.setSelection(source);
						}
						repaint();
						editor.setSelection(source); 
					} else if(current == Tool.EDGE || current == Tool.DIRECTED_EDGE) {
						if(editor.getEdgeBasePoint() == null) {
							editor.setEdgeBasePoint(Node.this);
						} else {
							Node sink = editor.getEdgeBasePoint();
							Edge newLine = new Edge(source, sink, editor.getGUI().getEdgeOptionsBar().getCurrentLineColor(), editor.getGUI().getEdgeOptionsBar().getCurrentLineWeight(), editor);
							if(current == Tool.DIRECTED_EDGE)
								newLine = new DirectedEdge(source, sink, editor.getGUI().getEdgeOptionsBar().getCurrentLineColor(), editor.getGUI().getEdgeOptionsBar().getCurrentLineWeight(), editor);
							editor.addEdge(newLine);
							editor.setEdgeBasePoint(null);
							editor.repaint();
						}
					}
				}
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {
				hovering = false;
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				Point locationOnEditor = new Point(Node.this.x + e.getX(), Node.this.y + e.getY());
				if(!editorLocationOnClick.equals(locationOnEditor))
					editor.getContext().pushAction(new MoveNodeAction(editor.getContext(), Node.this, editorLocationOnClick, locationOnEditor));
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
				}
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				boolean repaintNeeded = false;
				if(containsPoint(e.getPoint())) {
					if(!hovering)
						repaintNeeded = true;
					hovering = true;
				} else {
					if(hovering)
						repaintNeeded = true;
					hovering = false;
				}
				if(repaintNeeded)
					repaint();
			}
			
		});
	}
	
	/** Checks whether the Point object lies within the node's boundaries. */
	public boolean containsPoint(Point p) {
		return radius >= Math.sqrt((p.x - radius) * (p.x - radius) + (p.y - radius) * (p.y - radius));
	}
	
	public void addEdge(Edge l) {
		edges.add(l);
	}
	
	public HashSet<Edge> getEdges() {
		return edges;
	}
	
	/** Return the node's fill color. */
	public Color getColor() {
		return color;
	}
	
	/** Return the coordinates of the node's upper left corner. */
	public Point getCoords() {
		return new Point(x, y);
	}
	
	/** Set the coordinates of the node's upper left corner. */
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
	
	/** Return the point for the center of the circle. */
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
		g2d.setColor(color);
		g2d.fill(circle);
		if(lineColor != null) {
			g2d.setColor(lineColor);
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
		return String.format("Circle[x=%d,y=%d,r=%d,text=%s,id=%d]", x, y, radius, text == null ? "" : text, getID());
	}
	
}
