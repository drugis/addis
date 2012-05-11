package org.drugis.addis.entities.analysis.models;

import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.data.MCMCSettings;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.QuantileSummary;

public class SavedInconsistencyModel extends AbstractSavedModel  implements InconsistencyWrapper {

	public SavedInconsistencyModel(NetworkBuilder<DrugSet> builder, MCMCSettings settings,
			Map<Parameter, QuantileSummary> quantileSummaries, Map<Parameter, ConvergenceSummary> convergenceSummaries) {
		super(builder, settings, quantileSummaries, convergenceSummaries);
	}

	@Override
	public Parameter getInconsistencyVariance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Parameter> getInconsistencyFactors() {
		// TODO Auto-generated method stub
		return null;
	}
}
