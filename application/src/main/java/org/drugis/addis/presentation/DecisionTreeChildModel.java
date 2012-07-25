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
