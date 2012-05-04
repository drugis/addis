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

package org.drugis.addis.gui.builder;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.entities.CategoricalVariableType;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.components.ListPanel;
import org.drugis.addis.gui.components.StudiesTablePanel;
import org.drugis.addis.presentation.VariablePresentation;
import org.drugis.common.gui.OneWayObjectFormat;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.value.ConverterFactory;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class VariableView implements ViewBuilder {
	private VariablePresentation d_model;
	private AddisWindow d_frame;
	
	public VariableView(VariablePresentation model, AddisWindow main) {
		d_model = model;
		d_frame = main;
	}

	public JComponent buildPanel() {

		FormLayout layout = new FormLayout(
				"fill:0:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator(d_model.getCategoryName(), cc.xy(1, 1));
		builder.add(buildOverviewPart(),	cc.xy(1, 3));
		builder.addSeparator("Studies measuring this "+ CategoryKnowledgeFactory.getCategoryKnowledge(d_model.getBean().getClass()).getSingular(), cc.xy(1, 5));		
		builder.add(getStudiesComp(), cc.xy(1, 7));
		
		return builder.getPanel();
	}

	private JPanel buildOverviewPart() {
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, fill:0:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
		
		builder.addLabel("Name:", cc.xy(1, 1));
		builder.add(AuxComponentFactory.createAutoWrapLabel(
				d_model.getModel(Variable.PROPERTY_NAME)), cc.xy(3, 1));
		
		builder.addLabel("Description:", cc.xy(1, 3));
		
		builder.add(AuxComponentFactory.createAutoWrapLabel(d_model.getModel(Variable.PROPERTY_DESCRIPTION)), cc.xy(3, 3));

 		if(d_model.getBean().getVariableType() instanceof ContinuousVariableType) {
 			builder.addLabel("Unit of Measurement:", cc.xy(1, 5));
 			builder.add(AuxComponentFactory.createAutoWrapLabel(
 					d_model.getContinuousModel().getModel(ContinuousVariableType.PROPERTY_UNIT_OF_MEASUREMENT)), cc.xy(3, 5));
 		}

		ValueModel typeModel = ConverterFactory.createStringConverter(
				d_model.getModel(Variable.PROPERTY_VARIABLE_TYPE),
				new OneWayObjectFormat());
		builder.addLabel("Type:", cc.xy(1, 7));
		builder.add(AuxComponentFactory.createAutoWrapLabel(typeModel), cc.xy(3, 7));
		
		if (d_model.getBean() instanceof OutcomeMeasure) {
			builder.addLabel("Direction:", cc.xy(1, 9));
			ValueModel directionModel = ConverterFactory.createStringConverter(
					d_model.getModel(OutcomeMeasure.PROPERTY_DIRECTION),
					new OneWayObjectFormat());
			builder.add(AuxComponentFactory.createAutoWrapLabel(directionModel), cc.xy(3, 9));
		}
		
		if (d_model.getBean().getVariableType() instanceof CategoricalVariableType) {
			CategoricalVariableType variableType = (CategoricalVariableType) d_model.getBean().getVariableType();
			builder.addLabel("Categories:", cc.xy(1, 11));
			ListPanel listBox = new ListPanel(variableType, CategoricalVariableType.PROPERTY_CATEGORIES, String.class);
			Color c = d_frame.getRightPanel().getBackground();
			listBox.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue()));
			builder.add(listBox, cc.xy(3, 11));
		}
		return builder.getPanel();
	}

	private JComponent getStudiesComp() {
		JComponent studiesComp = null;
		if(d_model.getIncludedStudies().isEmpty()) {
			studiesComp = new JLabel("No studies found.");
		} else {
			studiesComp = new StudiesTablePanel(d_model, d_frame);
		}
		return studiesComp;
	}	
}
