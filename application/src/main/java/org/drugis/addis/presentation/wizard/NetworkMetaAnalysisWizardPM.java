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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyCharTableModel;
import org.drugis.addis.presentation.SelectableTreatmentDefinitionsGraphModel;
import org.drugis.addis.presentation.StudyListPresentation;
import org.drugis.addis.presentation.TreatmentDefinitionsGraphModel;
import org.drugis.addis.presentation.TreatmentDefinitionsGraphModel.Edge;
import org.drugis.addis.presentation.TreatmentDefinitionsGraphModel.Vertex;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.util.IndifferentListDataListener;
import org.drugis.common.CollectionUtil;
import org.drugis.common.beans.FilteredObservableList;
import org.drugis.common.beans.FilteredObservableList.Filter;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class NetworkMetaAnalysisWizardPM extends AbstractAnalysisWizardPresentation<MetaAnalysis> {
	private static final String TEMPLATE_DEFINITIONS_DESCRIPTION =
			"Select the %1$s to be used for the network meta-analysis. Click to select (green) or deselect (gray).  " +
			"To continue, (1) at least %2$s must be selected, and (2) all selected %1$s must be connected.";
	
	private final TreatmentDefinitionsGraphModel d_overviewGraph;
	private final ValueHolder<Boolean> d_selectedStudyGraphConnectedModel;
	protected PresentationModelFactory d_pmf;
	protected ModifiableHolder<OutcomeMeasure> d_outcomeHolder;
	private ObservableList<OutcomeMeasure> d_outcomes = new ArrayListModel<OutcomeMeasure>();
	
	/** First graph containing only Trivial Categorizations (previously DrugSets) **/
	protected final ObservableList<TreatmentDefinition> d_rawTreatmentDefinitions;
	protected final SelectableTreatmentDefinitionsGraphModel d_rawAlternativesGraph;
	
	/** Second graph containing definitions transformed by the selection of TreatmentCategorizations **/
	protected final ObservableList<TreatmentDefinition> d_refinedTreatmentDefinitions;
	protected final SelectableTreatmentDefinitionsGraphModel d_refinedAlternativesGraph;
	
	private final StudiesMeasuringValueModel d_studiesMeasuringValueModel;
	protected final Map<Study, Map<TreatmentDefinition, ModifiableHolder<Arm>>> d_selectedArms;
	protected final SelectableStudyCharTableModel d_selectableStudyListPm;
	private final ObservableList<Study> d_studiesEndpointIndication;
	private final Map<Drug, ValueHolder<TreatmentCategorization>> d_selectedCategorizations = new HashMap<Drug, ValueHolder<TreatmentCategorization>>();
	private final ObservableList<Study> d_selectableStudies = new ArrayListModel<Study>();
	
	private boolean d_rebuildRawNeeded = true;
	private boolean d_armSelectionRebuildNeeded = true;
	
	/**
	 * Create a filtered list of studies that includes only those studies that
	 * compare at least two of the definitions on the given variable.
	 */
	public static List<Study> filterStudiesComparing(final Variable var, final Collection<Study> studies, final Collection<TreatmentDefinition> defs) {
		return (List<Study>) CollectionUtils.select(studies, new Predicate<Study>() {
			public boolean evaluate(Study s) {
				int count = 0;
				for (TreatmentDefinition def : defs) { 
					if (!s.getMeasuredArms(var, def).isEmpty()) ++count;
				}
				return count > 1;
			}
		});
	}

	/**
	 * Create a filtered list of definitions that includes only those definitions that
	 * are measured on the given variable in at least one study.
	 */
	public static List<TreatmentDefinition> filterDefinitionsMeasured(final Variable var, Collection<TreatmentDefinition> defs, final Collection<Study> studies) {
		return (List<TreatmentDefinition>) CollectionUtils.select(defs, new Predicate<TreatmentDefinition>() {
			public boolean evaluate(TreatmentDefinition def) {
				for (Study s : studies) {
					if (!s.getMeasuredArms(var, def).isEmpty()) {
						return true;
					}
				}
				return false;
			}
		});
	}
	
	public NetworkMetaAnalysisWizardPM(Domain d, PresentationModelFactory pmf) {
		super(d, d.getMetaAnalyses());
		d_pmf = pmf;
	
		d_outcomeHolder = new ModifiableHolder<OutcomeMeasure>();
		
		d_indicationHolder.addPropertyChangeListener(new ClearValueModelsOnPropertyChangeListener(d_outcomeHolder));
		updateOutcomes();
		d_indicationHolder.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateOutcomes();
			}
		});
		
		d_studiesEndpointIndication = createStudiesIndicationOutcome();
		d_studiesEndpointIndication.addListDataListener(new IndifferentListDataListener() {
			protected void update() {
				d_rebuildRawNeeded = true;	
			}
		});

		d_rawTreatmentDefinitions = new ArrayListModel<TreatmentDefinition>();
		d_rawAlternativesGraph = buildRawAlternativesGraph();

		d_refinedTreatmentDefinitions = new ArrayListModel<TreatmentDefinition>();
		d_refinedAlternativesGraph = buildRefinedAlternativesGraph();
		
		getSelectedRefinedTreatmentDefinitions().addListDataListener(new IndifferentListDataListener() {
			protected void update() {
				d_armSelectionRebuildNeeded = true;	
			}
		});
		
		d_studiesMeasuringValueModel = new StudiesMeasuringValueModel();
		
		d_selectedArms = new HashMap<Study, Map<TreatmentDefinition, ModifiableHolder<Arm>>>();
		
		d_selectableStudyListPm = createSelectableStudyListPm();		
		d_overviewGraph = new TreatmentDefinitionsGraphModel(getSelectedStudies(), getSelectedRefinedTreatmentDefinitions(), getOutcomeMeasureModel());	
		d_selectedStudyGraphConnectedModel = new StudySelectionCompleteListener();
	}

	
	public ObservableList<TreatmentDefinition> getSelectedRawTreatmentDefinitions() {
		return d_rawAlternativesGraph.getSelectedDefinitions();
	}
	
	public ObservableList<TreatmentDefinition> getSelectedRefinedTreatmentDefinitions() {
		return d_refinedAlternativesGraph.getSelectedDefinitions();
	}
	
	public TreatmentDefinitionsGraphModel getOverviewGraph(){
		return d_overviewGraph;
	}

	protected SelectableTreatmentDefinitionsGraphModel buildRawAlternativesGraph() {
		return new SelectableTreatmentDefinitionsGraphModel(getStudiesEndpointAndIndication(), d_rawTreatmentDefinitions, d_outcomeHolder, 1, -1);
	}

	protected SelectableTreatmentDefinitionsGraphModel buildRefinedAlternativesGraph() {
		return new SelectableTreatmentDefinitionsGraphModel(getStudiesEndpointAndIndication(), d_refinedTreatmentDefinitions, d_outcomeHolder, 2, -1);
	}
	
	public ValueModel getRawSelectionCompleteModel() {
		return getRawAlternativesGraph().getSelectionCompleteModel();
	}
	
	public ValueModel getRefinedSelectionCompleteModel() {
		return getRefinedAlternativesGraph().getSelectionCompleteModel();
	}
	
	public ValueHolder<Boolean> getOverviewGraphConnectedModel() {
		return d_selectedStudyGraphConnectedModel;
	}
	
	@SuppressWarnings("serial")
	public class StudySelectionCompleteListener extends AbstractValueModel 
	implements ValueHolder<Boolean> {
		private boolean d_value;
		
		public StudySelectionCompleteListener() {
			update();
			getOverviewGraph().addGraphListener(new GraphListener<Vertex, Edge>() {
				
				public void vertexRemoved(GraphVertexChangeEvent<Vertex> e) {
					update();
				}
				
				public void vertexAdded(GraphVertexChangeEvent<Vertex> e) {
					update();
				}
				
				public void edgeRemoved(GraphEdgeChangeEvent<Vertex, Edge> e) {
					update();
				}
				
				public void edgeAdded(GraphEdgeChangeEvent<Vertex, Edge> e) {
					update();
				}
			});
		}
		
		private void update() {
			Boolean oldValue = d_value;
			Boolean newValue = selectedStudiesConnected();
			if (oldValue != newValue) {
				d_value = newValue;
				fireValueChange(oldValue, newValue);
			}
		}

		public Boolean getValue() {
			return d_value;
		}

		public void setValue(Object newValue) {
			throw new RuntimeException();
		}
	}
	
	private boolean selectedStudiesConnected() {
		ConnectivityInspector<Vertex, Edge> inspectorGadget = 
			new ConnectivityInspector<Vertex, Edge>(getOverviewGraph());
		return inspectorGadget.isGraphConnected();
	}

	@Override
	public MetaAnalysis createAnalysis(String name) {
		Indication indication = getIndicationModel().getValue();
		OutcomeMeasure om = getOutcomeMeasureModel().getValue();
		List<Study> studies = getSelectedStudies();
		List<TreatmentDefinition> definitions = getSelectedRefinedTreatmentDefinitions();
		Map<Study, Map<TreatmentDefinition, Arm>> armMap = getArmMap();
		
		return new NetworkMetaAnalysis(name, indication, om, studies, definitions, armMap);
	}

	private Map<Study, Map<TreatmentDefinition, Arm>> getArmMap() {
		Map<Study, Map<TreatmentDefinition, Arm>> map = new HashMap<Study, Map<TreatmentDefinition,Arm>>();
		for (Study s : d_selectedArms.keySet()) {
			map.put(s, new HashMap<TreatmentDefinition, Arm>());
			for (TreatmentDefinition d : d_selectedArms.get(s).keySet()) {
				map.get(s).put(d, d_selectedArms.get(s).get(d).getValue());
			}
		}
		return map;
	}

	public ObservableList<Study> getSelectedStudies() {
		return getSelectableStudyListPM().getSelectedStudiesModel();
	}
	
	public void rebuildOverviewGraph() {
		d_overviewGraph.rebuildGraph();
	}

	public void populateSelectableStudies() {
		d_selectableStudies.clear();
		d_selectableStudies.addAll(filterStudiesComparing(getOutcomeMeasureModel().getValue(), 
				getStudiesEndpointAndIndication(), getRefinedAlternativesGraph().getSelectedDefinitions()));
	}

	private SelectableStudyCharTableModel createSelectableStudyListPm() {
		SelectableStudyCharTableModel studyList = new SelectableStudyCharTableModel(
				new StudyListPresentation(d_selectableStudies), d_pmf);
		studyList.getSelectedStudiesModel().addListDataListener(new IndifferentListDataListener() {
			protected void update() {
				d_armSelectionRebuildNeeded = true;	
			}
		});
		return studyList;
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

	public ObservableList<OutcomeMeasure> getAvailableOutcomeMeasures() {
		return d_outcomes;
	}

	public ValueHolder<OutcomeMeasure> getOutcomeMeasureModel() {
		return d_outcomeHolder;
	}


	public ObservableList<TreatmentDefinition> getAvailableRawTreatmentDefinitions() {
		return d_rawTreatmentDefinitions;
	}


	public ObservableList<TreatmentDefinition> getAvailableRefinedTreatmentDefinitions() {
		return d_refinedTreatmentDefinitions;
	}

	public boolean rebuildRefinedAlternativesGraph() {
		SortedSet<TreatmentDefinition> definitions = new TreeSet<TreatmentDefinition>(permuteTreatmentDefinitions());
		OutcomeMeasure om = getOutcomeMeasureModel().getValue();
		List<Study> studies = filterStudiesComparing(om, getStudiesEndpointAndIndication(), definitions);
		List<TreatmentDefinition> defs = filterDefinitionsMeasured(om, definitions, studies);

		if (d_refinedTreatmentDefinitions.equals(defs)) {
			return false;
		}
		
		d_refinedTreatmentDefinitions.clear();
		d_refinedTreatmentDefinitions.addAll(defs);
		
		d_refinedAlternativesGraph.rebuildGraph();
		return true;
	}


	public boolean rebuildRawAlternativesGraph() {
		if (!d_rebuildRawNeeded) {
			return false;
		}
		// Determine set of treatment definitions
		SortedSet<TreatmentDefinition> definitions = new TreeSet<TreatmentDefinition>();
		if (d_indicationHolder.getValue() != null && d_outcomeHolder.getValue() != null) {
			for (Study s : getStudiesEndpointAndIndication()) {
				definitions.addAll(s.getMeasuredTreatmentDefinitions(d_outcomeHolder.getValue()));
			}			
		}
		d_rawTreatmentDefinitions.clear();
		d_rawTreatmentDefinitions.addAll(definitions);	
		
		// Rebuild the graph
		d_rawAlternativesGraph.rebuildGraph();
		
		d_rebuildRawNeeded = false;
		return true;
	}


	public SelectableTreatmentDefinitionsGraphModel getRefinedAlternativesGraph() {
		return d_refinedAlternativesGraph;
	}


	public SelectableTreatmentDefinitionsGraphModel getRawAlternativesGraph() {
		return d_rawAlternativesGraph;
	}


	public void rebuildArmSelection() {
		if (!d_armSelectionRebuildNeeded) {
			return;
		}
		d_selectedArms.clear();
		
		for (Study s : getSelectableStudyListPM().getSelectedStudiesModel()) {
			d_selectedArms.put(s, new HashMap<TreatmentDefinition, ModifiableHolder<Arm>>());
			for (TreatmentDefinition d : getSelectedRefinedTreatmentDefinitions()) {
				if (!s.getMeasuredArms(d_outcomeHolder.getValue(), d).isEmpty()) {
					d_selectedArms.get(s).put(d, new ModifiableHolder<Arm>(getDefaultArm(s, d)));
				}
			}
		}
		
		d_armSelectionRebuildNeeded = false;
	}

	private Arm getDefaultArm(Study s, TreatmentDefinition d) {
		return getArmsPerStudyPerDefinition(s, d).get(0);
	}


	public ObservableList<Arm> getArmsPerStudyPerDefinition(Study study, TreatmentDefinition definition) {
		return study.getMeasuredArms(d_outcomeHolder.getValue(), definition);
	}


	public ModifiableHolder<Arm> getSelectedArmModel(Study study, TreatmentDefinition definition) {
		return d_selectedArms.get(study).get(definition);
	}


	public SelectableStudyCharTableModel getSelectableStudyListPM() {
		return d_selectableStudyListPm;
	}


	/**
	 * Get the set of drugs that occur in the selected raw treatment definitions.
	 * @see {@link #getSelectedRawTreatmentDefinitions()}
	 */
	public SortedSet<Drug> getSelectedDrugs() {
		SortedSet<Drug> drugs = new TreeSet<Drug>();
		for(TreatmentDefinition definition : getSelectedRawTreatmentDefinitions()) {
			for(Category category : definition.getContents()) {
				drugs.add(category.getDrug());
			}
		}
		return drugs;
	}


	protected Set<TreatmentDefinition> permuteTreatmentDefinitions() {
		Set<TreatmentDefinition> set = new HashSet<TreatmentDefinition>();
		for (TreatmentDefinition trivial : getRawAlternativesGraph().getSelectedDefinitions()) {
			// Find the categorizations relevant to the current combination of drugs
			List<TreatmentCategorization> catzs = new ArrayList<TreatmentCategorization>();
			List<Integer> nCat = new ArrayList<Integer>();
			for (Category category : trivial.getContents()) {
				TreatmentCategorization catz = (TreatmentCategorization) getCategorizationModel(category.getDrug()).getValue();
				catzs.add(catz);
				nCat.add(catz.getCategories().size());
			}
			
			// Generate all permutations of the categories in the relevant categorizations
			int[] c = ArrayUtils.toPrimitive(nCat.toArray(new Integer[] {}));
			int[] x = new int[c.length]; // initialized to { 0, ... }
			do {
				Set<Category> categories = new HashSet<Category>();
				for (int i = 0; i < x.length; ++i) {
					categories.add(catzs.get(i).getCategories().get(x[i]));
				}
				set.add(new TreatmentDefinition(categories));
			} while (CollectionUtil.nextLexicographicElement(x, c));
		}
		return set;
	}


	public List<TreatmentCategorization> getAvailableCategorizations(Drug drug) {
		List<TreatmentCategorization> categorizations = d_domain.getCategorizations(drug);
		categorizations.add(0, TreatmentCategorization.createTrivial(drug));
		return categorizations;
	}

	public ValueModel getCategorizationModel(Drug drug) {
		if(d_selectedCategorizations.get(drug) == null) { 
			d_selectedCategorizations.put(drug, new ModifiableHolder<TreatmentCategorization>(TreatmentCategorization.createTrivial(drug)));
		}
		return d_selectedCategorizations.get(drug);
	}
	
	
	final class FalseFilter implements Filter<Study> {
		public boolean accept(Study s) {
			return false;
		}
	}

	final class IndicationOutcomeFilter implements Filter<Study> {
		public boolean accept(Study s) {
			return s.getIndication().equals(d_indicationHolder.getValue()) && 
					s.getOutcomeMeasures().contains(d_outcomeHolder.getValue()) && 
					s.getMeasuredTreatmentDefinitions(d_outcomeHolder.getValue()).size() >= 2;
		}
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
	
	/** 
	 * Internal utility for tests. This will reset all the graph states and usage is prohibited in the GUI.
	 */
	protected void rebuildAllGraphs() { 
		rebuildRawAlternativesGraph();
		permuteTreatmentDefinitions();
		rebuildRefinedAlternativesGraph();
		rebuildOverviewGraph();
	}

	public String getRawDescription() {
		return String.format(getDescriptionTemplate(), "drugs", "one drug");
	}

	public String getRefinedDescription() {
		return String.format(getDescriptionTemplate(), "treatment definitions", "two definitions");
	}
	
	protected String getDescriptionTemplate() {
		return TEMPLATE_DEFINITIONS_DESCRIPTION;
	}
}
