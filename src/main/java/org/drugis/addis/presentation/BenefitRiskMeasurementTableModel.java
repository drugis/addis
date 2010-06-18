/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
		((BenefitRiskPM)pmf.getModel(br)).getAllModelsReadyModel().addValueChangeListener(new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				fireTableDataChanged();				
			}
		});
	}

	public int getColumnCount() {
		return d_br.getOutcomeMeasures().size()+1;
	}

	public int getRowCount() {
		return d_br.getDrugs().size();
	}

	@Override
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
