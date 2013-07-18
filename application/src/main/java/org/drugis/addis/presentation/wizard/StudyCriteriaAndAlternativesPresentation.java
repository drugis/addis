/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.apache.commons.collections15.Predicate;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.entities.analysis.DecisionContext;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.beans.FilteredObservableList;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;

public class StudyCriteriaAndAlternativesPresentation extends CriteriaAndAlternativesPresentation<Arm> {
	
	public class IndicationFilter implements Predicate<Study> {
		private final Indication d_indication;
		public IndicationFilter(Indication i) {
			d_indication = i;
		}
		public boolean evaluate(Study s) {
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
	public StudyBenefitRiskAnalysis createAnalysis(String name, DecisionContext context) {
		List<Arm> alternatives = getSelectedAlternatives();
		List<OutcomeMeasure> studyAnalyses = getSelectedCriteria();
		StudyBenefitRiskAnalysis sbr = new StudyBenefitRiskAnalysis(name, d_indicationModel.getValue(), d_studyModel.getValue(), 
				studyAnalyses, (Arm) d_baselineModel.getValue(), alternatives, d_analysisTypeHolder.getValue(), context);
		return sbr;
	}
	
	@Override
	protected boolean getAlternativeShouldBeEnabled(Arm alt) {
		if (!super.getAlternativeShouldBeEnabled(alt) || d_studyModel.getValue() == null) {
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

	@Override
	protected boolean getCriterionShouldBeEnabled(OutcomeMeasure crit) {
		if (d_studyModel.getValue() == null) {
			return false;
		}
		Study study = d_studyModel.getValue();
		
		// Check that there are at least 2 non-missing measurements
		List<Arm> goodArms = new ArrayListModel<Arm>();
		for (Arm arm : study.getArms()) {
			if (study.getMeasurement(crit, arm) != null && study.getMeasurement(crit, arm).isComplete()) {
				goodArms.add(arm);
			}
		}
		if (goodArms.size() < 2) {
			return false;
		}
		
		// Check that at least one arm has non-zero measurement
		if (crit.getVariableType() instanceof RateVariableType) {
			boolean haveNonZero = false;
			boolean haveNonSat = false;
			for (Arm arm : goodArms) {
				BasicRateMeasurement m = (BasicRateMeasurement) study.getMeasurement(crit, arm);
				if (!m.getRate().equals(0)) {
					haveNonZero = true;
				}
				if (!m.getRate().equals(m.getSampleSize())) {
					haveNonSat = true;
				}
			}
			if (!haveNonZero || !haveNonSat) {
				return false;
			}
		}
		
		return super.getCriterionShouldBeEnabled(crit);
	}
	
	public ObservableList<Study> getStudiesWithIndication() {
		return d_studiesWithIndicationHolder;
	}
}
