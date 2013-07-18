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
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.common.EqualsUtil;
import org.drugis.common.beans.AbstractObservable;

public class Option<E extends Comparable<? super E>> extends AbstractObservable implements Comparable<Option<E>> {
	public static final String PROPERTY_TOGGLED = "toggled";
	public final E item;
	public final ModifiableHolder<Boolean> toggle;
	
	public Option(E it, boolean value) {
		item = it;
		toggle = new ModifiableHolder<Boolean>(value);
		toggle.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				firePropertyChange(PROPERTY_TOGGLED, evt.getOldValue(), evt.getNewValue());
			}
		});
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Option<?>) {
			Option<?> other = (Option<?>) obj;
			return EqualsUtil.equal(item, other.item);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return item.hashCode();
	}

	@Override
	public int compareTo(Option<E> o) {
		return item.compareTo(o.item);
	}
}