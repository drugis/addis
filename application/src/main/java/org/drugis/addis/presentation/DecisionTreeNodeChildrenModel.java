package org.drugis.addis.presentation;

import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DecisionTreeNodeComparator;
import org.drugis.addis.entities.treatment.DoseDecisionTree;
import org.drugis.common.beans.ReadOnlyObservableList;
import org.drugis.common.beans.SortedSetModel;

import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEvent.Edge;
import edu.uci.ics.jung.graph.event.GraphEvent.Vertex;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.graph.util.Pair;

public class DecisionTreeNodeChildrenModel extends ReadOnlyObservableList<DecisionTreeNode> {
	private static final class ModelImpl extends SortedSetModel<DecisionTreeNode> {
		public ModelImpl(final DoseDecisionTree tree, final DecisionTreeNode parent) {
			super(new DecisionTreeNodeComparator());
			this.addAll(tree.getChildren(parent));
			tree.getObservableGraph().addGraphEventListener(new GraphEventListener<DecisionTreeNode, String>() {
				@Override
				public void handleGraphEvent(GraphEvent<DecisionTreeNode, String> evt) {
					switch(evt.getType()) {
					case EDGE_ADDED: { 
						Edge<DecisionTreeNode, String> edge = (Edge<DecisionTreeNode, String>)evt;
						Pair<DecisionTreeNode> vertices = new Pair<DecisionTreeNode>(tree.getIncidentVertices(edge.getEdge()));
						if(vertices.getFirst().equals(parent)) { 
							add(vertices.getSecond());
						}
						break;
					}
					case EDGE_REMOVED:
						break;
					case VERTEX_ADDED:
						break;
					case VERTEX_REMOVED: {
						Vertex<DecisionTreeNode, String> vertex = (Vertex<DecisionTreeNode, String>)evt;
						remove(vertex.getVertex());
						break;
					}	
					default:
						break; 
					
					}
				}
			});
		}
	}
	
	public DecisionTreeNodeChildrenModel(final DoseDecisionTree tree, final DecisionTreeNode parent) {
		super(new ModelImpl(tree, parent));
	}
}
