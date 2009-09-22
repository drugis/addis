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

package org.drugis.addis.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.drugis.addis.entities.AbstractStudy;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Dose;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddStudyView implements ViewBuilder {
	private JTextField d_id;
	private JComboBox d_endpoint;
	private JComboBox d_indication;
	private PresentationModel<BasicStudy> d_model;
	private PresentationModel<EndpointHolder> d_endpointModel;
	private Domain d_domain;
	private NotEmptyValidator d_validator;

	public AddStudyView(PresentationModel<BasicStudy> presentationModel,
			PresentationModel<EndpointHolder> presentationModel2, Domain domain,
			JButton okButton) {
		d_validator = new NotEmptyValidator(okButton);		
		d_model = presentationModel;
		d_endpointModel = presentationModel2;
		d_domain = domain;
	}
	
	public void initComponents() {
		d_id = BasicComponentFactory.createTextField(d_model.getModel(AbstractStudy.PROPERTY_ID));
		d_id.setColumns(30);
		AutoSelectFocusListener.add(d_id);
		d_validator.add(d_id);
		
		SelectionInList<Indication> indicationSelectionInList =
			new SelectionInList<Indication>(
					new ArrayList<Indication>(d_domain.getIndications()),
					new CharacteristicHolder(
						d_model.getBean(),
						StudyCharacteristic.INDICATION));
		d_indication = BasicComponentFactory.createComboBox(indicationSelectionInList);
		d_validator.add(d_indication);
		
		SelectionInList<Endpoint> endpointSelectionInList =
			new SelectionInList<Endpoint>(
					new ArrayList<Endpoint>(d_domain.getEndpoints()), 
					d_endpointModel.getModel(EndpointHolder.PROPERTY_ENDPOINT));
		d_endpoint = BasicComponentFactory.createComboBox(endpointSelectionInList);
		d_validator.add(d_endpoint);
		ComboBoxPopupOnFocusListener.add(d_endpoint);
	}
	
	public JComponent buildPanel() {
		initComponents();
		
		FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:pref:grow, 3dlu, center:pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);	
		int fullWidth = 5;
		if (getEndpoint() != null) {
			for (int i = 0; i < MeasurementInputHelper.numComponents(getEndpoint()); ++i) {
				LayoutUtil.addColumn(layout);
				fullWidth += 2;
			}
		}
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Study", cc.xyw(1, 1, fullWidth));
		builder.addLabel("Identifier:", cc.xy(1, 3, "right, c"));
		builder.add(d_id, cc.xyw(3, 3, fullWidth-2));
		
		builder.addLabel("Intended indication:", cc.xy(1, 5, "right, c"));
		builder.add(d_indication, cc.xyw(3, 5, fullWidth-2));
		
		builder.addLabel("Endpoint:", cc.xy(1, 7, "right, c"));
		builder.add(d_endpoint, cc.xyw(3, 7, fullWidth-2));
		
		builder.addSeparator("Patient Groups", cc.xyw(1, 9, fullWidth));
		int row = 11;
		builder.addLabel("Size", cc.xy(1, row));
		builder.addLabel("Drug", cc.xy(3, row));
		builder.addLabel("Dose", cc.xy(5, row));
		if (getEndpoint() != null) {
			int col = 7;
			for (String header : MeasurementInputHelper.getHeaders(getEndpoint())) {
				builder.addLabel(header, cc.xy(col, row));
				col += 2;
			}
		}
		if (patientGroupsPresent()) {
			buildPatientGroups(layout, fullWidth, builder, cc, row + 2);
		} else {
			LayoutUtil.addRow(layout);
			builder.addLabel("No patient groups present", cc.xyw(1, row + 2, fullWidth));
		}
		
		return builder.getPanel();	
	}

	private Endpoint getEndpoint() {
		return d_endpointModel.getBean().getEndpoint();
	}

	private void buildPatientGroups(FormLayout layout, int fullWidth,
			PanelBuilder builder, CellConstraints cc, int row) {
		List<BasicPatientGroup> groups = d_model.getBean().getPatientGroups();
		for (BasicPatientGroup g : groups) {
			LayoutUtil.addRow(layout);
			PresentationModel<BasicPatientGroup> model = new PresentationModel<BasicPatientGroup>(g);
			JTextField field = MeasurementInputHelper.buildFormatted(model.getModel(BasicPatientGroup.PROPERTY_SIZE));
			d_validator.add(field);
			AutoSelectFocusListener.add(field);
			builder.add(field, cc.xy(1, row));
			
			JComboBox selector = GUIFactory.createDrugSelector(model, d_domain);
			d_validator.add(selector);
			ComboBoxPopupOnFocusListener.add(selector);
			builder.add(selector, cc.xy(3, row));
			
			DoseView view = new DoseView(new PresentationModel<Dose>(g.getDose()),
					d_validator);
			builder.add(view.buildPanel(), cc.xy(5, row));

			Measurement meas = d_model.getBean().getMeasurement(
					d_endpointModel.getBean().getEndpoint(),g);
			int col = 7;
			for (JTextField component : MeasurementInputHelper.getComponents((BasicMeasurement)meas)) {
				d_validator.add(component);
				builder.add(component, cc.xy(col, row));
				col += 2;
			}

			row += 2;
		}
	}

	private boolean patientGroupsPresent() {
		return !d_model.getBean().getPatientGroups().isEmpty();
	}
}
