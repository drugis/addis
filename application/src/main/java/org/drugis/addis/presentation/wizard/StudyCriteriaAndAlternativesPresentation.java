package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map.Entry;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class StudyCriteriaAndAlternativesPresentation extends CriteriaAndAlternativesPresentation<Arm> {

	@SuppressWarnings("serial")
	private class CompleteHolder extends ModifiableHolder<Boolean> implements PropertyChangeListener {
		public CompleteHolder() {
			super(false);
		}

		public void propertyChange(PropertyChangeEvent evt) {
			setValue(isComplete());
		}

		private boolean isComplete() {
			return getSelectedAlternatives().contains(getBaselineModel().getValue()) &&  
			(d_selectedAlternatives.getSelectedOptions().size() >= 2) && 
			(d_selectedCriteria.getSelectedOptions().size() >= 2);
		}
	}
	
	private ModifiableHolder<Study> d_studyModel;
	private ArrayListModel<Arm> d_availableAlternatives;
	private ArrayListModel<OutcomeMeasure> d_availableCriteria;
	private CompleteHolder d_completeModel;

	public StudyCriteriaAndAlternativesPresentation(ValueHolder<Indication> indication,	ModifiableHolder<AnalysisType> analysisType) {
		super(indication, analysisType);
		d_studyModel = new ModifiableHolder<Study>();
		d_availableAlternatives =  new ArrayListModel<Arm>();
		d_availableCriteria = new ArrayListModel<OutcomeMeasure>();
		d_completeModel = new CompleteHolder();
	}

	@Override
	public ObservableList<Arm> getAlternativesListModel() {
		return d_availableAlternatives;
	}

	@Override
	public ValueHolder<Boolean> getCompleteModel() {
		return d_completeModel;
	}
	
	public ModifiableHolder<Study> getStudyModel() {
		return d_studyModel;
	}

	@Override
	public ObservableList<OutcomeMeasure> getCriteriaListModel() {
		return d_availableCriteria;
	}

	@Override
	protected void reset() {
		d_studyModel.setValue(null);
		d_selectedCriteria.clear();
		d_alternativeEnabledMap.clear();
		d_selectedAlternatives.clear();
		d_outcomeEnabledMap.clear();
		d_baselineModel.setValue(null);	
		d_completeModel.propertyChange(null);
		if (d_indication.getValue() != null && d_studyModel.getValue() != null) {
			initializeValues();
		}
	}

	private void initializeValues() {
		initOutcomeMeasures();
		initAlternatives(d_studyModel.getValue().getArms());
	}

	@Override
	public StudyBenefitRiskAnalysis createAnalysis(String id) {
		List<Arm> alternatives = getSelectedAlternatives();
		List<OutcomeMeasure> studyAnalyses = getSelectedCriteria();
		StudyBenefitRiskAnalysis sbr = new StudyBenefitRiskAnalysis(id, d_indication.getValue(), d_studyModel.getValue(), 
				studyAnalyses, (Arm) d_baselineModel.getValue(), alternatives, d_analysisTypeHolder.getValue());
		return sbr;
	}
	
//	private boolean alternativeShouldBeEnabled(Arm alternative) {
//		// e is an arm in single-study
//		// safeguard added for spurious checks caused by listeners
//		if(! (alternative instanceof Arm))
//			return false;
//		for(Entry<OutcomeMeasure, ModifiableHolder<Boolean>> entry: d_criteriaSelectedMap.entrySet()) {
//			BasicMeasurement measurement = d_studyHolder.getValue().getMeasurement(entry.getKey(), (Arm) alternative);
//			if (entry.getValue().getValue() && (measurement == null || !measurement.isComplete())) {
//				return false;
//			}
//		}
//		if(d_analysisTypeHolder.getValue() == AnalysisType.LyndOBrien) { 
//			return (getAlternativeSelectedModel(alternative).getValue() == true) || nSelectedAlternatives() < 2;
//		} else {
//			return true;
//		}
//	}
}
