/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;

public abstract class AbstractRatio extends AbstractEntity implements RelativeEffectRate {
	private static final long serialVersionUID = 1647344976539753330L;
	
	protected RateMeasurement d_numerator;
	protected RateMeasurement d_denominator;		

	protected AbstractRatio(RateMeasurement denominator, RateMeasurement numerator) throws IllegalArgumentException {
		if (!denominator.getEndpoint().equals(numerator.getEndpoint())) {
			throw new IllegalArgumentException();
		}
		
		d_numerator = numerator;
		d_denominator = denominator;
	}
	
	public RateMeasurement getSubject() {
		return d_numerator;
	}

	public RateMeasurement getBaseline() {
		return d_denominator;
	}	

	public Endpoint getEndpoint() {
		return d_numerator.getEndpoint();
	}

	public Integer getSampleSize() {
		return d_numerator.getPatientGroup().getSize() + d_denominator.getPatientGroup().getSize();
	}

	protected double getCriticalValue() {
		return StudentTTable.getT(getSampleSize() - 2);
	}

	
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}

	public Interval<Double> getConfidenceInterval() {
		double lBound = Math.log(getRelativeEffect());
		lBound -= getCriticalValue() * getError();
		double uBound = Math.log(getRelativeEffect());
		uBound += getCriticalValue() * getError();
		return new Interval<Double>(Math.exp(lBound), Math.exp(uBound));
	}
}