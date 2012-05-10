package org.drugis.addis.entities.analysis.models;

import org.drugis.addis.entities.DrugSet;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.QuantileSummary;

public abstract class AbstractSavedModel  {

	public Parameter getRelativeEffect(DrugSet a, DrugSet b) {
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
	
	public Parameter getRandomEffectsVariance() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getBurnInIterations() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSimulationIterations() {
		// TODO Auto-generated method stub
		return 0;
	}

}
