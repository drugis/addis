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

package org.drugis.addis.presentation.wizard;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.Study;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;

public class AddArmsPresentation extends AddListItemsPresentation<Arm> {
	private Study d_study;

	public AddArmsPresentation(Study study, String itemName, int minElements) {
		super(study.getArms(), itemName, minElements);
		d_study = study;
	}

	@Override
	public ObservableList<Note> getNotes(Arm t) {
		return t.getNotes();
	}
	

	@Override
	public ValueModel getRemovable(Arm t) {
		return new ValueHolder(true);
	}
	
	@Override
	public Arm createItem() {
		return new Arm(nextItemName(), 0);
	}
	
	public void setStudy(Study study) {
		d_study = study;
		setList(d_study.getArms());
	}
	
	public Study getStudy() {
		return d_study;
	}
	
	@Override
	public void rename(int idx, String newName) {
		Arm oldArm = d_list.get(idx);
		d_study.replaceArm(oldArm, oldArm.rename(newName));
	}

}
