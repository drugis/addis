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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.models.ConsistencyWrapper;
import org.drugis.addis.entities.analysis.models.InconsistencyWrapper;
import org.drugis.addis.entities.analysis.models.MTCModelWrapper;
import org.drugis.addis.entities.analysis.models.NodeSplitWrapper;
import org.drugis.addis.gui.MCMCWrapper;
import org.drugis.common.gui.task.TaskProgressModel;
import org.drugis.common.threading.status.TaskTerminatedModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Network;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.summary.NodeSplitPValueSummary;
import org.drugis.mtc.summary.QuantileSummary;
import org.jfree.data.category.CategoryDataset;

import com.jgoodies.binding.list.ArrayListModel;

@SuppressWarnings("serial")
public class NetworkMetaAnalysisPresentation extends AbstractMetaAnalysisPresentation<NetworkMetaAnalysis> {
	private Map<MTCModelWrapper, WrappedNetworkMetaAnalysis> d_models;
	
	public NetworkMetaAnalysisPresentation(NetworkMetaAnalysis bean, PresentationModelFactory mgr) {
		super(bean, mgr);
		d_models = new HashMap<MTCModelWrapper, WrappedNetworkMetaAnalysis>();
		addModel(getConsistencyModel(), getBean().getOutcomeMeasure(), getBean().getName() + " \u2014 Consistency Model");
		addModel(getInconsistencyModel(), getBean().getOutcomeMeasure(), getBean().getName() + " \u2014 Inconsistency Model");
		for (BasicParameter p : getBean().getSplitParameters()) {
			NodeSplitWrapper m = getBean().getNodeSplitModel(p);
			addModel(m, getBean().getOutcomeMeasure(), getBean().getName() + " \u2014 Node Split on " + p.getName());
		}
	}
	
	public static class WrappedNetworkMetaAnalysis extends MCMCWrapper {
		private ValueHolder<Boolean> d_modelConstructionFinished;
		private final MTCModelWrapper d_wrapper;
		
		public WrappedNetworkMetaAnalysis(MTCModelWrapper mtc, OutcomeMeasure om, String name) {
			super(mtc, om, name);
			d_wrapper = mtc;
			d_modelConstructionFinished = new ValueModelWrapper<Boolean>(
					new TaskTerminatedModel(mtc.getActivityTask().getModel().getStartState()));
		}
		
		@Override
		public TaskProgressModel getProgressModel() {
			return new TaskProgressModel(getActivityTask());
		}

		@Override
		public ValueHolder<Boolean> isModelConstructed() {
			return d_modelConstructionFinished;
		}
		

		@Override
		public int compareTo(MCMCWrapper o) {
			int omCompare = d_om.compareTo(o.getOutcomeMeasure());
			int modelComp = (o.getModel() instanceof MixedTreatmentComparison) ? 1 : -1;
			return (omCompare == 0) ? modelComp : omCompare;
		}

		@Override
		public OutcomeMeasure getOutcomeMeasure() {
			return d_om;
		}

		@Override
		public boolean hasSavedResults() {
			return d_wrapper.hasSavedResults();
		}
	}
	
	public InconsistencyWrapper getInconsistencyModel() {
		return getBean().getInconsistencyModel();
	}

	public StudyGraphModel getStudyGraphModel() {
		return new StudyGraphModel(new ArrayListModel<Study>(getBean().getIncludedStudies()),
				new ArrayListModel<DrugSet>(getBean().getIncludedDrugs()), new UnmodifiableHolder<OutcomeMeasure>(getBean().getOutcomeMeasure()));
	}

	public CategoryDataset getRankProbabilityDataset() {
		return new RankProbabilityDataset(getBean().getConsistencyModel().getRankProbabilities());
	}
	

	public TableModel getRankProbabilityTableModel() {
		return new RankProbabilityTableModel(getBean().getConsistencyModel().getRankProbabilities());
	}

	public ValueHolder<Boolean> getInconsistencyModelConstructedModel() {
		return d_models.get(getBean().getInconsistencyModel()).isModelConstructed();
	}
	
	public ValueHolder<Boolean> getConsistencyModelConstructedModel() {
		return d_models.get(getBean().getConsistencyModel()).isModelConstructed();
	}

	public ValueHolder<Boolean> getNodesplitModelConstructedModel(BasicParameter p) {
		return d_models.get(getBean().getNodeSplitModel(p)).isModelConstructed();
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

	public TaskProgressModel getProgressModel(MTCModelWrapper mtc) {
		return d_models.get(mtc).getProgressModel();
	}
	
	public QuantileSummary getQuantileSummary(MTCModelWrapper m, Parameter p) {
		return m.getQuantileSummary(p);
	}
	
	private void addModel(MTCModelWrapper mtc, OutcomeMeasure om, String name) {
		d_models.put(mtc, new WrappedNetworkMetaAnalysis(mtc, om, name));
	}

	public List<BasicParameter> getSplitParameters() {
		return getBean().getSplitParameters();
	}

	public NodeSplitWrapper getNodeSplitModel(BasicParameter p) {
		return getBean().getNodeSplitModel(p);
	}

	public ConsistencyWrapper getConsistencyModel() {
		return getBean().getConsistencyModel();
	}

	public NodeSplitPValueSummary getNodeSplitPValueSummary(BasicParameter p) {
		return getBean().getNodeSplitModel(p).getNodesNodeSplitPValueSummary();
	}

	public List<Parameter> getInconsistencyFactors() {
		return getBean().getInconsistencyModel().getInconsistencyFactors();
	}

	public List<DrugSet> getIncludedDrugs() {
		return getBean().getIncludedDrugs();
	}

	public boolean isContinuous() {
		return getBean().isContinuous();
	}

	public Network getNetwork() {
		return getBean().getNetwork();
	}
	
	public MCMCWrapper getWrappedModel(MTCModelWrapper m) {
		return d_models.get(m);
	}
}
