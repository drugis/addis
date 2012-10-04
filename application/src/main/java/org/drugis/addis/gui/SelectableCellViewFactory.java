/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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

import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.presentation.TreatmentDefinitionsGraphModel.Vertex;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;

import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class SelectableCellViewFactory extends MyDefaultCellViewFactory {

	private ObservableList<TreatmentDefinition> d_selectedDefinitions;

	@SuppressWarnings("rawtypes")
	public SelectableCellViewFactory(JGraphModelAdapter model, ObservableList<TreatmentDefinition> observableList) {
		super(model);
		
		d_selectedDefinitions = observableList;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addVertexAttributes(AttributeMap map, Vertex v) {
		Color col = null;
		if (d_selectedDefinitions.contains(v.getTreatmentDefinition())) {
			col = Color.green;
		} else {
			col = Color.lightGray;
		}
		map.put(GraphConstants.BACKGROUND, col);
	}

}
