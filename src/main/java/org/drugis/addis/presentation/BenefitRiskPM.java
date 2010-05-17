package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;

import org.drugis.addis.entities.BenefitRiskAnalysis;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
import org.drugis.addis.util.JSMAAintegration.SMAAEntityFactory;

import com.jgoodies.binding.PresentationModel;

import fi.smaa.jsmaa.simulator.SMAA2Results;

@SuppressWarnings("serial")
public class BenefitRiskPM extends PresentationModel<BenefitRiskAnalysis>{
	
	private PresentationModelFactory d_pmf;
	private SMAAEntityFactory d_smaaf;
	
	public BenefitRiskPM(BenefitRiskAnalysis bean, PresentationModelFactory pmf) {
		super(bean);
		
		d_pmf = pmf;
		d_smaaf = new SMAAEntityFactory();	
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
