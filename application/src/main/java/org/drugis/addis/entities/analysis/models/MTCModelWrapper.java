package org.drugis.addis.entities.analysis.models;

import org.drugis.addis.entities.DrugSet;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.QuantileSummary;

public interface MTCModelWrapper {

	public QuantileSummary getQuantileSummary(Parameter ip);
	
	public Parameter getRelativeEffect(DrugSet a, DrugSet b);

	public ActivityTask getActivityTask();
	
	public MCMCModel getModel();

	public boolean isReady();

	Parameter getRandomEffectsVariance();

	int getBurnInIterations();

	int getSimulationIterations();

}
