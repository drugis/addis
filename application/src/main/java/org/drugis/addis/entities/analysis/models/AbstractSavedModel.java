package org.drugis.addis.entities.analysis.models;

import java.util.ArrayList;
import java.util.Map;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.data.MCMCSettings;
import org.drugis.common.threading.NullTask;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.threading.activity.ActivityModel;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.common.threading.activity.DirectTransition;
import org.drugis.common.threading.activity.Transition;
import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.parameterization.RandomEffectsVariance;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.QuantileSummary;

public abstract class AbstractSavedModel implements MTCModelWrapper  {
	
	private NetworkBuilder<DrugSet> d_builder;
	private final MCMCSettings d_settings;
	protected final Map<Parameter, QuantileSummary> d_quantileSummaries;
	protected final Map<Parameter, ConvergenceSummary> d_convergenceSummaries;
	private ActivityTask d_activityTask;

	public AbstractSavedModel(NetworkBuilder<DrugSet> builder, MCMCSettings settings, 
			Map<Parameter, QuantileSummary> quantileSummaries, Map<Parameter, ConvergenceSummary> convergenceSummaries) {
		d_builder = builder;
		d_settings = settings;
		d_quantileSummaries = quantileSummaries;
		d_convergenceSummaries = convergenceSummaries; 
		
		String msg = "Loaded from saved results";
		NullTask start = new NullTask();
		NullTask end = new NullTask("msg");
		ArrayList<Transition> transitions = new ArrayList<Transition>();
		transitions.add(new DirectTransition(start, end));
		d_activityTask = new ActivityTask(new ActivityModel(start, end, transitions), msg);
	}

	public ActivityTask getActivityTask() {
		ThreadHandler.getInstance().scheduleTask(d_activityTask);
		return d_activityTask;
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
		return d_quantileSummaries.get(p);
	}

	public ConvergenceSummary getConvergenceSummary(Parameter p) {
		return d_convergenceSummaries.get(p);
	}
	
	@Override
	public Parameter getRandomEffectsVariance() {
		for(Parameter p : d_quantileSummaries.keySet()) { 
			if(p instanceof RandomEffectsVariance) {
				return p;
			}
		}
		return null;
	}
	
	public int getBurnInIterations() {
		return d_settings.getTuningIterations();
	}

	public int getSimulationIterations() {
		return d_settings.getSimulationIterations();
	}
	
	@Override
	public Parameter[] getParameters() { 
		return d_convergenceSummaries.keySet().toArray(new Parameter[] {});
	}
	
	
	public MixedTreatmentComparison getModel() {
		throw new UnsupportedOperationException("Saved MTC models do not have a MixedTreatmentComparison model.");
	}
	
	@Override
	public MCMCResults getResults() {
		throw new UnsupportedOperationException("Saved MTC models do not have results");
	}

	@Override
	public void setBurnInIterations(int it) {
		throw new IllegalAccessError("Burn-in iterations are read-only for saved models");
	}

	@Override
	public void setSimulationIterations(int it) {
		throw new IllegalAccessError("Simulation iterations are read-only for saved models");
		
	}
}
