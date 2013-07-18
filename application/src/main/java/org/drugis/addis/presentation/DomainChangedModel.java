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

package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.EntityCategory;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.common.beans.ListPropertyChangeProxy;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class DomainChangedModel extends AbstractValueModel implements ValueHolder<Boolean> {
	private boolean d_changed;

	public DomainChangedModel(Domain domain, boolean changed) {
		d_changed = changed;

		ListDataListener listener = new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				setValue(true);
			}
			public void intervalAdded(ListDataEvent e) {
				setValue(true);
			}
			public void contentsChanged(ListDataEvent e) {
				setValue(true);
			}
		};
		for (EntityCategory c : domain.getCategories()) {
			domain.getCategoryContents(c).addListDataListener(listener);
		}
		new ListPropertyChangeProxy<MetaAnalysis>(domain.getNetworkMetaAnalyses(), new PropertyChangeListener() {
		    public void propertyChange(PropertyChangeEvent evt) {
		        setValue(true);
		    }
		});
	}
	
	public Boolean getValue() {
		return d_changed;
	}
	
	public void setValue(Object newValue) {
		boolean oldValue = d_changed;
		d_changed = ((Boolean)newValue);
		fireValueChange(oldValue, d_changed);
	}
}
