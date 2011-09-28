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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.presentation.DefaultSelectableStudyListPresentation;
import org.drugis.addis.presentation.DefaultStudyListPresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyListPresentation;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.util.FilteredObservableList;
import org.drugis.addis.util.FilteredObservableList.Filter;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public abstract class AbstractMetaAnalysisWizardPM<G extends StudyGraphModel> extends AbstractWizardWithSelectableIndicationPM {

	protected PresentationModelFactory d_pmf;
	protected ModifiableHolder<OutcomeMeasure> d_outcomeHolder;
	private ObservableList<OutcomeMeasure> d_outcomes = new ArrayListModel<OutcomeMeasure>();	
	protected ObservableList<DrugSet> d_drugListHolder;
	protected G d_studyGraphPresentationModel;	
	private StudiesMeasuringValueModel d_studiesMeasuringValueModel;
	protected Map<Study, Map<DrugSet, ModifiableHolder<Arm>>> d_selectedArms;
	protected DefaultSelectableStudyListPresentation d_studyListPm;
	private ObservableList<Study> d_studiesEndpointIndication;
	private ObservableList<Study> d_selectableStudies;
	
	public AbstractMetaAnalysisWizardPM(Domain d, PresentationModelFactory pmf) {
		super(d);
		d_pmf = pmf;
	
		d_outcomeHolder = new ModifiableHolder<OutcomeMeasure>();
		d_indicationHolder.addPropertyChangeListener(new SetEmptyListener(d_outcomeHolder));
	
		updateOutcomes();
		d_indicationHolder.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateOutcomes();
			}
		});
		
		d_studiesEndpointIndication = createStudiesIndicationOutcome();
		
		d_drugListHolder = new ArrayListModel<DrugSet>();
		d_outcomeHolder.addValueChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				SortedSet<DrugSet> drugs = new TreeSet<DrugSet>();
				if (d_indicationHolder.getValue() != null && d_outcomeHolder.getValue() != null) {
					List<Study> studies = getStudiesEndpointAndIndication();
					for (Study s : studies) {
						drugs.addAll(s.getMeasuredDrugs(d_outcomeHolder.getValue()));
					}			
				}
				d_drugListHolder.clear();
				d_drugListHolder.addAll(drugs);
			}
		});
		
		d_studyGraphPresentationModel = buildStudyGraphPresentation();
		buildDrugHolders();

		d_studiesMeasuringValueModel = new StudiesMeasuringValueModel();
		
		d_selectedArms = new HashMap<Study, Map<DrugSet, ModifiableHolder<Arm>>>();
		
		ListDataListener listener = new ListDataListener() {
			public void contentsChanged(ListDataEvent e) {
				updateArmHolders();
			}
			public void intervalAdded(ListDataEvent e) {
				updateArmHolders();
			}
			public void intervalRemoved(ListDataEvent e) {
				updateArmHolders();
			}
		};
		d_selectableStudies = createSelectableStudies();
		d_studyListPm = new DefaultSelectableStudyListPresentation(new DefaultStudyListPresentation(d_selectableStudies));
		d_studyListPm.getSelectedStudiesModel().addListDataListener(listener);
	}

	private void updateOutcomes() {
		SortedSet<OutcomeMeasure> outcomeSet = new TreeSet<OutcomeMeasure>();
		if (d_indicationHolder.getValue() != null) {
			for (Study s : d_domain.getStudies(this.d_indicationHolder.getValue())) {
				outcomeSet.addAll(Study.extractVariables(s.getEndpoints()));
				outcomeSet.addAll(Study.extractVariables(s.getAdverseEvents()));
			}			
		}	
		d_outcomes.clear();
		d_outcomes.addAll(outcomeSet);
	}
	
	private ObservableList<Study> createSelectableStudies() {
		final FilteredObservableList<Study> studies = new FilteredObservableList<Study>(getStudiesEndpointAndIndication(), new SelectedDrugsFilter());
		getSelectedDrugsModel().addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				studies.setFilter((Filter<Study>) new SelectedDrugsFilter());
			}
			public void intervalAdded(ListDataEvent e) {
				studies.setFilter((Filter<Study>) new SelectedDrugsFilter());
			}
			public void contentsChanged(ListDataEvent e) {
				studies.setFilter((Filter<Study>) new SelectedDrugsFilter());
			}
		});
		return studies;
	}
	
	private ObservableList<Study> createStudiesIndicationOutcome() {
		final FilteredObservableList<Study> studies = new FilteredObservableList<Study>(d_domain.getStudies(), getIndicationOutcomeFilter());
		PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				studies.setFilter(getIndicationOutcomeFilter());
			}
		};
		d_indicationHolder.addPropertyChangeListener(listener);
		d_outcomeHolder.addPropertyChangeListener(listener);
		return studies;
	}

	private Filter<Study> getIndicationOutcomeFilter() {
		if (d_indicationHolder.getValue() == null || d_outcomeHolder.getValue() == null) {
			return new FalseFilter();
		} else {
			return new IndicationOutcomeFilter();			
		}
	}

	protected abstract void buildDrugHolders();
	
	abstract protected G buildStudyGraphPresentation();
	
	/**
	 * Return all studies that measure the selected endpoint on the selected indication for at least two drugs.
	 * @return List of studies
	 */
	protected ObservableList<Study> getStudiesEndpointAndIndication() {
		return d_studiesEndpointIndication;
	}	
	
	public ValueModel getStudiesMeasuringLabelModel() {
		return d_studiesMeasuringValueModel;
	}	
	
	public ValueHolder<OutcomeMeasure> getOutcomeMeasureModel() {
		return d_outcomeHolder;
	}

	public ObservableList<DrugSet> getDrugListModel() {
		return d_drugListHolder;
	}

	public ObservableList<OutcomeMeasure> getOutcomeMeasureListModel() {
		return d_outcomes;
	}

	public abstract ObservableList<DrugSet> getSelectedDrugsModel();

	public G getStudyGraphModel() {
		return d_studyGraphPresentationModel;
	}

	protected void updateArmHolders() {
		d_selectedArms.clear();
		
		for(Study s : getStudyListModel().getSelectedStudiesModel()) {
			d_selectedArms.put(s, new HashMap<DrugSet, ModifiableHolder<Arm>>());
			for(DrugSet d : getSelectedDrugsModel()){
				if(s.getDrugs().contains(d)){
					d_selectedArms.get(s).put(d, new ModifiableHolder<Arm>(getDefaultArm(s, d)));
				}
			}
		}
	}

	private Arm getDefaultArm(Study s, DrugSet d) {
		return getArmsPerStudyPerDrug(s, d).get(0);
	}

	public ObservableList<Arm> getArmsPerStudyPerDrug(Study study, DrugSet drug) {
		return new FilteredObservableList<Arm>(study.getArms(), new StudyArmDrugsFilter(study, drug));
	}

	public ModifiableHolder<Arm> getSelectedArmModel(Study study, DrugSet drug) {
		return d_selectedArms.get(study).get(drug);
	}

	public SelectableStudyListPresentation getStudyListModel() {
		return d_studyListPm;
	}

	public MetaAnalysis saveMetaAnalysis(String name) throws EntityIdExistsException {
		MetaAnalysis ma = createMetaAnalysis(name);		
		d_domain.getMetaAnalyses().add(ma);
		return ma;
	}

	public abstract MetaAnalysis createMetaAnalysis(String name);

	protected ObservableList<Study> getSelectableStudies() {
		return d_selectableStudies;
	}
	
	@SuppressWarnings("serial")
	public class StudiesMeasuringValueModel extends AbstractValueModel implements PropertyChangeListener {
		
		public StudiesMeasuringValueModel() {
			d_outcomeHolder.addValueChangeListener(this);			
		}

		public Object getValue() {
			return constructString();
		}

		private Object constructString() {
			String indVal = d_indicationHolder.getValue() != null ? d_indicationHolder.getValue().toString() : "";
			String endpVal = d_outcomeHolder.getValue() != null ? d_outcomeHolder.getValue().toString() : "";
			return "Studies measuring " + indVal + " on " + endpVal;
		}
		
		public void setValue(Object newValue) {
			throw new RuntimeException("value set not allowed");
		}

		public void propertyChange(PropertyChangeEvent arg0) {
			fireValueChange(null, constructString());
		}		
	}

	private final class FalseFilter implements Filter<Study> {
		public boolean accept(Study s) {
			return false;
		}
	}

	private final class IndicationOutcomeFilter implements Filter<Study> {
		public boolean accept(Study s) {
			return s.getIndication().equals(d_indicationHolder.getValue()) && 
			s.getOutcomeMeasures().contains(d_outcomeHolder.getValue()) && 
			s.getMeasuredDrugs(d_outcomeHolder.getValue()).size() >= 2;
		}
	}
	
	public class SelectedDrugsFilter implements Filter<Study> {
		public boolean accept(Study s) {
			if(d_outcomeHolder.getValue() == null) {
				return false;
			}
			Set<DrugSet> drugs = new HashSet<DrugSet>(s.getMeasuredDrugs(d_outcomeHolder.getValue()));
			List<DrugSet> value = getSelectedDrugsModel();
			drugs.retainAll(value);
			return drugs.size() >= 2;
		}
	}

	public class StudyArmDrugsFilter implements Filter<Arm> {
		private final DrugSet d_drugs;
		private final Study d_study;
		public StudyArmDrugsFilter(Study s, DrugSet drugs) {
			d_study = s;
			d_drugs = drugs;
		}
		public boolean accept(Arm a) {
			return d_study.getDrugs(a).equals(d_drugs);
		}

	}
	
}
