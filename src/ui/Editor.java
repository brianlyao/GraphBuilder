package ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;

import actions.PlaceNodeAction;
import preferences.Preferences;
import tool.Tool;
import math.Complex;
import components.*;
import context.GraphBuilderContext;

/** The main panel on which the user will be drawing graphs on. */
public class Editor extends JPanel {
	
	private static final long serialVersionUID = 7327691115632869820L;
	
	private GUI gui; // The GUI this editor is placed in
	
	private Point lastMousePoint; // Keep track of the last mouse position
	private Point closestEdgeSelectPoint;
	private Edge closestEdge;
	
	private Node edgeBasePoint; // The first node that's selected when drawing an edge
	
	private GraphComponent selection; // The currently selected item
	
	private Edge previewEdge;
	private int previewEdgeIndex;
	private boolean drawPreviewUsingObject; // If true, the preview edge should be the object previewEdge
	
	/** 
	 * Constructor for an editor panel.
	 * 
	 * @param g The GUI object we want our editor placed in.
	 */
	public Editor(GUI g) {
		super();
		gui = g;
		lastMousePoint = new Point(0, 0);
		
		// Initialize the panel with default settings...
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(2048, 2048));
		
		// Listen for mouse events
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Tool current = gui.getCurrentTool();
				
				// Clicking the canvas will deselect any selection
				if(selection != null && current != Tool.EDGE_SELECT) {
					selection.setSelected(false);
					selection.repaint();
					selection = null;
				}
				
				// If user clicks the canvas with the node tool, add a new node
				if(current == Tool.NODE) {
					Color[] colors = gui.getNodeOptionsBar().getCurrentCircleColors();
					int currentRadius = gui.getNodeOptionsBar().getCurrentRadius();
					Point placed = new Point(Math.max(0, lastMousePoint.x - currentRadius), Math.max(0, lastMousePoint.y - currentRadius));
					Node newNode = new Node(placed.x, placed.y, currentRadius, "", colors[0], colors[1], colors[2], gui.getContext(), gui.getContext().getNextIDAndInc());
					
					// Perform the action for placing a node
					new PlaceNodeAction(gui.getContext(), newNode).actionPerformed(null);
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				Tool currentTool = gui.getCurrentTool();
				if(currentTool == Tool.EDGE_SELECT && closestEdge != null && arg0.getButton() == MouseEvent.BUTTON1) {
					// Select the edge if 
					selection = closestEdge;
					closestEdge.setSelected(true);
				}
				if(arg0.getButton() == MouseEvent.BUTTON3 && (currentTool == Tool.EDGE || currentTool == Tool.DIRECTED_EDGE) && edgeBasePoint != null) {
					// Reset base point to "cancel" edge placement
					edgeBasePoint = null;
					repaint(); // To "un"-draw the preview
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		
		// Listen for dragging and hovering mouse events
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseDragged(MouseEvent arg0) {
				Tool current = gui.getCurrentTool();
				if(current == Tool.PAN){
					Point currentPoint = arg0.getPoint();
					int changeX = currentPoint.x - lastMousePoint.x;
					int changeY = currentPoint.y - lastMousePoint.y;
					JScrollPane sp = gui.getScrollPane();
					JScrollBar horiz = sp.getHorizontalScrollBar();
					JScrollBar vert = sp.getVerticalScrollBar();
					double multiplier = (double) Preferences.PAN_SENSITIVITY.getData();
					int newHorizVal = (int) Math.min(horiz.getMaximum(), Math.max(horiz.getMinimum(), horiz.getValue() - multiplier*changeX));
					int newVertVal = (int) Math.min(vert.getMaximum(), Math.max(vert.getMinimum(), vert.getValue() - multiplier*changeY));
					horiz.setValue(newHorizVal);
					vert.setValue(newVertVal);
				}
				lastMousePoint = arg0.getPoint();
				repaint();
			}
			
			@Override
			public void mouseMoved(MouseEvent arg0) {
				lastMousePoint = arg0.getPoint();
				Tool currentTool = gui.getCurrentTool();
				
				if(currentTool == Tool.EDGE_SELECT) {
					// Compute the line and distance to the closest edge 
					Point2D.Double clickd = new Point2D.Double(lastMousePoint.x, lastMousePoint.y); // Move point
					
					// Iteratively updated variables for determining the closest edge
					closestEdgeSelectPoint = null;
					closestEdge = null;
					double minDistance = Double.MAX_VALUE;
					
					// Get the edgemap
					HashMap<Node.Pair, ArrayList<Edge>> em = gui.getContext().getEdgeMap();
					
					// Iterate through the edgemap
					for(Node.Pair endpoints : em.keySet()) {
						// Get set of edges between first and second
						ArrayList<Edge> betweenTwo = em.get(endpoints);
						
						// Iterate through edges between the nodes "first" and "second"
						// For each edge, find the closest distance from the current mouse position to the edge
						// The Edge Select tool requires this to find the edge closest to the mouse
						for(Edge l : betweenTwo) {
							double dist = Double.MAX_VALUE;
							Point closest = null;
							Point2D.Double[] bcp;
							if(l instanceof SimpleEdge) {
								SimpleEdge simpe = (SimpleEdge) l;
								bcp = simpe.getBezierPoints();
								if(bcp[1].x < 0 && bcp[1].y < 0) {
									// Only the endpoints of the line are stored...
									Point2D.Double c1 = bcp[0];
									Point2D.Double c2 = bcp[2];
									if(c2.x != c1.x && c2.y != c1.y) {
										// Find closest point to line using a parametric line
										Point2D.Double c1click = new Point2D.Double(clickd.x - c1.x, clickd.y - c1.y);
										Point2D.Double c1c2 = new Point2D.Double(c2.x - c1.x, c2.y - c1.y);
										double dot = c1click.x * c1c2.x + c1click.y * c1c2.y;
										double t = dot / (c1c2.x * c1c2.x + c1c2.y * c1c2.y);
										double intersectx = c1.x + c1c2.x * t;
										double intersecty = c1.y + c1c2.y * t;
										
									    // Only 3 candidates: both endpoints and the intersection of the edge and the line perpendicular to the edge which passes through the cursor
										double[] dists = new double[3];
										Point2D.Double[] points = {c1, new Point2D.Double(intersectx, intersecty), c2};
										for(int i = 0 ; i < dists.length ; i++)
											dists[i] = points[i].distance(clickd);
										
										// If the closest point on the line is NOT actually on the line segment
										if(t < 0 || t > 1)
											dists[1] = Double.MAX_VALUE;
										
										// Determine the closest point among the candidates
										int minind = 0;
										dist = dists[0];
										for(int i = 1 ; i < dists.length ; i++) {
											if(dists[i] < dist) {
												dist = dists[i];
												minind = i;
											}
										}
										closest = new Point((int) points[minind].x, (int) points[minind].y);
									} else if(c2.x == c1.x) {
										// If the line happens to be vertical...
										closest = new Point((int) c1.x, lastMousePoint.y);
										dist = Math.abs(c1.x - clickd.x);
									} else {
										// If the line happens to be horizontal...
										closest = new Point(lastMousePoint.x, (int) c1.y);
										dist = Math.abs(c1.y - clickd.y);
									}
								} else {
									// To get the closest point on a bezier curve, we're going to need to solve a cubic...
									double a = bcp[0].x;
									double b = bcp[0].y;
									double c = bcp[1].x;
									double d = bcp[1].y;
									double e = bcp[2].x;
									double f = bcp[2].y;
									double x = clickd.x;
									double y = clickd.y;
									
									// The coefficients of the cubic 
									double n1 = (a - 2*c + e)*(a - 2*c + e) + (b - 2*d + f)*(b - 2*d + f);
									double n2 = -3*((a - c)*(a - 2*c + e) + (b - d)*(b - 2*d + f));
									double n3 = a*(3*a - x + e) - 2*c*(3*a - c - x) - e*x + b*(3*b - y + f) - 2*d*(3*b - d - y) - f*y;
									double n4 = (c - a)*(a - x) + (d - b)*(b - y);
									
									// Compute the roots of the equation n1x^3 + n2x^2 + n3x + n4 = 0
									Complex[] roots = new Complex[3];
									Complex disc0 = new Complex(n2*n2 - 3*n1*n3);
									Complex disc1 = new Complex(2*n2*n2*n2 - 9*n1*n2*n3 + 27*n1*n1*n4);
									Complex incbrt = null;
									if(disc0.isZero()) {
										incbrt = disc1;
										if(disc1.getReal() < 0)
											incbrt = incbrt.neg();
									} else {
										incbrt = disc1.add(disc1.pow(2).subtract(disc0.pow(3).scale(4)).sqrt()[0]).scale(0.5); 
									}
									Complex[] cbrts = incbrt.cbrt();
									Complex B = new Complex(n2);
									for(int i = 0 ; i < 3 ; i++)
										roots[i] = B.add(cbrts[i]).add(disc0.divide(cbrts[i])).scale(-1 / (3 * n1));
									
									// Temporary holder of the points corresponding to the REAL roots of the cubic
									ArrayList<Point2D.Double> candidateClosestPoints = new ArrayList<Point2D.Double>();
									
									// Determine which roots are real; use these values of t to determine the candidate "closest" points
									for(Complex rt : roots)
										if(rt.isReal() && rt.getReal() >= 0 && rt.getReal() <= 1)
											candidateClosestPoints.add(getBezierPoint(bcp, rt.getReal()));
									
									// Make sure to include the endpoints of the bezier curve
									candidateClosestPoints.add(bcp[0]);
									candidateClosestPoints.add(bcp[2]);
									
									// Determine, out of the candidate points, which is the closest
									double tempdist;
									for(Point2D.Double pt : candidateClosestPoints) {
										if((tempdist = pt.distance(clickd)) < dist) {
											dist = tempdist; // Set the distance from the cursor for this edge
											closest = new Point((int) pt.x, (int) pt.y);
										}
									}
								}
							} else if(l instanceof SelfEdge) {
								SelfEdge selfe = (SelfEdge) l;
								Point2D.Double cen = selfe.getCenter();
								
								// Since a loop is a (part of a) circle, the math for determining the point closest to the mouse is simple
								dist = Math.abs(selfe.getRadius() - Math.sqrt((cen.x - clickd.x) * (cen.x - clickd.x) + (cen.y - clickd.y) * (cen.y - clickd.y)));
								
								// Determine closest point on the loop to the cursor
								double distcen = Math.sqrt((cen.x - clickd.x) * (cen.x - clickd.x) + (cen.y - clickd.y) * (cen.y - clickd.y));
								double closex = (clickd.x - cen.x) * selfe.getRadius() / distcen + cen.x;
								double closey = (clickd.y - cen.y) * selfe.getRadius() / distcen + cen.y;
								closest = new Point((int) closex, (int) closey);
							}
							
							// Update the closest edge; once the distances to all edges is computed, closestEdge will contain the closest edge
							if(dist < minDistance){
								minDistance = dist;
								closestEdge = l;
								closestEdgeSelectPoint = closest;
							}
						}
					}
				}
				repaint(); // Repaint whenever the mouse moves
			}
			
		});
	}
	
	public GraphBuilderContext getContext() {
		return gui.getContext();
	}
	
	public GUI getGUI() {
		return gui;
	}
	
	public Node getEdgeBasePoint() {
		return edgeBasePoint;
	}
	
	public void setEdgeBasePoint(Node n) {
		edgeBasePoint = n;
	}
	
	/**
	 * Sets the last mouse point; we need this if the mouse is not on the Editor panel.
	 * 
	 * @param x The x coordinate of the new position.
	 * @param y The y coordinate of the new position.
 	 */
	public void setLastMousePoint(int x, int y) {
		if(lastMousePoint != null)
			lastMousePoint.setLocation(x, y);
		else
			lastMousePoint = new Point(x, y);
	}
	
	/**
	 * Get the currently selected graph component.
	 * 
	 * @return The selected GraphComponent.
	 */
	public GraphComponent getSelection() {
		return selection;
	}
	
	/**
	 * Set which graph component is selected.
	 * 
	 * @param gc The GraphComponent which will be selected.
	 */
	public void setSelection(GraphComponent gc) {
		selection = gc;
	}
	
	/**
	 * Set the editor's preview edge and the preview edge's position
	 * 
	 * @param edge     The edge object representing the preview edge.
	 * @param position The position of the preview edge relative to the existing edges between the
	 *                 the same endpoints the preview edge has.
	 */
	public void setPreviewEdge(Edge edge, int position, boolean useThisEdge) {
		previewEdge = edge;
		previewEdgeIndex = position;
		drawPreviewUsingObject = useThisEdge;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		// Explicitly set the position of the nodes; this allows the pane to be scrollable while retaining the position of the circles relative to the top left corner of the editor panel
		for(Node c : gui.getContext().getNodes())
			c.setLocation(c.getCoords());
		
		// Draw all edges by iterating through pairs of nodes
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		HashMap<Node.Pair, ArrayList<Edge>> edgeMap = gui.getContext().getEdgeMap();
		
		// Check if the preview edge's endpoint pair exists in the edge map
		// If not, then we need to draw it separately
		if(previewEdge != null) {
			Node.Pair previewPair = new Node.Pair(previewEdge);
			if(!edgeMap.keySet().contains(previewPair)) {
				ArrayList<Edge> previewList = new ArrayList<>();
				previewList.add(previewEdge);
				drawEdgesBetweenNodePair(g2d, previewPair, previewList, previewEdge);
			}
		}
		
		// Iterate through the pairs of nodes in the edge map, and draw the edges between them
		// If there is a preview edge, we "add" it (not directly) to the list of existing edges
		// before we draw the list of edges
		ArrayList<Edge> pairEdges, toDrawEdges;
		for(Node.Pair pair : edgeMap.keySet()) {
			pairEdges = edgeMap.get(pair);
			if(previewEdge != null && pair.equals(new Node.Pair(previewEdge))) {
				toDrawEdges = new ArrayList<>();
				for(Edge e : pairEdges)
					toDrawEdges.add(e);
				int newIndex = previewEdgeIndex < 0 || previewEdgeIndex > pairEdges.size() ? pairEdges.size() : previewEdgeIndex;
				toDrawEdges.add(newIndex, previewEdge);
			} else {
				toDrawEdges = pairEdges;
			}
			drawEdgesBetweenNodePair(g2d, pair, toDrawEdges, previewEdge);
		}
		
		g2d.setStroke(new BasicStroke());
		// Draw tool-specific graphics
		Tool ctool = gui.getCurrentTool();
		if(ctool == Tool.NODE) {
			// Draw preview for the Circle tool
			g2d.setColor((Color) Preferences.CIRCLE_PREVIEW_COLOR.getData());
			int currentRadius = gui.getNodeOptionsBar().getCurrentRadius();
			Ellipse2D.Double preview = new Ellipse2D.Double(lastMousePoint.x - currentRadius,
					lastMousePoint.y - currentRadius, 2*currentRadius, 2*currentRadius);
			g2d.draw(preview);
		} else if(ctool == Tool.EDGE_SELECT) {
			if(closestEdgeSelectPoint != null) {
				// Draw the edge select preview
				g2d.setColor((Color) Preferences.EDGE_SELECT_PREVIEW_COLOR.getData());
				g2d.drawLine(closestEdgeSelectPoint.x, closestEdgeSelectPoint.y, lastMousePoint.x, lastMousePoint.y);
			}
		} else if(ctool == Tool.EDGE || ctool == Tool.DIRECTED_EDGE) {
			if(edgeBasePoint != null && !drawPreviewUsingObject) {
				int weight = gui.getEdgeOptionsBar().getCurrentLineWeight();
				g2d.setStroke(new BasicStroke(weight));
				g2d.setColor((Color) Preferences.EDGE_PREVIEW_COLOR.getData());
				Point center = edgeBasePoint.getLocation();
				center.x += edgeBasePoint.getRadius();
				center.y += edgeBasePoint.getRadius();
				g2d.drawLine(center.x, center.y, lastMousePoint.x, lastMousePoint.y);
				if(ctool == Tool.DIRECTED_EDGE) {
					double dist = Point.distance(lastMousePoint.x, lastMousePoint.y, center.x, center.y);
					double unitX = - (lastMousePoint.x - center.x) / dist;
					double unitY = - (lastMousePoint.y - center.y) / dist;
					drawArrowTip(g2d, unitX, unitY, lastMousePoint, weight);
				}
			}
		}
		
		// Draw the "selected" look on the selected edge
		if(selection instanceof Edge) {		
			// Graphically mark the edge as selected
			Edge line = (Edge) selection;
			int side = (Integer) Preferences.EDGE_SELECT_SQUARE_SIZE.getData();
			g2d.setColor((Color) Preferences.EDGE_SELECT_SQUARE_COLOR.getData());
			if(line instanceof SimpleEdge) {
				SimpleEdge simpe = (SimpleEdge) line;
				Point2D.Double[] pts = simpe.getBezierPoints();
				g2d.fillRect((int) (pts[0].x - side / 2.0), (int) (pts[0].y - side / 2.0), side, side);
				g2d.fillRect((int) (pts[2].x - side / 2.0), (int) (pts[2].y - side / 2.0), side, side);
				Point2D.Double mid;
				if(pts[1] != null)
					mid = getBezierPoint(pts, 0.5);
				else
					mid = new Point2D.Double((pts[0].x + pts[2].x) / 2.0, (pts[0].y + pts[2].y) / 2.0);
				g2d.fillRect((int) (mid.x - side / 2.0), (int) (mid.y - side / 2.0), side, side);
			}
		}
	}
	
	/** A helper method for drawing the edges between a pair of nodes. 
	 * 
	 * @param g2d      The Graphics2D object we want to draw with.
	 * @param nodePair The pair of nodes we are drawing edges between.
	 * @param edges    The list of edges we need to draw between c1 and c2.
	 * @param preview  The preview edge object.
	 * */
	private static void drawEdgesBetweenNodePair(Graphics2D g2d, Node.Pair nodePair, ArrayList<Edge> edges, Edge preview) {
		// Draw edges between nodes c1 and c2; if edges.size() > 1, draw them as quadratic bezier curves
		double angle = (Double) Preferences.EDGE_SPREAD_ANGLE.getData();
		double lowerAngle = (1 - edges.size()) * angle / 2.0;
		int count = 0; // Keep count of how many edges have been drawn so far
		
		Node n1 = nodePair.getFirst();
		
		// Draw the edges one by one
		Edge e;
		for(int i = 0 ; i < edges.size() ; i++) {
			e = edges.get(i);
			g2d.setStroke(new BasicStroke(e.getWeight()));
			g2d.setColor(e.getColor());
			
			// Split cases by the type of edge
			if(e instanceof SelfEdge) {
				// Draw this self edge (looks like a loop)
				SelfEdge selfe = (SelfEdge) e;
				double offsetAngle = selfe.getOffsetAngle();

				Point nodeCenter = n1.getCenter();

				double centralAngle = (Double) Preferences.SELF_EDGE_SUBTENDED_ANGLE.getData();
				double edgeAngle = (Double) Preferences.SELF_EDGE_ARC_ANGLE.getData();
				double edgeRadius = Math.sin(centralAngle / 2) * n1.getRadius() / Math.sin(edgeAngle / 2);
				double unitX = Math.cos(offsetAngle);
				double unitY = Math.sin(offsetAngle);
				double centralDist = edgeRadius * Math.cos(edgeAngle / 2) + n1.getRadius() * Math.cos(centralAngle / 2);
				double edgeCenterX = centralDist * unitX + nodeCenter.x;
				double edgeCenterY = centralDist * unitY + nodeCenter.y;
				g2d.drawOval((int) (edgeCenterX - edgeRadius), (int) (edgeCenterY - edgeRadius), (int) (2 * edgeRadius), (int) (2 * edgeRadius));
				if(e.isDirected()) {
					double intersectX = n1.getRadius() * unitX * Math.cos(centralAngle / 2) - n1.getRadius() * unitY * Math.sin(centralAngle / 2);
					double intersectY = n1.getRadius() * unitX * Math.sin(centralAngle / 2) + n1.getRadius() * unitY * Math.cos(centralAngle / 2);
					double tanUnitX = intersectX / n1.getRadius();
					double tanUnitY = intersectY / n1.getRadius();
					intersectX += nodeCenter.x;
					intersectY += nodeCenter.y;
					double leftCornerX = intersectX + 5 * e.getWeight() * tanUnitX - 2.5 * e.getWeight() * tanUnitY;
					double leftCornerY = intersectY + 5 * e.getWeight() * tanUnitY - 2.5 * e.getWeight() * (- tanUnitX);
					Point rightCorner = new Point((int) (leftCornerX + 5 * e.getWeight() * tanUnitY), (int) (leftCornerY + 5 * e.getWeight() * (- tanUnitX)));
					g2d.fillPolygon(new int[] {(int) intersectX, (int) leftCornerX, rightCorner.x}, new int[] {(int) intersectY, (int) leftCornerY, rightCorner.y}, 3);
				}
				selfe.setCenter(new Point2D.Double(edgeCenterX, edgeCenterY));
				selfe.setRadius(edgeRadius);
				count++;
			} else if(e instanceof SimpleEdge) {
				SimpleEdge simpe = (SimpleEdge) e;
				// The edge is either a line or bezier curve; either way, they get drawn the same way
				Node[] ends = e.getEndpoints();
				double initAngle = lowerAngle + count * angle;
				if(ends[0] != n1)
					initAngle = -initAngle;
				Point p1 = ends[1].getCenter();
				Point p2 = ends[0].getCenter();
				
				// Reciprocal of distance between the centers of the nodes
				double dist = 1 / Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
				
				// Compute components of vectors pointing from one node to the other
				// The length of the vectors is the radius of the node they point from
				double radiusVectorX1 = ends[1].getRadius() * dist * (p2.x - p1.x);
				double radiusVectorY1 = ends[1].getRadius() * dist * (p2.y - p1.y);
				double radiusVectorX2 = ends[0].getRadius() * dist * (p1.x - p2.x);
				double radiusVectorY2 = ends[0].getRadius() * dist * (p1.y - p2.y);
				
				// If the index corresponds to the "center" edge, it is drawn as a straight line.
				if(edges.size() % 2 == 1 && i == edges.size() / 2) {
					// Draw the line
					double linep1X = radiusVectorX1 + p1.x;
					double linep1Y = radiusVectorY1 + p1.y;
					double linep2X = radiusVectorX2 + p2.x;
					double linep2Y = radiusVectorY2 + p2.y;
					g2d.drawLine((int) linep2X, (int) linep2Y, (int) linep1X, (int) linep1Y);
					
					// Set the control point to a negative value to indicate it does not exist
					if(preview == null)
						simpe.setPoints(linep1X, linep1Y, -1, -1, linep2X, linep2Y);
					
					// If this edge is directed, draw the arrow
					if(e.isDirected()) {
						Point tip = new Point((int) radiusVectorX2 + p2.x, (int) radiusVectorY2 + p2.y);
						drawArrowTip(g2d, radiusVectorX2 / ends[1].getRadius(), radiusVectorY2 / ends[1].getRadius(), tip, e.getWeight());
					}
				} else {
					// Rotate the radius vectors by an angle; this is how the curved edges are separated
					double circ1X = radiusVectorX1 * Math.cos(initAngle) + radiusVectorY1 * Math.sin(initAngle);
					double circ1Y = radiusVectorY1 * Math.cos(initAngle) - radiusVectorX1 * Math.sin(initAngle);
					double circ2X = radiusVectorX2 * Math.cos(initAngle) - radiusVectorY2 * Math.sin(initAngle);
					double circ2Y = radiusVectorX2 * Math.sin(initAngle) + radiusVectorY2 * Math.cos(initAngle);
					
					if(e.isDirected()) {
						// Draw the triangular tip of the arrow if the edge is directed
						double unitX = circ2X / ends[0].getRadius();
						double unitY = circ2Y / ends[0].getRadius();
						Point tip = new Point((int) circ2X + p2.x, (int) circ2Y + p2.y);
						drawArrowTip(g2d, unitX, unitY, tip, e.getWeight());
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
					if(preview == null)
						simpe.setPoints(circ1X, circ1Y, controlX, controlY, circ2X, circ2Y);
				}
				count++;
			}
		}		
	}
	
	/**
	 * A helper method for drawing the tip of a directed edge (a triangle) given the
	 * direction in which the edge is pointing (unit vector), the locatin of the arrow's tip,
	 * and the weight of the edge.
	 * 
	 * @param g2d         The Graphics2D object which will draw the tip.
	 * @param unitVectorX The x component of the (opposite) direction the edge is pointing.
	 * @param unitVectorY The y component of the (opposite) direction the edge is pointing.
	 * @param tip         The Point object representing the coordinates of the edge's tip.
	 * @param weight      The weight of the edge.
	 */
	private static void drawArrowTip(Graphics2D g2d, double unitVectorX, double unitVectorY, Point tip, int weight) {
		double leftCornerX = tip.x + 5 * weight * unitVectorX - 2.5 * weight * unitVectorY;
		double leftCornerY = tip.y + 5 * weight * unitVectorY - 2.5 * weight * (- unitVectorX);
		double rightCornerX = leftCornerX + 5 * weight * unitVectorY;
		double rightCornerY = leftCornerY + 5 * weight * (- unitVectorX); 
		g2d.fillPolygon(new int[] {tip.x, (int) leftCornerX, (int) rightCornerX}, new int[] {tip.y, (int) leftCornerY, (int) rightCornerY}, 3);
	}
	
	/** 
	 * A helper method for obtaining the point on the given quadratic bezier at the parameter 0 <= t <= 1.
	 * 
	 * @param coords The array of Points which define the quadratic bezier.
	 * @param t      The parameter t where 0 <= t <= 1.
	 * @return       The Point on the bezier corresponding to the given value of t.
	 */
	private static Point2D.Double getBezierPoint(Point2D.Double[] coords, double t) {
		return new Point2D.Double(
			(1 - t)*(1 - t)*coords[0].x + 2*(1 - t)*t*coords[1].x + t*t*coords[2].x,
			(1 - t)*(1 - t)*coords[0].y + 2*(1 - t)*t*coords[1].y + t*t*coords[2].y
		);
	}
	
}
