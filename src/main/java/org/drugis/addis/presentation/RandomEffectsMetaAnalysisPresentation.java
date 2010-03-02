package org.drugis.addis.presentation;

import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;

@SuppressWarnings("serial")
public class RandomEffectsMetaAnalysisPresentation
extends AbstractMetaAnalysisPresentation<RandomEffectsMetaAnalysis>
implements StudyListPresentationModel {

	public RandomEffectsMetaAnalysisPresentation(RandomEffectsMetaAnalysis bean, PresentationModelFactory mgr) {
		super(bean, mgr);
	}
	
	public LabeledPresentationModel getFirstDrugModel() {
		return d_mgr.getLabeledModel(getBean().getFirstDrug());
	}
	
	public LabeledPresentationModel getSecondDrugModel() {
		return d_mgr.getLabeledModel(getBean().getSecondDrug());		
	}

	public ForestPlotPresentation getForestPlotPresentation(Class<? extends RelativeEffect<?>> type) {
		ForestPlotPresentation pm = new ForestPlotPresentation(getBean(), type, d_mgr);
		return pm;
	}
}
