package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.beans.FilteredObservableList;
import org.drugis.common.beans.FilteredObservableList.Filter;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;

public class StudyCriteriaAndAlternativesPresentation extends CriteriaAndAlternativesPresentation<Arm> {
	
	public class IndicationFilter implements Filter<Study> {
		private final Indication d_indication;
		public IndicationFilter(Indication i) {
			d_indication = i;
		}
		public boolean accept(Study s) {
			return s.getIndication().equals(d_indication);
		}
	}
	
	private final ModifiableHolder<Study> d_studyModel;
	private final ArrayListModel<OutcomeMeasure> d_availableCriteria;
	private final FilteredObservableList<Study> d_studiesWithIndicationHolder;

	public StudyCriteriaAndAlternativesPresentation(ValueHolder<Indication> indication,	ModifiableHolder<AnalysisType> analysisType, ObservableList<Study> studies) {
		super(indication, analysisType);
		d_studyModel = new ModifiableHolder<Study>();
		d_availableCriteria = new ArrayListModel<OutcomeMeasure>();
		
		d_studyModel.addValueChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				studyChanged();
			}
		});

		d_studiesWithIndicationHolder = new FilteredObservableList<Study>(studies, new IndicationFilter(d_indicationModel.getValue()));
		d_indicationModel.addValueChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				d_studiesWithIndicationHolder.setFilter(new IndicationFilter(d_indicationModel.getValue()));
			}
		});
	}

	@Override
	public ValueModel getCompleteModel() {
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
	}

	private void studyChanged() {
		d_alternativeEnabledMap.clear();
		d_criteriaEnabledMap.clear();
		
		d_selectedCriteria.clear();
		d_selectedAlternatives.clear();
		
		d_availableCriteria.clear();
		d_availableAlternatives.clear();

		d_baselineModel.setValue(null);
		if (d_indicationModel.getValue() != null && d_studyModel.getValue() != null) {
			initializeValues();
		}
	}

	private void initializeValues() {
		d_availableCriteria.addAll(d_studyModel.getValue().getOutcomeMeasures());
		initCriteria();
		initAlternatives(d_studyModel.getValue().getArms());
	}

	@Override
	public StudyBenefitRiskAnalysis createAnalysis(String id) {
		List<Arm> alternatives = getSelectedAlternatives();
		List<OutcomeMeasure> studyAnalyses = getSelectedCriteria();
		StudyBenefitRiskAnalysis sbr = new StudyBenefitRiskAnalysis(id, d_indicationModel.getValue(), d_studyModel.getValue(), 
				studyAnalyses, (Arm) d_baselineModel.getValue(), alternatives, d_analysisTypeHolder.getValue());
		return sbr;
	}
	
	@Override
	protected boolean getAlternativeShouldBeEnabled(Arm alt) {
		if (!super.getAlternativeShouldBeEnabled(alt)) {
			return false;
		}
		for (OutcomeMeasure crit : getSelectedCriteria()) {
			BasicMeasurement measurement = d_studyModel.getValue().getMeasurement(crit, alt);
			if (measurement == null || !measurement.isComplete()) {
				return false;
			}
		}
		return true;
	}

	public ObservableList<Study> getStudiesWithIndication() {
		return d_studiesWithIndicationHolder;
	}
}
