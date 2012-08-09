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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyGraphModel;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.StudyGraphModel.Edge;
import org.drugis.addis.presentation.StudyGraphModel.Vertex;
import org.drugis.addis.presentation.ValueHolder;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class NetworkMetaAnalysisWizardPM extends AbstractMetaAnalysisWizardPM<SelectableStudyGraphModel>{
	private final StudyGraphModel d_selectedStudyGraph;
	private final ValueHolder<Boolean> d_selectedStudyGraphConnectedModel;
	
	public NetworkMetaAnalysisWizardPM(Domain d, PresentationModelFactory pmf) {
		super(d, pmf);
		d_selectedStudyGraph = new StudyGraphModel(getSelectedStudiesModel(), getSelectedTreatmentDefinitions(), getOutcomeMeasureModel());
		d_refinedTreatmentDefinitions.addListDataListener(new ListDataListener() {
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
		d_selectedStudyGraphConnectedModel = new StudySelectionCompleteListener();
		
		d_rawTreatmentDefinitions.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {}
			
			@SuppressWarnings("unchecked")
			public void intervalAdded(ListDataEvent e) {
				for (int i = e.getIndex0(); i <= e.getIndex1(); ++i) {
					TreatmentDefinition t = ((ObservableList<TreatmentDefinition>)e.getSource()).get(i);
					for(Category category : t.getContents()) { 
						ValueModel model = getCategorizationModel(category.getDrug());
						model.addValueChangeListener(new PropertyChangeListener() {
							public void propertyChange(PropertyChangeEvent evt) {
								updateRefinedTreatmentDefinitions();
							}
						});
					}
				}
			}
			
			public void contentsChanged(ListDataEvent e) {}
		});
	}

	
	@Override
	public ObservableList<TreatmentDefinition> getSelectedTreatmentDefinitions() {
		return d_refinedStudyGraph.getSelectedDefinitions();
	}
	
	public StudyGraphModel getSelectedStudyGraph(){
		return d_selectedStudyGraph;
	}

	@Override
	protected SelectableStudyGraphModel buildRawSelectableStudyGraph() {
		return new SelectableStudyGraphModel(getStudiesEndpointAndIndication(), d_rawTreatmentDefinitions, d_outcomeHolder);
	}
	
	@Override
	protected SelectableStudyGraphModel buildRefinedSelectableStudyGraph() {
		return new SelectableStudyGraphModel(getStudiesEndpointAndIndication(), d_refinedTreatmentDefinitions, d_outcomeHolder);
	}
	
	public ValueModel getRawConnectedDrugsSelectedModel() {
		return getRawStudyGraph().getSelectionCompleteModel();
	}
	
	public ValueModel getRefinedConnectedDrugsSelectedModel() {
		return getRefinedStudyGraph().getSelectionCompleteModel();
	}
	
	public ValueHolder<Boolean> getSelectedStudyGraphConnectedModel() {
		return d_selectedStudyGraphConnectedModel;
	}
	
	@SuppressWarnings("serial")
	public class StudySelectionCompleteListener extends AbstractValueModel 
	implements ValueHolder<Boolean> {
		private boolean d_value;
		
		public StudySelectionCompleteListener() {
			update();
			getSelectedStudyGraph().addGraphListener(new GraphListener<Vertex, Edge>() {
				
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
			new ConnectivityInspector<Vertex, Edge>(getSelectedStudyGraph());
		return inspectorGadget.isGraphConnected();
	}

	
	public List<Drug> getSelectedDrugs() {
		List<Drug> drugs = new LinkedList<Drug>();
		for(TreatmentDefinition definition : d_rawStudyGraph.getSelectedDefinitions()) {
			for(Category category : definition.getContents()) {
				if(!drugs.contains(category.getDrug())) {
					drugs.add(category.getDrug());
				}
			}
		}
		return new ArrayListModel<Drug>(Collections.unmodifiableList(drugs));
	}
	
	@Override
	public NetworkMetaAnalysis createAnalysis(String name) {
		Indication indication = getIndicationModel().getValue();
		OutcomeMeasure om = getOutcomeMeasureModel().getValue();
		List<Study> studies = getSelectedStudiesModel();
		List<TreatmentDefinition> definitions = d_refinedTreatmentDefinitions;
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

	public ObservableList<Study> getSelectedStudiesModel() {
		return getSelectableStudyListPm().getSelectedStudiesModel();
	}

	public void rebuildSelectedStudyGraph() {
		d_selectedStudyGraph.rebuildGraph();
	}

	@Override
	protected void buildDefinitionHolders() {	}
}
