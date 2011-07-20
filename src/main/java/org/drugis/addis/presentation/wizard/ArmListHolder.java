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

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.AbstractListHolder;

@SuppressWarnings("serial")
public class ArmListHolder extends AbstractListHolder<Arm> implements PropertyChangeListener {
	Study d_study;
	DrugSet d_drug;
	
	public ArmListHolder(Study s, DrugSet d) {
		d_study = s;
		d_drug = d;
		
		d_study.addPropertyChangeListener(this);
		d_drug.addPropertyChangeListener(this);
	}

	@Override
	public List<Arm> getValue() {
		// get arms per study per drug
		ArrayList<Arm> armList = new ArrayList<Arm>();
		for (Arm curArm : d_study.getArms()) {
			if (d_study.getDrugs(curArm).equals(d_drug)) {
				armList.add(curArm);
			}
		}
		return armList;
	}
	
	public void propertyChange(PropertyChangeEvent arg0) {
		fireValueChange(null,getValue());			
	}
}