package org.drugis.addis.gui;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;

public class OutcomeMeasureTreeModel extends DefaultTreeModel implements GraphModelListener {

	public OutcomeMeasureTreeModel(List<OutcomeMeasure> oms) {
		super(null);
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Benefit-risk balance");
		DefaultMutableTreeNode benefits = new DefaultMutableTreeNode("Benefits");
		DefaultMutableTreeNode risks = new DefaultMutableTreeNode("Risks");
		setRoot(root);
		root.add(benefits);
		root.add(risks);
		for (OutcomeMeasure om : oms) {
			if (om instanceof AdverseEvent) {
				risks.add(new DefaultMutableTreeNode(om.getLabel()));
			} else if (om instanceof Endpoint) {
				benefits.add(new DefaultMutableTreeNode(om.getLabel()));
			}
		}
	}

	private static final long serialVersionUID = -7193972341109263149L;

	@Override
	public void graphChanged(GraphModelEvent e) {
		reload();
	}

}
