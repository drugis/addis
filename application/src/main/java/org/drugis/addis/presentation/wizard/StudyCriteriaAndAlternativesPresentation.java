package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
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
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.common.validation.ListMinimumSizeModel;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractConverter;
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
	private final ArrayListModel<Arm> d_availableAlternatives;
	private final ArrayListModel<OutcomeMeasure> d_availableCriteria;
	private final ValueModel d_completeModel;
	private final FilteredObservableList<Study> d_studiesWithIndicationHolder;

	public StudyCriteriaAndAlternativesPresentation(ValueHolder<Indication> indication,	ModifiableHolder<AnalysisType> analysisType, ObservableList<Study> studies) {
		super(indication, analysisType);
		d_studyModel = new ModifiableHolder<Study>();
		d_availableAlternatives =  new ArrayListModel<Arm>();
		d_availableCriteria = new ArrayListModel<OutcomeMeasure>();
		
		d_studyModel.addValueChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				studyChanged();
			}
		});
		
		AbstractConverter baselineValidModel = new AbstractConverter(d_baselineModel) {
			private static final long serialVersionUID = -8879640617811142054L;

			@Override
			public void setValue(Object newValue) {
			}
			
			@Override
			public Boolean convertFromSubject(Object subjectValue) {
				return getSelectedAlternatives().contains(subjectValue);
			}
		};
		
		d_completeModel = new BooleanAndModel(Arrays.<ValueModel>asList(
				new ListMinimumSizeModel(getSelectedAlternatives(), 2),
				new ListMinimumSizeModel(getSelectedCriteria(), 2),
				baselineValidModel));
		
		d_studiesWithIndicationHolder = new FilteredObservableList<Study>(studies, new IndicationFilter(d_indication.getValue()));
		d_indication.addValueChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				d_studiesWithIndicationHolder.setFilter(new IndicationFilter(d_indication.getValue()));
			}
		});
	}

	@Override
	public ObservableList<Arm> getAlternativesListModel() {
		return d_availableAlternatives;
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
		
		d_availableCriteria.clear();
		d_baselineModel.setValue(null);
		if (d_indication.getValue() != null && d_studyModel.getValue() != null) {
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
		StudyBenefitRiskAnalysis sbr = new StudyBenefitRiskAnalysis(id, d_indication.getValue(), d_studyModel.getValue(), 
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
