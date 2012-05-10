package org.drugis.addis.entities.analysis.models;

import org.drugis.mtc.ConsistencyModel;

public class SimulationConsistencyModel extends AbstractSimulationModel implements ConsistencyWrapper {

	public SimulationConsistencyModel(ConsistencyModel model) {
		super(model);
		d_nested = model;
	}

}
