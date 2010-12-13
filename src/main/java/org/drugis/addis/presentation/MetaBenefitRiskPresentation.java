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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.mcmcmodel.AbstractBaselineModel;
import org.drugis.common.gui.task.TaskProgressModel;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.mtc.MCMCModel;

@SuppressWarnings("serial")
public class MetaBenefitRiskPresentation extends AbstractBenefitRiskPresentation<Drug, MetaBenefitRiskAnalysis> {
	
	private AllSummariesDefinedModel d_allSummariesDefinedModel;
	private List<MCMCModel> d_baselineModels = new ArrayList<MCMCModel>();
	private Map<Task, TaskProgressModel> d_progressModels = new HashMap<Task, TaskProgressModel>();

	public MetaBenefitRiskPresentation(MetaBenefitRiskAnalysis bean, PresentationModelFactory pmf) {
		super(bean, pmf);
		
		d_pmf = pmf;
		initAllBaselineModels();
		initAllProgressModels();
		d_allSummariesDefinedModel = new AllSummariesDefinedModel(bean.getEffectSummaries());
		
		if (bean.getAnalysisType().equals(AnalysisType.SMAA)) {
			startSMAA();
		} else {
			startLyndOBrien();
		}
	}

	private void startSMAA() {
		if ((Boolean)getMeasurementsReadyModel().getValue()) {
			getSMAAPresentation().startSMAA();
		}
		
		getMeasurementsReadyModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				getSMAAPresentation().startSMAA();
			}
		});
	}

	private void startLyndOBrien() {
		if (getMeasurementsReadyModel().getValue()) {
			getLyndOBrienPresentation().startLyndOBrien();
		}
		
		getMeasurementsReadyModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				getLyndOBrienPresentation().startLyndOBrien();
			}
		});
	}
	
	public ListHolder<MetaAnalysis> getAnalysesModel() {
		// FIXME: By the time it's possible the edit BR-analyses, this listholder should be hooked up.
		return new DefaultListHolder<MetaAnalysis>(getBean().getMetaAnalyses());
	}

	
	@Override
	public ValueHolder<Boolean> getMeasurementsReadyModel() {
		return d_allSummariesDefinedModel;
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
			tasks.add(model.getActivityTask());
		}
		return tasks;
	}
	
	private void initAllBaselineModels() {
		AbstractBaselineModel<?> model;
		for (OutcomeMeasure om : getBean().getOutcomeMeasures()) {
			model = getBean().getBaselineModel(om);
			d_baselineModels.add(model);
		}
	}
	

	private void initAllProgressModels() {
		for (MetaAnalysis ma : getBean().getMetaAnalyses()) {
			if (ma instanceof NetworkMetaAnalysis) {
				NetworkMetaAnalysis nma = (NetworkMetaAnalysis)ma;
				NetworkMetaAnalysisPresentation p = (NetworkMetaAnalysisPresentation)d_pmf.getModel(nma);
				d_progressModels.put(nma.getConsistencyModel().getActivityTask(), 
						p.getProgressModel(nma.getConsistencyModel()));
			}
		}
		for (Task t : getBaselineTasks()) {
			d_progressModels.put(t, new TaskProgressModel(t));
		}
	}
	
	public BenefitRiskMeasurementTableModel<Drug> getAbsoluteMeasurementTableModel() {
		return new BenefitRiskMeasurementTableModel<Drug>(getBean(), getBean().getAbsoluteMeasurementSource() , d_pmf);
	}

	public BenefitRiskMeasurementTableModel<Drug> getRelativeMeasurementTableModel() {
		return new BenefitRiskMeasurementTableModel<Drug>(getBean(), getBean().getRelativeMeasurementSource(), d_pmf);
	}

	public Drug getBaseline() {
		return getBean().getBaseline();
	}

	public TaskProgressModel getProgressModel(Task t) {
		return d_progressModels.get(t);
	}
}