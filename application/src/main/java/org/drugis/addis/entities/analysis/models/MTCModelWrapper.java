package org.drugis.addis.entities.analysis.models;

import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.summary.QuantileSummary;

public interface MTCModelWrapper extends MixedTreatmentComparison {

	public QuantileSummary getQuantileSummary(Parameter ip);
	
	public Parameter getRelativeEffect(Treatment a, Treatment b);

	public ActivityTask getActivityTask();
	
	public MCMCModel getModel();

}
