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

package org.drugis.addis.presentation;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.wizard.AddStudyWizardPresentation;

import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class SelectEndpointPresentation
extends SelectFromFiniteListPresentationImpl<Endpoint> implements NoteModelPresentation{
	
	private final AddStudyWizardPresentation d_pm;

	public SelectEndpointPresentation(ListHolder<Endpoint> options, Main main, AddStudyWizardPresentation pm) {
		super(options, "Endpoint", "Select Endpoint", "Please select the appropriate endpoints.", main);
		d_pm = pm;
	}

	@Override
	public void showAddOptionDialog(int idx) {
		d_main.showAddDialog(CategoryKnowledgeFactory.getCategoryKnowledge(Endpoint.class), getSlot(idx));
	}

	public ValueModel getNoteModel(int idx) {
		return d_pm.getEndpointNoteModel(idx);
	}
	
	@Override
	public void removeSlot(int idx) {
		super.removeSlot(idx);
		d_pm.removeImportEndpoint(idx);
	}
	
}