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
import org.drugis.common.beans.AbstractObservableList;
import org.drugis.common.event.ListDataEventProxy;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class PairWiseMetaAnalysisWizardPresentation extends NetworkMetaAnalysisWizardPM {
	private ModifiableHolder<TreatmentDefinition> d_firstDrugHolder;
	private ModifiableHolder<TreatmentDefinition> d_secondDrugHolder;
	private MetaAnalysisCompleteListener d_metaAnalysisCompleteListener;
	private ObservableList<TreatmentDefinition> d_selectedTreatmentDefinitions;
	private PairWiseMetaAnalysisPresentation d_pm;
	public PairWiseMetaAnalysisWizardPresentation(Domain d, PresentationModelFactory pmm) {
		super(d, pmm);
				
		d_metaAnalysisCompleteListener = new MetaAnalysisCompleteListener();		
		d_selectableStudyListPm.getSelectedStudiesModel().addListDataListener(d_metaAnalysisCompleteListener);
		buildDefinitionHolders();
	}

	private void buildDefinitionHolders() {
		d_firstDrugHolder = new ModifiableHolder<TreatmentDefinition>();		
		d_secondDrugHolder = new ModifiableHolder<TreatmentDefinition>();

		d_firstDrugHolder.addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() != null && evt.getNewValue().equals(getSecondDrug())) {
					d_secondDrugHolder.setValue(null);
				}
			}
		});

		d_secondDrugHolder.addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() != null && evt.getNewValue().equals(getFirstDrug())) {
					d_firstDrugHolder.setValue(null);
				}									
			}			
		});
		
		d_outcomeHolder.addPropertyChangeListener(new SetEmptyListener(new ModifiableHolder[]{d_firstDrugHolder, d_secondDrugHolder}));
		
		d_selectedTreatmentDefinitions = new SelectedDrugsHolder(d_firstDrugHolder, d_secondDrugHolder);
		d_selectedTreatmentDefinitions.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				updateArmHolders();
			}
			public void intervalAdded(ListDataEvent e) {
				updateArmHolders();
			}
			public void contentsChanged(ListDataEvent e) {
				updateArmHolders();
			}
		});
	}
	
	private static class SelectedDrugsHolder extends AbstractObservableList<TreatmentDefinition> {
		ObservableList<TreatmentDefinition> d_list = new ArrayListModel<TreatmentDefinition>();
		private ModifiableHolder<TreatmentDefinition> d_firstDrugHolder;
		private ModifiableHolder<TreatmentDefinition> d_secondDrugHolder;

		public SelectedDrugsHolder(ModifiableHolder<TreatmentDefinition> firstDrugHolder, ModifiableHolder<TreatmentDefinition> secondDrugHolder) {
			d_list.addListDataListener(new ListDataEventProxy(d_manager));
			
			d_firstDrugHolder = firstDrugHolder;
			d_secondDrugHolder = secondDrugHolder;
			d_firstDrugHolder.addValueChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getOldValue() == null) {
						if (evt.getNewValue() != null) {
							d_list.add(0, (TreatmentDefinition)evt.getNewValue());
						}
					} else {
						if (evt.getNewValue() == null) {
							d_list.remove(0);
						} else {
							d_list.set(0, (TreatmentDefinition) evt.getNewValue());
						}
					}
				}
			});
			d_secondDrugHolder.addValueChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if(evt.getOldValue() == null) {
						if (evt.getNewValue() != null) {
							d_list.add(d_list.size(),(TreatmentDefinition)evt.getNewValue());
						}
					} else {
						if (evt.getNewValue() == null) {
							d_list.remove(d_list.size() - 1);
						} else {
							d_list.set(d_list.size() - 1, (TreatmentDefinition) evt.getNewValue());
						}
					}
				}
			});
		}

		@Override
		public TreatmentDefinition get(int index) {
			return d_list.get(index);
		}

		@Override
		public int size() {
			return d_list.size();
		}
	}
		
	public ModifiableHolder<TreatmentDefinition> getFirstDrugModel() {
		return d_firstDrugHolder;
	}
	
	public ModifiableHolder<TreatmentDefinition> getSecondDrugModel() {
		return d_secondDrugHolder;
	}
	
	private TreatmentDefinition getFirstDrug() {
		return d_firstDrugHolder.getValue();
	}

	private TreatmentDefinition getSecondDrug() {
		return d_secondDrugHolder.getValue();
	}
	
	public RandomEffectsMetaAnalysis buildMetaAnalysis() {
		List<StudyArmsEntry> studyArms = new ArrayList <StudyArmsEntry>();
		TreatmentDefinition base = d_firstDrugHolder.getValue();
		TreatmentDefinition subj = d_secondDrugHolder.getValue();
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
