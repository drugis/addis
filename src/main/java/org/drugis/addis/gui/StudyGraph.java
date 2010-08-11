/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.StudyGraphModel.Edge;
import org.drugis.addis.presentation.StudyGraphModel.Vertex;
import org.drugis.common.gui.ImageExporter;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphLayoutCache;
import org.jgrapht.ext.JGraphModelAdapter;

import com.jgraph.components.labels.CellConstants;
import com.jgraph.components.labels.MultiLineVertexRenderer;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

@SuppressWarnings("serial")
public class StudyGraph extends JPanel {
	protected StudyGraphModel d_pm;
	
	@SuppressWarnings("unchecked")
	protected JGraphModelAdapter d_model;
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

	@SuppressWarnings("unchecked")
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
		
		//BufferedImage img = jgraph.getImage(Color.white, 10);
		//PNGExporter.writePNG("graph.png", img);
	}

	protected MyDefaultCellViewFactory getCellFactory() {
		return new MyDefaultCellViewFactory(d_model);
	}

	protected JGraph createGraph(GraphLayoutCache layoutCache) {
		JGraph jgraph = new JGraph(d_model, layoutCache);
		jgraph.setAntiAliased(true);
		jgraph.setEditable(false);
		jgraph.setEnabled(false);
		
		return jgraph;
	}
	
	public void saveAsPng(JFrame frame) {
		Color oldCol = d_jgraph.getBackground();
		d_jgraph.setBackground(Color.white);
		//ImageExporter.writePNG(frame, this, (int) getPreferredSize().getWidth(), (int) getPreferredSize().getHeight());
		ImageExporter.writeImage(frame, this, (int) getPreferredSize().getWidth(), (int) getPreferredSize().getHeight());
		d_jgraph.setBackground(oldCol);
	}
	
	public static class MutableDrugListHolder extends AbstractListHolder<Drug> {
		private List<Drug> d_drugs;
		
		public MutableDrugListHolder(List<Drug> drugs) {
			d_drugs = new ArrayList<Drug>(drugs);
		}

		@Override
		public List<Drug> getValue() {
			return d_drugs;
		}
		
		@SuppressWarnings("unchecked")
		@Override 
		public void setValue(Object o) {
			setValue((List<Drug>)o);
		}
		
		public void setValue(List<Drug> drugs) {
			List<Drug> oldValue = d_drugs;
			d_drugs = new ArrayList<Drug>(drugs);
			fireValueChange(oldValue, d_drugs);
		}
	}
}
