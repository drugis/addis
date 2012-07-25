package org.drugis.addis.entities.treatment;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import org.apache.commons.lang.StringUtils;
import org.drugis.common.gui.GUIHelper;

import com.jgoodies.binding.beans.BeanUtils;

public class ChoiceNode implements DecisionTreeNode {
	private final Class<?> d_beanClass;
	private final PropertyDescriptor d_descriptor;

	public ChoiceNode(final Class<?> beanClass, final String propertyName) {
		d_beanClass = beanClass;
		try {
			d_descriptor = new PropertyDescriptor(propertyName, beanClass, "get" + StringUtils.capitalize(propertyName), null);
		} catch (final IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Calculate the choice-value for the given object.
	 * @param obj Object under consideration.
	 * @return Value for current choice.
	 */
	public Object getValue(final Object obj) {
		return BeanUtils.getValue(obj, d_descriptor);
	}

	public Class<?> getBeanClass() {
		return d_beanClass;
	}

	public String getPropertyName() {
		return d_descriptor.getName();
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(GUIHelper.humanize(d_descriptor.getName()));
	}

	@Override
	public String toString() {
		return getName();
	}
}
