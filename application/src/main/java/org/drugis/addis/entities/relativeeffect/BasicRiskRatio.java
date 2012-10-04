/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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

import org.drugis.addis.entities.RateMeasurement;

public class BasicRiskRatio extends BasicRatio {

	public BasicRiskRatio(RateMeasurement baseline, RateMeasurement subject) {
		super(baseline, subject);
	}

	@Override
	public String toString() {
		return "[" + d_baseline.toString() + "] / [" 
		+ d_subject.toString() + "]";
	}

	@Override
	public Double getError() { //NB: this is the LOG error
		if (!isDefined())
			return Double.NaN;

		double a = getA();
		double n1 = a + getB();

		double c = getC();
		double n2 = c + getD();
		
		return Math.sqrt((1.0 / a) +
				(1.0 / c) -
				(1.0 / n1) -
				(1.0 / n2));		
	}

	public String getName() {
		return "Risk ratio";
	}
	
	@Override
	public boolean isDefined() {
		return super.isDefined() && d_baseline.getRate() > 0 && d_subject.getRate() > 0;
	}

	@Override
	protected double getMu() {
		if (!isDefined())
			return Double.NaN;
		
		double a = getA();
		double n1 = a + getB();

		double c = getC();
		double n2 = c + getD();
		
		return Math.log(( a / n1 ) / ( c / n2 ));
	}

	@Override
	protected Integer getDegreesOfFreedom() {
		return getSampleSize() - 2;
	}

	@Override
	protected double getSigma() {
		return getError();
	}

	public RelativeEffect<RateMeasurement> getCorrected() {
		return new CorrectedBasicRiskRatio(this);
	}
	
	protected double getA() {
		return getSubject().getRate();
	}

	protected double getB() {
		return getSubject().getSampleSize() - getSubject().getRate();
	}

	protected double getC() {
		return getBaseline().getRate();
	}

	protected double getD() {
		return getBaseline().getSampleSize() - getBaseline().getRate();
	}
	
}
