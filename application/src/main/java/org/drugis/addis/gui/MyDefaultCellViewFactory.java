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

import org.drugis.addis.presentation.StudyGraphModel.Vertex;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.VertexView;
import org.jgrapht.ext.JGraphModelAdapter;

import com.jgraph.components.labels.MultiLineVertexView;

@SuppressWarnings("serial")	
public class MyDefaultCellViewFactory extends DefaultCellViewFactory {
	
	@SuppressWarnings("rawtypes")
	private JGraphModelAdapter d_model;

	@SuppressWarnings("rawtypes")
	public MyDefaultCellViewFactory(JGraphModelAdapter model) {
		this.d_model = model;
	}
	
	@Override
	protected VertexView createVertexView(Object cell) {
		if (cell instanceof DefaultGraphCell) {
			MultiLineVertexView multiLineVertexView = new MultiLineVertexView(cell);
			AttributeMap map = new AttributeMap(d_model.getDefaultVertexAttributes());
			multiLineVertexView.setAttributes(map);
			Object obj = ((DefaultGraphCell)cell).getUserObject();
			if (obj instanceof Vertex) {
				addVertexAttributes(map, (Vertex) obj);
			}
			return multiLineVertexView;
		}
		return super.createVertexView(cell);
	}
	
	/**
	 * Set custom attributes for the given Vertex.
	 * @param map AttributeMap to write the attributes to.
	 * @param v The vertex.
	 */
	protected void addVertexAttributes(AttributeMap map, Vertex v) {
	}
}