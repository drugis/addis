package org.drugis.addis.entities.analysis.models;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.data.MCMCSettings;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.QuantileSummary;

public abstract class AbstractSavedModel implements MTCModelWrapper  {
	
	private NetworkBuilder<DrugSet> d_builder;
	private final MCMCSettings d_settings;

	public AbstractSavedModel(NetworkBuilder<DrugSet> builder, MCMCSettings settings) {
		d_builder = builder;
		d_settings = settings; 
	}

	public ActivityTask getActivityTask() {
		// FIXME: finished null task?
		throw new UnsupportedOperationException("Saved MTC models do not have an ActivityTask.");
	}

	public MixedTreatmentComparison getModel() {
		throw new UnsupportedOperationException("Saved MTC models do not have a MixedTreatmentComparison model.");
	}

	public Parameter getRelativeEffect(DrugSet a, DrugSet b) {
		return new BasicParameter(d_builder.getTreatmentMap().get(a), d_builder.getTreatmentMap().get(b));
	}
	
	public boolean isReady() {
		return true;
	}
	
	public boolean hasSavedResults() {
		return true;
	}

	public QuantileSummary getQuantileSummary(Parameter p) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Parameter getRandomEffectsVariance() {
		// TODO Auto-generated method stub
		return null;
	}

	public ConvergenceSummary getConvergenceSummary(Parameter p) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getBurnInIterations() {
		return d_settings.getTuningIterations();
	}

	public int getSimulationIterations() {
		return d_settings.getSimulationIterations();
	}
}
