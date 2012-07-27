package org.drugis.addis.entities.treatment;

import java.util.ArrayList;
import java.util.Collection;

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

	public LeafNode getCategory(final Object obj) {
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
		return new Pair<DecisionTreeNode>(getIncidentVertices(e)).getSecond();
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
}