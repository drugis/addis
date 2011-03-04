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


import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Study.StudyOutcomeMeasure;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.CategoryKnowledgeFactory;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class SelectEndpointPresentation
extends SelectVariablesPresentation<Endpoint> implements NoteModelPresentation{
	public SelectEndpointPresentation(ListHolder<Endpoint> options, AddisWindow mainWindow) {
		super(options, "Endpoint", "Select Endpoint", "Please select the appropriate endpoints.", mainWindow);
	}

	@Override
	public void showAddOptionDialog(int idx) {
		d_mainWindow.showAddDialog(CategoryKnowledgeFactory.getCategoryKnowledge(Endpoint.class), getSlot(idx));
	}

	public ValueModel getNoteModel(int idx) {
		return new NoteModel(getSlot(idx));
	}
	
	static class NoteModel extends AbstractValueModel {
		private final StudyOutcomeMeasure<Endpoint> d_slot;

		public NoteModel(StudyOutcomeMeasure<Endpoint> slot) {
			d_slot = slot;
		}
		
		public String getValue() {
			return d_slot.getNotes().size() > 0 ? d_slot.getNotes().get(0).getText() : null;
		}

		public void setValue(Object newValue) {
		}
	}
	
	@Override
	public void removeSlot(int idx) {
		super.removeSlot(idx);
	}
	
}