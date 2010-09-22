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

import javax.swing.JProgressBar;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MeasurementSource;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.mcmcmodel.AbstractBaselineModel;
import org.drugis.addis.util.threading.ThreadHandler;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.ProgressEvent.EventType;

import fi.smaa.jsmaa.model.CardinalCriterion;

@SuppressWarnings("serial")
public class MetaBenefitRiskPresentation extends BenefitRiskPresentation<Drug, MetaBenefitRiskAnalysis> {
	
	private class AnalysisProgressListener implements ProgressListener {
		JProgressBar d_progBar;
		private MCMCModel d_networkModel;

		public AnalysisProgressListener(MCMCModel networkModel) {
			networkModel.addProgressListener(this);
			d_networkModel = networkModel;
		}
		
		public void attachBar(JProgressBar bar) {
			d_progBar = bar;
			bar.setVisible(!d_networkModel.isReady());
		}

		public void update(MCMCModel mtc, ProgressEvent event) {
			if(event.getType() == EventType.SIMULATION_PROGRESS && d_progBar != null){
				d_progBar.setString("Simulating: " + event.getIteration()/(event.getTotalIterations()/100) + "%");
				d_progBar.setValue(event.getIteration()/(event.getTotalIterations()/100));
			} else if(event.getType() == EventType.BURNIN_PROGRESS && d_progBar != null){
				d_progBar.setString("Burn in: " + event.getIteration()/(event.getTotalIterations()/100) + "%");
				d_progBar.setValue(event.getIteration()/(event.getTotalIterations()/100));
			} else if(event.getType() == EventType.SIMULATION_FINISHED && d_progBar != null) {
				d_progBar.setString("Done!");
				d_progBar.setValue(100);
			}
		}
	}
	
	private static class AllModelsReadyListener extends UnmodifiableHolder<Boolean> implements ProgressListener {
		private List<MCMCModel> d_models = new ArrayList<MCMCModel>();
		
		public AllModelsReadyListener() {
			super(true);
		}
		
		public void addModel(MCMCModel model) {
			model.addProgressListener(this);
			d_models.add(model);
		}
		
		@Override 
		public Boolean getValue() {
			return allModelsReady();
		}

		public void update(MCMCModel mtc, ProgressEvent event) {
			if (event.getType() == ProgressEvent.EventType.SIMULATION_FINISHED){
				if(allModelsReady()) {
					fireValueChange(false, true);
				}
			}
		}

		public boolean allModelsReady() {
			for (MCMCModel model : d_models){
				if (!model.isReady())
					return false;
			}
			return true;
		}
	}
	
	
	private AllModelsReadyListener d_allNetworkModelsReadyListener;
	private List<AnalysisProgressListener> d_NMAnalysisProgressListeners;
	private List<AnalysisProgressListener> d_baselineProgressListeners;

	private List<MCMCModel> d_baselineModels;

	public boolean allNMAModelsReady() {
		return d_allNetworkModelsReadyListener.allModelsReady();
	}
	
	public MetaBenefitRiskPresentation(MetaBenefitRiskAnalysis bean, PresentationModelFactory pmf) {
		super(bean, pmf);
		
		d_pmf = pmf;
		d_allNetworkModelsReadyListener = new AllModelsReadyListener();
		d_NMAnalysisProgressListeners = new ArrayList<AnalysisProgressListener>();
		d_baselineProgressListeners = new ArrayList<AnalysisProgressListener>();
	
		/* 
		 * Only start SMAA if all networks are already done calculating when running this constructor.
		 * If not, the 'ready' event of the networks will trigger the creation of the SMAA model.
		 */
		d_baselineModels = new ArrayList<MCMCModel>();
		initAllBaselineModels();
		initAllNetworkAnalyses();
	}
	
	public int getNumNMAProgBars() {
		return d_NMAnalysisProgressListeners.size();
	}
	
	public int getNumBaselineProgBars() {
		return d_baselineProgressListeners.size();
	}
	
	public void attachNMAProgBar(JProgressBar bar, int progBarNum) {
		d_NMAnalysisProgressListeners.get(progBarNum).attachBar(bar);
	}
	
	public void attachBaselineProgBar(JProgressBar bar, int progBarNum) {
		d_baselineProgressListeners.get(progBarNum).attachBar(bar);
	}
	
	public ListHolder<MetaAnalysis> getAnalysesModel() {
		// FIXME: By the time it's possible the edit BR-analyses, this listholder should be hooked up.
		return new DefaultListHolder<MetaAnalysis>(getBean().getMetaAnalyses());
	}

	public OutcomeMeasure getOutcomeMeasureForCriterion(CardinalCriterion crit) {
		return d_smaaf.getOutcomeMeasure(crit);
	}
	
	private void initAllNetworkAnalyses() {
		for (MetaAnalysis ma : getBean().getMetaAnalyses() ){
			if (ma instanceof NetworkMetaAnalysis) {
				ConsistencyModel consistencyModel = ((NetworkMetaAnalysis) ma).getConsistencyModel();
				d_allNetworkModelsReadyListener.addModel(consistencyModel);
				d_NMAnalysisProgressListeners.add(new AnalysisProgressListener(consistencyModel));
			}
		}
	}
	
	public ValueHolder<Boolean> getAllModelsReadyModel() {
		return d_allNetworkModelsReadyListener;
	}
	
	public synchronized void startAllSimulations() {
		getBean().runAllConsistencyModels();
		List<Runnable> models = new ArrayList<Runnable>();
		for (MCMCModel model : d_baselineModels) {
			if (!model.isReady()) {
				models.add(model);
			}
		}
		ThreadHandler.getInstance().scheduleTasks(models);
	}
	
	private void initAllBaselineModels() {
		AbstractBaselineModel<?> model;
		for (OutcomeMeasure om : getBean().getOutcomeMeasures()) {
			model = getBean().getBaselineModel(om);
			d_baselineModels.add(model);
			d_allNetworkModelsReadyListener.addModel(model);
			d_baselineProgressListeners.add(new AnalysisProgressListener(model));
		}
	}
	
	public BenefitRiskMeasurementTableModel<Drug> getAbsoluteMeasurementTableModel() {
		return new BenefitRiskMeasurementTableModel<Drug>(getBean(), getBean().getAbsoluteMeasurementSource() , d_pmf);
	}

	public BenefitRiskMeasurementTableModel<Drug> getRelativeMeasurementTableModel() {
		return new BenefitRiskMeasurementTableModel<Drug>(getBean(), getBean().getRelativeMeasurementSource(), d_pmf);
	}
}

