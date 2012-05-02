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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.mcmcmodel.AbstractBaselineModel;
import org.drugis.addis.presentation.mcmc.TaskFinishedModel;
import org.drugis.common.gui.task.TaskProgressModel;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.mtc.MCMCModel;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class MetaBenefitRiskPresentation extends AbstractBenefitRiskPresentation<DrugSet, MetaBenefitRiskAnalysis> {
	
	private ValueHolder<Boolean> d_measurementsReadyModel;
	private List<MCMCModel> d_baselineModels;
	private Map<Task, TaskProgressModel> d_progressModels;

	public MetaBenefitRiskPresentation(MetaBenefitRiskAnalysis bean, PresentationModelFactory pmf) {
		super(bean, pmf);
		
		d_pmf = pmf;
		initSimulations();
	}

	@Override
	protected void initSimulations() {
		initAllBaselineModels();
		initAllProgressModels();
	}

	@Override
	protected void startSMAA() {
		if ((Boolean)getMeasurementsReadyModel().getValue()) {
			getSMAAPresentation().startSMAA();
		}
		
		getMeasurementsReadyModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				getSMAAPresentation().startSMAA();
			}
		});
	}

	@Override
	protected void startLyndOBrien() {
		if (getMeasurementsReadyModel().getValue()) {
			getLyndOBrienPresentation().startLyndOBrien();
		}
		
		getMeasurementsReadyModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				getLyndOBrienPresentation().startLyndOBrien();
			}
		});
	}
	
	public ObservableList<MetaAnalysis> getAnalysesModel() {
		return new ArrayListModel<MetaAnalysis>(getBean().getMetaAnalyses());
	}

	
	@Override
	public ValueHolder<Boolean> getMeasurementsReadyModel() {
		if (d_measurementsReadyModel != null) {
			return d_measurementsReadyModel;
		}
		
		List<ValueModel> models = new ArrayList<ValueModel>();
		for (MCMCModel model : d_baselineModels) {
			models.add(new TaskFinishedModel(model.getActivityTask()));
		}
		for (Task task : getBean().getNetworkTasks()) {
			models.add(new TaskFinishedModel(task));
		}
		d_measurementsReadyModel = new ValueModelWrapper<Boolean>(new BooleanAndModel(models));
		
		return d_measurementsReadyModel;
	}
	
	public List<Task> getMeasurementTasks() {
		List<Task> tasks = getBaselineTasks();
		tasks.addAll(getBean().getNetworkTasks());
		return tasks;
	}
	
	public synchronized void startAllSimulations() {
		ThreadHandler.getInstance().scheduleTasks(getMeasurementTasks());
	}

	private List<Task> getBaselineTasks() {
		List<Task> tasks = new ArrayList<Task>();
		for (MCMCModel model : d_baselineModels) {
			tasks.add(model.getActivityTask());
		}
		return tasks;
	}
	
	private void initAllBaselineModels() {
		d_baselineModels = new ArrayList<MCMCModel>();
		AbstractBaselineModel<?> model;
		for (OutcomeMeasure om : getBean().getCriteria()) {
			model = getBean().getBaselineModel(om);
			d_baselineModels.add(model);
		}
	}
	
	private void initAllProgressModels() {
		d_progressModels = new HashMap<Task, TaskProgressModel>();
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
	
	public BRBaselineMeasurementTableModel getBaselineMeasurementTableModel() {
		return new BRBaselineMeasurementTableModel(getBean());

	}

	public BRRelativeMeasurementTableModel getRelativeMeasurementTableModel() {
		return new BRRelativeMeasurementTableModel(getBean());
	}
	
	public BenefitRiskMeasurementTableModel<DrugSet> getMeasurementTableModel() {
		return new BenefitRiskMeasurementTableModel<DrugSet>(getBean());
	}

	public DrugSet getBaseline() {
		return getBean().getBaseline();
	}

	public TaskProgressModel getProgressModel(Task t) {
		return d_progressModels.get(t);
	}

}