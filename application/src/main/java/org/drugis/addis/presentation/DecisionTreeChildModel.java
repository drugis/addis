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
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.presentation;

import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;

import com.jgoodies.binding.value.AbstractValueModel;

import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEvent.Edge;
import edu.uci.ics.jung.graph.event.GraphEventListener;

/**
 * ValueModel to set and monitor the target of a DecisionTreeEdge.
 */
public class DecisionTreeChildModel extends AbstractValueModel {
	private static final long serialVersionUID = -1412185396526083292L;
	private final DecisionTree d_tree;
	private final DecisionTreeEdge d_edge;
	private DecisionTreeNode d_value;

	public DecisionTreeChildModel(final DecisionTree tree, final DecisionTreeEdge edge) {
		d_tree = tree;
		d_edge = edge;
		d_value = determineValue();
		d_tree.getObservableGraph().addGraphEventListener(new GraphEventListener<DecisionTreeNode, DecisionTreeEdge>() {
			@Override
			public void handleGraphEvent(final GraphEvent<DecisionTreeNode, DecisionTreeEdge> evt) {
				switch(evt.getType()) {
				case EDGE_ADDED:
				case EDGE_REMOVED:
					final Edge<DecisionTreeNode, DecisionTreeEdge> edgeEvent = (Edge<DecisionTreeNode, DecisionTreeEdge>)evt;
					if (edgeEvent.getEdge().equals(d_edge)) {
						update();
					}
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	public Object getValue() {
		return d_value;
	}

	private void update() {
		final DecisionTreeNode oldValue = d_value;
		d_value = determineValue();
		fireValueChange(oldValue, d_value);
	}

	private DecisionTreeNode determineValue() {
		return d_tree.containsEdge(d_edge) ? d_tree.getEdgeTarget(d_edge) : null;
	}

	@Override
	public void setValue(final Object newValue) {
		d_tree.replaceChild(d_edge, (DecisionTreeNode) newValue);
	}

}
