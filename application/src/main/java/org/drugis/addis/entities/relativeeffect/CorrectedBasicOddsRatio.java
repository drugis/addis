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

public class CorrectedBasicOddsRatio extends BasicOddsRatio implements
		RelativeEffect<RateMeasurement> {

	public CorrectedBasicOddsRatio(RateMeasurement baseline,
			RateMeasurement subject) {
		super(baseline, subject);
	}

	public CorrectedBasicOddsRatio(BasicOddsRatio bor) {
		this(bor.getBaseline(), bor.getSubject());
	}

	@Override
	public String getName() {
		return "Odds ratio (corrected for zeroes)";
	}
	
	@Override
	public boolean isDefined() {
		return (getDegreesOfFreedom() > 0) &&
			(getA() != 0.5 || getC() != 0.5) && 
			(getB() != 0.5 || getD() != 0.5);
	}
	
	@Override
	protected double getA() {
		return super.getA() + 0.5;
	}
	
	@Override
	protected double getB() {
		return getSubject().getSampleSize() - getSubject().getRate() + 0.5;
	}

	@Override
	protected double getC() {
		return getBaseline().getRate() + 0.5;
	}

	@Override
	protected double getD() {
		return getBaseline().getSampleSize() - getBaseline().getRate() + 0.5;
	}
}
