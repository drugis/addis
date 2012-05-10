package org.drugis.addis.entities.analysis.models;

import java.util.List;

import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.MixedTreatmentComparison.ExtendSimulation;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.summary.QuantileSummary;

public abstract class AbstractSavedModel  {

	public Parameter getRelativeEffect(Treatment a, Treatment b) {
		// TODO Auto-generated method stub
		return null;
	}

	public ActivityTask getActivityTask() {
		// FIXME: finished null task?
		throw new UnsupportedOperationException("Saved MTC models do not have an activity task.");
	}

	public MCMCModel getModel() {
		return null;
	}

	public List<Parameter> getInconsistencyFactors() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isReady() {
		return true;
	}
	
	public boolean hasSavedResults() {
		return true;
	}

	public QuantileSummary getQuantileSummary(Parameter ip) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setExtendSimulation(ExtendSimulation finish) {
		// TODO Auto-generated method stub
		
	}
	
	public Parameter getRandomEffectsVariance() {
		// TODO Auto-generated method stub
		return null;
	}

	public MCMCResults getResults() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getBurnInIterations() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setBurnInIterations(int it) {
		// TODO Auto-generated method stub
		
	}

	public int getSimulationIterations() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setSimulationIterations(int it) {
		// TODO Auto-generated method stub
		
	}

}
