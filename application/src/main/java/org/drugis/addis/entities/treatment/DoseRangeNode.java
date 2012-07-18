package org.drugis.addis.entities.treatment;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.beans.BeanUtils;

public class DoseRangeNode extends RangeNode implements Comparable<RangeNode> {

	private final DoseUnit d_doseUnit;

	public DoseRangeNode(Class<?> beanClass, String propertyName, DoseUnit doseUnit) {
		super(beanClass, propertyName);
		d_doseUnit = doseUnit;
	}
	
	public DoseRangeNode(
			Class<?> beanClass, 
			String propertyName,
			double lowerBound, 
			boolean lowerBoundIsOpen, 
			double upperBound,
			boolean upperBoundIsOpen, 
			DoseUnit doseUnit) {
		super(beanClass, propertyName, lowerBound, lowerBoundIsOpen, upperBound,
				upperBoundIsOpen);
		d_doseUnit = doseUnit;
	}
	
	@Override
	public boolean decide(Object object) {
		try {
			PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(getBeanClass(), d_propertyName);
			Object value = BeanUtils.getValue(object, propertyDescriptor);
			try { 
				DoseUnit unit = ((AbstractDose)object).getDoseUnit();		
				return getInterval().getRange().containsDouble((DoseUnit.convert((Double)value, unit, d_doseUnit)));
			} catch(ClassCastException e) { 
				throw new IllegalArgumentException("Object was not an AbstractDose, or property was not numeric. Object was a: " + object.getClass().getName());
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		} 
		throw new IllegalStateException("Could not decide the fate of " + object.toString());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DoseRangeNode) {
			DoseRangeNode other = (DoseRangeNode) obj;
			return super.equals(other) && EqualsUtil.equal(d_doseUnit, other.d_doseUnit);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return d_doseUnit.hashCode() + 31 * super.hashCode();
	}

	public String getLabel(boolean nodeIsLast) {
		return super.getLabel(nodeIsLast, d_doseUnit);
	}
		
	@Override
	public int compareTo(RangeNode o) {
		if(o instanceof DoseRangeNode) { 
			return super.compareTo((DoseRangeNode)o);
		} else { 
			return -1;
		}
	}
}
