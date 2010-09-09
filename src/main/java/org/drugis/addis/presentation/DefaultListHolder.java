/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

package org.drugis.addis.presentation;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
public class DefaultListHolder<E> extends AbstractListHolder<E> {
	
	private List<E> d_list;

	public DefaultListHolder(List<E> list) {
		d_list = list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object newValue) {
		List<E> oldValue = d_list;
		d_list = (List<E>) newValue;
		fireValueChange(oldValue, newValue);
	}

	@Override
	public List<E> getValue() {
		return Collections.unmodifiableList(d_list);
	}
}
