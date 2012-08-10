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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableTreatmentDefinitionsGraphModel;
import org.drugis.addis.presentation.TreatmentDefinitionsGraphModel;
import org.drugis.addis.presentation.TreatmentDefinitionsGraphModel.Edge;
import org.drugis.addis.presentation.TreatmentDefinitionsGraphModel.Vertex;
import org.drugis.addis.presentation.ValueHolder;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class NetworkMetaAnalysisWizardPM extends AbstractMetaAnalysisWizardPM<SelectableTreatmentDefinitionsGraphModel> {
	private final TreatmentDefinitionsGraphModel d_selectedStudyGraph;
	private final ValueHolder<Boolean> d_selectedStudyGraphConnectedModel;
	
	public NetworkMetaAnalysisWizardPM(Domain d, PresentationModelFactory pmf) {
		super(d, pmf);
		
		d_selectedStudyGraph = new TreatmentDefinitionsGraphModel(getSelectedStudiesModel(), getSelectedRawTreatmentDefinitions(), getOutcomeMeasureModel());
		
		d_selectedStudyGraphConnectedModel = new StudySelectionCompleteListener();
	}

	
	@Override
	public ObservableList<TreatmentDefinition> getSelectedRawTreatmentDefinitions() {
		return d_rawAlternativesGraph.getSelectedDefinitions();
	}
	
	@Override
	public ObservableList<TreatmentDefinition> getSelectedRefinedTreatmentDefinitions() {
		return d_refinedAlternativesGraph.getSelectedDefinitions();
	}
	
	public TreatmentDefinitionsGraphModel getOverviewGraph(){
		return d_selectedStudyGraph;
	}

	@Override
	protected SelectableTreatmentDefinitionsGraphModel buildRawAlternativesGraph() {
		return new SelectableTreatmentDefinitionsGraphModel(getStudiesEndpointAndIndication(), d_rawTreatmentDefinitions, d_outcomeHolder);
	}

	@Override
	protected SelectableTreatmentDefinitionsGraphModel buildRefinedAlternativesGraph() {
		return new SelectableTreatmentDefinitionsGraphModel(getStudiesEndpointAndIndication(), d_refinedTreatmentDefinitions, d_outcomeHolder);
	}
	
	public ValueModel getRawSelectionCompleteModel() {
		return getRawAlternativesGraph().getSelectionCompleteModel();
	}
	
	public ValueModel getRefinedConnectedDrugsSelectedModel() {
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
	public NetworkMetaAnalysis createAnalysis(String name) {
		Indication indication = getIndicationModel().getValue();
		OutcomeMeasure om = getOutcomeMeasureModel().getValue();
		List<Study> studies = getSelectedStudiesModel();
		List<TreatmentDefinition> definitions = d_rawTreatmentDefinitions;
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
		return getSelectableStudyListPM().getSelectedStudiesModel();
	}
	
	public void rebuildRawAlternativesGraph() {
		d_rawAlternativesGraph.rebuildGraph();
	}
	
	public void rebuildOverviewGraph() {
		d_selectedStudyGraph.rebuildGraph();
	}

	@Override
	protected void buildDefinitionHolders() {	}
}
