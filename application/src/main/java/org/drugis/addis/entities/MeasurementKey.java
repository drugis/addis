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

package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

import org.drugis.common.EqualsUtil;

public class MeasurementKey extends AbstractEntity implements Entity, Comparable<MeasurementKey> {

	private final Arm d_arm;
	private final WhenTaken d_wt;
	private final StudyOutcomeMeasure<? extends Variable> d_som;

	public MeasurementKey(StudyOutcomeMeasure<? extends Variable> som, Arm a, WhenTaken wt) {
		if (som.getValue() == null) {
			throw new NullPointerException("Variable may not be null");
		}
		if (som.getValue() instanceof OutcomeMeasure && a == null) {
			throw new NullPointerException(
					"Arm may not be null for Endpoints/ADEs");
		}
		if (wt == null) {
			throw new NullPointerException("Moment of measurement may not be null");
		}
		d_som = som;
		d_arm = a;
		d_wt = wt;
	}

	public Variable getVariable() {
		return d_som.getValue();
	}

	public StudyOutcomeMeasure<? extends Variable> getOutcomeMeasure() { 
		return d_som;
	}
	
	public Arm getArm() {
		return d_arm;
	}

	public WhenTaken getWhenTaken() {
		return d_wt;
	}

	@Override
	public String toString() {
		return "<" + d_som + ", " + d_arm + " at " + d_wt + ">";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MeasurementKey) {
			MeasurementKey other = (MeasurementKey) o;
			return d_som.equals(other.d_som)
					&& EqualsUtil.equal(d_arm, other.d_arm)
					&& EqualsUtil.equal(d_wt, other.d_wt);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int code = 1;
		code = code * 31 + d_som.hashCode();
		code = code * 31 + (d_arm == null ? 0 : d_arm.hashCode());
		code = code * 31 + (d_wt == null ? 0 : d_wt.hashCode());
		return code;
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}

	@Override
	public int compareTo(MeasurementKey o) {
		Variable variable = d_som.getValue();
		Variable otherVariable = o.d_som.getValue();
		if (variable.compareTo(otherVariable) == 0) {
			if (d_arm != null) {
				if (d_arm.compareTo(o.d_arm) == 0) {
					return d_wt.compareTo(o.d_wt);
				}
				return d_arm.compareTo(o.d_arm);
			} else if (o.d_arm == null) {
				return d_wt.compareTo(o.d_wt);
			} else {
				return -1;
			}
		}
		return variable.compareTo(otherVariable);
	}
}