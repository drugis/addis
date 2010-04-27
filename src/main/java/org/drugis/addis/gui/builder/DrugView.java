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

package org.drugis.addis.gui.builder;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.StudiesTablePanel;
import org.drugis.addis.presentation.DrugPresentationModel;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DrugView implements ViewBuilder{
	private DrugPresentationModel d_model;
	private Main d_parent;

	public DrugView(DrugPresentationModel model, Main parent) {
		d_model = model;
		d_parent = parent;
	}
	
	public JComponent buildPanel() {
		
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Drug", cc.xy(1, 1));
		builder.add(GUIFactory.createCollapsiblePanel(createOverviewPart()),
				cc.xy(1, 3));
		builder.addSeparator("Studies measuring this drug", cc.xy(1, 5));
		builder.add(GUIFactory.createCollapsiblePanel(buildStudiesComp()), 
				cc.xy(1, 7));
				
		return builder.getPanel();	
	}

	private JComponent buildStudiesComp() {
		JComponent studiesComp = null;
		if(d_model.getIncludedStudies().getValue().isEmpty()) {
			studiesComp = new JLabel("No studies found.");
		} else {
			studiesComp = new StudiesTablePanel(d_model, d_parent);
		}
		return studiesComp;
	}

	private JPanel createOverviewPart() {
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, left:pref:grow",
				"p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		builder.addLabel("Name:", cc.xy(1, 1));
		builder.add(BasicComponentFactory.createLabel(d_model.getModel(Drug.PROPERTY_NAME)), cc.xy(3,1));
		builder.addLabel("ATC Code:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(d_model.getModel(Drug.PROPERTY_ATCCODE)), cc.xy(3, 3));
		
		return builder.getPanel();
	}
}