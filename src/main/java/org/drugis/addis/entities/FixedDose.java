package org.drugis.addis.entities;

import static org.drugis.common.EqualsUtil.equal;

public class FixedDose extends AbstractDose {
	private static final long serialVersionUID = 8020828306122176413L;

	public static final String PROPERTY_QUANTITY = "quantity";

	private Double d_quantity;
	
	public FixedDose(double quantity, SIUnit unit) {
		d_quantity = quantity;
		d_unit = unit;
	}
	
	public Double getQuantity() {
		return d_quantity;
	}

	public void setQuantity(Double quantity) {
		Double oldVal = d_quantity;
		d_quantity = quantity;
		firePropertyChange(PROPERTY_QUANTITY, oldVal, d_quantity);
	}
	
	public String toString() {
		if (d_quantity == null || d_unit == null) {
			return "INCOMPLETE";
		}
		return d_quantity.toString() + " " + d_unit.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof FixedDose) {
			FixedDose other = (FixedDose)o;
			return equal(other.getQuantity(), getQuantity()) &&
				equal(other.getUnit(), getUnit());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash *= 31; 
		hash += getQuantity().hashCode();
		hash = hash * 31 + getUnit().hashCode();
		return hash;
	}
}
