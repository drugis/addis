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

import java.util.List;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.jgrapht.graph.SimpleDirectedGraph;

public class ValueTreeModel extends SimpleDirectedGraph<Object, ValueTreeModel.Edge> {
	public static class Edge {
		@Override
		public String toString() {
			return "";
		}
	}

	private static final long serialVersionUID = -7193972341109263149L;

	public ValueTreeModel(List<OutcomeMeasure> oms) {
		super(ValueTreeModel.Edge.class);
		
		String root = "BR balance";
		String benefits = "Benefits";
		String risks = "Risks";
		addVertex(root);
		addVertex(benefits);
		addVertex(risks);
		addEdge(root, benefits);
		addEdge(root, risks);
		for (OutcomeMeasure om : oms) {
			if (om instanceof AdverseEvent) {
				addVertex(om);
				addEdge(risks, om);
			} else if (om instanceof Endpoint) {
				addVertex(om);
				addEdge(benefits, om);
			}
		}
	}
}
