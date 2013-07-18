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
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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
import java.util.Date;
import java.util.Set;

import javax.xml.datatype.Duration;

import org.drugis.addis.presentation.DurationPresentation;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.EqualsUtil;

public class DoseUnit extends AbstractEntity implements TypeWithDuration {

	public static final String PROPERTY_SCALE_MODIFIER = "scaleModifier";
	public static final String PROPERTY_UNIT = "unit";
	public static final String PROPERTY_PER_TIME = "perTime";

	private ScaleModifier d_scaleModifier;
	private Unit d_unit;
	private Duration d_perTime;
	private static final DoseUnit MILLIGRAMS_A_DAY = new DoseUnit(Domain.GRAM, ScaleModifier.MILLI, EntityUtil.createDuration("P1D"));

	public DoseUnit(final Unit u, final ScaleModifier scaleMod, final Duration perTime) {
		d_unit = u;
		d_scaleModifier = scaleMod;
		d_perTime = perTime;
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.singleton(d_unit);
	}

	public Unit getUnit() {
		return d_unit;
	}

	public ScaleModifier getScaleModifier() {
		return d_scaleModifier;
	}

	public Duration getPerTime() {
		return d_perTime;
	}

	public void setUnit(final Unit unit) {
		Unit oldValue = d_unit;
		d_unit = unit;
		firePropertyChange(PROPERTY_UNIT, oldValue, d_unit);
	}

	public void setScaleModifier(final ScaleModifier scaleMod) {
		ScaleModifier oldValue = d_scaleModifier;
		d_scaleModifier = scaleMod;
		firePropertyChange(PROPERTY_SCALE_MODIFIER, oldValue, d_scaleModifier);
	}

	public void setPerTime(final Duration perTime) {
		Duration oldValue = d_perTime;
		d_perTime = perTime;
		firePropertyChange(PROPERTY_PER_TIME, oldValue, d_perTime);
	}

	@Override
	public String getLabel() {
		return d_scaleModifier.getSymbol() + d_unit.getSymbol() + "/" + DurationPresentation.parseDuration(d_perTime, null);
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null || !(other instanceof DoseUnit)) {
			return false;
		}
		DoseUnit o = (DoseUnit) other;
		return EqualsUtil.equal(d_unit, o.d_unit) &&
			EqualsUtil.equal(d_scaleModifier, o.d_scaleModifier) &&
			EqualsUtil.equal(d_perTime, o.d_perTime);
	}

	@Override
	public boolean deepEquals(final Entity other) {
		if(equals(other)) {
			DoseUnit o = (DoseUnit) other;
			return EntityUtil.deepEqual(d_unit, o.d_unit);
		}
		return false;
	}

	@Override
	public DoseUnit clone() {
		return new DoseUnit(d_unit, d_scaleModifier, d_perTime);
	}

	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public Duration getDuration() {
		return getPerTime();
	}

	@Override
	public void setDuration(final Duration duration) {
		setPerTime(duration);
	}

	public static double convert(final double quantity, final DoseUnit from, final DoseUnit to) {
		double fromMillis = from.getPerTime().getTimeInMillis(new Date(0));
		double toMillis = to.getPerTime().getTimeInMillis(new Date(0));
		double scale = from.d_scaleModifier.getFactor() / to.d_scaleModifier.getFactor();

		return quantity * scale * (toMillis / fromMillis);
	}

	public static DoseUnit createDefault() {
		return createMilliGramsPerDay();
	}

	public static DoseUnit createMilliGramsPerDay() {
		return MILLIGRAMS_A_DAY.clone();
	}
}
