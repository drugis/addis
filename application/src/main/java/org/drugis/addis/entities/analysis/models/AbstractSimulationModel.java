package org.drugis.addis.entities.analysis.models;

import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.MixedTreatmentComparison.ExtendSimulation;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.summary.QuantileSummary;

public abstract class AbstractSimulationModel {

	protected MixedTreatmentComparison d_nested;
	private QuantileSummary d_summary;

	protected AbstractSimulationModel(MixedTreatmentComparison mtc) {
		d_nested = mtc; 
	}
	
	public boolean isReady() {
		return d_nested.isReady();
	}
	
	public QuantileSummary getQuantileSummary(Parameter ip) {
		if (d_summary == null) {
			d_summary = new QuantileSummary(d_nested.getResults(), ip);
		}
		return d_summary;
	}
	
	public Parameter getRelativeEffect(Treatment a, Treatment b) {
		return d_nested.getRelativeEffect(a, b);
	}

	public ActivityTask getActivityTask() {
		return d_nested.getActivityTask();
	}

	public MCMCModel getModel() {
		return d_nested;
	}

	public void setExtendSimulation(ExtendSimulation s) {
		d_nested.setExtendSimulation(s);
	}
	
	public Parameter getRandomEffectsVariance() {
		return d_nested.getRandomEffectsVariance();
	}

	public MCMCResults getResults() {
		return d_nested.getResults();
	}

	public int getBurnInIterations() {
		return d_nested.getBurnInIterations();
	}

	public void setBurnInIterations(int it) {
		d_nested.setBurnInIterations(it);
	}

	public int getSimulationIterations() {
		return d_nested.getSimulationIterations();
	}

	public void setSimulationIterations(int it) {
		d_nested.setSimulationIterations(it);
	}
}
