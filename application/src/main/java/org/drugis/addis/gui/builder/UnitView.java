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

package org.drugis.addis.gui.builder;

import javax.swing.JComponent;

import org.drugis.addis.entities.Unit;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.presentation.UnitPresentation;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class UnitView implements ViewBuilder{

	private final UnitPresentation d_model;

	public UnitView(UnitPresentation model) {
		d_model = model;
		
	}

	@Override
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, 5dlu, 3dlu, left:pref:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Unit.class).getSingularCapitalized(), cc.xyw(1, 1, 5));
		addColonSeparated(builder, 3, "Name", d_model.getModel(Unit.PROPERTY_NAME).getString());
		addColonSeparated(builder, 5, "Symbol", d_model.getModel(Unit.PROPERTY_SYMBOL).getString());
		
		return builder.getPanel();
	}

	private void addColonSeparated(PanelBuilder builder, int row, String beforeColon, String afterColon) {
		CellConstraints cc = new CellConstraints();
		builder.addLabel(beforeColon, cc.xy(1,row));
		builder.addLabel(":", cc.xy(3, row));
		builder.addLabel(afterColon, cc.xy(5, row));
	}

}
