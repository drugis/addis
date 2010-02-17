package org.drugis.addis.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import org.drugis.addis.presentation.StudyGraphModel;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

@SuppressWarnings("serial")
public class DrugVertexView extends VertexView {
	public static class EllipseLabel extends JLabel {
		public EllipseLabel(String text) {
			super(text);
		}
		
		public void paint(Graphics g) {
			setSize(200, 50);
			System.out.println(getSize());
			Graphics2D g2d = (Graphics2D)g;
			setBorder(BorderFactory.createLineBorder(Color.black, 3));
			setBackground(Color.WHITE);
			setForeground(Color.BLACK);
			g.setColor(Color.BLACK);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.draw(new Ellipse2D.Double(0, 0, 200, 50));
			char[] text = getText().toCharArray();
			g.drawChars(text, 0, text.length, 20, 20);
			//super.paint(g);
		}
		
		public Dimension getPreferredSize() {
			return new Dimension(400, 80);
		}
	}

	public class DrugVertexRenderer extends VertexRenderer {
		public DrugVertexRenderer() {
		}
		
		
		public Dimension getPreferredSize() {
			return new Dimension(400, 80);
		}
		
		@Override
		public Component getRendererComponent(JGraph graph, CellView view,
				boolean sel, boolean focus, boolean preview) {
			// TODO Auto-generated method stub
			return new EllipseLabel(d_vertex.getDrug().getName());
		}
	}

	private StudyGraphModel.Vertex d_vertex;

	public DrugVertexView(StudyGraphModel.Vertex v, DefaultGraphCell cell) {
		super(cell);
		d_vertex = v;
	}
	
	@Override
	public CellViewRenderer getRenderer() {
		return new DrugVertexRenderer();
	}
}