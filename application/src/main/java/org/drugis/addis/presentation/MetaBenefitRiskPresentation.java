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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.mtcwrapper.MCMCModelWrapper;
import org.drugis.addis.entities.mtcwrapper.MTCModelWrapper;
import org.drugis.addis.gui.MCMCPresentation;
import org.drugis.addis.mcmcmodel.AbstractBaselineModel;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.threading.status.TaskTerminatedModel;
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.mtc.MixedTreatmentComparison;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class MetaBenefitRiskPresentation extends AbstractBenefitRiskPresentation<DrugSet, MetaBenefitRiskAnalysis> {
	private ValueHolder<Boolean> d_measurementsReadyModel;
	private HashMap<MCMCModelWrapper, MCMCPresentation> d_models;
	
	public static class WrappedBaselineModel extends MCMCPresentation {

		public WrappedBaselineModel(MCMCModelWrapper wrapper, OutcomeMeasure om, String name) {
			super(wrapper, om, name);
		}

		@Override
		public ValueHolder<Boolean> isModelConstructed() {
			return new UnmodifiableHolder<Boolean>(true);
		}
				
		@Override
		public int compareTo(MCMCPresentation o) {
			int omCompare = d_om.compareTo(o.getOutcomeMeasure());
			int modelComp = (o.getModel() instanceof MixedTreatmentComparison) ? 1 : -1;
			return (omCompare == 0) ? modelComp : omCompare;
		}

		@Override
		public boolean hasSavedResults() {
			return false;
		}
	}
	
	public MetaBenefitRiskPresentation(MetaBenefitRiskAnalysis bean, PresentationModelFactory pmf) {
		super(bean, pmf);
		
		d_pmf = pmf;
	}
	
	@Override
	protected void initSimulations() {
		d_models = new HashMap<MCMCModelWrapper, MCMCPresentation>();
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
		for (MCMCModelWrapper wrapper : d_models.keySet()) {
			if (!wrapper.isSaved()) {
				models.add(new TaskTerminatedModel(wrapper.getModel().getActivityTask()));
			}
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
		for (MCMCPresentation model : d_models.values()) {
			if(model.getModel() instanceof AbstractBaselineModel) {
				tasks.add(model.getModel().getActivityTask());
			}
		}
		return tasks;
	}
	
	private void initAllBaselineModels() {
		MCMCModelWrapper wrapper;
		for (OutcomeMeasure om : getBean().getCriteria()) {
			wrapper = getBean().getBaselineModel(om);
			String name = om.getName() + " \u2014 Baseline Model";
			d_models.put(wrapper, new WrappedBaselineModel(wrapper, om, name));
		}
	}
	
	private void initNetworkMetaAnalysisModels() {
		for (MetaAnalysis ma : getBean().getMetaAnalyses()) {
			if (ma instanceof NetworkMetaAnalysis) {
				final NetworkMetaAnalysis nma = (NetworkMetaAnalysis)ma;
				addConsistencyModel(nma);
			}
		}
	}

	private void addConsistencyModel(final NetworkMetaAnalysis nma) {
		d_models.put(nma.getConsistencyModel(), 
				new NetworkMetaAnalysisPresentation.WrappedNetworkMetaAnalysis(nma.getConsistencyModel(), 
				nma.getOutcomeMeasure(),
				nma.getName() + " \u2014 " + nma.getConsistencyModel().getDescription()));
		nma.getConsistencyModel().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals(MTCModelWrapper.PROPERTY_DESTROYED)) {
					MTCModelWrapper source = (MTCModelWrapper) evt.getSource();
					source.removePropertyChangeListener(this);
					d_models.remove(source);
					addConsistencyModel(nma);
				}
			}
		});
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

	public Collection<MCMCPresentation> getWrappedModels() {
		return new TreeSet<MCMCPresentation>( d_models.values() );
	}

	public synchronized void startAllSimulations() {
		ThreadHandler.getInstance().scheduleTasks(getMeasurementTasks());
	}
}