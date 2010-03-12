package org.drugis.addis.presentation;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.LogContinuousMeasurementEstimate;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.Treatment;

@SuppressWarnings("serial")
public class NetworkMetaAnalysisTableModel  extends AbstractTableModel{
	private NetworkMetaAnalysisPresentation d_pm;
	private PresentationModelFactory d_pmf;

	public NetworkMetaAnalysisTableModel(NetworkMetaAnalysisPresentation pm, PresentationModelFactory pmf) {
		d_pm = pm;
		d_pmf = pmf;
	}

	public int getColumnCount() {
		return d_pm.getBean().getIncludedDrugs().size();
	}

	public int getRowCount() {
		return d_pm.getBean().getIncludedDrugs().size();
	}
	
	public String getDescriptionAt(int row, int col) {
		if (row == col) {
			return null;
		}
		return "\"" + d_pm.getBean().getIncludedDrugs().get(col) + "\" relative to \"" + d_pm.getBean().getIncludedDrugs().get(row) + "\"";
	}
	
	public Object getValueAt(int row, int col) {
		if (row == col) {
			return d_pmf.getModel(d_pm.getBean().getIncludedDrugs().get(row));
		}
		
		while (!d_pm.getBean().getModel().isReady()) {
			try {
				wait(1);
			} catch (Exception e) {
				// TODO Auto-generated catch block

			}
//			d_model.run(); 
//			return d_pmf.getModel(new LogContinuousMeasurementEstimate(0, 0));
		}

		final Treatment drug1 = d_pm.getBean().getBuilder().getTreatment(d_pm.getBean().getIncludedDrugs().get(row).toString());
		final Treatment drug2 = d_pm.getBean().getBuilder().getTreatment(d_pm.getBean().getIncludedDrugs().get(col).toString());
		
		Estimate relEffect = d_pm.getBean().getModel().getRelativeEffect(drug1, drug2);

		// convert to Log Odds-ratio
		return d_pmf.getModel(new LogContinuousMeasurementEstimate(relEffect.getMean(), relEffect.getStandardDeviation()));
	}

	public String getDescription() {
		return "Network Meta-Analysis (Inconsistency Model)";
	}

	public String getTitle() {
		return getDescription();
	}
}
