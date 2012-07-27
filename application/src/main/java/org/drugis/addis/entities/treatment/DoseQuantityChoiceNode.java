package org.drugis.addis.entities.treatment;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.DoseUnit;

import com.jgoodies.binding.beans.BeanUtils;

public class DoseQuantityChoiceNode extends ChoiceNode {

	private final DoseUnit d_doseUnit;
	private PropertyDescriptor d_descriptor;

	public DoseQuantityChoiceNode(Class<?> beanClass, String propertyName, DoseUnit doseUnit) {
		super(beanClass, propertyName);
		try {
			d_descriptor = new PropertyDescriptor(AbstractDose.PROPERTY_DOSE_UNIT, beanClass);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
		d_doseUnit = doseUnit;
	}
	
	@Override
	public Object getValue(Object obj) {
		final double value = (Double) super.getValue(obj);
		return DoseUnit.convert(value, (DoseUnit) BeanUtils.getValue(obj, d_descriptor), d_doseUnit);
	}

}
