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

package org.drugis.addis.gui;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.ValueModelWrapper;
import org.drugis.mtc.presentation.MCMCModelWrapper;
import org.drugis.mtc.presentation.MCMCPresentation;
import org.drugis.mtc.presentation.MTCModelWrapper;

public class AddisMCMCPresentation extends MCMCPresentation implements Comparable<AddisMCMCPresentation> {
	protected final OutcomeMeasure d_om;
	
	public AddisMCMCPresentation(final MCMCModelWrapper wrapper, final OutcomeMeasure om, final String name) {
		super(wrapper, name);
		d_om = om;
	}
	
	@Override
	public ValueHolder<Boolean> isModelConstructed() {
		return new ValueModelWrapper<Boolean>(super.isModelConstructed());
	}

	@Override
	public int compareTo(AddisMCMCPresentation o) {
		int omCompare = d_om.compareTo(o.getOutcomeMeasure());
		int modelComp = (o.getWrapper() instanceof MTCModelWrapper) ? 1 : -1;
		return (omCompare == 0) ? modelComp : omCompare;
	}

	public OutcomeMeasure getOutcomeMeasure() {
		return d_om;
	}
}
