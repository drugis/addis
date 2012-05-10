package org.drugis.addis.entities.analysis.models;

import java.util.List;

import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.QuantileSummary;

public interface InconsistencyWrapper extends MTCModelWrapper {

	public List<Parameter> getInconsistencyFactors();

	public boolean isReady();

	QuantileSummary getQuantileSummary(Parameter ip);

	public Parameter getInconsistencyVariance();

}
