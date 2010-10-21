/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Treatment;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

@SuppressWarnings("serial")
public class NetworkMetaAnalysisPresentation extends AbstractMetaAnalysisPresentation<NetworkMetaAnalysis> {

	private DefaultCategoryDataset d_dataset;
	ValueHolder<Boolean> d_inconsistencyModelConstructed;
	
	static class ModelConstructionFinishedModel extends UnmodifiableHolder<Boolean> implements TaskListener {
		private Task d_task;
		public ModelConstructionFinishedModel(MixedTreatmentComparison model) {
			super(model.getActivityTask().getModel().getStartState().isFinished());
			d_task = model.getActivityTask().getModel().getStartState();
			d_task.addTaskListener(this);
		}

		public void taskEvent(TaskEvent event) {
			fireValueChange(false, true);
		}

		public Boolean getValue() {
			return d_task.isFinished();
		}
	}

	public NetworkMetaAnalysisPresentation(NetworkMetaAnalysis bean, PresentationModelFactory mgr) {
		super(bean, mgr);
		d_inconsistencyModelConstructed = new ModelConstructionFinishedModel(getBean().getInconsistencyModel());
	}
	
	public String getNetworkXML() {
		return getBean().getNetwork().toPrettyXML();
	}

	public StudyGraphModel getStudyGraphModel() {
		return new StudyGraphModel(new DefaultListHolder<Study>(getBean().getIncludedStudies()),
				new DefaultListHolder<Drug>(getBean().getIncludedDrugs()));
	}

	public CategoryDataset getRankProbabilityDataset() {

		d_dataset = new DefaultCategoryDataset();
		ConsistencyModel consistencyModel = getBean().getConsistencyModel();

		if (!consistencyModel.isReady()) {
			consistencyModel.getActivityTask().addTaskListener(new TaskListener() {
				public void taskEvent(TaskEvent event) {
					if (event.getType() == EventType.TASK_FINISHED) {
						fillDataSet();
					}
				}
			});
		}
		else
			fillDataSet();

		return d_dataset;
	}

	private void fillDataSet() {
		NetworkBuilder<? extends org.drugis.mtc.Measurement> builder = getBean().getBuilder();
		ConsistencyModel consistencyModel = getBean().getConsistencyModel();
		for (Drug d : getBean().getIncludedDrugs()) {
			for (int rank = 1; rank <= getBean().getIncludedDrugs().size(); ++rank) {	
				Treatment treatment = builder.getTreatment(d.toString());
				double rankProb = consistencyModel.rankProbability(treatment, rank);
				d_dataset.addValue((Number) rankProb, "Rank " + rank, d);
			}	
		}
	}
	
	public ValueHolder<Boolean> getInconsistencyModelConstructedModel() {
		return d_inconsistencyModelConstructed;
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
}
