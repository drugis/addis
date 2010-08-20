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

package org.drugis.addis.entities.relativeeffect;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.drugis.addis.entities.AbstractObservable;

public abstract class GaussianBase extends AbstractObservable implements Distribution {
	private double d_mu;
	private double d_sigma;

	public GaussianBase(double mu, double sigma) {
		if (Double.isNaN(mu)) throw new IllegalArgumentException("mu may not be NaN");
		if (Double.isNaN(sigma)) throw new IllegalArgumentException("sigma may not be NaN");
		if (sigma < 0.0) throw new IllegalArgumentException("sigma must be >= 0.0");
		d_mu = mu;
		d_sigma = sigma;
	}

	protected double calculateQuantile(double p) {
		try {
			if (getSigma() == 0.0) {
				return getMu();
			}
			NormalDistribution dist = new NormalDistributionImpl(getMu(), getSigma());
			return dist.inverseCumulativeProbability(p);
		} catch (MathException e) {
			throw new RuntimeException(e);
		}
	}

	public double getSigma() {
		return d_sigma;
	}

	public double getMu() {
		return d_mu;
	}

	public GaussianBase plus(GaussianBase other) {
		if (!canEqual(other)) throw new IllegalArgumentException(
				"Cannot add together " + getClass().getSimpleName() +
				" and " + other.getClass().getSimpleName());
		return newInstance(getMu() + other.getMu(), 
				Math.sqrt(getSigma() * getSigma() + other.getSigma() * other.getSigma()));
	}

	protected abstract GaussianBase newInstance(double mu, double sigma);

	abstract protected boolean canEqual(GaussianBase other);
}
