package org.ribozyme.polygons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.ribozyme.util.Line;
import org.ribozyme.util.ProjPoint;

public class PolygonWindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	int level;
	int p;
	PolygonCanvas canvas;
	TreePanel tree_panel;
	JTextField coeffs_text;
	
	public PolygonWindow()
	{
		super("Polygon Ehrhardt Linear Terms");
		
		// Font slider_font = new Font("Uni Sans Heavy", Font.PLAIN, 24);
		Font button_font = new Font("Uni Sans Heavy", Font.PLAIN, 16);
		UIManager.put("Button.font", button_font);
		UIManager.put("TextField.font", button_font);
		UIManager.put("TextField.background", Color.WHITE);
		UIManager.put("RadioButton.font", button_font.deriveFont(Font.PLAIN, 20));
		
		setLayout(new GridBagLayout());
		
		ActionHandler handler = new ActionHandler();
		
		level = 2;
		p = 2;
		
		{
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.insets = new Insets(10, 20, 0, 20);
			
			JPanel panel = new JPanel(new GridLayout(1, 0, 50, 0));
			ButtonGroup group = new ButtonGroup();
			
			int[] primes = {2, 3, 5};
			
			for(int p: primes) {
				JRadioButton radio = new JRadioButton(Integer.toString(p));
				panel.add(radio);
				group.add(radio);
				radio.addActionListener(handler);
				radio.setActionCommand(Integer.toString(p));
				if(p == 2)
					radio.setSelected(true);
			}
			
			add(panel, c);
		}
		
		{
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.insets = new Insets(20, 20, 0, 20);
			
			canvas = new PolygonCanvas();
			
			add(canvas, c);
			canvas.addPropertyChangeListener(handler);
		}
		
		{
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 2;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.insets = new Insets(0, 20, 20, 20);
			
			coeffs_text = new JTextField();
			coeffs_text.setPreferredSize(new Dimension(500, 30));
			coeffs_text.setEditable(false);
			
			add(coeffs_text, c);
		}
		
		{
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = 0;
			c.gridwidth = 1;
			c.gridheight = 3;
			c.insets = new Insets(20, 20, 20, 20);
			
			tree_panel = new TreePanel();
			
			add(tree_panel, c);
			tree_panel.addMouseWheelListener(handler);
			tree_panel.addPropertyChangeListener(handler);
		}
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		
		setResizable(false);
		
		reset();
		tree_panel.requestFocusInWindow();
	}
	
	void reset()
	{
		canvas.changeLattice(new ProjPoint(p));
		canvas.repaint();
		calculateTree();
	}
	
	void marinate(Node node, List<Line> lines)
	{
		if(node.lattice().e() < level) {
			ProjPoint p = node.lattice();
			
			for(ProjPoint np: p.children()) {
				Node newnode = new Node(lines, np);
				marinate(newnode, lines);
				node.children().add(newnode);
			}
		}
	}
	
	void calculateTree()
	{
		List<Line> lines = canvas.getLines();
		
		Node root = new Node(lines, new ProjPoint(p));
		marinate(root, lines);
		
		List<Long> coeffs = new ArrayList<>();
		Queue<Node> nodes = new LinkedList<>();
		nodes.add(root);
		while(!nodes.isEmpty()) {
			Node node = nodes.poll();
			coeffs.add(node.c1());
			nodes.addAll(node.children());
		}
		
		tree_panel.setTree(root);
		tree_panel.repaint();
		
		String text = coeffs.toString();
		coeffs_text.setText(text.substring(1, text.length() - 1).replace(" ", ""));
		coeffs_text.setCaretPosition(0);
	}
	
	public static void main(String[] args)
	{
		PolygonWindow window = new PolygonWindow();
		
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		window.calculateTree();
	}
	
	class ActionHandler implements ActionListener, MouseWheelListener, PropertyChangeListener
	{
		@Override
		public void propertyChange(PropertyChangeEvent e)
		{
			String prop = e.getPropertyName();
			
			if(prop.equals("changeLines"))
				calculateTree();
			
			if(prop.equals("clickedNode")) {
				canvas.changeLattice((ProjPoint) e.getNewValue());
				canvas.repaint();
			}
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e)
		{
			level = level - e.getWheelRotation();
			if(level < 1)
				level = 1;
			if(level > 8)
				level = 8;
			calculateTree();
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String cmd = e.getActionCommand();
			p = Integer.parseInt(cmd);
			
			reset();
		}
	}
}
