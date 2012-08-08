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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.presentation.DefaultSelectableStudyListPresentation;
import org.drugis.addis.presentation.DefaultStudyListPresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyListPresentation;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.beans.FilteredObservableList;
import org.drugis.common.beans.FilteredObservableList.Filter;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public abstract class AbstractMetaAnalysisWizardPM<G extends StudyGraphModel> extends AbstractAnalysisWizardPresentation<MetaAnalysis> {

	protected PresentationModelFactory d_pmf;
	protected ModifiableHolder<OutcomeMeasure> d_outcomeHolder;
	private ObservableList<OutcomeMeasure> d_outcomes = new ArrayListModel<OutcomeMeasure>();	
	
	/** First graph containing only Trivial Categorizations (previously DrugSets) **/
	protected ObservableList<TreatmentDefinition> d_rawTreatmentDefinitionHolder;
	protected G d_rawStudyGraphPresentationModel;	
	
	/** Second graph containing definitions transformed by the selection of TreatmentCategorizations **/
	protected ObservableList<TreatmentDefinition> d_refinedTreatmentDefinitionHolder;
	protected G d_refinedStudyGraphPresentationModel;	
	
	private StudiesMeasuringValueModel d_studiesMeasuringValueModel;
	protected Map<Study, Map<TreatmentDefinition, ModifiableHolder<Arm>>> d_selectedArms;
	protected DefaultSelectableStudyListPresentation d_studyListPm;
	private ObservableList<Study> d_studiesEndpointIndication;
	private ObservableList<Study> d_selectableStudies;
	private final Map<Drug, ValueHolder<TreatmentCategorization>> d_selectedCatgorization = new HashMap<Drug, ValueHolder<TreatmentCategorization>>();
	
	public AbstractMetaAnalysisWizardPM(Domain d, PresentationModelFactory pmf) {
		super(d, d.getMetaAnalyses());
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
		
		d_rawTreatmentDefinitionHolder = new ArrayListModel<TreatmentDefinition>();
		d_outcomeHolder.addValueChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				SortedSet<TreatmentDefinition> definitions = new TreeSet<TreatmentDefinition>();
				if (d_indicationHolder.getValue() != null && d_outcomeHolder.getValue() != null) {
					List<Study> studies = getStudiesEndpointAndIndication();
					for (Study s : studies) {
						definitions.addAll(s.getMeasuredTreatmentDefinitions(d_outcomeHolder.getValue()));
					}			
				}
				d_rawTreatmentDefinitionHolder.clear();
				d_rawTreatmentDefinitionHolder.addAll(definitions);
			}
		});
		
		d_refinedTreatmentDefinitionHolder = new ArrayListModel<TreatmentDefinition>();
		
		d_rawStudyGraphPresentationModel = buildRawStudyGraphPresentation();
		d_refinedStudyGraphPresentationModel = buildRefinedStudyGraphPresentation();

		buildDefinitionHolders();

		d_studiesMeasuringValueModel = new StudiesMeasuringValueModel();
		
		d_selectedArms = new HashMap<Study, Map<TreatmentDefinition, ModifiableHolder<Arm>>>();
		
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
		refineTreatmentDefinitionHolder();
	}

	protected void refineTreatmentDefinitionHolder() {
		d_refinedTreatmentDefinitionHolder.clear();
		for (TreatmentDefinition raw : getSelectedTreatmentDefinitionModel()) {
			TreatmentDefinition refined = new TreatmentDefinition();
			for (Category cat : raw.getContents()) {
				TreatmentCategorization refinedCategorization = (TreatmentCategorization) getCategorizationModel(cat.getDrug()).getValue();
				refined.getContents().addAll(refinedCategorization.getCategories());
			}
			d_refinedTreatmentDefinitionHolder.add(refined);
		}
	}
	
	private void updateOutcomes() {
		SortedSet<OutcomeMeasure> outcomeSet = new TreeSet<OutcomeMeasure>();
		if (d_indicationHolder.getValue() != null) {
			for (Study s : d_domain.getStudies(d_indicationHolder.getValue())) {
				outcomeSet.addAll(Study.extractVariables(s.getEndpoints()));
				outcomeSet.addAll(Study.extractVariables(s.getAdverseEvents()));
			}			
		}	
		d_outcomes.clear();
		d_outcomes.addAll(outcomeSet);
	}
	
	private ObservableList<Study> createSelectableStudies() {
		final FilteredObservableList<Study> studies = new FilteredObservableList<Study>(getStudiesEndpointAndIndication(), new SelectedDrugsFilter());
		getSelectedTreatmentDefinitionModel().addListDataListener(new ListDataListener() {
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

	protected abstract void buildDefinitionHolders();
	
	abstract protected G buildRawStudyGraphPresentation();
	
	abstract protected G buildRefinedStudyGraphPresentation();

	
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

	public ObservableList<TreatmentDefinition> getRawTreatmentDefinitionListModel() {
		return d_rawTreatmentDefinitionHolder;
	}

	public ObservableList<OutcomeMeasure> getOutcomeMeasureListModel() {
		return d_outcomes;
	}

	public abstract ObservableList<TreatmentDefinition> getSelectedTreatmentDefinitionModel();
	
	public void updateRawStudyGraphModel() {
		d_rawStudyGraphPresentationModel.rebuildGraph();
	}
	
	public void updateRefinedStudyGraphModel() {
		d_refinedStudyGraphPresentationModel.rebuildGraph();
	}

	public G getRawStudyGraphModel() {
		return d_rawStudyGraphPresentationModel;
	}
	
	public G getRefinedStudyGraphModel() {
		return d_refinedStudyGraphPresentationModel;
	}

	protected void updateArmHolders() {
		d_selectedArms.clear();
		
		for(Study s : getStudyListModel().getSelectedStudiesModel()) {
			d_selectedArms.put(s, new HashMap<TreatmentDefinition, ModifiableHolder<Arm>>());
			for(TreatmentDefinition d : getSelectedTreatmentDefinitionModel()){
				if(s.getMeasuredTreatmentDefinitions(d_outcomeHolder.getValue()).contains(d)) {
					d_selectedArms.get(s).put(d, new ModifiableHolder<Arm>(getDefaultArm(s, d)));
				}
			}
		}
	}

	private Arm getDefaultArm(Study s, TreatmentDefinition d) {
		return getArmsPerStudyPerDefinition(s, d).get(0);
	}

	public ObservableList<Arm> getArmsPerStudyPerDefinition(Study study, TreatmentDefinition definition) {
		return new FilteredObservableList<Arm>(study.getArms(), new StudyArmDrugsFilter(study, definition));
	}

	public ModifiableHolder<Arm> getSelectedArmModel(Study study, TreatmentDefinition definition) {
		return d_selectedArms.get(study).get(definition);
	}

	public SelectableStudyListPresentation getStudyListModel() {
		return d_studyListPm;
	}

	protected ObservableList<Study> getSelectableStudies() {
		return d_selectableStudies;
	}
	
	public ObservableList<Drug> getCategorizableDrugs() {
		List<Drug> drugs = new LinkedList<Drug>();
		for(TreatmentDefinition definition : getSelectedTreatmentDefinitionModel()) { 
			for(Category category : definition.getContents()) {
				if(!drugs.contains(category.getDrug())) {
					drugs.add(category.getDrug());
				}
			}
		}
		return new ArrayListModel<Drug>(Collections.unmodifiableList(drugs));
	}

	public ValueModel getCategorizationModel(Drug drug) {
		if(d_selectedCatgorization.get(drug) == null) { 
			d_selectedCatgorization.put(drug, new ModifiableHolder<TreatmentCategorization>(TreatmentCategorization.createTrivial(drug)));
		}
		return d_selectedCatgorization.get(drug);
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
			s.getMeasuredTreatmentDefinitions(d_outcomeHolder.getValue()).size() >= 2;
		}
	}
	
	public class SelectedDrugsFilter implements Filter<Study> {
		public boolean accept(Study s) {
			if(d_outcomeHolder.getValue() == null) {
				return false;
			}
			Set<TreatmentDefinition> drugs = new HashSet<TreatmentDefinition>(s.getMeasuredTreatmentDefinitions(d_outcomeHolder.getValue()));
			List<TreatmentDefinition> value = getSelectedTreatmentDefinitionModel();
			drugs.retainAll(value);
			return drugs.size() >= 2;
		}
	}

	public class StudyArmDrugsFilter implements Filter<Arm> {
		private final TreatmentDefinition d_definitions;
		private final Study d_study;
		public StudyArmDrugsFilter(Study s, TreatmentDefinition definitions) {
			d_study = s;
			d_definitions = definitions;
		}
		public boolean accept(Arm a) {
			BasicMeasurement measurement = d_study.getMeasurement(d_outcomeHolder.getValue(), a);
			return d_study.getTreatmentDefinition(a).equals(d_definitions) && measurement != null && measurement.isComplete();
		}

	}
}
