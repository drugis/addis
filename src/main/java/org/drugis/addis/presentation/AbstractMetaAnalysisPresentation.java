package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;


@SuppressWarnings("serial")
public class AbstractMetaAnalysisPresentation<T extends MetaAnalysis> extends PresentationModel<T> {

	protected PresentationModelFactory d_mgr;
	protected DefaultStudyListPresentationModel d_studyModel;
	
	public AbstractMetaAnalysisPresentation(T bean, PresentationModelFactory mgr) {
		super(bean);
		d_mgr = mgr;
		d_studyModel = new DefaultStudyListPresentationModel(new MyListHolder());
	}
	
	
	protected class MyListHolder extends AbstractListHolder<Study> {
		@Override
		public List<Study> getValue() {
			List<Study> studies = new ArrayList<Study>(getBean().getIncludedStudies());
			for (Study s : studies) {
				if (!(s instanceof Study)) {
					studies.remove(s);
				}
			}
			return studies;
		}		
	}


	public LabeledPresentationModel getIndicationModel() {
		return d_mgr.getLabeledModel(getBean().getIndication());
	}


	public LabeledPresentationModel getOutcomeMeasureModel() {
		return d_mgr.getLabeledModel(getBean().getOutcomeMeasure());
	}


	public AbstractValueModel getCharacteristicVisibleModel(Characteristic c) {
		return d_studyModel.getCharacteristicVisibleModel(c);
	}


	public ListHolder<Study> getIncludedStudies() {
		return d_studyModel.getIncludedStudies();
	}


	public Variable.Type getAnalysisType() {
		return getBean().getOutcomeMeasure().getType();
	}
}
