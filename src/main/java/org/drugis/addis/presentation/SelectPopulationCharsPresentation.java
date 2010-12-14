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

import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.CategoryKnowledgeFactory;

@SuppressWarnings("serial")
public class SelectPopulationCharsPresentation
extends SelectFromFiniteListPresentationImpl<PopulationCharacteristic> {
	public SelectPopulationCharsPresentation(ListHolder<PopulationCharacteristic> options, AddisWindow mainWindow) {
		super(options, "Population Baseline Characteristics", "Select Population Baseline Characteristics",
			"Please select the appropriate population baseline characteristics.", mainWindow);
	}
	
	@Override
	public void showAddOptionDialog(int idx) {
		d_mainWindow.showAddDialog(
				CategoryKnowledgeFactory.getCategoryKnowledge(PopulationCharacteristic.class),
				getSlot(idx));
	}
}
