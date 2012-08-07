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

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.gui.AddisMCMCPresentation;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.mcmcmodel.AbstractBaselineModel;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.threading.status.TaskTerminatedModel;
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.mtc.presentation.MCMCModelWrapper;
import org.drugis.mtc.presentation.MTCModelWrapper;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class MetaBenefitRiskPresentation extends AbstractBenefitRiskPresentation<TreatmentDefinition, MetaBenefitRiskAnalysis> {
	private ValueHolder<Boolean> d_measurementsReadyModel;
	private HashMap<MCMCModelWrapper, AddisMCMCPresentation> d_models;

	public MetaBenefitRiskPresentation(final MetaBenefitRiskAnalysis bean, final PresentationModelFactory pmf) {
		super(bean, pmf);

		d_pmf = pmf;
	}

	@Override
	protected void initSimulations() {
		d_models = new HashMap<MCMCModelWrapper, AddisMCMCPresentation>();
		initAllBaselineModels();
		initNetworkMetaAnalysisModels();
	}

	@Override
	protected void startSMAA() {
		if (getMeasurementsReadyModel().getValue()) {
			getSMAAPresentation().startSMAA();
		}

		getMeasurementsReadyModel().addValueChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
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
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
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

		final List<ValueModel> models = new ArrayList<ValueModel>();
		for (final MCMCModelWrapper wrapper : d_models.keySet()) {
			if (!wrapper.isSaved()) {
				models.add(new TaskTerminatedModel(wrapper.getModel().getActivityTask()));
			}
		}
		d_measurementsReadyModel = new ValueModelWrapper<Boolean>(new BooleanAndModel(models));

		return d_measurementsReadyModel;
	}

	public List<Task> getMeasurementTasks() {
		final List<Task> tasks = getBaselineTasks();
		tasks.addAll(getBean().getNetworkTasks());
		return tasks;
	}


	private List<Task> getBaselineTasks() {
		final List<Task> tasks = new ArrayList<Task>();
		for (final AddisMCMCPresentation model : d_models.values()) {
			if(model.getModel() instanceof AbstractBaselineModel) {
				tasks.add(model.getModel().getActivityTask());
			}
		}
		return tasks;
	}

	private void initAllBaselineModels() {
		for (final OutcomeMeasure om : getBean().getCriteria()) {
			addBaselineModel(om);
		}
	}

	private void addBaselineModel(final OutcomeMeasure om) {
		final MCMCModelWrapper baselineModel = getBean().getBaselineModel(om);
		d_models.put(baselineModel, new AddisMCMCPresentation(baselineModel, om, om.getName() + " \u2014 " + baselineModel.getDescription()));
		baselineModel.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals(MCMCModelWrapper.PROPERTY_DESTROYED)) {
					final MCMCModelWrapper source = (MCMCModelWrapper) evt.getSource();
					source.removePropertyChangeListener(this);
					d_models.remove(source);
					addBaselineModel(om);
				}
			}
		});
	}

	private void initNetworkMetaAnalysisModels() {
		for (final MetaAnalysis ma : getBean().getMetaAnalyses()) {
			if (ma instanceof NetworkMetaAnalysis) {
				final NetworkMetaAnalysis nma = (NetworkMetaAnalysis)ma;
				addConsistencyModel(nma);
			}
		}
	}

	private void addConsistencyModel(final NetworkMetaAnalysis nma) {
		d_models.put(nma.getConsistencyModel(),
				new AddisMCMCPresentation(nma.getConsistencyModel(),
				nma.getOutcomeMeasure(),
				nma.getName() + " \u2014 " + nma.getConsistencyModel().getDescription()));
		nma.getConsistencyModel().addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals(MTCModelWrapper.PROPERTY_DESTROYED)) {
					final MCMCModelWrapper source = (MCMCModelWrapper) evt.getSource();
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

	public BenefitRiskMeasurementTableModel<TreatmentDefinition> getMeasurementTableModel() {
		return new BenefitRiskMeasurementTableModel<TreatmentDefinition>(getBean());
	}

	public TreatmentDefinition getBaseline() {
		return getBean().getBaseline();
	}

	public Collection<AddisMCMCPresentation> getWrappedModels() {
		return new TreeSet<AddisMCMCPresentation>( d_models.values() );
	}

	public synchronized void startAllSimulations() {
		ThreadHandler.getInstance().scheduleTasks(getMeasurementTasks());
	}
}
