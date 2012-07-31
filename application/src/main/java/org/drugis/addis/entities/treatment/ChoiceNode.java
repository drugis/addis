/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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
