package org.drugis.addis.entities.analysis.models;

import java.util.ArrayList;
import java.util.Map;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.data.MCMCSettings;
import org.drugis.common.threading.NullTask;
import org.drugis.common.threading.activity.ActivityModel;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.common.threading.activity.DirectTransition;
import org.drugis.common.threading.activity.Transition;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.QuantileSummary;

public abstract class AbstractSavedModel implements MTCModelWrapper  {
	
	private NetworkBuilder<DrugSet> d_builder;
	private final MCMCSettings d_settings;
	private final Map<Parameter, QuantileSummary> d_quantileSummaries;
	private final Map<Parameter, ConvergenceSummary> d_convergenceSummaries;

	public AbstractSavedModel(NetworkBuilder<DrugSet> builder, MCMCSettings settings, 
			Map<Parameter, QuantileSummary> quantileSummaries, Map<Parameter, ConvergenceSummary> convergenceSummaries) {
		d_builder = builder;
		d_settings = settings;
		d_quantileSummaries = quantileSummaries;
		d_convergenceSummaries = convergenceSummaries; 
	}

	public ActivityTask getActivityTask() {
		NullTask start = new NullTask();
		String msg = "Loaded from saved results";
		NullTask end = new NullTask(msg);
		ArrayList<Transition> transitions =  new ArrayList<Transition>();
		transitions.add(new DirectTransition(start, end));
		return new ActivityTask(new ActivityModel(start, end, transitions ), msg);
	}

	public MixedTreatmentComparison getModel() {
		return null;
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
	
	public Parameter getRandomEffectsVariance() {
		// TODO Auto-generated method stub
		return null;
	}

	public ConvergenceSummary getConvergenceSummary(Parameter p) {
		return d_convergenceSummaries.get(p);
	}
	
	public int getBurnInIterations() {
		return d_settings.getTuningIterations();
	}

	public int getSimulationIterations() {
		return d_settings.getSimulationIterations();
	}
}
