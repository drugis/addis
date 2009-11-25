package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class RandomEffectsMetaAnalysisPresentation extends PresentationModel<RandomEffectsMetaAnalysis> implements StudyListPresentationModel {

	private PresentationModelFactory d_mgr;
	private DefaultStudyListPresentationModel d_studyModel;

	public RandomEffectsMetaAnalysisPresentation(RandomEffectsMetaAnalysis bean, PresentationModelFactory mgr) {
		super(bean);
		d_mgr = mgr;
		d_studyModel = new DefaultStudyListPresentationModel(new MyListHolder());
	}
	
	public LabeledPresentationModel getIndicationModel() {
		return d_mgr.getLabeledModel(getBean().getIndication());
	}
	
	public LabeledPresentationModel getEndpointModel() {
		return d_mgr.getLabeledModel(getBean().getEndpoint());
	}
	
	public LabeledPresentationModel getFirstDrugModel() {
		return d_mgr.getLabeledModel(getBean().getFirstDrug());
	}
	
	public LabeledPresentationModel getSecondDrugModel() {
		return d_mgr.getLabeledModel(getBean().getSecondDrug());		
	}

	public AbstractValueModel getCharacteristicVisibleModel(
			StudyCharacteristic c) {
		return d_studyModel.getCharacteristicVisibleModel(c);
	}

	public ListHolder<Study> getIncludedStudies() {
		return d_studyModel.getIncludedStudies();
	}
	
	public ForestPlotPresentation getForestPlotPresentation(Class<? extends RelativeEffect<?>> type) {
		ForestPlotPresentation pm = new ForestPlotPresentation(getBean(), type);
		return pm;
	}
	
	private class MyListHolder extends AbstractListHolder<Study> {
		@Override
		public List<Study> getValue() {
			List<Study> studies = new ArrayList<Study>(getBean().getStudies());
			for (Study s : studies) {
				if (!(s instanceof BasicStudy)) {
					studies.remove(s);
				}
			}
			return studies;
		}		
	}
	
	public Endpoint.Type getAnalysisType() {
		return getBean().getEndpoint().getType();
	}
}
