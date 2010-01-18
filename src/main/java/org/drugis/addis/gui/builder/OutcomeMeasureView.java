/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.OutcomePresentationModel;
import org.drugis.common.gui.OneWayObjectFormat;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ConverterFactory;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class OutcomeMeasureView implements ViewBuilder {
	private OutcomePresentationModel d_model;
	private Main d_frame;
	
	public OutcomeMeasureView(OutcomePresentationModel model, Main frame) {
		d_model = model;
		d_frame = frame;
	}

	public JComponent buildPanel() {

		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator(d_model.getCategoryName(), cc.xy(1, 1));
		builder.add(GUIFactory.createCollapsiblePanel(buildOverviewPart()),	cc.xy(1, 3));
		builder.addSeparator("Studies measuring this "+ d_model.getCategoryName(), cc.xy(1, 5));		
		builder.add(GUIFactory.createCollapsiblePanel(getStudiesComp()), cc.xy(1, 7));
		
		return builder.getPanel();
	}

	private JPanel buildOverviewPart() {
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
		
		builder.addLabel("Name:", cc.xy(1, 1));
		builder.add(BasicComponentFactory.createLabel(
				d_model.getModel(Endpoint.PROPERTY_NAME)), cc.xy(3, 1));
		
		builder.addLabel("Description:", cc.xy(1, 3));
 		builder.add(BasicComponentFactory.createLabel(
				d_model.getModel(Endpoint.PROPERTY_DESCRIPTION)), cc.xy(3, 3));

		builder.addLabel("Unit of Measurement:", cc.xy(1, 5));
		builder.add(BasicComponentFactory.createLabel(
				d_model.getModel(OutcomeMeasure.PROPERTY_UNIT_OF_MEASUREMENT)), cc.xy(3, 5));
		
		builder.addLabel("Direction:", cc.xy(1, 7));
		ValueModel directionModel = ConverterFactory.createStringConverter(
				d_model.getModel(Endpoint.PROPERTY_DIRECTION),
				new OneWayObjectFormat());
		builder.add(BasicComponentFactory.createLabel(
				directionModel), cc.xy(3, 7));

		return builder.getPanel();
	}

	private JComponent getStudiesComp() {
		JComponent studiesComp = null;
		if(d_model.getIncludedStudies().getValue().isEmpty()) {
			studiesComp = new JLabel("No studies found.");
		} else {
			StudyTablePanelView d_studyView = new StudyTablePanelView(d_model, d_frame);
			studiesComp = d_studyView.buildPanel();
		}
		return studiesComp;
	}	
}
