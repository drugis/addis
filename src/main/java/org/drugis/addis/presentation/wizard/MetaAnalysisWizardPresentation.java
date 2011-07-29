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
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.RandomEffectsMetaAnalysisPresentation;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.util.ListDataEventProxy;
import org.drugis.addis.util.ListDataListenerManager;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class MetaAnalysisWizardPresentation extends AbstractMetaAnalysisWizardPM<StudyGraphModel> {				
	private ModifiableHolder<DrugSet> d_firstDrugHolder;
	private ModifiableHolder<DrugSet> d_secondDrugHolder;
	private MetaAnalysisCompleteListener d_metaAnalysisCompleteListener;
	private ObservableList<DrugSet> d_selectedDrugs;
	private RandomEffectsMetaAnalysisPresentation d_pm;
	public MetaAnalysisWizardPresentation(Domain d, PresentationModelFactory pmm) {
		super(d, pmm);
				
		d_metaAnalysisCompleteListener = new MetaAnalysisCompleteListener();		
		d_studyListPm.getSelectedStudiesModel().addListDataListener(d_metaAnalysisCompleteListener);
	}

	@Override
	protected void buildDrugHolders() {
		d_firstDrugHolder = new ModifiableHolder<DrugSet>();		
		d_secondDrugHolder = new ModifiableHolder<DrugSet>();

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
		
		d_selectedDrugs = new SelectedDrugsHolder(d_firstDrugHolder, d_secondDrugHolder);
		d_selectedDrugs.addListDataListener(new ListDataListener() {
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
	
	private static class SelectedDrugsHolder extends AbstractList<DrugSet> implements ObservableList<DrugSet> {
		ObservableList<DrugSet> d_list = new ArrayListModel<DrugSet>();
		private ModifiableHolder<DrugSet> d_firstDrugHolder;
		private ModifiableHolder<DrugSet> d_secondDrugHolder;
		private ListDataListenerManager d_listenerManager = new ListDataEventProxy(this, d_list);


		public SelectedDrugsHolder(ModifiableHolder<DrugSet> firstDrugHolder, ModifiableHolder<DrugSet> secondDrugHolder) {
			d_firstDrugHolder = firstDrugHolder;
			d_secondDrugHolder = secondDrugHolder;
			d_firstDrugHolder.addValueChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getOldValue() == null) {
						if (evt.getNewValue() != null) {
							d_list.add(0, (DrugSet)evt.getNewValue());
						}
					} else {
						if (evt.getNewValue() == null) {
							d_list.remove(0);
						} else {
							d_list.set(0, (DrugSet) evt.getNewValue());
						}
					}
				}
			});
			d_secondDrugHolder.addValueChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if(evt.getOldValue() == null) {
						if (evt.getNewValue() != null) {
							d_list.add(d_list.size(),(DrugSet)evt.getNewValue());
						}
					} else {
						if (evt.getNewValue() == null) {
							d_list.remove(d_list.size() - 1);
						} else {
							d_list.set(d_list.size() - 1, (DrugSet) evt.getNewValue());
						}
					}
				}
			});
		}

		@Override
		public DrugSet get(int index) {
			return d_list.get(index);
		}

		@Override
		public int size() {
			return d_list.size();
		}


		@Override
		public Object getElementAt(int index) {
			return get(index);
		}

		@Override
		public int getSize() {
			return size();
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			d_listenerManager.addListDataListener(l);
		}
		
		@Override
		public void removeListDataListener(ListDataListener l) {
			d_listenerManager.removeListDataListener(l);
		}
		
	}
		
	public ModifiableHolder<DrugSet> getFirstDrugModel() {
		return d_firstDrugHolder;
	}
	
	public ModifiableHolder<DrugSet> getSecondDrugModel() {
		return d_secondDrugHolder;
	}
	
	private DrugSet getFirstDrug() {
		return d_firstDrugHolder.getValue();
	}

	private DrugSet getSecondDrug() {
		return d_secondDrugHolder.getValue();
	}
	
	@Override
	public ObservableList<DrugSet> getSelectedDrugsModel() {
		return d_selectedDrugs;
	}

	public RandomEffectsMetaAnalysis buildMetaAnalysis() {
		List<StudyArmsEntry> studyArms = new ArrayList <StudyArmsEntry>();
		
		for (Study s : getStudyListModel().getSelectedStudiesModel()) {
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
	public class MetaAnalysisCompleteListener extends AbstractValueModel implements ListDataListener {

		private void fireChange() {
			firePropertyChange(PROPERTYNAME_VALUE, null, getValue());
		}

		public Object getValue() {
			return new Boolean(!d_studyListPm.getSelectedStudiesModel().isEmpty());
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

	public RandomEffectsMetaAnalysisPresentation getMetaAnalysisModel() {
		RandomEffectsMetaAnalysis buildMetaAnalysis = buildMetaAnalysis();
		d_pm = (RandomEffectsMetaAnalysisPresentation) d_pmf.getModel(buildMetaAnalysis);
		return d_pm;
	}

	@Override
	protected StudyGraphModel buildStudyGraphPresentation() {
		return new StudyGraphModel(getStudiesEndpointAndIndication(), d_drugListHolder,  d_outcomeHolder);				
	}	
}
