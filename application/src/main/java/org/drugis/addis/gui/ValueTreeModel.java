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
