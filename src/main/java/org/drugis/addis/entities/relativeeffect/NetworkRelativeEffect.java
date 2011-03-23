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

public class NetworkRelativeEffect<T extends Measurement> extends AbstractRelativeEffect<T> implements RelativeEffect<T> {
	private Distribution d_distribution;
	private final boolean d_defined;
	
	public NetworkRelativeEffect(Distribution d) {
		d_distribution = d;
		d_defined = true;
	}
	
	public NetworkRelativeEffect() {
		d_defined = false;
	}

	static public NetworkRelativeEffect<? extends Measurement> buildOddsRatio(double mu, double sigma) {
		return new NetworkRelativeEffect<Measurement>(new LogGaussian(mu, sigma));
	}
	
	static public NetworkRelativeEffect<? extends Measurement> buildMeanDifference(double mu, double sigma) {
		return new NetworkRelativeEffect<Measurement>(new Gaussian(mu, sigma));
	}

	public Distribution getDistribution() {
		return d_distribution;
	}

	public String getName() {
		return "Network Meta-Analysis Relative Effect";
	}

	public boolean isDefined() {
		return d_defined;
	}
}