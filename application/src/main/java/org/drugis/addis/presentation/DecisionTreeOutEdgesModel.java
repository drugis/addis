package org.drugis.addis.presentation;

import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DecisionTreeEdgeComparator;
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
			this.addAll(tree.getOutEdges(parent));
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
