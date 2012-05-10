package org.drugis.addis.entities.analysis.models;

import java.util.List;

import org.drugis.mtc.Parameter;


public interface InconsistencyWrapper extends MTCModelWrapper {

	List<Parameter> getInconsistencyFactors();

	Parameter getInconsistencyVariance();

}
