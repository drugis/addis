package org.drugis.addis.presentation;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.lyndobrien.BenefitRiskDistributionImpl;
import org.drugis.addis.lyndobrien.LyndOBrienModel;
import org.drugis.addis.lyndobrien.LyndOBrienModelImpl;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;

public class LyndOBrienPresentation<Alternative extends Entity, AnalysisType extends BenefitRiskAnalysis<Alternative>> {
	AnalysisType d_a;
	StudyBenefitRiskAnalysis sbr;
	private LyndOBrienModelImpl d_model;
	private ValueHolder<Boolean> d_initializedModel = new ModifiableHolder<Boolean>(false);
	
	public LyndOBrienPresentation(AnalysisType at) {
		d_a = at;
	}
	
	public LyndOBrienModel getModel() {
		return d_model;
	}
	
	public ValueHolder<Boolean> getInitializedModel() {
		return d_initializedModel;
	}

	public void startLyndOBrien() {
		d_model = new LyndOBrienModelImpl(new BenefitRiskDistributionImpl<Alternative>(d_a));
		d_initializedModel.setValue(true);
		ThreadHandler.getInstance().scheduleTask(d_model.getTask());
	}

	public Task getTask() {
		return d_model.getTask();
	}
}
