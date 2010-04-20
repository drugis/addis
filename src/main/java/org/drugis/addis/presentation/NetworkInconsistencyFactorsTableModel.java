package org.drugis.addis.presentation;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.InconsistencyParameter;

@SuppressWarnings("serial")
public class NetworkInconsistencyFactorsTableModel  extends AbstractTableModel implements TableModelWithDescription{
	private NetworkMetaAnalysisPresentation d_pm;
	private PresentationModelFactory d_pmf;

	public NetworkInconsistencyFactorsTableModel(NetworkMetaAnalysisPresentation pm, PresentationModelFactory pmf) {
		d_pm = pm;
		d_pmf = pmf;
	}
	
	public String getColumnName(int column) {
		return column == 0 ? "Cycle" : "Confidence Interval";
	}

	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return d_pm.getBean().getInconsistencyFactors().size();
	}
	
	public String getValueAt(int row, int col) {
		if(!d_pm.getBean().getInconsistencyModel().isReady()){
			return "n/a";
		}
		InconsistencyParameter ip = d_pm.getBean().getInconsistencyModel().getInconsistencyFactors().get(row);
		if(col == 0){
			String out = "";
			for (int i=0; i<ip.treatmentList().size() - 1; ++i){
				out += ip.treatmentList().get(i).id() + ", ";
			}
			return out.substring(0, out.length()-2);
		} else{
			Estimate ic = d_pm.getBean().getInconsistency(ip);

			BasicContinuousMeasurement contMeas = new BasicContinuousMeasurement(ic.getMean(), ic.getStandardDeviation(), 0);
			ContinuousMeasurementPresentation<BasicContinuousMeasurement> pm = 
								(ContinuousMeasurementPresentation<BasicContinuousMeasurement>) d_pmf.getModel(contMeas);
			return pm.normConfIntervalString();
		}
	}

	public String getDescription() {
		return "Inconsistency Factors";
	}

	public String getTitle() {
		return getDescription();
	}
}
