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

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.mcmcmodel.AbstractBaselineModel;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.summary.Summary;

@SuppressWarnings("serial")
public class MetaBenefitRiskPresentation extends AbstractBenefitRiskPresentation<Drug, MetaBenefitRiskAnalysis> {
	private static class AllModelsReadyListener extends UnmodifiableHolder<Boolean> implements TaskListener {
		private List<Task> d_tasks = new ArrayList<Task>();
		
		public AllModelsReadyListener() {
			super(true);
		}
		
		public void addTask(Task task) {
			task.addTaskListener(this);
			d_tasks.add(task);
		}

		public void taskEvent(TaskEvent event) {
			if (event.getType() == EventType.TASK_FINISHED){
				if(allModelsReady()) {
					fireValueChange(false, true);
				}
			}
		}
		
		@Override 
		public Boolean getValue() {
			return allModelsReady();
		}

		public boolean allModelsReady() {
			for (Task task : d_tasks){
				if (!task.isFinished())
					return false;
			}
			return true;
		}
	}
	
	
	private AllModelsReadyListener d_allNetworkModelsReadyListener;
	private List<MCMCModel> d_baselineModels;

	public boolean allNMAModelsReady() {
		return d_allNetworkModelsReadyListener.allModelsReady();
	}
	
	public MetaBenefitRiskPresentation(MetaBenefitRiskAnalysis bean, PresentationModelFactory pmf) {
		super(bean, pmf);
		
		d_pmf = pmf;
		d_allNetworkModelsReadyListener = new AllModelsReadyListener();

		/* 
		 * Only start SMAA if all networks are already done calculating when running this constructor.
		 * If not, the 'ready' event of the networks will trigger the creation of the SMAA model.
		 */
		
		d_baselineModels = new ArrayList<MCMCModel>();
		initAllBaselineModels();
		initAllNetworkAnalyses();
	}
	
//	public int getNumNMAProgBars() {
//		return d_NMAnalysisProgressListeners.size();
//	}
//	
//	public int getNumBaselineProgBars() {
//		return d_baselineProgressListeners.size();
//	}
//	
//	public void attachNMAProgBar(JProgressBar bar, int progBarNum) {
//		d_NMAnalysisProgressListeners.get(progBarNum).attachBar(bar);
//	}
//	
//	public void attachBaselineProgBar(JProgressBar bar, int progBarNum) {
//		d_baselineProgressListeners.get(progBarNum).attachBar(bar);
//	}
	
	public ListHolder<MetaAnalysis> getAnalysesModel() {
		// FIXME: By the time it's possible the edit BR-analyses, this listholder should be hooked up.
		return new DefaultListHolder<MetaAnalysis>(getBean().getMetaAnalyses());
	}

	
	private void initAllNetworkAnalyses() {
		for (MetaAnalysis ma : getBean().getMetaAnalyses() ){
			if (ma instanceof NetworkMetaAnalysis) {
				ConsistencyModel consistencyModel = ((NetworkMetaAnalysis) ma).getConsistencyModel();
				d_allNetworkModelsReadyListener.addTask(consistencyModel.getActivityTask());
			}
		}
	}
	
	@Override
	public ValueHolder<Boolean> getMeasurementsReadyModel() {
		return d_allNetworkModelsReadyListener;
	}
	
	public List<Task> getMeasurementTasks() {
		List<Task> tasks = getBaselineTasks();
		tasks.addAll(getBean().getNetworkTasks());
		return tasks;
	}
	
	@Override
	public synchronized void startAllSimulations() {
		getBean().runAllConsistencyModels();
		List<Task> tasks = getBaselineTasks();
		ThreadHandler.getInstance().scheduleTasks(tasks);
	}

	private List<Task> getBaselineTasks() {
		List<Task> tasks = new ArrayList<Task>();
		for (MCMCModel model : d_baselineModels) {
			if (!model.isReady()) {
				tasks.add((Task) model.getActivityTask());
			}
		}
		return tasks;
	}
	
	private void initAllBaselineModels() {
		AbstractBaselineModel<?> model;
		for (OutcomeMeasure om : getBean().getOutcomeMeasures()) {
			model = getBean().getBaselineModel(om);
			d_baselineModels.add(model);
			d_allNetworkModelsReadyListener.addTask(model.getActivityTask());
		}
	}
	
	public BenefitRiskMeasurementTableModel<Drug> getAbsoluteMeasurementTableModel() {
		return new BenefitRiskMeasurementTableModel<Drug>(getBean(), getBean().getAbsoluteMeasurementSource() , d_pmf);
	}

	public BenefitRiskMeasurementTableModel<Drug> getRelativeMeasurementTableModel() {
		return new BenefitRiskMeasurementTableModel<Drug>(getBean(), getBean().getRelativeMeasurementSource(), d_pmf);
	}
}

