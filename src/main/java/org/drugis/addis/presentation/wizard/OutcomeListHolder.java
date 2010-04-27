/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.ModifiableHolder;

@SuppressWarnings("serial")
public class OutcomeListHolder extends AbstractListHolder<OutcomeMeasure> implements PropertyChangeListener {
	private ModifiableHolder<Indication> d_indication;
	private Domain d_domain;

	public OutcomeListHolder(ModifiableHolder<Indication> indication, Domain domain) {
		this.d_indication = indication;
		this.d_domain = domain;
		d_indication.addValueChangeListener(this);
	}
	
	@Override
	public List<OutcomeMeasure> getValue() {	
		return getEndpointSet();
	}
	
	private List<OutcomeMeasure> getEndpointSet() {
		TreeSet<OutcomeMeasure> outcomeMeasures = new TreeSet<OutcomeMeasure>();
		if (this.d_indication.getValue() != null) {
			for (Study s : d_domain.getStudies(this.d_indication.getValue()).getValue()) {
				outcomeMeasures.addAll(s.getOutcomeMeasures());
			}			
		}	
		
		return new ArrayList<OutcomeMeasure>(outcomeMeasures);
	}		

	public void propertyChange(PropertyChangeEvent event) {
		fireValueChange(null, getValue());
	}
}