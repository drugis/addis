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

	public DoseUnit getDoseUnit() {
		return d_doseUnit;
	}

	@Override
	public boolean equivalent(DecisionTreeNode o) {
		if (!(o instanceof DoseQuantityChoiceNode)) {
			return false;
		}
		return getDoseUnit().equals(((DoseQuantityChoiceNode)o).getDoseUnit()) && super.equivalent(o);
	}
}
