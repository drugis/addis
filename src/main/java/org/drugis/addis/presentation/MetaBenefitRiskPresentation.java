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
import org.drugis.common.threading.ThreadHandler;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.Treatment;
import org.drugis.mtc.summary.Summary;

@SuppressWarnings("serial")
public class MetaBenefitRiskPresentation extends AbstractBenefitRiskPresentation<Drug, MetaBenefitRiskAnalysis> {
	
	private AllSummariesDefinedModel d_allSummariesDefinedModel;
	private List<MCMCModel> d_baselineModels;

	public MetaBenefitRiskPresentation(MetaBenefitRiskAnalysis bean, PresentationModelFactory pmf) {
		super(bean, pmf);
		
		d_pmf = pmf;
		d_baselineModels = new ArrayList<MCMCModel>();
		initAllBaselineModels();

		List<Summary> l = new ArrayList<Summary>();
		for(MCMCModel m: d_baselineModels) {
			AbstractBaselineModel<?> abm = (AbstractBaselineModel<?>) m; 
			l.add(abm.getSummary());
		}
		for(Drug d: bean.getAlternatives()) {
			if (!d.equals(bean.getBaseline())) {
				for(MetaAnalysis ma: bean.getMetaAnalyses()) {
					if (ma instanceof NetworkMetaAnalysis) {
						NetworkMetaAnalysis nma = (NetworkMetaAnalysis) ma;
						l.add(nma.getNormalSummary(nma.getConsistencyModel(), 
								nma.getConsistencyModel().getRelativeEffect(new Treatment(bean.getBaseline().getName()), new Treatment(d.getName()))));
					}
				}
			}
		}
		d_allSummariesDefinedModel = new AllSummariesDefinedModel(l);
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
		}
	}
	
	public BenefitRiskMeasurementTableModel<Drug> getAbsoluteMeasurementTableModel() {
		return new BenefitRiskMeasurementTableModel<Drug>(getBean(), getBean().getAbsoluteMeasurementSource() , d_pmf);
	}

	public BenefitRiskMeasurementTableModel<Drug> getRelativeMeasurementTableModel() {
		return new BenefitRiskMeasurementTableModel<Drug>(getBean(), getBean().getRelativeMeasurementSource(), d_pmf);
	}
}

