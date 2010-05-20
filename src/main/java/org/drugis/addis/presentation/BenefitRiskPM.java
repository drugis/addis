package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;

import org.drugis.addis.entities.BenefitRiskAnalysis;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
import org.drugis.addis.entities.metaanalysis.NetworkMetaAnalysis;
import org.drugis.addis.util.JSMAAintegration.SMAAEntityFactory;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.ProgressEvent.EventType;

import com.jgoodies.binding.PresentationModel;

import fi.smaa.jsmaa.simulator.SMAA2Results;

@SuppressWarnings("serial")
public class BenefitRiskPM extends PresentationModel<BenefitRiskAnalysis>{

	private class AnalysisProgressListener implements ProgressListener {
		JProgressBar d_progBar;
		private MixedTreatmentComparison d_networkModel;

		public AnalysisProgressListener(MixedTreatmentComparison networkModel) {
			networkModel.addProgressListener(this);
			d_networkModel = networkModel;
		}
		
		public void attachBar(JProgressBar bar) {
			d_progBar = bar;
			bar.setVisible(!d_networkModel.isReady());
		}

		public void update(MixedTreatmentComparison mtc, ProgressEvent event) {
			if(event.getType() == EventType.SIMULATION_PROGRESS && d_progBar != null){
				d_progBar.setString("Simulating: " + event.getIteration()/(event.getTotalIterations()/100) + "%");
				d_progBar.setValue(event.getIteration()/(event.getTotalIterations()/100));
			} else if(event.getType() == EventType.BURNIN_PROGRESS && d_progBar != null){
				d_progBar.setString("Burn in: " + event.getIteration()/(event.getTotalIterations()/100) + "%");
				d_progBar.setValue(event.getIteration()/(event.getTotalIterations()/100));
			} else if(event.getType() == EventType.SIMULATION_FINISHED) {
				d_progBar.setVisible(false);
			}
		}
	}
	
	private class AllModelsReadyListener implements ProgressListener {
		private List<ConsistencyModel> d_models = new ArrayList<ConsistencyModel>();
		
		public void addModel(ConsistencyModel model) {
			model.addProgressListener(this);
			d_models.add(model);
		}

		public void update(MixedTreatmentComparison mtc, ProgressEvent event) {
			if (event.getType() == ProgressEvent.EventType.SIMULATION_FINISHED){
				if(allModelsReady())
					firePropertyChange(PROPERTY_ALLMODELSREADY, false, true);
			}
		}

		public boolean allModelsReady() {
			for (ConsistencyModel model : d_models)
				if (!model.isReady())
					return false;
			return true;
		}
	}
	
	public static final String PROPERTY_ALLMODELSREADY = "allModelsReady";
	
	private PresentationModelFactory d_pmf;
	private SMAAEntityFactory d_smaaf;
	private AllModelsReadyListener d_allModelsReadyListener;
	private List<AnalysisProgressListener> d_analysisProgressListeners;
	
	public boolean allModelsReady() {
		return d_allModelsReadyListener.allModelsReady();
	}
	
	public BenefitRiskPM(BenefitRiskAnalysis bean, PresentationModelFactory pmf) {
		super(bean);
		
		d_pmf = pmf;
		d_smaaf = new SMAAEntityFactory();
		d_allModelsReadyListener = new AllModelsReadyListener();
		d_analysisProgressListeners = new ArrayList<AnalysisProgressListener>();
		startAllNetworkAnalyses();
	}

	public int getNumProgBars() {
		return d_analysisProgressListeners.size();
	}
	
	public void attachProgBar(JProgressBar bar, int progBarNum) {
		if (progBarNum >= d_analysisProgressListeners.size() )
			throw new IllegalArgumentException();
		d_analysisProgressListeners.get(progBarNum).attachBar(bar);
	}

	public SMAA2Results getSmaaModelResults(JProgressBar progressBar) {
		return d_smaaf.createSmaaModelResults(getBean(),progressBar);
	}
	
	
	public List<PresentationModel<MetaAnalysis>> getAnalysesPMList() {
		List<PresentationModel<MetaAnalysis>> entitiesPMs = new ArrayList<PresentationModel<MetaAnalysis>>();
		for (MetaAnalysis a : getBean().getMetaAnalyses())
			entitiesPMs.add(d_pmf.getModel(a));
		return entitiesPMs;
	}

	public BenefitRiskMeasurementTableModel getMeasurementTableModel() {
		return new BenefitRiskMeasurementTableModel(getBean(), d_pmf);
	}
	
	private void startAllNetworkAnalyses() {
		for (MetaAnalysis ma : getBean().getMetaAnalyses() ){
			if (ma instanceof NetworkMetaAnalysis) {
				ConsistencyModel consistencyModel = ((NetworkMetaAnalysis) ma).getConsistencyModel();
				d_allModelsReadyListener.addModel(consistencyModel);
				((NetworkMetaAnalysis) ma).runConsistency();
				d_analysisProgressListeners.add(new AnalysisProgressListener(consistencyModel));
			}
		}
	}
}
