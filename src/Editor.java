import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.*;

public class Editor extends JPanel{
	private GUI gui;
	private Point lastMousePoint;
	private boolean isDrawing;
	
	public Editor(GUI g){
		super();
		gui = g;
		lastMousePoint = new Point(0, 0);
		isDrawing = false;
		
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(2048, 2048));
		addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(gui.getSelection() != null){
					gui.getSelection().setSelected(false);
					gui.getSelection().repaint();
					gui.setSelection(null);
				}
				Tool current = gui.getCurrentTool();
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
			}
		});
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		if(gui.getCurrentTool() == Tool.NODE){
			g2d.setColor((Color) Preferences.CIRCLE_PREVIEW_COLOR.getData());
			Ellipse2D.Double preview = new Ellipse2D.Double(lastMousePoint.x - gui.getCurrentRadius(),
					lastMousePoint.y - gui.getCurrentRadius(), 2*gui.getCurrentRadius(), 2*gui.getCurrentRadius());
			g2d.draw(preview);
		}
		for(Circle c : gui.getCircles())
			c.setLocation(c.getCoords());
		HashMap<Circle, HashMap<Circle, HashSet<Line>>> edgeMap = gui.getEdgeMap();
		HashSet<Line> edges;
		for(Circle first : edgeMap.keySet()){
			for(Circle second : edgeMap.get(first).keySet()){
				edges = edgeMap.get(first).get(second);
				drawNEdges(g2d, first, second, edges, edges.size());
			}
		}
		repaint(); // Need to find an alternative to this
	}
	
	private void drawNEdges(Graphics2D g2d, Circle c1, Circle c2, HashSet<Line> edges, int numEdges){
		Circle[] ends;
		Point p1, p2, tip, rightCorner;
		double f1, unitX, unitY, leftCornerX, leftCornerY;
		if(numEdges == 1){
			for(Line e : edges){
				g2d.setStroke(new BasicStroke(e.getWeight()));
				g2d.setColor(e.getColor());
				if(c1 == c2){
					Point nodeCenter = c1.getCenter();
					double defaultAngle = 0;
					double centralAngle = (Double) Preferences.SELF_EDGE_SUBTENDED_ANGLE.getData();
					double edgeAngle = (Double) Preferences.SELF_EDGE_ARC_ANGLE.getData();
					double edgeRadius = Math.sin(centralAngle / 2) * c1.getRadius() / Math.sin(edgeAngle / 2);
					unitX = Math.cos(defaultAngle);
					unitY = Math.sin(defaultAngle);
					double centralDist = edgeRadius * Math.cos(edgeAngle / 2) + c1.getRadius() * Math.cos(centralAngle / 2);
					double edgeCenterX = centralDist * unitX + nodeCenter.x;
					double edgeCenterY = centralDist * unitY + nodeCenter.y;
					g2d.drawOval((int) (edgeCenterX - edgeRadius), (int) (edgeCenterY - edgeRadius), (int) (2 * edgeRadius), (int) (2 * edgeRadius));
					if(e instanceof Arrow){
						double intersectX = c1.getRadius() * unitX * Math.cos(centralAngle / 2) - c1.getRadius() * unitY * Math.sin(centralAngle / 2);
						double intersectY = c1.getRadius() * unitX * Math.sin(centralAngle / 2) + c1.getRadius() * unitY * Math.cos(centralAngle / 2);
						double ang = Math.atan(intersectY / intersectX);
						intersectX += nodeCenter.x;
						intersectY += nodeCenter.y;
						double tanUnitX = Math.cos(ang);
						double tanUnitY = Math.sin(ang);
						leftCornerX = intersectX + 5 * e.getWeight() * tanUnitX - 2.5 * e.getWeight() * tanUnitY;
						leftCornerY = intersectY + 5 * e.getWeight() * tanUnitY - 2.5 * e.getWeight() * (- tanUnitX);
						rightCorner = new Point((int) (leftCornerX + 5 * e.getWeight() * tanUnitY), (int) (leftCornerY + 5 * e.getWeight() * (- tanUnitX)));
						g2d.fillPolygon(new int[] {(int) intersectX, (int) leftCornerX, rightCorner.x}, new int[] {(int) intersectY, (int) leftCornerY, rightCorner.y}, 3);
					}
				}else{
					ends = e.getEndpoints();
					p1 = ends[1].getCenter();
					p2 = ends[0].getCenter();
					if(e instanceof Arrow){
						f1 = 1 / Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
						unitX = f1 * (p1.x - p2.x);
						unitY = f1 * (p1.y - p2.y);
						tip = new Point((int) (ends[0].getRadius() * unitX) + p2.x, (int) (ends[0].getRadius() * unitY) + p2.y);
						leftCornerX = tip.x + 5 * e.getWeight() * unitX - 2.5 * e.getWeight() * unitY;
						leftCornerY = tip.y + 5 * e.getWeight() * unitY - 2.5 * e.getWeight() * (- unitX);
						rightCorner = new Point((int) (leftCornerX + 5 * e.getWeight() * unitY), (int) (leftCornerY + 5 * e.getWeight() * (- unitX)));
						g2d.fillPolygon(new int[] {tip.x, (int) leftCornerX, rightCorner.x}, new int[] {tip.y, (int) leftCornerY, rightCorner.y}, 3);
					}
					g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
					e.setPoints(p1.x, p1.y, Double.NaN, Double.NaN, p2.x, p2.y);
				}
			}
		}else{
			double angle = (Double) Preferences.EDGE_SPREAD_ANGLE.getData();
//			System.out.println("Angle: " + angle);
			double lowerAngle = (1 - numEdges) * angle / 2;
			double initAngle, dist, circ1X, circ1Y, circ2X, circ2Y, slope1, slope2, controlX, controlY;
			QuadCurve2D.Double curve;
			
			int count = 0;
			double defaultAngle = 0;
			double offsetAngle = (Double) Preferences.SELF_EDGE_OFFSET_ANGLE.getData();
			for(Line e : edges){
				g2d.setStroke(new BasicStroke(e.getWeight()));
				g2d.setColor(e.getColor());
				if(c1 == c2){
					Point nodeCenter = c1.getCenter();
					defaultAngle = count * offsetAngle;
					double centralAngle = (Double) Preferences.SELF_EDGE_SUBTENDED_ANGLE.getData();
					double edgeAngle = (Double) Preferences.SELF_EDGE_ARC_ANGLE.getData();
					double edgeRadius = Math.sin(centralAngle / 2) * c1.getRadius() / Math.sin(edgeAngle / 2);
					unitX = Math.cos(defaultAngle);
					unitY = Math.sin(defaultAngle);
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
						leftCornerX = intersectX + 5 * e.getWeight() * tanUnitX - 2.5 * e.getWeight() * tanUnitY;
						leftCornerY = intersectY + 5 * e.getWeight() * tanUnitY - 2.5 * e.getWeight() * (- tanUnitX);
						rightCorner = new Point((int) (leftCornerX + 5 * e.getWeight() * tanUnitY), (int) (leftCornerY + 5 * e.getWeight() * (- tanUnitX)));
						g2d.fillPolygon(new int[] {(int) intersectX, (int) leftCornerX, rightCorner.x}, new int[] {(int) intersectY, (int) leftCornerY, rightCorner.y}, 3);
					}
					count++;
				}else{
					ends = e.getEndpoints();
					initAngle = lowerAngle + count * angle;
					if(ends[0] != c1)
						initAngle = -initAngle;
					p1 = ends[1].getCenter();
					p2 = ends[0].getCenter();
					dist = 1 / Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
					double unitX1 = ends[1].getRadius() * dist * (p2.x - p1.x);
					double unitY1 = ends[1].getRadius() * dist * (p2.y - p1.y);
					double unitX2 = ends[0].getRadius() * dist * (p1.x - p2.x);
					double unitY2 = ends[0].getRadius() * dist * (p1.y - p2.y);
//					System.out.printf("Circle 1 between radius: (%f, %f)\n", unitX1, unitY1);
//					System.out.printf("Circle 2 between radius: (%f, %f)\n", unitX2, unitY2);
					circ1X = unitX1 * Math.cos(initAngle) + unitY1 * Math.sin(initAngle);
					circ1Y = unitY1 * Math.cos(initAngle) - unitX1 * Math.sin(initAngle);
//					System.out.printf("Circle 1 radius: (%f, %f)\n", circ1X, circ1Y);
					circ2X = unitX2 * Math.cos(initAngle) - unitY2 * Math.sin(initAngle);
					circ2Y = unitX2 * Math.sin(initAngle) + unitY2 * Math.cos(initAngle);
//					System.out.printf("Circle 2 radius: (%f, %f)\n", circ2X, circ2Y);
					if(e instanceof Arrow){
						unitX = circ2X / ends[0].getRadius();
						unitY = circ2Y / ends[0].getRadius();
						tip = new Point((int) circ2X + p2.x, (int) circ2Y + p2.y);
						leftCornerX = tip.x + 5 * e.getWeight() * unitX - 2.5 * e.getWeight() * unitY;
						leftCornerY = tip.y + 5 * e.getWeight() * unitY - 2.5 * e.getWeight() * (- unitX);
						rightCorner = new Point((int) (leftCornerX + 5 * e.getWeight() * unitY), (int) (leftCornerY + 5 * e.getWeight() * (- unitX)));
						g2d.fillPolygon(new int[] {tip.x, (int) leftCornerX, rightCorner.x}, new int[] {tip.y, (int) leftCornerY, rightCorner.y}, 3);
					}
					slope1 = circ1Y / circ1X;
					slope2 = circ2Y / circ2X;
//					System.out.printf("Slope 1 : %f, Slope 2 : %f", slope1, slope2);
					circ1X += p1.x;
					circ1Y += p1.y;
					circ2X += p2.x;
					circ2Y += p2.y;
					controlX = (slope2 * circ2X - slope1 * circ1X + circ1Y - circ2Y) / (slope2 - slope1);
					controlY = slope1 * (controlX - circ1X) + circ1Y;
//					System.out.printf("Control point: (%f, %f)\n", controlX, controlY);
					curve = new QuadCurve2D.Double(circ1X, circ1Y, controlX, controlY, circ2X, circ2Y);
					e.setPoints(circ1X, circ1Y, controlX, controlY, circ2X, circ2Y);
					g2d.draw(curve);
					count++;
				}
			}
		}
		
	}
}
