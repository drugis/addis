package org.drugis.addis.entities.analysis.models;

import org.drugis.addis.entities.DrugSet;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.NodeSplitModel;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.summary.NodeSplitPValueSummary;

public class SimulationNodeSplitModel extends AbstractSimulationModel<NodeSplitModel> implements NodeSplitWrapper {
	private NodeSplitPValueSummary d_pValueSummary;

	public SimulationNodeSplitModel(NetworkBuilder<DrugSet> builder, NodeSplitModel model) {
		super(builder, model);
	}

	@Override
	public Parameter getDirectEffect() {
		return d_nested.getDirectEffect();
	}

	@Override
	public Parameter getIndirectEffect() {
		return d_nested.getIndirectEffect();
	}

	@Override
	public BasicParameter getSplitNode() {
		return d_nested.getSplitNode();
	}

	public NodeSplitPValueSummary getNodesNodeSplitPValueSummary() {
		if(d_pValueSummary == null) {
			d_pValueSummary = new NodeSplitPValueSummary(d_nested.getResults(), getDirectEffect(), getIndirectEffect());
		}
		return d_pValueSummary;
	}

}
