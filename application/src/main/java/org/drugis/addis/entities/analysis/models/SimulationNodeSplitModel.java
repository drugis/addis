package org.drugis.addis.entities.analysis.models;

import org.drugis.mtc.NodeSplitModel;
import org.drugis.mtc.Parameter;

public class SimulationNodeSplitModel extends AbstractSimulationModel implements NodeSplitWrapper {

	private final NodeSplitModel d_nested;

	public SimulationNodeSplitModel(NodeSplitModel model) {
		super(model);
		d_nested = model;
	}

	@Override
	public Parameter getDirectEffect() {
		return d_nested.getDirectEffect();
	}

	@Override
	public Parameter getIndirectEffect() {
		return d_nested.getIndirectEffect();
	}


}
