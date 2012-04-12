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

import java.text.DecimalFormat;

import org.drugis.mtc.summary.QuantileSummary;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;



@SuppressWarnings("serial")
public class QuantileSummaryPresentation extends PresentationModel<QuantileSummary> implements LabeledPresentation {

	public class LabelModel extends DefaultLabelModel {
		
		public LabelModel() {
			super(getBean());
		}

		@Override
		public Object getValue() {			
			DecimalFormat format = new DecimalFormat("###0.00");
			
			double mean = getBean().getQuantile(1);
			double lowerCI = getBean().getQuantile(0);
			double upperCI = getBean().getQuantile(2);
			return format.format(applyExpIfNecessary(mean)) + 
					" (" + format.format(applyExpIfNecessary(lowerCI)) + ", " + 
					format.format(applyExpIfNecessary(upperCI)) + ")";
		}

		private double applyExpIfNecessary(double val) {
			return getBean().isContinuous() ? val : Math.exp(val);
		}
	}
	
	public QuantileSummaryPresentation(QuantileSummary bean) {
		super(bean);
	}
	
	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}
	
	@Override
	public String toString() {
		return (String) getLabelModel().getValue();
	}

}
