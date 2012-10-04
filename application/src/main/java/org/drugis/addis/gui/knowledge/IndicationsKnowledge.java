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

package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.gui.AddIndicationDialog;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.builder.IndicationView;
import org.drugis.addis.presentation.IndicationPresentation;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.value.ValueModel;

public class IndicationsKnowledge extends CategoryKnowledgeBase {
	public IndicationsKnowledge() {
		super("indication", FileNames.ICON_INDICATION, Indication.class);
	}
	
		
	public JDialog getAddDialog(AddisWindow mainWindow, Domain domain,
			ValueModel selectionModel) {
		return new AddIndicationDialog(mainWindow, domain, selectionModel);
	}
	
	@Override
	protected String[] getShownProperties() {
		return new String[] { Indication.PROPERTY_NAME, Indication.PROPERTY_CODE };
	}


	public ViewBuilder getEntityViewBuilder(AddisWindow main, Domain domain, Entity entity) {
		return new IndicationView(
				(IndicationPresentation) main.getPresentationModelFactory().getModel(((Indication) entity)), main);
	}
}
