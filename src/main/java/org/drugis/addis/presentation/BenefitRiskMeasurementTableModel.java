package org.drugis.addis.presentation;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.NetworkRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;

@SuppressWarnings("serial")
public class BenefitRiskMeasurementTableModel extends AbstractTableModel {
	interface MeasurementSource {
		public  RelativeEffect<? extends Measurement> getMeasurement(Drug drug, OutcomeMeasure om);
	}
	
	protected BenefitRiskAnalysis d_br;
	private PresentationModelFactory d_pmf;
	private MeasurementSource d_source;

	public BenefitRiskMeasurementTableModel(BenefitRiskAnalysis br, PresentationModelFactory pmf, boolean relative) {
		d_br = br;
		d_pmf = pmf;
		if (relative) {
			d_source = new MeasurementSource() {
				public RelativeEffect<? extends Measurement> getMeasurement(Drug drug,
						OutcomeMeasure om) {
					return d_br.getRelativeEffect(drug, om);
				}
			};
		} else {
			d_source = new MeasurementSource() {
				public RelativeEffect<? extends Measurement> getMeasurement(Drug drug,
						OutcomeMeasure om) {
					GaussianBase dist = d_br.getAbsoluteEffectDistribution(drug, om);
					if (dist == null) return new NetworkRelativeEffect<Measurement>(); // empty relative effect.
					switch (om.getType()) {
					case CONTINUOUS:
						return NetworkRelativeEffect.buildMeanDifference(dist.getMu(), dist.getSigma());
					case RATE:
						return NetworkRelativeEffect.buildOddsRatio(dist.getMu(), dist.getSigma());
					default:
						throw new IllegalStateException();	
					}
				}
			};
		}
	}

	public int getColumnCount() {
		return d_br.getOutcomeMeasures().size()+1;
	}

	public int getRowCount() {
		return d_br.getDrugs().size();
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}

	@Override
	public String getColumnName(int index) {
		if (index == 0) {
			return "Alternative";
		}
		return d_br.getOutcomeMeasures().get(index-1).toString();	
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Drug drug = d_br.getDrugs().get(rowIndex);
		if (columnIndex == 0) {
			return drug.getName();
		}

		OutcomeMeasure om = d_br.getOutcomeMeasures().get(columnIndex-1);
		
		RelativeEffect<? extends Measurement> measurement = getMeasurement(drug, om);

		return d_pmf.getLabeledModel(measurement);
	}

	private RelativeEffect<? extends Measurement> getMeasurement(Drug drug,
			OutcomeMeasure om) {
		return d_source.getMeasurement(drug, om);
	}

}
