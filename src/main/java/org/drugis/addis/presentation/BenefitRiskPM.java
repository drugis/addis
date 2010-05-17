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

import com.jgoodies.binding.PresentationModel;

import fi.smaa.jsmaa.simulator.SMAA2Results;

@SuppressWarnings("serial")
public class BenefitRiskPM extends PresentationModel<BenefitRiskAnalysis>{
	
	public static final String PROPERTY_ALLMODELSREADY = "allModelsReady";
	
	private class AllModelsReadyListener implements ProgressListener {
		private List<ConsistencyModel> d_models = new ArrayList<ConsistencyModel>();
		
		public void addModel(ConsistencyModel model) {
			model.addProgressListener(this);
			d_models.add(model);
		}

		public void update(MixedTreatmentComparison mtc, ProgressEvent event) {
			if (event.getType() == ProgressEvent.EventType.SIMULATION_FINISHED)
				if(allModelsReady())
					firePropertyChange(PROPERTY_ALLMODELSREADY, false, true);
		}

		public boolean allModelsReady() {
			for (ConsistencyModel model : d_models)
				if (!model.isReady())
					return false;
			return true;
		}
	}
	
	private PresentationModelFactory d_pmf;
	private SMAAEntityFactory d_smaaf;
	private AllModelsReadyListener d_allModelsReadyListener;
	
	public boolean allModelsReady() {
		return d_allModelsReadyListener.allModelsReady();
	}
	
	public BenefitRiskPM(BenefitRiskAnalysis bean, PresentationModelFactory pmf) {
		super(bean);
		
		d_pmf = pmf;
		d_smaaf = new SMAAEntityFactory();
		d_allModelsReadyListener = new AllModelsReadyListener();
		startAllNetworkAnalyses();
	}

	private void startAllNetworkAnalyses() {
		for (MetaAnalysis ma : getBean().getMetaAnalyses() )
			if (ma instanceof NetworkMetaAnalysis) {
				d_allModelsReadyListener.addModel(((NetworkMetaAnalysis) ma).getConsistencyModel());
				((NetworkMetaAnalysis) ma).runConsistency();
			}
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
}
