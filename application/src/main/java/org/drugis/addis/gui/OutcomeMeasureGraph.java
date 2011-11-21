package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.drugis.addis.entities.OutcomeMeasure;
import org.jgraph.JGraph;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

public class OutcomeMeasureGraph extends JPanel {
	private static final long serialVersionUID = 6066555189994560156L;
	private JGraph d_jgraph;
	private final List<OutcomeMeasure> d_alternatives;

	public OutcomeMeasureGraph(List<OutcomeMeasure> oms) {
		d_alternatives = oms;
		createGraph(oms);
		add(d_jgraph, BorderLayout.CENTER);
	}

	private JGraph createGraph(List<OutcomeMeasure> oms) {
		d_jgraph = new JGraph();
		OutcomeMeasureTreeModel model = new OutcomeMeasureTreeModel(oms);
		d_jgraph.getModel().addGraphModelListener(model);
		return d_jgraph;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doLayout() {
		// create graph
		removeAll();
		d_jgraph = createGraph(d_alternatives);
		add(d_jgraph, BorderLayout.CENTER);
		
		// Layout the graph
		final JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
		final JGraphFacade facade = new JGraphFacade(d_jgraph);
		layout.run(facade);
		Map nested = facade.createNestedMap(true, true);
		d_jgraph.getGraphLayoutCache().edit(nested);
		
		d_jgraph.repaint();
	}

}
