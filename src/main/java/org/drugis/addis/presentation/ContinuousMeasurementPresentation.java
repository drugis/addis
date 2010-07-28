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

import java.text.DecimalFormat;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.common.Interval;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

// FIXME: there should be separate implementations of this class for each concrete Measurement,
// and these should implement the PROPERTY_LABEL, in stead of the Measurement itself.

@SuppressWarnings("serial")
public class ContinuousMeasurementPresentation<T extends ContinuousMeasurement> 
extends PresentationModel<T> implements LabeledPresentation {
	
	public ContinuousMeasurementPresentation(T bean) {
		super(bean);
	}

	public AbstractValueModel getLabelModel() {
		return new DefaultLabelModel(getBean());
	}
	
	@Override
	public String toString() {
		return (String) getLabelModel().getValue(); 
	}
	
	public String normConfIntervalString() {
		DecimalFormat df = new DecimalFormat("###0.00");
		NormalDistribution distribution = new NormalDistributionImpl(getBean().getMean(), getBean().getStdDev());
		Interval<Double> confInterval;
		try {
			confInterval = new Interval<Double>(distribution.inverseCumulativeProbability(0.025),
					distribution.inverseCumulativeProbability(0.975));
		} catch (MathException e) {
			e.printStackTrace();
			return null;
		}

		return df.format(getBean().getMean()) + 
				" (" + df.format(confInterval.getLowerBound()) + ", " + df.format(confInterval.getUpperBound()) + ")";
	}
}
