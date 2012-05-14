package org.drugis.addis.entities.analysis.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.data.MCMCSettings;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.parameterization.InconsistencyParameter;
import org.drugis.mtc.parameterization.InconsistencyVariance;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.QuantileSummary;

public class SavedInconsistencyModel extends AbstractSavedModel implements InconsistencyWrapper {

	private List<Parameter> d_inconsistencyFactors;

	public SavedInconsistencyModel(NetworkBuilder<DrugSet> builder, MCMCSettings settings,
			Map<Parameter, QuantileSummary> quantileSummaries, Map<Parameter, ConvergenceSummary> convergenceSummaries) {
		super(builder, settings, quantileSummaries, convergenceSummaries);
		d_inconsistencyFactors = new ArrayList<Parameter>();
		for(Parameter p : d_quantileSummaries.keySet()) { 
			if((p instanceof InconsistencyParameter)) {
				d_inconsistencyFactors.add(p);
			}
		}
	}

	@Override
	public List<Parameter> getInconsistencyFactors() {
		return d_inconsistencyFactors;
	}
	
	@Override
	public Parameter getInconsistencyVariance() {
		for(Parameter p : d_quantileSummaries.keySet()) { 
			if(p instanceof InconsistencyVariance) {
				return p;
			}
		}
		return null;
	}
	
}
