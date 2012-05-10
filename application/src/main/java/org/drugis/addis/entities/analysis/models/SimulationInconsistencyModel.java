package org.drugis.addis.entities.analysis.models;

import java.util.List;

import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.Parameter;

public class SimulationInconsistencyModel extends AbstractSimulationModel implements InconsistencyWrapper {

	public SimulationInconsistencyModel(InconsistencyModel model) {
		super(model);
	}

	@Override
	public List<Parameter> getInconsistencyFactors() {	
		return ((InconsistencyModel)d_nested).getInconsistencyFactors();
	}

	@Override
	public Parameter getInconsistencyVariance() {
		return ((InconsistencyModel)d_nested).getInconsistencyVariance();
	}

}
