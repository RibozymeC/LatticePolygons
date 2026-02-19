package org.ribozyme.polygons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.ribozyme.util.Fraction;

public class TreePanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	static final int WIDTH = 600;
	static final int HEIGHT = 600;
	
	Node tree = null;
	
	List<Area> clickable;
	boolean shifting;
	
	public TreePanel()
	{
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		clickable = new ArrayList<>();
		shifting = false;
		
		MouseInput mouse = new MouseInput();
		addMouseListener(mouse);
		KeyInput key = new KeyInput();
		addKeyListener(key);
	}
	
	public void setTree(Node tree)
	{
		this.tree = tree;
	}
	
	public void drawTree(Graphics2D g, Node node, double sector_start, double sector_end, int last_x, int last_y)
	{
		double radius = 50.0 * node.point().e();
		double angle = (sector_start + sector_end) / 2;
		
		int x = (int)Math.round(Math.cos(angle) * radius);
		int y = (int)Math.round(Math.sin(angle) * radius);
		
		g.setColor(node.optimal() ? Color.RED : Color.BLACK);
		
		if(!(x == last_x && y == last_y)) {
			double dist = Math.hypot(x - last_x, y - last_y);
			int ax = (int)Math.round(last_x + 16.0 / dist * (x - last_x));
			int ay = (int)Math.round(last_y + 16.0 / dist * (y - last_y));
			int bx = (int)Math.round(x + 15.0 / dist * (last_x - x));
			int by = (int)Math.round(y + 15.0 / dist * (last_y - y));
			g.drawLine(ax, ay, bx, by);
		}
		
		g.setColor(Color.BLACK);
		
		g.drawOval(x - 15, y - 15, 30, 30);
		
		String label;
		if(!shifting) {
			long c1 = node.c1();
			label = Fraction.valueOf(c1).half().toString();
		}
		else {
			label = String.format("%d:%d", node.point().x(), node.point().y());
		}
		int str_width = g.getFontMetrics().stringWidth(label);
		int str_height = g.getFontMetrics().getHeight();
		g.drawString(label, x - str_width / 2, y + str_height / 4);
		
		clickable.add(new Area(new Ellipse2D.Float(x - 15, y - 15, 30, 30), node));
		
		List<Node> children = node.children();
		int n = children.size();
		
		double sector_diff = (sector_end - sector_start) / n;
		
		for(int i = 0; i < n; i++) {
			Node child = children.get(i);
			
			drawTree(g, child, sector_start + i * sector_diff, sector_start + (i + 1) * sector_diff, x, y);
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
		
		if(tree != null) {
			int n = tree.children().size();
			drawTree(g2d, tree, -Math.PI / n, 2 * Math.PI - Math.PI / n, 0, 0);
		}
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
					firePropertyChange("clickedNode", null, area.node().point());
				}
		}
	}
	
	class KeyInput extends KeyAdapter
	{
	    public void keyPressed(KeyEvent e)
	    {
	    	if(e.getKeyCode() == KeyEvent.VK_SHIFT)
	    		shifting = true;
	    	repaint();
	    }

	    public void keyReleased(KeyEvent e)
	    {
	    	if(e.getKeyCode() == KeyEvent.VK_SHIFT)
	    		shifting = false;
	    	repaint();
	    }
	}
}
