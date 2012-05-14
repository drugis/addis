package org.drugis.addis.entities.analysis.models;

import org.drugis.addis.entities.DrugSet;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.QuantileSummary;

public interface MTCModelWrapper extends MCMCModel {

	public QuantileSummary getQuantileSummary(Parameter ip);
	
	public Parameter getRelativeEffect(DrugSet a, DrugSet b);

	public ActivityTask getActivityTask();
	
	public MixedTreatmentComparison getModel();
	
	public boolean hasSavedResults();

	public boolean isReady();

	public Parameter getRandomEffectsVariance();

	public int getBurnInIterations();

	public int getSimulationIterations();
	
	public ConvergenceSummary getConvergenceSummary(Parameter p);

	public Parameter[] getParameters();
}
