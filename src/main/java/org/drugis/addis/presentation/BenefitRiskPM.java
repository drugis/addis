package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.mcmcmodel.AbstractBaselineModel;
import org.drugis.addis.util.JSMAAintegration.BRSMAASimulationBuilder;
import org.drugis.addis.util.JSMAAintegration.SMAAEntityFactory;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.ProgressEvent.EventType;

import com.jgoodies.binding.PresentationModel;

import fi.smaa.jsmaa.gui.components.SimulationProgressBar;
import fi.smaa.jsmaa.gui.jfreechart.CentralWeightsDataset;
import fi.smaa.jsmaa.gui.jfreechart.RankAcceptabilitiesDataset;
import fi.smaa.jsmaa.gui.presentation.CentralWeightTableModel;
import fi.smaa.jsmaa.gui.presentation.PreferencePresentationModel;
import fi.smaa.jsmaa.gui.presentation.RankAcceptabilityTableModel;
import fi.smaa.jsmaa.gui.presentation.SMAA2ResultsTableModel;
import fi.smaa.jsmaa.model.CardinalCriterion;
import fi.smaa.jsmaa.model.ModelChangeEvent;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.model.SMAAModelListener;
import fi.smaa.jsmaa.simulator.BuildQueue;
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

		public void update(MCMCModel mtc, ProgressEvent event) {
			if(event.getType() == EventType.SIMULATION_PROGRESS && d_progBar != null){
				d_progBar.setString("Simulating: " + event.getIteration()/(event.getTotalIterations()/100) + "%");
				d_progBar.setValue(event.getIteration()/(event.getTotalIterations()/100));
			} else if(event.getType() == EventType.BURNIN_PROGRESS && d_progBar != null){
				d_progBar.setString("Burn in: " + event.getIteration()/(event.getTotalIterations()/100) + "%");
				d_progBar.setValue(event.getIteration()/(event.getTotalIterations()/100));
			} else if(event.getType() == EventType.SIMULATION_FINISHED && d_progBar != null) {
				d_progBar.setVisible(false);
			}
		}
	}
	
	private class AllModelsReadyListener implements ProgressListener {
		private List<MCMCModel> d_models = new ArrayList<MCMCModel>();
		
		public void addModel(MCMCModel model) {
			model.addProgressListener(this);
			d_models.add(model);
		}

		public void update(MCMCModel mtc, ProgressEvent event) {
			if (event.getType() == ProgressEvent.EventType.SIMULATION_FINISHED){
				//System.out.println("A model is ready.");
				if(allModelsReady()) {
					if (allNMAModelsReady())
						startSmaa();					
					firePropertyChange(PROPERTY_ALLMODELSREADY, false, true);
				}
			}
		}

		public boolean allModelsReady() {
//			for (MCMCModel model : d_models)
//				System.out.print(model.isReady()+"\t");
//			System.out.println("\n");
			
			for (MCMCModel model : d_models){
				if (!model.isReady())
					return false;
			}
			return true;
		}
	}
	
	public static final String PROPERTY_ALLMODELSREADY = "allModelsReady";
	
	private PresentationModelFactory d_pmf;
	private AllModelsReadyListener d_allNetworkModelsReadyListener;
	private AllModelsReadyListener d_allBaselineModelsReadyListener;
	private List<AnalysisProgressListener> d_analysisProgressListeners;

	private RankAcceptabilityTableModel d_rankAccepTM;
	private RankAcceptabilitiesDataset d_rankAccepDS;	
	private BuildQueue d_buildQueue;

	private CentralWeightsDataset d_cwDS;

	private CentralWeightTableModel d_cwTM;

	private PreferencePresentationModel d_prefPresModel;

	private SMAAModel d_model;

	private SimulationProgressBar d_progressBar;

	private SMAAEntityFactory d_smaaf;
	
	public boolean allNMAModelsReady() {
		return d_allNetworkModelsReadyListener.allModelsReady();
	}
	
	public boolean allBaselineModelsReady() {
		return d_allBaselineModelsReadyListener.allModelsReady();
	}
	
	public BenefitRiskPM(BenefitRiskAnalysis bean, PresentationModelFactory pmf) {
		super(bean);
		
		d_pmf = pmf;
		d_allNetworkModelsReadyListener = new AllModelsReadyListener();
		d_allBaselineModelsReadyListener = new AllModelsReadyListener();
		d_analysisProgressListeners = new ArrayList<AnalysisProgressListener>();
		d_buildQueue = new BuildQueue();
		d_progressBar = new SimulationProgressBar();
		
		/* 
		 * Only start SMAA if all networks are already done calculating when running this constructor.
		 * If not, the 'ready' event of the networks will trigger the creation of the SMAA model.
		 */
		if (startAllNetworkAnalyses())
			startSmaa();
	}
	
	private void startSmaa() {
		d_smaaf = new SMAAEntityFactory();
		d_model = d_smaaf.createSmaaModel(getBean());
		SMAA2Results emptyResults = new SMAA2Results(d_model.getAlternatives(), d_model.getCriteria(), 10);
		d_rankAccepDS = new RankAcceptabilitiesDataset(emptyResults);
		d_rankAccepTM = new RankAcceptabilityTableModel(emptyResults);
		d_cwTM = new CentralWeightTableModel(emptyResults);
		d_cwDS = new CentralWeightsDataset(emptyResults);
		d_prefPresModel = new PreferencePresentationModel(d_model);

		d_model.addModelListener(new SMAAModelListener() {
			public void modelChanged(ModelChangeEvent type) {
				startSimulation();
			}			
		});
		startSimulation();
	}
	
	public SimulationProgressBar getSmaaSimulationProgressBar() {
		return d_progressBar;
	}

	private void startSimulation() {
		d_buildQueue.add(new BRSMAASimulationBuilder(d_model,
				d_rankAccepTM, d_rankAccepDS, d_cwTM, d_cwDS, d_progressBar));
	}

	public int getNumProgBars() {
		return d_analysisProgressListeners.size();
	}
	
	public void attachNMAProgBar(JProgressBar bar, int progBarNum) {
		if (progBarNum >= d_analysisProgressListeners.size() )
			throw new IllegalArgumentException();
		d_analysisProgressListeners.get(progBarNum).attachBar(bar);
	}
	
	public PreferencePresentationModel getSmaaPreferenceModel() {
		return null;
	}
	
	
	public List<PresentationModel<MetaAnalysis>> getAnalysesPMList() {
		List<PresentationModel<MetaAnalysis>> entitiesPMs = new ArrayList<PresentationModel<MetaAnalysis>>();
		for (MetaAnalysis a : getBean().getMetaAnalyses())
			entitiesPMs.add(d_pmf.getModel(a));
		return entitiesPMs;
	}

	public BenefitRiskMeasurementTableModel getMeasurementTableModel(boolean relative) {
		if (!relative) {
			startAllBaselineModels();
		}
		return new BenefitRiskMeasurementTableModel(getBean(), d_pmf, relative);
	}

	public OutcomeMeasure getOutcomeMeasureForCriterion(CardinalCriterion crit) {
		return d_smaaf.getOutcomeMeasure(crit);
	}
	
	public PreferencePresentationModel getPreferencePresentationModel() {
		return d_prefPresModel;
	}

	public SMAA2ResultsTableModel getRankAcceptabilitiesTableModel() {
		return d_rankAccepTM;
	}

	public RankAcceptabilitiesDataset getRankAcceptabilityDataSet() {
		return d_rankAccepDS;
	}

	public CentralWeightsDataset getCentralWeightsDataSet() {
		return d_cwDS;
	}

	public CentralWeightTableModel getCentralWeightsTableModel() {
		return d_cwTM;
	}	
	
	private boolean startAllNetworkAnalyses() {
		getBean().runAllConsistencyModels();
		boolean allNetworksFinished = true;
		for (MetaAnalysis ma : getBean().getMetaAnalyses() ){
			if (ma instanceof NetworkMetaAnalysis) {
				ConsistencyModel consistencyModel = ((NetworkMetaAnalysis) ma).getConsistencyModel();
				if (!consistencyModel.isReady()) // FIXME: possible (but rare) Race condition
					allNetworksFinished = false;
				d_allNetworkModelsReadyListener.addModel(consistencyModel);
				d_analysisProgressListeners.add(new AnalysisProgressListener(consistencyModel));
			}
		}
		return allNetworksFinished;
	}
	
	private void startAllBaselineModels() {
		AbstractBaselineModel<?> model;
		for (OutcomeMeasure om : getBean().getOutcomeMeasures()) {
			model = getBean().getBaselineModel(om);
			if (!model.isReady()) d_allBaselineModelsReadyListener.addModel(model);
		}
	}
}
