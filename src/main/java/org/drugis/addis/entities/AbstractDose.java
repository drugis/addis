package org.drugis.addis.entities;

import java.util.Set;

public abstract class AbstractDose extends AbstractEntity {
	private static final long serialVersionUID = 3825538297852622266L;
	
	protected SIUnit d_unit;
	public static final String PROPERTY_UNIT = "unit";

	public SIUnit getUnit() {
		return d_unit;
	}

	public void setUnit(SIUnit unit) {
		SIUnit oldVal = d_unit;
		d_unit = unit;
		firePropertyChange(PROPERTY_UNIT, oldVal, d_unit);
	}

	public Set<Entity> getDependencies() {
		return null;
	}

}
