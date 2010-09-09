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

package org.drugis.addis.presentation.wizard;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;

public class AbstractWizardWithSelectableIndicationPM implements WizardWithSelectableIndicationPresentation {

	protected Domain d_domain;
	protected ModifiableHolder<Indication> d_indicationHolder;

	public AbstractWizardWithSelectableIndicationPM(Domain d) {
		d_domain = d;
		d_indicationHolder = new ModifiableHolder<Indication>();
	}

	public ValueHolder<Indication> getIndicationModel() {
		return d_indicationHolder; 
	}

	@SuppressWarnings("serial")
	public ListHolder<Indication> getIndicationListModel() {
		return new AbstractListHolder<Indication>() {
			@Override
			public List<Indication> getValue() {
				return new ArrayList<Indication>(d_domain.getIndications());
			}
		};
	}

}