package org.ribozyme.polygons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class TreePanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	static final int WIDTH = 600;
	static final int HEIGHT = 600;
	
	Node tree = null;
	
	List<Area> clickable;
	
	public TreePanel()
	{
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		clickable = new ArrayList<>();
		
		MouseInput mouse = new MouseInput();
		addMouseListener(mouse);
	}
	
	public void setTree(Node tree)
	{
		this.tree = tree;
	}
	
	public void drawTree(Graphics2D g, Node node, double angle, double angle_incr, double radius, int last_x, int last_y)
	{
		int x = (int)Math.round(Math.cos(angle) * radius);
		int y = (int)Math.round(Math.sin(angle) * radius);
		
		g.setColor(Color.BLACK);
		
		if(!(x == last_x && y == last_y)) {
			double dist = Math.hypot(x - last_x, y - last_y);
			int ax = (int)Math.round(last_x + 16.0 / dist * (x - last_x));
			int ay = (int)Math.round(last_y + 16.0 / dist * (y - last_y));
			int bx = (int)Math.round(x + 15.0 / dist * (last_x - x));
			int by = (int)Math.round(y + 15.0 / dist * (last_y - y));
			g.drawLine(ax, ay, bx, by);
		}
		
		g.drawOval(x - 15, y - 15, 30, 30);
		
		//String c1 = Fraction.valueOf(node.c1()).half().toString();
		long c1 = node.c1();
		String label = c1 % 2 == 1 ? String.format("%d/2", c1) : Long.toString(c1 / 2);
		int str_width = g.getFontMetrics().stringWidth(label);
		int str_height = g.getFontMetrics().getHeight();
		g.drawString(label, x - str_width / 2, y + str_height / 4);
		
		clickable.add(new Area(new Ellipse2D.Float(x - 15, y - 15, 30, 30), node));
		
		List<Node> children = node.children();
		double next_incr = angle_incr / children.size();
		double next_base = children.size() == 3 ? angle : angle - next_incr / 2;
		for(int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			
			drawTree(g, child, next_base + i * next_incr, next_incr, radius + 50, x, y);
		}
	}
	
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		g2d.clearRect(0, 0, WIDTH, HEIGHT);
		
		g2d.translate(WIDTH / 2, HEIGHT / 2);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(1.5f));
		
		clickable.clear();
		
		if(tree != null)
			drawTree(g2d, tree, 0.0, 2 * Math.PI, 0.0, 0, 0);
	}
	
	record Area(Ellipse2D.Float area, Node node)
	{
	}
	
	class MouseInput extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			int x = e.getX() - WIDTH / 2;
			int y = e.getY() - HEIGHT / 2;
			
			for(Area area: clickable)
				if(area.area().contains(x, y)) {
					firePropertyChange("clickedNode", null, area.node());
				}
		}
	}
}
