package org.drugis.addis.entities.mtcwrapper;

import java.util.HashMap;
import java.util.Map;

import org.drugis.common.beans.AbstractObservable;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MCMCSettings;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.QuantileSummary;

public class MCMCSimulationWrapper<ModelType extends MCMCModel> extends AbstractObservable implements MCMCModelWrapper {
	protected final ModelType d_nested;
	private final Map<Parameter, QuantileSummary> d_quantileSummaryMap = new HashMap<Parameter, QuantileSummary>();
	private final Map<Parameter, ConvergenceSummary> d_convergenceSummaryMap = new HashMap<Parameter, ConvergenceSummary>();
	private boolean d_destroy = false;
	private final String d_description;

	public MCMCSimulationWrapper(ModelType mtc, String description) {
		d_nested = mtc;
		d_description = description;
	}

	@Override
	public ModelType getModel() {
		return d_nested;
	}

	@Override
	public boolean isSaved() { 
		return false;
	}

	@Override
	public boolean isApproved() {
		return d_nested.getActivityTask().isFinished();
	}

	@Override
	public MCMCSettings getSettings() {
		return d_nested.getSettings();
	}

	@Override
	public QuantileSummary getQuantileSummary(Parameter p) {
		if(d_quantileSummaryMap.get(p) == null) { 
			d_quantileSummaryMap.put(p, new QuantileSummary(d_nested.getResults(), p));
		}
		return d_quantileSummaryMap.get(p);
	}

	@Override
	public ConvergenceSummary getConvergenceSummary(Parameter p) {
		if(d_convergenceSummaryMap.get(p) == null) { 
			d_convergenceSummaryMap.put(p, new ConvergenceSummary(d_nested.getResults(), p));
		}
		return d_convergenceSummaryMap.get(p);
	}

	@Override
	public Parameter[] getParameters() { 
		return d_nested.getResults().getParameters();
	}

	@Override
	public void selfDestruct() {
		d_destroy  = true;
		firePropertyChange(PROPERTY_DESTROYED, false, true);
	}

	@Override
	public boolean getDestroyed() { 
		return d_destroy;
	}

	@Override
	public String getDescription() {
		return d_description;
	}
}