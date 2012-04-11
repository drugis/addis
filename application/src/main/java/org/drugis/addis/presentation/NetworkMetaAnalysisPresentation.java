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
import org.drugis.common.gui.task.TaskProgressModel;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.NodeSplitModel;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Network;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.summary.NodeSplitPValueSummary;
import org.drugis.mtc.summary.NormalSummary;
import org.drugis.mtc.summary.QuantileSummary;
import org.jfree.data.category.CategoryDataset;

import com.jgoodies.binding.list.ArrayListModel;

@SuppressWarnings("serial")
public class NetworkMetaAnalysisPresentation extends AbstractMetaAnalysisPresentation<NetworkMetaAnalysis> {
	ValueHolder<Boolean> d_inconsistencyModelConstructed;
	private Map<MixedTreatmentComparison,TaskProgressModel> d_progressModels;
	private ValueHolder<Boolean> d_consistencyModelConstructed;
	private Map<BasicParameter, ValueHolder<Boolean>> d_nodesplitModelsConstructed;
	
	static class ModelConstructionFinishedModel extends UnmodifiableHolder<Boolean> implements TaskListener {
		private Task d_task;
		public ModelConstructionFinishedModel(MixedTreatmentComparison model) {
			super(model.getActivityTask().getModel().getStartState().isFinished());
			d_task = model.getActivityTask().getModel().getStartState();
			d_task.addTaskListener(this);
		}

		public void taskEvent(TaskEvent event) {
			if (event.getType().equals(EventType.TASK_FINISHED)) {
				fireValueChange(false, true);
			}
		}

		public Boolean getValue() {
			return d_task.isFinished();
		}
	}

	public NetworkMetaAnalysisPresentation(NetworkMetaAnalysis bean, PresentationModelFactory mgr) {
		super(bean, mgr);
		d_inconsistencyModelConstructed = new ModelConstructionFinishedModel(getInconsistencyModel());
		d_consistencyModelConstructed = new ModelConstructionFinishedModel(getConsistencyModel());
		d_progressModels = new HashMap<MixedTreatmentComparison, TaskProgressModel>();
		d_nodesplitModelsConstructed = new HashMap<BasicParameter, ValueHolder<Boolean>>();
		addModel(getConsistencyModel());
		addModel(getInconsistencyModel());
		for (BasicParameter p : getBean().getSplitParameters()) {
			NodeSplitModel m = getBean().getNodeSplitModel(p);
			addModel(m);
			d_nodesplitModelsConstructed.put(p, new ModelConstructionFinishedModel(m));
		}
	}
	
	public InconsistencyModel getInconsistencyModel() {
		return getBean().getInconsistencyModel();
	}

	public StudyGraphModel getStudyGraphModel() {
		return new StudyGraphModel(new ArrayListModel<Study>(getBean().getIncludedStudies()),
				new ArrayListModel<DrugSet>(getBean().getIncludedDrugs()), new UnmodifiableHolder<OutcomeMeasure>(getBean().getOutcomeMeasure()));
	}

	public CategoryDataset getRankProbabilityDataset() {
		return new RankProbabilityDataset(getBean().getRankProbabilities());
	}
	

	public TableModel getRankProbabilityTableModel() {
		return new RankProbabilityTableModel(getBean().getRankProbabilities());
	}

	public ValueHolder<Boolean> getInconsistencyModelConstructedModel() {
		return d_inconsistencyModelConstructed;
	}
	
	public ValueHolder<Boolean> getConsistencyModelConstructedModel() {
		return d_consistencyModelConstructed;
	}

	public ValueHolder<Boolean> getNodesplitModelConstructedModel(BasicParameter p) {
		return d_nodesplitModelsConstructed.get(p);
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

	public TaskProgressModel getProgressModel(MixedTreatmentComparison mtc) {
		return d_progressModels.get(mtc);
	}
	
	public QuantileSummary getQuantileSummary(MixedTreatmentComparison m, Parameter p) {
		return getBean().getQuantileSummary(m, p);
	}
	
	private TaskProgressModel addModel(MixedTreatmentComparison mtc) {
		return d_progressModels.put(mtc, new TaskProgressModel(mtc.getActivityTask()));
	}

	public List<BasicParameter> getSplitParameters() {
		return getBean().getSplitParameters();
	}

	public NodeSplitModel getNodeSplitModel(BasicParameter p) {
		return getBean().getNodeSplitModel(p);
	}

	public ConsistencyModel getConsistencyModel() {
		return getBean().getConsistencyModel();
	}

	public NodeSplitPValueSummary getNodeSplitPValueSummary(Parameter p) {
		return getBean().getNodesNodeSplitPValueSummary(p);
	}

	public List<Parameter> getInconsistencyFactors() {
		return getBean().getInconsistencyFactors();
	}

	@Deprecated
	public NormalSummary getNormalSummary(MixedTreatmentComparison model, Parameter p) {
		return getBean().getNormalSummary(model, p);
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
}
