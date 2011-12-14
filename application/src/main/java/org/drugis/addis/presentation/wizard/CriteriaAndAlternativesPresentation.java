/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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
import java.util.Arrays;
import java.util.Collection;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.DecisionContext;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.common.validation.ListMinimumSizeModel;
import org.pietschy.wizard.InvalidStateException;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractConverter;
import com.jgoodies.binding.value.ValueModel;

public abstract class CriteriaAndAlternativesPresentation<Alternative extends Comparable<Alternative>> {
	final SelectableOptionsModel<OutcomeMeasure> d_selectedCriteria;
	final SelectableOptionsModel<Alternative> d_selectedAlternatives;
	private OptionsEnabledModel<OutcomeMeasure> d_enabledCriteria;
	final OptionsEnabledModel<Alternative> d_enabledAlternatives;
	protected final ModifiableHolder<AnalysisType> d_analysisTypeHolder;
	protected final ModifiableHolder<Alternative> d_baselineModel;
	protected final ValueHolder<Indication> d_indicationModel;
	protected final ValueModel d_completeModel;
	protected final ObservableList<Alternative> d_availableAlternatives;
	protected final ListDataListener d_resetAlternativeEnabledModelsListener;

	public CriteriaAndAlternativesPresentation(final ValueHolder<Indication> indication, final ModifiableHolder<AnalysisType> analysisType) {
		d_indicationModel = indication;
		d_analysisTypeHolder = analysisType;
		d_selectedCriteria = new SelectableOptionsModel<OutcomeMeasure>();
		d_enabledCriteria = new OptionsEnabledModel<OutcomeMeasure>(d_selectedCriteria, true) {
			public boolean optionShouldBeEnabled(OutcomeMeasure option) {
				return getCriterionShouldBeEnabled(option);
			}
		};
		d_selectedAlternatives = new SelectableOptionsModel<Alternative>();
		d_enabledAlternatives = new OptionsEnabledModel<Alternative>(d_selectedAlternatives, true) {
			public boolean optionShouldBeEnabled(Alternative alt) {
				return getAlternativeShouldBeEnabled(alt);
			}
		};
		d_baselineModel = new ModifiableHolder<Alternative>();
		d_availableAlternatives = new ArrayListModel<Alternative>();
		
		PropertyChangeListener resetValuesListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				reset();
			}
		};

		d_indicationModel.addValueChangeListener(resetValuesListener);
		d_analysisTypeHolder.addValueChangeListener(resetValuesListener);
		
		d_resetAlternativeEnabledModelsListener = new ListDataListener() {
			public void contentsChanged(ListDataEvent e) {
				d_enabledAlternatives.update();
			}
			public void intervalAdded(ListDataEvent e) {
				d_enabledAlternatives.update();
			}
			public void intervalRemoved(ListDataEvent e) {
				d_enabledAlternatives.update();
			}
		};
		getSelectedCriteria().addListDataListener(d_resetAlternativeEnabledModelsListener);
		getSelectedAlternatives().addListDataListener(d_resetAlternativeEnabledModelsListener);
		
		ListDataListener resetCriteriaEnabledModels = new ListDataListener() {
			public void contentsChanged(ListDataEvent e) {
				updateCriteriaEnabled();
			}
			public void intervalAdded(ListDataEvent e) {
				updateCriteriaEnabled();			
			}
			public void intervalRemoved(ListDataEvent e) {
				updateCriteriaEnabled();
			}
		};
		getSelectedCriteria().addListDataListener(resetCriteriaEnabledModels);
		
		
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
		
	}
	
	protected abstract void reset();

	public abstract ObservableList<OutcomeMeasure> getCriteriaListModel();
	
	public ValueHolder<Boolean> getCriterionSelectedModel(OutcomeMeasure om) {
		return d_selectedCriteria.getSelectedModel(om);
	}
	
	public ValueHolder<Boolean> getAlternativeSelectedModel(Alternative alternative) {
		return d_selectedAlternatives.getSelectedModel(alternative);
	}
	
	public ValueHolder<Boolean> getAlternativeEnabledModel(Alternative e) {
		return d_enabledAlternatives.getEnabledModel(e);
	}
	
	public ValueHolder<Boolean> getCriterionEnabledModel(OutcomeMeasure out) {
		return d_enabledCriteria.getEnabledModel(out);
	}

	public abstract ValueModel getCompleteModel();
	
	public BenefitRiskAnalysis<Alternative> saveAnalysis(Domain domain, String id, DecisionContext context) throws InvalidStateException, EntityIdExistsException {
		BenefitRiskAnalysis<Alternative> brAnalysis = createAnalysis(id, context);

		if(domain.getBenefitRiskAnalyses().contains(brAnalysis))
			throw new EntityIdExistsException("Benefit Risk Analysis with this ID already exists in domain");

		domain.getBenefitRiskAnalyses().add(brAnalysis);
		return brAnalysis;
	}
	
	public abstract BenefitRiskAnalysis<Alternative> createAnalysis(String id, DecisionContext context) throws InvalidStateException;

	public ObservableList<Alternative> getSelectedAlternatives() {
		return d_selectedAlternatives.getSelectedOptions();
	}
	
	public ObservableList<OutcomeMeasure> getSelectedCriteria() {
		return d_selectedCriteria.getSelectedOptions();
	}

	public ValueModel getBaselineModel() {
		return d_baselineModel;
	}
	
	protected void initCriteria() {
		for (OutcomeMeasure om : getCriteriaListModel()) {
			d_selectedCriteria.addOption(om, false);
		}
	}

	protected void initAlternatives(Collection<Alternative> alternatives) {
		d_availableAlternatives.addAll(alternatives);
		
		// create alternative selected models
		for (Alternative alt : alternatives) {
			d_selectedAlternatives.addOption(alt, false);
		}
	}
	
	protected boolean getAlternativeShouldBeEnabled(Alternative alt) {
		if (getAlternativeSelectedModel(alt).getValue() == true) {
			return true;
		}
		if (d_analysisTypeHolder.getValue() == AnalysisType.LyndOBrien) { 
			return getSelectedAlternatives().size() < 2;
		}
		return true;
	}

	protected boolean getCriterionShouldBeEnabled(OutcomeMeasure crit) {
		if (getCriterionSelectedModel(crit).getValue() == true) {
			return true;
		}
		if (d_analysisTypeHolder.getValue() == AnalysisType.LyndOBrien) {
			return getSelectedCriteria().size() < 2;
		}
		return true;
	}
	
	private void updateCriteriaEnabled() {
		d_enabledCriteria.update();
	}

	public ObservableList<Alternative> getAlternativesListModel() {
		return d_availableAlternatives;
	}
}
