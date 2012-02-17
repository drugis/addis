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

import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.DurationPresentation;
import org.drugis.addis.util.EntityUtil;

import com.jgoodies.binding.list.ObservableList;

public class AddEpochsPresentation extends AddListItemsPresentation<Epoch> {

	private Study d_study;

	public AddEpochsPresentation(Study study, String itemName, int minElements) {
		super(study.getEpochs(), itemName, minElements);
		d_study = study;
	}

	@Override
	public ObservableList<Note> getNotes(Epoch t) {
		return t.getNotes();
	}
	
	@Override
	public Epoch createItem() {
		return new Epoch(nextItemName(), EntityUtil.createDuration("P0D"));
	}

	public DurationPresentation<Epoch> getDurationModel(int idx) {
		return new DurationPresentation<Epoch>(getList().get(idx));
	}

	@Override
	public void rename(int idx, String newName) {
		Epoch oldEpoch = d_list.get(idx);
		d_study.replaceEpoch(oldEpoch, oldEpoch.rename(newName));
	}

	public void setStudy(Study study) {
		d_study = study;
		setList(d_study.getEpochs());
	}
	
	public Study getStudy() {
		return d_study;
	}
}
