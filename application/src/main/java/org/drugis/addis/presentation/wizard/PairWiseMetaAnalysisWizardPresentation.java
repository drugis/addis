/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.PairWiseMetaAnalysisPresentation;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableTreatmentDefinitionsGraphModel;
import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class PairWiseMetaAnalysisWizardPresentation extends NetworkMetaAnalysisWizardPM {
	private static final String TEMPLATE_DEFINITIONS_DESCRIPTION =
			"Select the %1$s to be used for the meta-analysis. " +
			"To continue, (1) at least %2$s must be selected, and (2) the selected %1$s must be connected.";
	
	private final ModifiableHolder<TreatmentDefinition> d_rawFirstDefinitionHolder = new ModifiableHolder<TreatmentDefinition>();
	private final ModifiableHolder<TreatmentDefinition> d_rawSecondDefinitionHolder = new ModifiableHolder<TreatmentDefinition>();
	
	private final ModifiableHolder<TreatmentDefinition> d_refinedFirstDefinitionHolder = new ModifiableHolder<TreatmentDefinition>();
	private final ModifiableHolder<TreatmentDefinition> d_refinedSecondDefinitionHolder = new ModifiableHolder<TreatmentDefinition>();
	
	private MetaAnalysisCompleteListener d_metaAnalysisCompleteListener;
	private PairWiseMetaAnalysisPresentation d_pm;
	
	public PairWiseMetaAnalysisWizardPresentation(Domain d, PresentationModelFactory pmm) {
		super(d, pmm);
				
		d_metaAnalysisCompleteListener = new MetaAnalysisCompleteListener();		
		d_selectableStudyListPm.getSelectedStudiesModel().addListDataListener(d_metaAnalysisCompleteListener);
		d_rawFirstDefinitionHolder.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				commitRawSelectionToGraph();
			}
		});
		
		d_rawSecondDefinitionHolder.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				commitRawSelectionToGraph();
			}			
		});
		
		d_refinedFirstDefinitionHolder.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() != null && evt.getNewValue().equals(d_refinedSecondDefinitionHolder.getValue())) {
					d_refinedSecondDefinitionHolder.setValue(null);
				}
				commitRefinedSelectionToGraph();
			}
		});
		
		d_refinedSecondDefinitionHolder.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() != null && evt.getNewValue().equals(d_refinedFirstDefinitionHolder.getValue())) {
					d_refinedFirstDefinitionHolder.setValue(null);
				}
				commitRefinedSelectionToGraph();
			}
		});
			
		d_outcomeHolder.addPropertyChangeListener(new ClearValueModelsOnPropertyChangeListener(new ModifiableHolder[]{d_rawFirstDefinitionHolder, d_rawSecondDefinitionHolder}));
	}

	@Override
	public boolean rebuildRawAlternativesGraph() {
		if (super.rebuildRawAlternativesGraph()) {
			commitRawSelectionToGraph();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean rebuildRefinedAlternativesGraph() {
		if (super.rebuildRefinedAlternativesGraph()) {
			propagateRawToRefined(getRawFirstDefinitionModel(), getRefinedFirstDefinitionModel());
			propagateRawToRefined(getRawSecondDefinitionModel(), getRefinedSecondDefinitionModel());
			commitRefinedSelectionToGraph();
			return true;
		}
		return false;
	}

	private void propagateRawToRefined(
			ModifiableHolder<TreatmentDefinition> rawModel,
			ModifiableHolder<TreatmentDefinition> refinedModel) {
		if (getRefinedAlternativesGraph().getDefinitions().contains(rawModel.getValue())) {
			refinedModel.setValue(rawModel.getValue());
		} else {
			refinedModel.setValue(null);
		}
	}
	
	protected void commitRefinedSelectionToGraph() {
		commitSelection(getSelectedRefinedTreatmentDefinitions(),
				d_refinedFirstDefinitionHolder.getValue(),
				d_refinedSecondDefinitionHolder.getValue());
	}
	
	private void commitRawSelectionToGraph() {
		commitSelection(getSelectedRawTreatmentDefinitions(),
				d_rawFirstDefinitionHolder.getValue(),
				d_rawSecondDefinitionHolder.getValue());
	}

	private void commitSelection(ObservableList<TreatmentDefinition> list,
			TreatmentDefinition firstDef,
			TreatmentDefinition secondDef) {
		if (firstDef == null) { // ensure the first is never null unless both are null
			firstDef = secondDef;
			secondDef = null;
		}
		
		if (firstDef == null) { // both null
			list.clear();
		} else if (EqualsUtil.equal(firstDef, secondDef)) { // both equal, non-null
			replaceOrInsert(list, 0, firstDef);
			trim(list, 1);
		} else { // both different, second one may be null
			replaceOrInsert(list, 0, firstDef); // first is non-null
			if (secondDef != null) {
				replaceOrInsert(list, 1, secondDef);
				trim(list, 2);
			} else {
				trim(list, 1);
			}
		}
	}

	private void trim(List<TreatmentDefinition> list, int size) {
		while (list.size() > size) {
			list.remove(size);
		}
	}

	private void replaceOrInsert(List<TreatmentDefinition> list, int index, TreatmentDefinition element) {
		if (list.size() > index) {
			if (list.get(index) != element) {
				list.set(index, element);
			}
		} else {
			list.add(index, element);
		}
	}
	
	public ModifiableHolder<TreatmentDefinition> getRawFirstDefinitionModel() {
		return d_rawFirstDefinitionHolder;
	}
	
	public ModifiableHolder<TreatmentDefinition> getRawSecondDefinitionModel() {
		return d_rawSecondDefinitionHolder;
	}
	
	public ModifiableHolder<TreatmentDefinition> getRefinedFirstDefinitionModel() {
		return d_refinedFirstDefinitionHolder;
	}
	
	public ModifiableHolder<TreatmentDefinition> getRefinedSecondDefinitionModel() {
		return d_refinedSecondDefinitionHolder;
	}
	
	
	public RandomEffectsMetaAnalysis buildMetaAnalysis() {
		List<StudyArmsEntry> studyArms = new ArrayList <StudyArmsEntry>();
		TreatmentDefinition base = d_refinedFirstDefinitionHolder.getValue();
		TreatmentDefinition subj = d_refinedSecondDefinitionHolder.getValue();
		for (Study s : getSelectableStudyListPM().getSelectedStudiesModel()) {
			Arm left = d_selectedArms.get(s).get(base).getValue();
			Arm right = d_selectedArms.get(s).get(subj).getValue();
			studyArms.add(new StudyArmsEntry(s, left, right));
		}
		OutcomeMeasure om = (OutcomeMeasure) getOutcomeMeasureModel().getValue();
		return new RandomEffectsMetaAnalysis("", om, base, subj, studyArms, false);
	}
	
	public ValueModel getMetaAnalysisCompleteModel() {
		return d_metaAnalysisCompleteListener;
	}
	
	@SuppressWarnings("serial")
	public class MetaAnalysisCompleteListener extends AbstractValueModel implements ListDataListener {

		private void fireChange() {
			firePropertyChange(PROPERTYNAME_VALUE, null, getValue());
		}

		public Object getValue() {
			return new Boolean(!d_selectableStudyListPm.getSelectedStudiesModel().isEmpty());
		}

		public void setValue(Object newValue) {			
		}

		public void contentsChanged(ListDataEvent e) {
			fireChange();
		}
		public void intervalAdded(ListDataEvent e) {
			fireChange();
		}
		public void intervalRemoved(ListDataEvent e) {
			fireChange();
		}		
	}
	
	@Override
	protected SelectableTreatmentDefinitionsGraphModel buildRawAlternativesGraph() {
		return new SelectableTreatmentDefinitionsGraphModel(getStudiesEndpointAndIndication(), d_rawTreatmentDefinitions, d_outcomeHolder, 1, 2);
	}
	
	@Override
	protected SelectableTreatmentDefinitionsGraphModel buildRefinedAlternativesGraph() {
		return new SelectableTreatmentDefinitionsGraphModel(getStudiesEndpointAndIndication(), d_refinedTreatmentDefinitions, d_outcomeHolder, 2, 2);
	}

	public PairWiseMetaAnalysisPresentation getMetaAnalysisModel() {
		RandomEffectsMetaAnalysis buildMetaAnalysis = buildMetaAnalysis();
		d_pm = (PairWiseMetaAnalysisPresentation) d_pmf.getModel(buildMetaAnalysis);
		return d_pm;
	}

	public MetaAnalysis createAnalysis(String name) {
		RandomEffectsMetaAnalysis ma = null;
		if (d_pm == null) {
			ma = buildMetaAnalysis();
		} else {
			ma = d_pm.getBean();
		}
		ma.setName(name);
		return ma;
	}
	
	@Override
	protected String getDescriptionTemplate() {
		return TEMPLATE_DEFINITIONS_DESCRIPTION;
	}
}
