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
import org.drugis.addis.entities.treatment.DecisionTreeEdgeComparator;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.common.beans.ReadOnlyObservableList;
import org.drugis.common.beans.SortedSetModel;

import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEvent.Edge;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.graph.util.Pair;

public class DecisionTreeOutEdgesModel extends ReadOnlyObservableList<DecisionTreeEdge> {
	private static final class ModelImpl extends SortedSetModel<DecisionTreeEdge> {
		public ModelImpl(final DecisionTree tree, final DecisionTreeNode parent) {
			super(new DecisionTreeEdgeComparator());
			if (tree.containsVertex(parent)) {
				this.addAll(tree.getOutEdges(parent));
			}
			tree.getObservableGraph().addGraphEventListener(new GraphEventListener<DecisionTreeNode, DecisionTreeEdge>() {
				@Override
				public void handleGraphEvent(final GraphEvent<DecisionTreeNode, DecisionTreeEdge> evt) {
					switch(evt.getType()) {
					case EDGE_ADDED: {
						final Edge<DecisionTreeNode, DecisionTreeEdge> edgeEvent = (Edge<DecisionTreeNode, DecisionTreeEdge>)evt;
						final Pair<DecisionTreeNode> vertices = new Pair<DecisionTreeNode>(tree.getIncidentVertices(edgeEvent.getEdge()));
						if (vertices.getFirst().equals(parent)) {
							add(edgeEvent.getEdge());
						}
						break;
					}
					case EDGE_REMOVED: {
						final Edge<DecisionTreeNode, DecisionTreeEdge> edgeEvent = (Edge<DecisionTreeNode, DecisionTreeEdge>)evt;
						if (contains(edgeEvent.getEdge())) {
							remove(edgeEvent.getEdge());
						}
						break;
					}
					default:
						break;

					}
				}
			});
		}
	}

	public DecisionTreeOutEdgesModel(final DecisionTree tree, final DecisionTreeNode parent) {
		super(new ModelImpl(tree, parent));
	}
}
