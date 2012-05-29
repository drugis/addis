/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import org.drugis.addis.gui.util.JGraphUtil;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.StudyGraphModel.Edge;
import org.drugis.addis.presentation.StudyGraphModel.Vertex;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphLayoutCache;
import org.jgrapht.ext.JGraphModelAdapter;

import com.jgraph.components.labels.CellConstants;
import com.jgraph.components.labels.MultiLineVertexRenderer;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

@SuppressWarnings("serial")
public class StudyGraph extends JPanel {
	protected StudyGraphModel d_pm;
	
	protected JGraphModelAdapter<Vertex, Edge> d_model;
	private AttributeMap d_vertexAttributes;

	private JGraph d_jgraph;
	
	public StudyGraph(StudyGraphModel pm) {
		super(new BorderLayout());
		d_pm = pm;

		d_vertexAttributes = new AttributeMap();
		CellConstants.setVertexShape(d_vertexAttributes, MultiLineVertexRenderer.SHAPE_RECTANGLE);
		CellConstants.setBackground(d_vertexAttributes, Color.WHITE);
		CellConstants.setForeground(d_vertexAttributes, Color.BLACK);
		CellConstants.setBorder(d_vertexAttributes, BorderFactory.createLineBorder(Color.BLACK, 2));
	}

	@SuppressWarnings("rawtypes")
	public void layoutGraph() {
		// in the JGraphModelAdapter, the Vertex size is set. Therefore, this must be done every time the graph is redrawn
		d_model = new JGraphModelAdapter<Vertex, Edge>(d_pm);
		
		// set out vertex (layout) attributes
		d_model.setDefaultVertexAttributes(d_vertexAttributes);
		
		// the graph layout cache 
		GraphLayoutCache layoutCache = new GraphLayoutCache(d_model, getCellFactory());
		
		// create graph
		removeAll();
		d_jgraph = createGraph(layoutCache);
		add(d_jgraph, BorderLayout.CENTER);
		
		// Layout the graph
		final JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
		final JGraphFacade facade = new JGraphFacade(d_jgraph);
		layout.run(facade);
		Map nested = facade.createNestedMap(true, true);
		d_jgraph.getGraphLayoutCache().edit(nested);
		
		d_jgraph.repaint();
	}
	
	/**
	 * Update vertex attributes.
	 * @see MyDefaultCellViewFactory.addVertexAttributes
	 */
	public void resetVertexAttributes() {
		for (int i = 0; i < d_jgraph.getModel().getRootCount(); ++i) {
			DefaultGraphCell cell = (DefaultGraphCell) d_jgraph.getModel().getRootAt(i);
			if (cell.getUserObject() instanceof Vertex) {
				getCellFactory().addVertexAttributes(d_jgraph.getAttributes(cell), (Vertex)cell.getUserObject());
			}
		}
		d_jgraph.refresh();
	}

	protected MyDefaultCellViewFactory getCellFactory() {
		return new MyDefaultCellViewFactory(d_model);
	}

	private class StudyJGraph extends JGraph {
		public StudyJGraph(JGraphModelAdapter<Vertex, Edge> model, GraphLayoutCache layoutCache) {
			super(model, layoutCache);
		}

		// Return Cell Label as a Tooltip
		public String getToolTipText(MouseEvent e) {
			if(e != null) {
				Object c = getFirstCellForLocation(e.getX(), e.getY());
				if (c != null) {
					try {
						Integer.parseInt(convertValueToString(c)); // Don't display integers
					} catch(Exception ex) {
						return convertValueToString(c);
					}
				}
			}
			return null;
		}
	}
	
	protected JGraph createGraph(GraphLayoutCache layoutCache) {
		JGraph jgraph = new StudyJGraph(d_model, layoutCache);
		jgraph.setAntiAliased(true);
		jgraph.setEditable(false);
		jgraph.setEnabled(false);
	    ToolTipManager.sharedInstance().registerComponent(jgraph);
		return jgraph;
	}
	
	public void saveImage(JFrame frame) {
		JGraphUtil.writeGraphImage(frame, d_jgraph);
	}	
}
