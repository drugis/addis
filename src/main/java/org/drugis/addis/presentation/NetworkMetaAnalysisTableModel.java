package org.drugis.addis.presentation;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.LogContinuousMeasurementEstimate;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Treatment;

@SuppressWarnings("serial")
public class NetworkMetaAnalysisTableModel  extends AbstractTableModel implements RelativeEffectTableModel{
	private InconsistencyModel d_model;
    private NetworkBuilder d_builder;
    private List<Drug> d_drugs;
	private PresentationModelFactory d_pmf;
	
	public NetworkMetaAnalysisTableModel(Study study, List<Drug> drugList,
			OutcomeMeasure outcomeMeasure,
			PresentationModelFactory presentationModelFactory, InconsistencyModel model, NetworkBuilder builder) {
//		super(study, outcomeMeasure, presentationModelFactory);
		d_pmf = presentationModelFactory;
		d_model = model;
		d_builder = builder;
		d_drugs = drugList;
	}

	public int getColumnCount() {
		return d_drugs.size();
	}

	public int getRowCount() {
		return d_drugs.size();
	}
	
	public String getDescriptionAt(int row, int col) {
		if (row == col) {
			return null;
		}
		return "\"" + d_drugs.get(col) + "\" relative to \"" + d_drugs.get(row) + "\"";
	}
	
	public Object getValueAt(int row, int col) {
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
		return d_pmf.getModel(new LogContinuousMeasurementEstimate(relEffect.getMean(), relEffect.getStandardDeviation()));
	}

	public String getDescription() {
		return "Network Meta-Analysis";
	}

	public ForestPlotPresentation getPlotPresentation(int row, int column) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		return getDescription();
	}
}
