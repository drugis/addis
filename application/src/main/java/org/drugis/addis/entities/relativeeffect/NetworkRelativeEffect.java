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

package org.drugis.addis.entities.relativeeffect;

import org.drugis.addis.entities.Measurement;
import org.drugis.mtc.summary.QuantileSummary;

public class NetworkRelativeEffect<T extends Measurement> extends AbstractRelativeEffect<T> implements RelativeEffect<T> {
	private QuantileSummary d_quantiles;
	private final boolean d_defined;
	private boolean d_coninuous = false;
	
	public NetworkRelativeEffect(QuantileSummary q, boolean continuous) {
		d_coninuous = continuous;
		d_quantiles = q;
		d_defined = true;
	}
	
	public NetworkRelativeEffect() {
		d_defined = false;
	}
	
	static public NetworkRelativeEffect<? extends Measurement> buildOddsRatio(QuantileSummary estimate) {
		return new NetworkRelativeEffect<Measurement>(estimate, true);
	}

	static public NetworkRelativeEffect<? extends Measurement> buildMeanDifference(QuantileSummary estimate) {
		return new NetworkRelativeEffect<Measurement>(estimate, false);
	}

	public String getName() {
		return "Network Meta-Analysis Relative Effect";
	}

	public boolean isDefined() {
		return d_defined;
	}
	
	@Override
	public double getNeutralValue() {
		if (d_coninuous) {
			return 1;
		} else { 
			return 0;
		}
	}
	
	@Override 
	public ConfidenceInterval getConfidenceInterval() {
		if (!isDefined()) {
			return new ConfidenceInterval(Double.NaN, Double.NaN, Double.NaN);
		}
		if(d_coninuous) { 
			return new ConfidenceInterval(Math.exp(d_quantiles.getQuantile(1)), Math.exp(d_quantiles.getQuantile(0)), Math.exp(d_quantiles.getQuantile(2)));
		} else { 
			return new ConfidenceInterval(d_quantiles.getQuantile(1), d_quantiles.getQuantile(0), d_quantiles.getQuantile(2));
		}
		
	}

	@Override
	@Deprecated
	public Distribution getDistribution() {
		double mean = d_quantiles.getQuantile(1);
		double stdev = (d_quantiles.getQuantile(2) - mean) / 1.960 ;
		if(d_coninuous) { 
			return new Gaussian(mean, stdev);
		} else { 
			return new LogGaussian(mean, stdev);
		}
	} 
	
}