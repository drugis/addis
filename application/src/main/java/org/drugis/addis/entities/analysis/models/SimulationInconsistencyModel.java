package org.drugis.addis.entities.analysis.models;

import java.util.List;

import org.drugis.addis.entities.DrugSet;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;

public class SimulationInconsistencyModel extends AbstractSimulationModel<InconsistencyModel> implements InconsistencyWrapper {

	public SimulationInconsistencyModel(NetworkBuilder<DrugSet> builder, InconsistencyModel model) {
		super(builder, model);
	}

	@Override
	public List<Parameter> getInconsistencyFactors() {	
		return d_nested.getInconsistencyFactors();
	}

	@Override
	public Parameter getInconsistencyVariance() {
		return d_nested.getInconsistencyVariance();
	}

}
