/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.presentation.wizard.WhenTakenFactory;

import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class SelectEndpointPresentation extends SelectVariablesPresentation<Endpoint> {
	public SelectEndpointPresentation(ObservableList<Endpoint> options, WhenTakenFactory wtf, AddisWindow mainWindow) {
		super(options, Endpoint.class, "Select Endpoint", "Please select the appropriate endpoints.", wtf, mainWindow);
	}

	@Override
	public void showAddOptionDialog(int idx) {
		d_mainWindow.showAddDialog(CategoryKnowledgeFactory.getCategoryKnowledge(Endpoint.class), getSlot(idx));
	}
}