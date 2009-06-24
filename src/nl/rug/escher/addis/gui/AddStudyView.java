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

package nl.rug.escher.addis.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.common.gui.LayoutUtil;
import nl.rug.escher.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddStudyView implements ViewBuilder {
	private JTextField d_id;
	private JComboBox d_endpoint;
	private PresentationModel<BasicStudy> d_model;
	private PresentationModel<EndpointHolder> d_endpointModel;
	private Domain d_domain;

	public AddStudyView(PresentationModel<BasicStudy> presentationModel,
			PresentationModel<EndpointHolder> presentationModel2, Domain domain) {
		d_model = presentationModel;
		d_endpointModel = presentationModel2;
		d_domain = domain;
	}
	
	public void initComponents() {
		d_id = BasicComponentFactory.createTextField(d_model.getModel(BasicStudy.PROPERTY_ID));
		d_id.setColumns(15);
		AutoSelectFocusListener.add(d_id);
		
		SelectionInList<Endpoint> endpointSelectionInList =
			new SelectionInList<Endpoint>(
					new ArrayList<Endpoint>(d_domain.getEndpoints()), 
					d_endpointModel.getModel(EndpointHolder.PROPERTY_ENDPOINT));
		d_endpoint = BasicComponentFactory.createComboBox(endpointSelectionInList);
		ComboBoxPopupOnFocusListener.add(d_endpoint);
	}
	
	public JComponent buildPanel() {
		initComponents();
		
		FormLayout layout = new FormLayout(
				"pref, 3dlu, pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
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
		builder.add(d_id, cc.xyw(3, 3, 3));
		builder.addLabel("Endpoint:", cc.xy(1, 5, "right, c"));
		builder.add(d_endpoint, cc.xyw(3, 5, 3));
		
		builder.addSeparator("Patient Groups", cc.xyw(1, 7, fullWidth));
		int row = 9;
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
			//PresentationModel<Measurement> mModel =
			//	new PresentationModel<Measurement>(g.getMeasurements().get(0));
			JTextField field = MeasurementInputHelper.buildFormatted(model.getModel(BasicPatientGroup.PROPERTY_SIZE));
			AutoSelectFocusListener.add(field);
			builder.add(field, cc.xy(1, row));
			
			JComboBox selector = createDrugSelector(model);
			ComboBoxPopupOnFocusListener.add(selector);
			builder.add(selector, cc.xy(3, row));
			
			DoseView view = new DoseView(new PresentationModel<Dose>(g.getDose()));
			builder.add(view.buildPanel(), cc.xy(5, row));
			
			
			if (g.getMeasurements().size() > 0) {
				int col = 7;
				for (JComponent component : MeasurementInputHelper.getComponents(g.getMeasurements().get(0))) {
					builder.add(component, cc.xy(col, row));
					col += 2;
				}
			}
			
			row += 2;
		}
	}

	private JComboBox createDrugSelector(PresentationModel<BasicPatientGroup> model) {
		SelectionInList<Drug> drugSelectionInList =
			new SelectionInList<Drug>(
					new ArrayList<Drug>(d_domain.getDrugs()),
					model.getModel(BasicPatientGroup.PROPERTY_DRUG));
		return BasicComponentFactory.createComboBox(drugSelectionInList);
	}

	private boolean patientGroupsPresent() {
		return !d_model.getBean().getPatientGroups().isEmpty();
	}
}
