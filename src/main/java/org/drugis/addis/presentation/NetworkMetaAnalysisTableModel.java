package org.drugis.addis.presentation;

import java.util.List;

import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.LogOddsRatio;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Treatment;

@SuppressWarnings("serial")
public class NetworkMetaAnalysisTableModel extends MeanDifferenceTableModel{
	private InconsistencyModel d_model;
    private NetworkBuilder d_builder;
    private List<Drug> d_drugs;
	
	public NetworkMetaAnalysisTableModel(Study study, List<Drug> drugList,
			OutcomeMeasure outcomeMeasure,
			PresentationModelFactory presentationModelFactory, InconsistencyModel model, NetworkBuilder builder) {
		super(study, outcomeMeasure, presentationModelFactory);
		d_model = model;
		d_builder = builder;
		d_drugs = drugList;
	}


	@Override
	public Object getValueAt(int row, int col) {
		//System.out.println("NetworkMetaAnalysisTableModel.java-> study: "+d_study+", row: "+row+"/"+d_drugs.size()+", col: "+col+"/"+d_drugs.size());	
		if (row == col) {
			return d_pmf.getModel(d_drugs.get(row));
		}

		final Treatment drug1 = d_builder.getTreatment(d_drugs.get(row).toString());
		final Treatment drug2 = d_builder.getTreatment(d_drugs.get(col).toString());
		if (!d_model.isReady()) {
			d_model.run();
		}
		
		Estimate relEffect = d_model.getRelativeEffect(drug1, drug2);

		// convert to Log Odds-ratio
		return d_pmf.getLabeledModel(new LogOddsRatio(new BasicRateMeasurement(1, 10), new BasicRateMeasurement(2, 10)));
//		return d_pmf.getLabeledModel(new LogOddsRatio(relEffect.getMean(), relEffect.getStandardDeviation()));
	}
	
}
