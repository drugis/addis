/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
		
		@Override
		public void paint(Graphics g) {
			setSize(200, 50);
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
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(400, 80);
		}
	}

	public class DrugVertexRenderer extends VertexRenderer {
		public DrugVertexRenderer() {
		}
		
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(400, 80);
		}
		
		@Override
		public Component getRendererComponent(JGraph graph, CellView view,
				boolean sel, boolean focus, boolean preview) {
			// TODO Auto-generated method stub
			return new EllipseLabel(d_vertex.getDrug().getDescription());
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