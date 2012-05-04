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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.gui.MCMCWrapper;
import org.drugis.addis.mcmcmodel.AbstractBaselineModel;
import org.drugis.common.gui.task.TaskProgressModel;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.threading.status.TaskFinishedModel;
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.mtc.MixedTreatmentComparison;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class MetaBenefitRiskPresentation extends AbstractBenefitRiskPresentation<DrugSet, MetaBenefitRiskAnalysis> {
	private ValueHolder<Boolean> d_measurementsReadyModel;
	private HashMap<Task, MCMCWrapper> d_models;
	
	public static class WrappedBaselineModel extends MCMCWrapper {

		public WrappedBaselineModel(AbstractBaselineModel<?> model, OutcomeMeasure om, String name) {
			super(model, om, name);
		}

		@Override
		public ValueHolder<Boolean> isModelConstructed() {
			return new UnmodifiableHolder<Boolean>(true);
		}
				
		@Override
		public int compareTo(MCMCWrapper o) {
			int omCompare = d_om.compareTo(o.getOutcomeMeasure());
			int modelComp = (o.getModel() instanceof MixedTreatmentComparison) ? 1 : -1;
			return (omCompare == 0) ? modelComp : omCompare;
		}
	}
	
	public MetaBenefitRiskPresentation(MetaBenefitRiskAnalysis bean, PresentationModelFactory pmf) {
		super(bean, pmf);
		
		d_pmf = pmf;
	}
	
	@Override
	protected void initSimulations() {
		d_models = new HashMap<Task, MCMCWrapper>();
		initAllBaselineModels();
		initNetworkMetaAnalysisModels();
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
		for (Task task : d_models.keySet()) {
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


	private List<Task> getBaselineTasks() {
		List<Task> tasks = new ArrayList<Task>();
		for (MCMCWrapper model : d_models.values()) {
			if(model.getModel() instanceof AbstractBaselineModel) {
				tasks.add(model.getActivityTask());
			}
		}
		return tasks;
	}
	
	private void initAllBaselineModels() {
		AbstractBaselineModel<?> model;
		for (OutcomeMeasure om : getBean().getCriteria()) {
			model = getBean().getBaselineModel(om);
			String name = getBean().getName() + " \u2014 Baseline Model (" + om.getName() + ")";
			d_models.put(model.getActivityTask(), new WrappedBaselineModel(model, om, name));
		}
	}
	

	private void initNetworkMetaAnalysisModels() {
		for (MetaAnalysis ma : getBean().getMetaAnalyses()) {
			if (ma instanceof NetworkMetaAnalysis) {
				NetworkMetaAnalysis nma = (NetworkMetaAnalysis)ma;
				MixedTreatmentComparison mtc = nma.getConsistencyModel();
				String name = nma.getName() + " \u2014 Consistency Model";
				MCMCWrapper wm = new NetworkMetaAnalysisPresentation.WrappedNetworkMetaAnalysis(mtc, nma.getOutcomeMeasure(), name);
				d_models.put(nma.getConsistencyModel().getActivityTask(), wm);
			}
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
		return d_models.get(t).getProgressModel();
	}

	public MCMCWrapper getWrappedModel(Task t) {
		return d_models.get(t);
	}

	public Collection<MCMCWrapper> getWrappedModels() {
		return new TreeSet<MCMCWrapper>( d_models.values() );
	}

	public synchronized void startAllSimulations() {
		ThreadHandler.getInstance().scheduleTasks(getMeasurementTasks());
	}
}