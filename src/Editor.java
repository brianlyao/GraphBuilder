import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.*;

public class Editor extends JPanel{
	private static final long serialVersionUID = 7327691115632869820L;
	
	private GUI gui;
	private Point lastMousePoint;
	private boolean isDrawing;
	private Point closestEdgeSelectPoint;
	private Line closestEdge;
	
	public Editor(GUI g){
		super();
		gui = g;
		lastMousePoint = new Point(0, 0);
		isDrawing = false;
		
		// Initialize with default settings...
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(2048, 2048));
		addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Tool current = gui.getCurrentTool();
				if(gui.getSelection() != null && current != Tool.EDGE_SELECT){
					gui.getSelection().setSelected(false);
					gui.getSelection().repaint();
					gui.setSelection(null);
				}
				if(current == Tool.NODE){
					Color[] colors = gui.getCurrentCircleColors();
					int currentRadius = gui.getCurrentRadius();
					Circle c = new Circle(Math.max(0, lastMousePoint.x - currentRadius), Math.max(0, lastMousePoint.y - currentRadius), gui.getCurrentRadius(),
							String.format("text%d", gui.currentID()), colors[0], colors[1], colors[2], gui);
					gui.addCircle(c);
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
				isDrawing = true;
				Tool currentTool = gui.getCurrentTool();
				if(currentTool == Tool.EDGE_SELECT && closestEdge != null){
					gui.setSelection(closestEdge);
					closestEdge.setSelected(true);
				}
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				isDrawing = false;
			}
		});
		addMouseMotionListener(new MouseMotionListener(){
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
				if(gui.getCurrentTool() == Tool.NODE)
					repaint();
				
				if(gui.getCurrentTool() == Tool.EDGE_SELECT){
					// Compute the line and distance to the closest edge 
					Point2D.Double clickd = new Point2D.Double(lastMousePoint.x, lastMousePoint.y); // Move point
					
					// Iteratively updated variables for determining the closest edge
					closestEdgeSelectPoint = null;
					closestEdge = null;
					double minDistance = Double.MAX_VALUE;
					
					// Get the edgemap
					HashMap<Circle, HashMap<Circle, HashSet<Line>>> em = gui.getEdgeMap();
					
					// Iterate through the edgemap
					for(Circle first : em.keySet()){
						for(Circle second : em.get(first).keySet()){
							// Get set of edges between first and second
							HashSet<Line> betweenTwo = em.get(first).get(second);
							
							// Iterate through edges between first and second
							for(Line l : betweenTwo){
								double dist = Double.MAX_VALUE;
								Point closest = null;
								Point2D.Double[] bcp;
								switch(l.getType()){
									case Line.LINE:
										bcp = l.getBezierPoints(); // Only the endpoints of the line are stored...
										Point2D.Double c1 = bcp[0];
										Point2D.Double c2 = bcp[2];
										if(c2.x != c1.x && c2.y != c1.y){
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
											for(int i = 1 ; i < dists.length ; i++){
												if(dists[i] < dist){
													dist = dists[i];
													minind = i;
												}
											}
											closest = new Point((int) points[minind].x, (int) points[minind].y);
										}else if(c2.x == c1.x){
											// If the line happens to be vertical...
											closest = new Point((int) c1.x, lastMousePoint.y);
											dist = Math.abs(c1.x - clickd.x);
										}else{
											// If the line happens to be horizontal...
											closest = new Point(lastMousePoint.x, (int) c1.y);
											dist = Math.abs(c1.y - clickd.y);
										}
										break;
									case Line.CURVE:
										bcp = l.getBezierPoints();
										
										// To get the closest point on a bezier curve, we're going to need to solve a cubic...
										// Here, we compute the coefficients of the cubic 
										double a = bcp[0].x;
										double b = bcp[0].y;
										double c = bcp[1].x;
										double d = bcp[1].y;
										double e = bcp[2].x;
										double f = bcp[2].y;
										double x = clickd.x;
										double y = clickd.y;
										double n1 = (a - 2*c + e)*(a - 2*c + e) + (b - 2*d + f)*(b - 2*d + f);
										double n2 = -3*((a - c)*(a - 2*c + e) + (b - d)*(b - 2*d + f));
										double n3 = a*(3*a - x + e) - 2*c*(3*a - c - x) - e*x + b*(3*b - y + f) - 2*d*(3*b - d - y) - f*y;
										double n4 = (c - a)*(a - x) + (d - b)*(b - y);
										
										// Compute the roots of the equation n1x^3 + n2x^2 + n3x + n4 = 0
										Complex[] roots = new Complex[3];
										Complex disc0 = new Complex(n2*n2 - 3*n1*n3);
										Complex disc1 = new Complex(2*n2*n2*n2 - 9*n1*n2*n3 + 27*n1*n1*n4);
										Complex incbrt = null;
										if(disc0.isZero()){
											incbrt = disc1;
											if(disc1.getReal() < 0)
												incbrt = incbrt.neg();
										}else{
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
										for(Point2D.Double pt : candidateClosestPoints){
											if((tempdist = pt.distance(clickd)) < dist){
												dist = tempdist; // Set the distance from the cursor for this edge
												closest = new Point((int) pt.x, (int) pt.y);
											}
										}
										break;
									case Line.LOOP:
										Point2D.Double cen = l.getCenter();
										
										// Since a loop is a (part of a) circle, the math for determining the point closest to the mouse is simple
										dist = Math.abs(l.getRadius() - Math.sqrt((cen.x - clickd.x) * (cen.x - clickd.x) + (cen.y - clickd.y) * (cen.y - clickd.y)));
										
										// Determine closest point on the loop to the cursor
										double distcen = Math.sqrt((cen.x - clickd.x) * (cen.x - clickd.x) + (cen.y - clickd.y) * (cen.y - clickd.y));
										double closex = (clickd.x - cen.x) * l.getRadius() / distcen + cen.x;
										double closey = (clickd.y - cen.y) * l.getRadius() / distcen + cen.y;
										closest = new Point((int) closex, (int) closey);
										break;
									default:
										dist = Double.MAX_VALUE;
								}
								
								// Update the closest edge; once the distances to all edges is computed, closestEdge will contain the closest edge
								if(dist < minDistance){
									minDistance = dist;
									closestEdge = l;
									closestEdgeSelectPoint = closest;
								}
//								closestPoints.add(closest); //debug
							}
						}
					}
//					gui.setSelection(closestEdge);
//					if(closestEdge != null)
//						closestEdge.setSelected(true);
				}
			}
		});
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		// Explicitly set the position of the nodes; this allows the pane to be scrollable while retaining the position of the circles relative to the top left corner of the editor panel
		for(Circle c : gui.getCircles())
			c.setLocation(c.getCoords());
		
		// Draw all edges
		HashMap<Circle, HashMap<Circle, HashSet<Line>>> edgeMap = gui.getEdgeMap();
		HashSet<Line> edges;
		for(Circle first : edgeMap.keySet()){
			for(Circle second : edgeMap.get(first).keySet()){
				edges = edgeMap.get(first).get(second);
				drawNEdges(g2d, first, second, edges, edges.size()); // Draw the edges between every pair of nodes
			}
		}
		
		// Draw tool-specific graphics
		Tool ctool = gui.getCurrentTool();
		if(ctool == Tool.NODE){
			// Draw preview for the Circle tool
			g2d.setColor((Color) Preferences.CIRCLE_PREVIEW_COLOR.getData());
			Ellipse2D.Double preview = new Ellipse2D.Double(lastMousePoint.x - gui.getCurrentRadius(),
					lastMousePoint.y - gui.getCurrentRadius(), 2*gui.getCurrentRadius(), 2*gui.getCurrentRadius());
			g2d.draw(preview);
		}else if(ctool == Tool.EDGE_SELECT){
			if(closestEdgeSelectPoint != null){
				// Draw the edge select preview
				g2d.setColor((Color) Preferences.EDGE_SELECT_PREVIEW_COLOR.getData());
				g2d.drawLine(closestEdgeSelectPoint.x, closestEdgeSelectPoint.y, lastMousePoint.x, lastMousePoint.y);
			}
		}
		
		// Draw the "selected" look on the selected edge
		if(gui.getSelection() instanceof Line){		
			// Graphically mark the edge as selected
			Line line = (Line) gui.getSelection();
			int side = (Integer) Preferences.EDGE_SELECT_SQUARE_SIZE.getData();
			g2d.setColor((Color) Preferences.EDGE_SELECT_SQUARE_COLOR.getData());
			if(line.getType() == Line.LINE || line.getType() == Line.CURVE){
				Point2D.Double[] pts = line.getBezierPoints();
				g2d.fillRect((int) (pts[0].x - side / 2), (int) (pts[0].y - side / 2), side, side);
				g2d.fillRect((int) (pts[2].x - side / 2), (int) (pts[2].y - side / 2), side, side);
				Point2D.Double mid;
				if(pts[1] != null)
					mid = getBezierPoint(pts, 0.5);
				else
					mid = new Point2D.Double((pts[0].x + pts[2].x) / 2.0, (pts[0].y + pts[2].y) / 2.0);
				g2d.fillRect((int) (mid.x - side / 2.0), (int) (mid.y - side / 2.0), side, side);
			}
		}
		repaint(); // Need to find an alternative to this
	}
	
	private void drawNEdges(Graphics2D g2d, Circle c1, Circle c2, HashSet<Line> edges, int numEdges){
		// Draw edges between nodes c1 and c2; if numEdges > 1, draw them as quadratic bezier curves
		double angle = (Double) Preferences.EDGE_SPREAD_ANGLE.getData();
		double lowerAngle = (1 - numEdges) * angle / 2;
		int count = 0;
		double defaultAngle = 0;
		double offsetAngle = (Double) Preferences.SELF_EDGE_OFFSET_ANGLE.getData();
		
		for(Line e : edges){
			g2d.setStroke(new BasicStroke(e.getWeight()));
			g2d.setColor(e.getColor());
			if(c1 == c2){
				// If the endpoints are identical, then this is a self-edge; draw a loop
				Point nodeCenter = c1.getCenter();
				defaultAngle = count * offsetAngle;
				double centralAngle = (Double) Preferences.SELF_EDGE_SUBTENDED_ANGLE.getData();
				double edgeAngle = (Double) Preferences.SELF_EDGE_ARC_ANGLE.getData();
				double edgeRadius = Math.sin(centralAngle / 2) * c1.getRadius() / Math.sin(edgeAngle / 2);
				double unitX = Math.cos(defaultAngle);
				double unitY = Math.sin(defaultAngle);
				double centralDist = edgeRadius * Math.cos(edgeAngle / 2) + c1.getRadius() * Math.cos(centralAngle / 2);
				double edgeCenterX = centralDist * unitX + nodeCenter.x;
				double edgeCenterY = centralDist * unitY + nodeCenter.y;
				g2d.drawOval((int) (edgeCenterX - edgeRadius), (int) (edgeCenterY - edgeRadius), (int) (2 * edgeRadius), (int) (2 * edgeRadius));
				if(e instanceof Arrow){
					double intersectX = c1.getRadius() * unitX * Math.cos(centralAngle / 2) - c1.getRadius() * unitY * Math.sin(centralAngle / 2);
					double intersectY = c1.getRadius() * unitX * Math.sin(centralAngle / 2) + c1.getRadius() * unitY * Math.cos(centralAngle / 2);
					double tanUnitX = intersectX / c1.getRadius();
					double tanUnitY = intersectY / c1.getRadius();
					intersectX += nodeCenter.x;
					intersectY += nodeCenter.y;
					double leftCornerX = intersectX + 5 * e.getWeight() * tanUnitX - 2.5 * e.getWeight() * tanUnitY;
					double leftCornerY = intersectY + 5 * e.getWeight() * tanUnitY - 2.5 * e.getWeight() * (- tanUnitX);
					Point rightCorner = new Point((int) (leftCornerX + 5 * e.getWeight() * tanUnitY), (int) (leftCornerY + 5 * e.getWeight() * (- tanUnitX)));
					g2d.fillPolygon(new int[] {(int) intersectX, (int) leftCornerX, rightCorner.x}, new int[] {(int) intersectY, (int) leftCornerY, rightCorner.y}, 3);
				}
				e.setCenter(new Point2D.Double(edgeCenterX, edgeCenterY));
				e.setRadius(edgeRadius);
				e.setType(Line.LOOP);
				count++;
			}else{
				// The edge is either a line or bezier curve; either way, they get drawn the same way
				Circle[] ends = e.getEndpoints();
				double initAngle = lowerAngle + count * angle;
				if(ends[0] != c1)
					initAngle = -initAngle;
				Point p1 = ends[1].getCenter();
				Point p2 = ends[0].getCenter();
				double dist = 1 / Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
				double unitX1 = ends[1].getRadius() * dist * (p2.x - p1.x);
				double unitY1 = ends[1].getRadius() * dist * (p2.y - p1.y);
				double unitX2 = ends[0].getRadius() * dist * (p1.x - p2.x);
				double unitY2 = ends[0].getRadius() * dist * (p1.y - p2.y);
				double circ1X = unitX1 * Math.cos(initAngle) + unitY1 * Math.sin(initAngle);
				double circ1Y = unitY1 * Math.cos(initAngle) - unitX1 * Math.sin(initAngle);
				double circ2X = unitX2 * Math.cos(initAngle) - unitY2 * Math.sin(initAngle);
				double circ2Y = unitX2 * Math.sin(initAngle) + unitY2 * Math.cos(initAngle);
				if(e instanceof Arrow){
					double unitX = circ2X / ends[0].getRadius();
					double unitY = circ2Y / ends[0].getRadius();
					Point tip = new Point((int) circ2X + p2.x, (int) circ2Y + p2.y);
					double leftCornerX = tip.x + 5 * e.getWeight() * unitX - 2.5 * e.getWeight() * unitY;
					double leftCornerY = tip.y + 5 * e.getWeight() * unitY - 2.5 * e.getWeight() * (- unitX);
					Point rightCorner = new Point((int) (leftCornerX + 5 * e.getWeight() * unitY), (int) (leftCornerY + 5 * e.getWeight() * (- unitX)));
					g2d.fillPolygon(new int[] {tip.x, (int) leftCornerX, rightCorner.x}, new int[] {tip.y, (int) leftCornerY, rightCorner.y}, 3);
				}
				double slope1 = circ1Y / circ1X;
				double slope2 = circ2Y / circ2X;
				circ1X += p1.x;
				circ1Y += p1.y;
				circ2X += p2.x;
				circ2Y += p2.y;
				double controlX = (slope2 * circ2X - slope1 * circ1X + circ1Y - circ2Y) / (slope2 - slope1);
				double controlY = slope1 * (controlX - circ1X) + circ1Y; //Compute control point for bezier
				QuadCurve2D curve = new QuadCurve2D.Double(circ1X, circ1Y, controlX, controlY, circ2X, circ2Y);
				e.setPoints(circ1X, circ1Y, controlX, controlY, circ2X, circ2Y);
				if(numEdges == 1)
					e.setType(Line.LINE);
				else
					e.setType(Line.CURVE);
				g2d.draw(curve);
				count++;
			}
		}		
	}
	
	private Point2D.Double getBezierPoint(Point2D.Double[] coords, double t){
		return new Point2D.Double(
			(1 - t)*(1 - t)*coords[0].x + 2*(1 - t)*t*coords[1].x + t*t*coords[2].x,
			(1 - t)*(1 - t)*coords[0].y + 2*(1 - t)*t*coords[1].y + t*t*coords[2].y
		);
	}
}
