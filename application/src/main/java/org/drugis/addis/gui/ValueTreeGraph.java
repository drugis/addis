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

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;

import org.drugis.addis.entities.OutcomeMeasure;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphLayoutCache;
import org.jgrapht.ext.JGraphModelAdapter;

import com.jgraph.components.labels.CellConstants;
import com.jgraph.components.labels.MultiLineVertexRenderer;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.tree.JGraphTreeLayout;

public class ValueTreeGraph extends JPanel {
	private static final long serialVersionUID = 6066555189994560156L;

	public ValueTreeGraph(List<OutcomeMeasure> oms) {
		super(new BorderLayout());
		add(createGraph(oms), BorderLayout.CENTER);
	}

	private JGraph createGraph(List<OutcomeMeasure> oms) {
		ValueTreeModel model = new ValueTreeModel(oms);
		JGraphModelAdapter<Object, ValueTreeModel.Edge> jModel = new JGraphModelAdapter<Object, ValueTreeModel.Edge>(model);

		// set out vertex (layout) attributes
		AttributeMap vertexAttributes = new AttributeMap();
		CellConstants.setVertexShape(vertexAttributes, MultiLineVertexRenderer.SHAPE_RECTANGLE);
		CellConstants.setBackground(vertexAttributes, Color.WHITE);
		CellConstants.setForeground(vertexAttributes, Color.BLACK);
		CompoundBorder border = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 2), BorderFactory.createEmptyBorder(5, 5, 5, 5));
		CellConstants.setBorder(vertexAttributes, border);
		CellConstants.setAutoSize(vertexAttributes, true);
		jModel.setDefaultVertexAttributes(vertexAttributes);

		// the graph layout cache 
		GraphLayoutCache layoutCache = new GraphLayoutCache(jModel, new MyDefaultCellViewFactory(jModel));
		// create graph
		JGraph jgraph = new JGraph(jModel, layoutCache);
		jgraph.setAntiAliased(true);
		jgraph.setEditable(false);
		jgraph.setEnabled(false);
		jgraph.setBackground(new JPanel().getBackground());
	
		// Layout the graph
		final JGraphTreeLayout layout = new JGraphTreeLayout();
		layout.setOrientation(SwingConstants.WEST);
		final JGraphFacade facade = new JGraphFacade(jgraph);
		layout.run(facade);
		jgraph.getGraphLayoutCache().edit(facade.createNestedMap(true, true));

		return jgraph;
	}
}
