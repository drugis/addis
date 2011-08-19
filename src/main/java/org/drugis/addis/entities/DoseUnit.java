package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

import javax.xml.datatype.Duration;

import org.drugis.addis.util.EntityUtil;
import org.drugis.common.EqualsUtil;

public class DoseUnit extends AbstractEntity {

	public static final String PROPERTY_SCALE_MODIFIER = "scaleModifier";
	public static final String PROPERTY_UNIT = "unit";
	public static final String PROPERTY_PER_TIME = "perTime";
	
	private ScaleModifier d_scaleModifier;
	private Unit d_unit;
	private Duration d_perTime;

	public DoseUnit(Unit u, ScaleModifier scaleMod, Duration perTime) {
		d_unit = u;
		d_scaleModifier = scaleMod;
		d_perTime = perTime;
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
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
	
	public void setUnit(Unit unit) {
		Unit oldValue = d_unit;
		d_unit = unit;
		firePropertyChange(PROPERTY_UNIT, oldValue, d_unit);
	}

	public void setScaleModifier(ScaleModifier scaleMod) {
		ScaleModifier oldValue = d_scaleModifier;
		d_scaleModifier = scaleMod;
		firePropertyChange(PROPERTY_SCALE_MODIFIER, oldValue, d_scaleModifier);
	}

	public void setPerTime(Duration perTime) {
		Duration oldValue = d_perTime;
		d_perTime = perTime;
		firePropertyChange(PROPERTY_PER_TIME, oldValue, d_perTime);
	}

	@Override
	public String getLabel() {
		return d_scaleModifier.getSymbol() + d_unit.getSymbol() + "/" + "day";
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof DoseUnit)) {
			return false;
		}
		DoseUnit o = (DoseUnit) other;
		return EqualsUtil.equal(d_unit, o.d_unit) &&
			EqualsUtil.equal(d_scaleModifier, o.d_scaleModifier) &&
			EqualsUtil.equal(d_perTime, o.d_perTime);
	}
	
	@Override
	public boolean deepEquals(Entity other) {
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
	
}
