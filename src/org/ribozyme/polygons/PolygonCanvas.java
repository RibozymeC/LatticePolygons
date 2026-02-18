package org.ribozyme.polygons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.ribozyme.util.Line;
import org.ribozyme.util.Point;

public class PolygonCanvas extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	static final int SIZE = 512;
	
	static final int[] GRIDLEVELS = {4, 8, 16, 32, 64, 128};
	
	int GRID_LEN = 8;
	int GRID_CELL = SIZE / GRID_LEN;
	
	LinkedList<Line> lines;
	
	public PolygonCanvas() {
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(SIZE, SIZE));
		
		lines = new LinkedList<>();
		
		MouseInput mouse = new MouseInput();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		addMouseWheelListener(mouse);
	}
	
	/*
	void changeBasis(Node node) {
		point_map = node.point_map();
		base1 = node.x();
		base2 = node.y();
	}
	*/
	
	List<Line> getLines() {
		return lines;
	}
	
	// point -> screen
	int p2s(double x) {
		return (int) ((x + GRID_LEN / 2) * GRID_CELL);
	}
	
	// screen -> point
	// rounds to nearest point
	int s2p(int x) {
		return (x + GRID_CELL / 2) / GRID_CELL - GRID_LEN / 2;
	}
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		g2d.clearRect(0, 0, SIZE, SIZE);
		
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.setStroke(new BasicStroke(0.5f));
		
		for(int a = -400; a <= 400; a++)
			for(int b = -400; b <= 400; b++) {
				//double x = a * base1[0] + b * base2[0];
				//double y = a * base1[1] + b * base2[1];
				int px = p2s(a);
				int py = p2s(b);
				if(px >= 0 && px <= SIZE && py >= 0 && py <= SIZE)
					g2d.fillOval(px - 2, py - 2, 4, 4);
			}
		
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(1.0f));
		
		for(Line line: lines) {
			Point a = line.a(), b = line.b();
			g2d.drawLine(p2s(a.x()), p2s(a.y()), p2s(b.x()), p2s(b.y()));
		}
		
		g2d.setColor(Color.RED);
		Set<Point> points = new HashSet<>();
		for(Line line: lines) {
			points.addAll(line.points());
		}
		for(Point p: points) {
			g2d.fillOval(p2s(p.x()) - 3, p2s(p.y()) - 3, 6, 6);
		}
	}
	
	class MouseInput extends MouseAdapter
	{
		static double distance(int x1, int y1, int x2, int y2, int xp, int yp)
		{
			int n = (x2 - x1) * (xp - x1) + (y2 - y1) * (yp - y1);
			int m = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
			
			double λ = (double)n / (double)m;
			if(λ >= 0.0 && λ <= 1.0) {
				double lx = x1 + λ * (x2 - x1);
				double ly = y1 + λ * (y2 - y1);
				return Math.hypot(lx - xp, ly - yp);
			}
			
			return Math.min(Math.hypot(x1 - xp, y1 - yp), Math.hypot(x2 - xp, y2 - yp));
		}
		
		@Override
		public void mouseClicked(MouseEvent e)
		{
			int x = e.getX();
			int y = e.getY();
			
			if(SwingUtilities.isRightMouseButton(e)) {
				double min_dist = Double.POSITIVE_INFINITY;
				Line min_line = null;
				
				for(Line line: lines) {
					Point a = line.a(), b = line.b();
					double dist = distance(p2s(a.x()), p2s(a.y()), p2s(b.x()), p2s(b.y()), x, y);
					if(dist < min_dist) {
						min_dist = dist;
						min_line = line;
					}
				}
				
				if(min_line != null && min_dist < GRID_CELL)
					lines.remove(min_line);
			}
			
			repaint();
			firePropertyChange("changeLines", false, true);
		}
		
		@Override
		public void mousePressed(MouseEvent e)
		{
			int x = s2p(e.getX());
			int y = s2p(e.getY());
			
			if(SwingUtilities.isLeftMouseButton(e)) {
				Point p = new Point(x, y);
				lines.add(new Line(p, p));
			}
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if(SwingUtilities.isLeftMouseButton(e)) {
				Line last = lines.pollLast();
				Line lastx = new Line(last.b(), last.a());
				if(lines.contains(last)) {
					lines.remove(last);
				}
				else if(lines.contains(lastx)) {
					lines.remove(lastx);
				}
				else if(!last.a().equals(last.b())) {
					lines.addLast(last);
				}
			}
			
			repaint();
			firePropertyChange("changeLines", false, true);
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			int x = s2p(e.getX());
			int y = s2p(e.getY());
			
			boolean changed = false;
			
			if(SwingUtilities.isLeftMouseButton(e)) {
				Line last = lines.peekLast();
				Point old_end = last.b();
				Point new_end = new Point(x, y);
				if(!old_end.equals(new_end)) {
					lines.pollLast();
					lines.addLast(new Line(last.a(), new_end));
					changed = true;
				}
			}
			
			if(changed)
				repaint();
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e)
		{
			int clicks = e.getWheelRotation();
			int ix = Arrays.binarySearch(GRIDLEVELS, GRID_CELL);
			
			if(clicks < 0 && ix < GRIDLEVELS.length - 1) {
				GRID_CELL = GRIDLEVELS[++ix];
			}
			if(clicks > 0 && ix > 0) {
				GRID_CELL = GRIDLEVELS[--ix];
			}
			GRID_LEN = SIZE / GRID_CELL;
			repaint();
		}
	}
}
