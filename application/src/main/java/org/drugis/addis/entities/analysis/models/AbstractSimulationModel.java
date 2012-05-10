package org.drugis.addis.entities.analysis.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.DrugSet;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.MixedTreatmentComparison.ExtendSimulation;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.summary.QuantileSummary;

public abstract class AbstractSimulationModel<MTCType extends MixedTreatmentComparison> implements MTCModelWrapper {
	protected final MTCType d_nested;
	private final Map<Parameter, QuantileSummary> d_summaryMap = new HashMap<Parameter, QuantileSummary>();
	protected final NetworkBuilder<DrugSet> d_builder;
	
	protected AbstractSimulationModel(NetworkBuilder<DrugSet> builder, MTCType mtc) { 
		d_builder = builder;
		d_nested = mtc;
	}
	
	@Override
	public boolean isReady() {
		return d_nested.isReady();
	}
	
	@Override
	public QuantileSummary getQuantileSummary(Parameter p) {
		if(d_summaryMap.get(p) == null) { 
			d_summaryMap.put(p, new QuantileSummary(d_nested.getResults(), p));
		}
		return d_summaryMap.get(p);
	}
	
	@Override
	public Parameter getRelativeEffect(DrugSet a, DrugSet b) {
		return d_nested.getRelativeEffect(getTreatment(a), getTreatment(b));
	}
	
	@Override
	public ActivityTask getActivityTask() {
		return d_nested.getActivityTask();
	}
	
	@Override
	public MCMCModel getModel() {
		return d_nested;
	}
	
	public void setExtendSimulation(ExtendSimulation s) {
		d_nested.setExtendSimulation(s);
	}
	
	@Override
	public Parameter getRandomEffectsVariance() {
		return d_nested.getRandomEffectsVariance();
	}
	
	public MCMCResults getResults() {
		return d_nested.getResults();
	}

	@Override
	public int getBurnInIterations() {
		return d_nested.getBurnInIterations();
	}

	public void setBurnInIterations(int it) {
		d_nested.setBurnInIterations(it);
	}

	@Override
	public int getSimulationIterations() {
		return d_nested.getSimulationIterations();
	}

	public void setSimulationIterations(int it) {
		d_nested.setSimulationIterations(it);
	}
	
	protected List<Treatment> getTreatments(List<DrugSet> drugs) {
		List<Treatment> treatments = new ArrayList<Treatment>();
		for (DrugSet d : drugs) {
			treatments.add(getTreatment(d));
		}
		return treatments;
	}

	protected Treatment getTreatment(DrugSet d) {
		return d_builder.getTreatmentMap().get(d);
	}
	
}
