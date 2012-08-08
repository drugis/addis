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

package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.gui.AddisMCMCPresentation;
import org.drugis.common.gui.task.TaskProgressModel;
import org.drugis.mtc.model.Network;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.presentation.ConsistencyWrapper;
import org.drugis.mtc.presentation.InconsistencyWrapper;
import org.drugis.mtc.presentation.MTCModelWrapper;
import org.drugis.mtc.presentation.NodeSplitWrapper;
import org.drugis.mtc.presentation.results.NodeSplitResultsTableModel;
import org.drugis.mtc.presentation.results.RankProbabilityDataset;
import org.drugis.mtc.presentation.results.RankProbabilityTableModel;
import org.jfree.data.category.CategoryDataset;

import com.jgoodies.binding.list.ArrayListModel;

@SuppressWarnings("serial")
public class NetworkMetaAnalysisPresentation extends AbstractMetaAnalysisPresentation<NetworkMetaAnalysis> {
	private Map<MTCModelWrapper<TreatmentDefinition>, AddisMCMCPresentation> d_models;
	public NetworkMetaAnalysisPresentation(NetworkMetaAnalysis bean, PresentationModelFactory mgr) {
		super(bean, mgr);
		d_models = new HashMap<MTCModelWrapper<TreatmentDefinition>, AddisMCMCPresentation>();
		addModel(getConsistencyModel(), getBean().getOutcomeMeasure(), getBean().getName() + " \u2014 " + getConsistencyModel().getDescription());
		addModel(getInconsistencyModel(), getBean().getOutcomeMeasure(), getBean().getName() + " \u2014 " + getInconsistencyModel().getDescription());
		for (BasicParameter p : getBean().getSplitParameters()) {
			NodeSplitWrapper<TreatmentDefinition> m = getBean().getNodeSplitModel(p);
			addModel(m, getBean().getOutcomeMeasure(), getBean().getName() + " \u2014 " + m.getDescription());
		}
		for(MTCModelWrapper<TreatmentDefinition> model : d_models.keySet()) { 
			model.addPropertyChangeListener(new PropertyChangeListener() {		
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if(evt.getPropertyName().equals(MTCModelWrapper.PROPERTY_DESTROYED)) { 
						d_models.remove(evt.getSource());
					}
				}
			});
		}
	}

	public TreatmentDefinition getTreatmentCategorySet(Treatment t) { 
		return getBean().getBuilder().getTreatmentMap().getKey(t);
	}
	
	public StudyGraphModel getStudyGraphModel() {
		return new StudyGraphModel(new ArrayListModel<Study>(getBean().getIncludedStudies()),
				new ArrayListModel<TreatmentDefinition>(getBean().getAlternatives()), new UnmodifiableHolder<OutcomeMeasure>(getBean().getOutcomeMeasure()));
	}

	public CategoryDataset getRankProbabilityDataset() {
		return new RankProbabilityDataset(getBean().getConsistencyModel().getRankProbabilities(), this.getConsistencyModel());
	}
	
	public TableModel getRankProbabilityTableModel() {
		return new RankProbabilityTableModel(getBean().getConsistencyModel().getRankProbabilities(), this.getConsistencyModel());
	}

	public String getRankProbabilityRankChartNote() {
		if(getBean().getOutcomeMeasure().getDirection() == Direction.HIGHER_IS_BETTER) {
			//return "A lower rank indicates the drug is better";
			return "Rank 1 is best, rank N is worst.";
		} else {
			//return "A higher rank indicates the drug is better";
			return "Rank 1 is worst, rank N is best.";
		}
	}

	public TaskProgressModel getProgressModel(MTCModelWrapper<TreatmentDefinition> mtc) {
		return d_models.get(mtc).getProgressModel();
	}
	
	private void addModel(MTCModelWrapper<TreatmentDefinition> mtc, OutcomeMeasure om, String name) {
		d_models.put(mtc, new AddisMCMCPresentation(mtc, om, name));
	}

	public List<BasicParameter> getSplitParameters() {
		return getBean().getSplitParameters();
	}

	public NodeSplitWrapper<TreatmentDefinition> getNodeSplitModel(BasicParameter p) {
		return getBean().getNodeSplitModel(p);
	}

	public ConsistencyWrapper<TreatmentDefinition> getConsistencyModel() {
		return getBean().getConsistencyModel();
	}
	
	public InconsistencyWrapper<TreatmentDefinition> getInconsistencyModel() {
		return getBean().getInconsistencyModel();
	}

	public List<TreatmentDefinition> getIncludedDrugs() {
		return getBean().getAlternatives();
	}

	public boolean isContinuous() {
		return getBean().isContinuous();
	}

	public Network getNetwork() {
		return getBean().getNetwork();
	}
	
	public AddisMCMCPresentation getWrappedModel(MTCModelWrapper<TreatmentDefinition> m) {
		if(d_models.get(m) == null) {
			addModel(m, getBean().getOutcomeMeasure(),  getBean().getName() + " \u2014 " + m.getDescription());
		}
		return d_models.get(m);
	}

	public NodeSplitResultsTableModel createNodeSplitResultsTableModel() {
		List<NodeSplitWrapper<?>> list = new ArrayList<NodeSplitWrapper<?>>();
		for (BasicParameter p : this.getSplitParameters()) {
			list.add(this.getNodeSplitModel(p));
		}
		final List<NodeSplitWrapper<?>> nodeSplitModels = list;
		return new NodeSplitResultsTableModel(this.getConsistencyModel(), nodeSplitModels);
	}	
}
