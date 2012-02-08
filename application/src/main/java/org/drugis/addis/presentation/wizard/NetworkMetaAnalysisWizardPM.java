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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyGraphModel;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.StudyGraphModel.Edge;
import org.drugis.addis.presentation.StudyGraphModel.Vertex;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class NetworkMetaAnalysisWizardPM extends AbstractMetaAnalysisWizardPM<SelectableStudyGraphModel>{
	private StudyGraphModel d_selectedStudyGraph;
	private ValueHolder<Boolean> d_selectedStudyGraphConnectedModel;

	public NetworkMetaAnalysisWizardPM(Domain d, PresentationModelFactory pmf) {
		super(d, pmf);
		d_selectedStudyGraph = new StudyGraphModel(getSelectedStudiesModel(), getSelectedDrugsModel(), getOutcomeMeasureModel());
		getSelectedDrugsModel().addListDataListener(new ListDataListener() {
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
	}

	@Override
	public ObservableList<DrugSet> getSelectedDrugsModel() {
		return d_studyGraphPresentationModel.getSelectedDrugsModel();
	}
	
	public StudyGraphModel getSelectedStudyGraphModel(){
		return d_selectedStudyGraph;
	}

	@Override
	protected SelectableStudyGraphModel buildStudyGraphPresentation() {
		return new SelectableStudyGraphModel(getStudiesEndpointAndIndication(), d_drugListHolder, d_outcomeHolder);
	}
	
	public ValueModel getConnectedDrugsSelectedModel() {
		return getStudyGraphModel().getSelectionCompleteModel();
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
			getSelectedStudyGraphModel().addGraphListener(new GraphListener<Vertex, Edge>() {
				
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
			new ConnectivityInspector<Vertex, Edge>(getSelectedStudyGraphModel());
		return inspectorGadget.isGraphConnected();
	}

	@Override
	public NetworkMetaAnalysis createMetaAnalysis(String name) {
		Indication indication = getIndicationModel().getValue();
		OutcomeMeasure om = getOutcomeMeasureModel().getValue();
		List<Study> studies = getSelectedStudiesModel();
		List<DrugSet> drugs = getSelectedDrugsModel();
		Map<Study, Map<DrugSet, Arm>> armMap = getArmMap();
		
		return new NetworkMetaAnalysis(name, indication, om, studies, drugs, armMap);
	}

	private Map<Study, Map<DrugSet, Arm>> getArmMap() {
		Map<Study, Map<DrugSet, Arm>> map = new HashMap<Study, Map<DrugSet,Arm>>();
		for (Study s : d_selectedArms.keySet()) {
			map.put(s, new HashMap<DrugSet, Arm>());
			for (DrugSet d : d_selectedArms.get(s).keySet()) {
				map.get(s).put(d, d_selectedArms.get(s).get(d).getValue());
			}
		}
		return map;
	}

	public ObservableList<Study> getSelectedStudiesModel() {
		return getStudyListModel().getSelectedStudiesModel();
	}

	public void updateSelectedStudyGraphModel() {
		d_selectedStudyGraph.drugsChanged();
	}

	@Override
	protected void buildDrugHolders() {
		// TODO Auto-generated method stub
		
	}
}
