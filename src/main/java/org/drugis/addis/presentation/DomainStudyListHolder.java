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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainEvent;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;

@SuppressWarnings("serial")
public class DomainStudyListHolder extends AbstractListHolder<Study> {
	private final ValueHolder<OutcomeMeasure> d_outcome;
	private final ValueHolder<Indication> d_indication;
	private final Domain d_domain;

	public DomainStudyListHolder(Domain domain,
			ValueHolder<Indication> indication, 
			ValueHolder<OutcomeMeasure> outcome) {
		d_domain = domain;
		d_indication = indication;
		d_outcome = outcome;
		
		d_domain.addListener(new DomainListener() {
			public void domainChanged(DomainEvent evt) {
				if (evt.getType().equals(DomainEvent.Type.STUDIES)) {
					fireValueChange(null, getValue());
				}
			}
		});
		PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				fireValueChange(null, getValue());
			}
		};
		d_indication.addValueChangeListener(listener);
		d_outcome.addValueChangeListener(listener);
	}

	@Override
	public List<Study> getValue() {
		if (d_indication.getValue() == null || d_outcome.getValue() == null)
			return Collections.emptyList();
		List<Study> studies = d_domain.getStudies(d_indication.getValue()).getValue();
		studies.retainAll(d_domain.getStudies(d_outcome.getValue()).getValue());
		return studies;
	}
}