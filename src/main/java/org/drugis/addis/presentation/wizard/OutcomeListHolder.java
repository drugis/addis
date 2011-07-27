/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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
import java.util.SortedSet;
import java.util.TreeSet;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.util.comparator.OutcomeComparator;

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
		return getOutcomeSet();
	}
	
	private List<OutcomeMeasure> getOutcomeSet() {
		SortedSet<OutcomeMeasure> endpoints = new TreeSet<OutcomeMeasure>(new OutcomeComparator());
		SortedSet<OutcomeMeasure> ades = new TreeSet<OutcomeMeasure>(new OutcomeComparator());
		if (this.d_indication.getValue() != null) {
			for (Study s : d_domain.getStudies(this.d_indication.getValue())) {
				ArrayList<OutcomeMeasure> tempEndpoints = new ArrayList<OutcomeMeasure>();
				tempEndpoints.addAll(Study.extractVariables(s.getEndpoints()));
				tempEndpoints.removeAll(endpoints);
				endpoints.addAll(tempEndpoints);
			}			
			for (Study s : d_domain.getStudies(this.d_indication.getValue())) {
				ArrayList<OutcomeMeasure> tempAdes = new ArrayList<OutcomeMeasure>();
				tempAdes.addAll(Study.extractVariables(s.getAdverseEvents()));
				tempAdes.removeAll(ades);
				ades.addAll(tempAdes);
			}			
		}	
		
		ArrayList<OutcomeMeasure> outcomes = new ArrayList<OutcomeMeasure>();
		outcomes.addAll(endpoints);
		outcomes.addAll(ades);
		return outcomes;
	}		

	public void propertyChange(PropertyChangeEvent event) {
		fireValueChange(null, getValue());
	}
}