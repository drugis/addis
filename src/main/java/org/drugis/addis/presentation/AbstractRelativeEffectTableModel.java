/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;

@SuppressWarnings("serial")
public abstract class AbstractRelativeEffectTableModel extends AbstractTableModel
implements RelativeEffectTableModel {
	protected Study d_study;
	protected OutcomeMeasure d_outMeas;
	protected PresentationModelFactory d_pmf;
	
	protected AbstractRelativeEffectTableModel(Study study, OutcomeMeasure om, PresentationModelFactory pmf) {
		d_study = study;
		d_outMeas = om;
		d_pmf = pmf;
	}
	
	public abstract String getTitle();

	protected abstract RelativeEffect<?> getRelativeEffect(Measurement baseline, Measurement subject);
	
	protected abstract Class<? extends RelativeEffect<?>> getRelativeEffectType();


	public int getColumnCount() {
		return d_study.getArms().size();
	}

	public int getRowCount() {
		return d_study.getArms().size();
	}

	public Object getValueAt(int row, int col) {
		if (row == col) {
			return d_pmf.getModel(d_study.getArms().get(row));
		}
		
		Measurement denominator = d_study.getMeasurement(d_outMeas, d_study.getArms().get(row));
		Measurement numerator = d_study.getMeasurement(d_outMeas, d_study.getArms().get(col));
		return d_pmf.getModel(getRelativeEffect(denominator, numerator));
	}

	/**
	 * @see org.drugis.addis.presentation.RelativeEffectTableModel#getDescriptionAt(int, int)
	 */
	public String getDescriptionAt(int row, int col) {
		if (row == col) {
			return null;
		}
		return "\"" + getArmLabel(col) +
			"\" relative to \"" + getArmLabel(row) + "\"";
	}

	private String getArmLabel(int index) {
		return d_pmf.getLabeledModel(d_study.getArms().get(index)).getLabelModel().getString();
	}

	public String getDescription() {
		return getTitle() + " for \"" + d_study.getName() 
				+ "\" on Endpoint \"" + d_outMeas.getName() + "\"";
	}

	public ForestPlotPresentation getPlotPresentation(int row, int column) {
		Arm rowArm = d_study.getArms().get(row);
		Arm colArm = d_study.getArms().get(column);
		return new ForestPlotPresentation((Study)d_study, d_outMeas, d_study.getDrug(rowArm),
				d_study.getDrug(colArm), getRelativeEffectType(), d_pmf);
	}
}
