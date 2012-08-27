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

package org.drugis.addis.entities.treatment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.drugis.common.EqualsUtil;

import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.FixedObservableGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class DecisionTree extends DelegateTree<DecisionTreeNode, DecisionTreeEdge> {
	private static final long serialVersionUID = -2669529780972041770L;

	public static class ObservableDirectedGraph<V, E> extends FixedObservableGraph<V, E> implements DirectedGraph<V, E> {
		private static final long serialVersionUID = 442135818546886998L;
		public ObservableDirectedGraph(final Graph<V, E> delegate) {
			super(delegate);
		}

		@Override
		public Collection<V> getSuccessors(V vertex) {
			return new ArrayList<V>(super.getSuccessors(vertex));
		}
	}

	public DecisionTree(final DecisionTreeNode rootNode) {
		super(new ObservableDirectedGraph<DecisionTreeNode, DecisionTreeEdge>(new DirectedSparseGraph<DecisionTreeNode, DecisionTreeEdge>()));
		setRoot(rootNode);
	}

	public ObservableGraph<DecisionTreeNode, DecisionTreeEdge> getObservableGraph() {
		return (ObservableGraph<DecisionTreeNode, DecisionTreeEdge>) delegate;
	}

	/**
	 * Classify the given object.
	 * @throws IllegalStateException If the tree is incomplete, or the obj is of an incompatible type.
	 */
	public LeafNode decide(final Object obj) {
		return decide(obj, getRoot());
	}

	private LeafNode decide(final Object obj, final DecisionTreeNode parent) {
		if (parent instanceof ChoiceNode) {
			final ChoiceNode choice = (ChoiceNode) parent;
			final Object value = choice.getValue(obj);
			final DecisionTreeEdge e = findMatchingEdge(parent, value);
			if (e != null) {
				return decide(obj, getEdgeTarget(e));
			} else {
				throw new IllegalStateException("Object " + obj + " could not be classified");
			}
		}
		return (LeafNode) parent;
	}

	public DecisionTreeNode getEdgeTarget(final DecisionTreeEdge e) {
		return containsEdge(e) ? new Pair<DecisionTreeNode>(getIncidentVertices(e)).getSecond() : null;
	}

	public DecisionTreeNode getEdgeSource(final DecisionTreeEdge e) {
		return new Pair<DecisionTreeNode>(getIncidentVertices(e)).getFirst();
	}

	public DecisionTreeEdge findMatchingEdge(final DecisionTreeNode parent, final Object value) {
		for (final DecisionTreeEdge e : getOutEdges(parent)) {
			if (e.decide(value)) {
				return e;
			}
		}
		return null;
	}

	public void replaceChild(final DecisionTreeEdge edge, final DecisionTreeNode newChild) {
		final DecisionTreeNode parent = getEdgeSource(edge);
		removeChild(getEdgeTarget(edge));
		addChild(edge, parent, newChild);
	}

	public boolean equivalent(DecisionTree obj) {
		return equivalent(getRoot(), obj.getRoot(), this, obj);
	}

	private static boolean equivalent(DecisionTreeNode n1, DecisionTreeNode n2, DecisionTree t1, DecisionTree t2) {
		boolean equivalent = n1.equivalent(n2);
		Collection<DecisionTreeEdge> n1Edges = t1.getOutEdges(n1);
		Collection<DecisionTreeEdge> n2Edges = t2.getOutEdges(n2);
		if(equivalent && n1Edges.size() == n2Edges.size()) {
			for (DecisionTreeEdge e1 : n1Edges) {
				DecisionTreeEdge e2 = containsEquivalent(n2Edges, e1);
				if (e2 != null) {
					equivalent = equivalent(t1.getEdgeTarget(e1), t2.getEdgeTarget(e2), t1, t2);
				} else {
					equivalent = false;
				}

				if (!equivalent) {
					break;
				}
			}
		}
		return equivalent;
	}

	private static DecisionTreeEdge containsEquivalent(Collection<DecisionTreeEdge> list, DecisionTreeEdge edge) {
		for (DecisionTreeEdge e2 : list) {
			if (e2.equivalent(edge)) {
				return e2;
			}
		}
		return null;
	}


	public String getLabel(Category category) {
		List<LeafNode> leafs = findLeafNodes(category);
		Collections.sort(leafs, new Comparator<LeafNode>() {
			public int compare(LeafNode o1, LeafNode o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		List<String> labels = new ArrayList<String>();
		for (LeafNode leaf : leafs) {
			labels.add(getLabel(leaf));
		}
		Collections.sort(labels);
		if(labels.size() < 2) {
			return StringUtils.join(labels, ") OR (");

		} else {
			return "(" + StringUtils.join(labels, ") OR (") + ")";
		}
	}

	private String getLabel(DecisionTreeNode leaf) {
		List<String> labels = new ArrayList<String>();

		DecisionTreeEdge parentEdge = getParentEdge(leaf);
		DecisionTreeNode parent = getParent(leaf);
		labels.add(parent + " " + parentEdge);
		if(!isRoot(parent)) {
			labels.add(getLabel(parent));
		}
		Collections.reverse(labels);
		return StringUtils.join(labels, " AND ");
	}

	private List<LeafNode> findLeafNodes(Category category) {
		List<LeafNode> leafs = new ArrayList<LeafNode>();
		for (DecisionTreeNode node : getVertices()) {
			if (node instanceof LeafNode) {
				LeafNode leafNode = (LeafNode) node;
				if (EqualsUtil.equal(leafNode.getCategory(), category)) leafs.add(leafNode);
			}
		}
		return leafs;
	}
}