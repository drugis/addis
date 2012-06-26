package org.drugis.addis.entities.treatment;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.DoseUnit;

import com.jgoodies.binding.beans.BeanUtils;

public class DoseRangeNode extends RangeNode {

	private final DoseUnit d_doseUnit;

	public DoseRangeNode(Class<?> beanClass, String propertyName, DoseUnit doseUnit) {
		super(beanClass, propertyName);
		d_doseUnit = doseUnit;
	}
	
	public DoseRangeNode(Class<?> beanClass, String propertyName, DoseUnit doseUnit, DecisionTreeNode child) {
		super(beanClass, propertyName, child);
		d_doseUnit = doseUnit;
	}
	
	public DoseRangeNode(Class<?> beanClass, String propertyName,
			double lowerBound, 
			boolean lowerBoundIsOpen, 
			double upperBound,
			boolean upperBoundIsOpen, 
			DoseUnit doseUnit,
			DecisionTreeNode child) {
		super(beanClass, propertyName, lowerBound, lowerBoundIsOpen, upperBound,
				upperBoundIsOpen, child);
		d_doseUnit = doseUnit;
	}
	
	@Override
	public DecisionTreeNode decide(Object object) {
		try {
			PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(getBeanClass(), d_propertyName);
			Object value = BeanUtils.getValue(object, propertyDescriptor);
			try { 
				DoseUnit unit = ((AbstractDose)object).getDoseUnit();		
				return getNodeByValue(DoseUnit.convert((Double)value, unit, d_doseUnit));
			} catch(ClassCastException e) { 
				throw new IllegalArgumentException("Object was not an AbstractDose, or property was not numeric. Object was a: " + object.getClass().getName());
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		} 
		throw new IllegalStateException("Could not decide the fate of " + object.toString());
	}
	

	
}
