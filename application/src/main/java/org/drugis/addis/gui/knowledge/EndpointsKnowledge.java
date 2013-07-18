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

package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.gui.AddVariableDialog;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.builder.VariableView;
import org.drugis.addis.presentation.VariablePresentation;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.value.ValueModel;

public class EndpointsKnowledge extends CategoryKnowledgeBase {
	public EndpointsKnowledge() {
		super("endpoint", FileNames.ICON_ENDPOINT, Endpoint.class);
	}
	
	public JDialog getAddDialog(AddisWindow mainWindow, Domain domain,
			ValueModel selectionModel) {
		Variable variable = new Endpoint("", Endpoint.convertVarType(Variable.Type.RATE));
		return new AddVariableDialog(mainWindow, domain, variable, selectionModel);
	}
	
	@Override
	protected String[] getShownProperties() {
		return new String[] { Variable.PROPERTY_NAME, Variable.PROPERTY_DESCRIPTION, Variable.PROPERTY_VARIABLE_TYPE, Endpoint.PROPERTY_DIRECTION };
	}

	public ViewBuilder getEntityViewBuilder(AddisWindow mainWindow, Domain domain, Entity entity) {
		return new VariableView((VariablePresentation) mainWindow.getPresentationModelFactory().getModel(((Variable) entity)), mainWindow);
	}
}
