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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.RandomEffectsMetaAnalysisPresentation;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.ModifiableHolder;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class MetaAnalysisWizardPresentation extends AbstractMetaAnalysisWizardPM<StudyGraphModel> {				
	private ModifiableHolder<Drug> d_firstDrugHolder;
	private ModifiableHolder<Drug> d_secondDrugHolder;
	private MetaAnalysisCompleteListener d_metaAnalysisCompleteListener;
	private ListHolder<Drug> d_selectedDrugs;
	private RandomEffectsMetaAnalysisPresentation d_pm;
	public MetaAnalysisWizardPresentation(Domain d, PresentationModelFactory pmm) {
		super(d, pmm);
				
		d_metaAnalysisCompleteListener = new MetaAnalysisCompleteListener();		
		d_studyListPm.getSelectedStudiesModel().addValueChangeListener(d_metaAnalysisCompleteListener);
	}

	@Override
	protected void buildDrugHolders() {
		d_firstDrugHolder = new ModifiableHolder<Drug>();		
		d_secondDrugHolder = new ModifiableHolder<Drug>();

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
		
		d_outcomeHolder.addPropertyChangeListener(
				new SetEmptyListener(new ModifiableHolder[]{d_firstDrugHolder, d_secondDrugHolder}));
		
		d_selectedDrugs = new SelectedDrugsHolder(d_firstDrugHolder, d_secondDrugHolder);
		d_selectedDrugs.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				updateArmHolders();
			}
		});
	}
	
	@SuppressWarnings("serial")
	private static class SelectedDrugsHolder extends AbstractListHolder<Drug> {
		private ArrayList<Drug> d_list;
		private ModifiableHolder<Drug> d_firstDrugHolder;
		private ModifiableHolder<Drug> d_secondDrugHolder;

		public SelectedDrugsHolder(ModifiableHolder<Drug> firstDrugHolder,
				ModifiableHolder<Drug> secondDrugHolder) {
			d_list = new ArrayList<Drug>();
			d_firstDrugHolder = firstDrugHolder;
			d_secondDrugHolder = secondDrugHolder;
			updated();
			PropertyChangeListener listener = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					updated();
				}
			};
			d_firstDrugHolder.addValueChangeListener(listener);
			d_secondDrugHolder.addValueChangeListener(listener);
		}
		
		private void updated() {
			d_list.clear();
			if (d_firstDrugHolder.getValue() != null) {
				d_list.add(d_firstDrugHolder.getValue());
			}
			if (d_secondDrugHolder.getValue() != null) {
				d_list.add(d_secondDrugHolder.getValue());
			}
			fireValueChange(null, Collections.unmodifiableList(d_list));
		}

		@Override
		public List<Drug> getValue() {
			return Collections.unmodifiableList(d_list);
		}
		
	}
		
	public ModifiableHolder<Drug> getFirstDrugModel() {
		return d_firstDrugHolder;
	}
	
	public ModifiableHolder<Drug> getSecondDrugModel() {
		return d_secondDrugHolder;
	}
	
	private Drug getFirstDrug() {
		return d_firstDrugHolder.getValue();
	}

	private Drug getSecondDrug() {
		return d_secondDrugHolder.getValue();
	}
	
	@Override
	public ListHolder<Drug> getSelectedDrugsModel() {
		return d_selectedDrugs;
	}

	public RandomEffectsMetaAnalysis buildMetaAnalysis() {
		List<StudyArmsEntry> studyArms = new ArrayList <StudyArmsEntry>();
		
		for (Study s : getStudyListModel().getSelectedStudiesModel().getValue()) {
			Arm left = d_selectedArms.get(s).get(d_firstDrugHolder.getValue()).getValue();
			Arm right = d_selectedArms.get(s).get(d_secondDrugHolder.getValue()).getValue();
			studyArms.add(new StudyArmsEntry(s, left, right));
		}	
		return new RandomEffectsMetaAnalysis("", (OutcomeMeasure) getOutcomeMeasureModel().getValue(), studyArms);
	}
	
	public RandomEffectsMetaAnalysis createMetaAnalysis(String name) {
		RandomEffectsMetaAnalysis ma = null;
		if (d_pm == null) {
			ma = buildMetaAnalysis();
		} else {
			ma = d_pm.getBean();
		}
		ma.setName(name);
		return ma;
	}
	
	public ValueModel getMetaAnalysisCompleteModel() {
		return d_metaAnalysisCompleteListener;
	}
	
	@SuppressWarnings("serial")
	public class MetaAnalysisCompleteListener extends AbstractValueModel implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			firePropertyChange(PROPERTYNAME_VALUE, null, getValue());
		}

		public Object getValue() {
			return new Boolean(!d_studyListPm.getSelectedStudiesModel().getValue().isEmpty());
		}

		public void setValue(Object newValue) {			
		}		
	}

	public RandomEffectsMetaAnalysisPresentation getMetaAnalysisModel() {
		d_pm = (RandomEffectsMetaAnalysisPresentation) d_pmf.getModel(buildMetaAnalysis());
		return d_pm;
	}

	@Override
	protected StudyGraphModel buildStudyGraphPresentation() {
		return new StudyGraphModel(d_indicationHolder, d_outcomeHolder, d_drugListHolder, d_domain);				
	}	
}
