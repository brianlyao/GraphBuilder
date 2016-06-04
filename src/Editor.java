import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

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
//				repaint();
			}
			@Override
			public void mouseMoved(MouseEvent arg0) {
				lastMousePoint = arg0.getPoint();
//				repaint();
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
//		if(gui.getRedrawLine()){
		Circle[] ends;
		Point p1, p2;
		for(Line l : gui.getLines()){
			g2d.setStroke(new BasicStroke(l.getWeight()));
			g2d.setColor(l.getColor());
			ends = l.getEndpoints();
			p1 = ends[0].getCenter();
			p2 = ends[1].getCenter();
			g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
		gui.setRedrawLine(false);
//		}
		Point tip, rightCorner;
		double f1, f2, unitX, unitY, leftCornerX, leftCornerY;
		for(Arrow a : gui.getArrows()){
			g2d.setStroke(new BasicStroke(a.getWeight()));
			g2d.setColor(a.getColor());
			ends = a.getEndpoints();
			p1 = ends[1].getCenter();
			p2 = ends[0].getCenter();
			f1 = 1 / Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
			unitX = f1 * (p1.x - p2.x);
			unitY = f1 * (p1.y - p2.y);
			tip = new Point((int) (ends[0].getRadius() * unitX) + p2.x, (int) (ends[0].getRadius() * unitY) + p2.y);
			f2 = 1 / Math.sqrt(p2.x * p2.x + p2.y * p2.y);
			leftCornerX = tip.x + 5 * a.getWeight() * unitX - 2.5 * a.getWeight() * unitY;
			leftCornerY = tip.y + 5 * a.getWeight() * unitY - 2.5 * a.getWeight() * (- unitX);
			rightCorner = new Point((int) (leftCornerX + 5 * a.getWeight() * unitY), (int) (leftCornerY + 5 * a.getWeight() * (- unitX)));
			g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
			g2d.fillPolygon(new int[] {tip.x, (int) leftCornerX, rightCorner.x}, new int[] {tip.y, (int) leftCornerY, rightCorner.y}, 3);
		}
		repaint();
	}
}
