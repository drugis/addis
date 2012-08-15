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
import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class PairWiseMetaAnalysisWizardPresentation extends NetworkMetaAnalysisWizardPM {
	private final ModifiableHolder<TreatmentDefinition> d_firstDefinitionHolder = new ModifiableHolder<TreatmentDefinition>();
	private final ModifiableHolder<TreatmentDefinition> d_secondDefinitionHolder = new ModifiableHolder<TreatmentDefinition>();
	private MetaAnalysisCompleteListener d_metaAnalysisCompleteListener;
	private PairWiseMetaAnalysisPresentation d_pm;
	
	public PairWiseMetaAnalysisWizardPresentation(Domain d, PresentationModelFactory pmm) {
		super(d, pmm);
				
		d_metaAnalysisCompleteListener = new MetaAnalysisCompleteListener();		
		d_selectableStudyListPm.getSelectedStudiesModel().addListDataListener(d_metaAnalysisCompleteListener);
		d_firstDefinitionHolder.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
//				if (evt.getNewValue() != null && evt.getNewValue().equals(getSecondDefinition())) {
//					d_secondDefinitionHolder.setValue(null);
//				}
				updateRawDefinitionsGraph();
			}
		});
		
		d_secondDefinitionHolder.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
//				if (evt.getNewValue() != null && evt.getNewValue().equals(getFirstDefinition())) {
//					d_firstDefinitionHolder.setValue(null);
//				}
				updateRawDefinitionsGraph();
			}			
		});
			
		d_outcomeHolder.addPropertyChangeListener(new ClearValueModelsOnPropertyChangeListener(new ModifiableHolder[]{d_firstDefinitionHolder, d_secondDefinitionHolder}));
	}
	
	@Override
	public boolean rebuildRawAlternativesGraph() {
		if (super.rebuildRawAlternativesGraph()) {
			updateRawDefinitionsGraph();
			return true;
		}
		return false;
	}
	
	private void updateRawDefinitionsGraph() {
		ObservableList<TreatmentDefinition> list = getSelectedRawTreatmentDefinitions();
		if (getFirstDefinition() == null && getSecondDefinition() == null) { // both null
			list.clear();
		} else if (EqualsUtil.equal(getFirstDefinition(), getSecondDefinition())) { // both equal, non-null
			replaceOrInsert(list, 0, getFirstDefinition());
			trim(list, 1);
		} else { // both different, one may be null
			TreatmentDefinition first = getFirstDefinition() != null ? getFirstDefinition() : getSecondDefinition();
			TreatmentDefinition second = getFirstDefinition() != null ? getSecondDefinition() : null;
			replaceOrInsert(list, 0, first); // first is non-null
			if (second != null) {
				replaceOrInsert(list, 1, second);
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
	
	public ModifiableHolder<TreatmentDefinition> getFirstDefinitionModel() {
		return d_firstDefinitionHolder;
	}
	
	public ModifiableHolder<TreatmentDefinition> getSecondDefinitionModel() {
		return d_secondDefinitionHolder;
	}
	
	private TreatmentDefinition getFirstDefinition() {
		return d_firstDefinitionHolder.getValue();
	}

	private TreatmentDefinition getSecondDefinition() {
		return d_secondDefinitionHolder.getValue();
	}
	
	public RandomEffectsMetaAnalysis buildMetaAnalysis() {
		List<StudyArmsEntry> studyArms = new ArrayList <StudyArmsEntry>();
		TreatmentDefinition base = d_firstDefinitionHolder.getValue();
		TreatmentDefinition subj = d_secondDefinitionHolder.getValue();
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

}
