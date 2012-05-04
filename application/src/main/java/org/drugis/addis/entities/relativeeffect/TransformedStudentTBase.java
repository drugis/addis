/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

import org.apache.commons.math3.distribution.TDistribution;
import org.drugis.common.beans.AbstractObservable;

public abstract class TransformedStudentTBase extends AbstractObservable implements	Distribution {

	protected final double d_mu;
	protected final double d_sigma;
	protected final int d_degreesOfFreedom;
	protected final TDistribution d_dist;

	public TransformedStudentTBase(double mu, double sigma, int degreesOfFreedom) {
		if (Double.isNaN(mu)) throw new IllegalArgumentException("mu may not be NaN");
		if (Double.isNaN(sigma)) throw new IllegalArgumentException("sigma may not be NaN");
		if (sigma < 0.0) throw new IllegalArgumentException("sigma must be >= 0.0");
		if (degreesOfFreedom < 1) throw new IllegalArgumentException("degreesOfFreedom must be >= 1");
		d_mu = mu;
		d_sigma = sigma;
		d_degreesOfFreedom = degreesOfFreedom;
		d_dist = new TDistribution(getDegreesOfFreedom());
	}

	protected double calculateQuantile(double p) {
		return d_dist.inverseCumulativeProbability(p) * d_sigma + d_mu;
	}

	protected double calculateCumulativeProbability(double x) {
		return d_dist.cumulativeProbability((x - d_mu) / d_sigma);
	}

	public double getMu() {
		return d_mu;
	}

	public double getSigma() {
		return d_sigma;
	}

	public int getDegreesOfFreedom() {
		return d_degreesOfFreedom;
	}


	@Override
	public boolean equals(Object o) {
		if(o instanceof TransformedStudentTBase) {
			TransformedStudentTBase other = (TransformedStudentTBase) o;
			return canEqual(other) && other.d_mu == d_mu && other.d_sigma == d_sigma && other.d_degreesOfFreedom == d_degreesOfFreedom;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return ((Double)d_mu).hashCode() + 31 * ((Double)d_sigma).hashCode() + 31 * 31 * ((Integer) d_degreesOfFreedom).hashCode();
	}
	
	protected abstract boolean canEqual(TransformedStudentTBase other);
}