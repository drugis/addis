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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Dose;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.MeasurementInputHelper;
import org.drugis.addis.gui.components.AutoSelectFocusListener;
import org.drugis.addis.gui.components.ComboBoxPopupOnFocusListener;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;

public class StudyAddPatientGroupView implements ViewBuilder {

	private Study d_study;
	private Domain d_domain;
	private Map<Endpoint, Measurement> d_measurements = new HashMap<Endpoint,Measurement>();
	private NotEmptyValidator d_validator;
	private BasicPatientGroup d_group;
	private Set<Endpoint> d_endpoints;

	public StudyAddPatientGroupView(Domain domain, Study study, JButton okButton) {
		d_domain = domain;
		d_study = study;
		d_group = new BasicPatientGroup(study, null, new Dose(0.0, SIUnit.MILLIGRAMS_A_DAY), 0);
		d_validator = new NotEmptyValidator(okButton);
		d_endpoints = d_study.getEndpoints();
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:pref:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);	
		int fullWidth = 3;
		int maxEpComponents = 0;
		for (Endpoint e : d_endpoints) {
			if (maxEpComponents < MeasurementInputHelper.numComponents(e)) {
				maxEpComponents = MeasurementInputHelper.numComponents(e);
			}
		}
		if (maxEpComponents > 1) {
			for (int i=1;i<maxEpComponents;i++) {
				layout.appendColumn(ColumnSpec.decode("3dlu"));
				layout.appendColumn(ColumnSpec.decode("fill:pref:grow"));
				fullWidth += 2;
			}
		}
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		int row = 1;
		
		PresentationModel<BasicPatientGroup> gmodel = new PresentationModel<BasicPatientGroup>(d_group);
		
		builder.addSeparator("Patient Group", cc.xyw(1, 1, fullWidth));
	
		row = 3;
		builder.addLabel("Size", cc.xy(1, row));
		JTextField field = MeasurementInputHelper.buildFormatted(gmodel.getModel(BasicPatientGroup.PROPERTY_SIZE));
		d_validator.add(field);
		AutoSelectFocusListener.add(field);
		builder.add(field, cc.xyw(3, row, fullWidth-2));
		
		row += 2;
		builder.addLabel("Drug", cc.xy(1, row));
		JComboBox selector = GUIFactory.createDrugSelector(gmodel, d_domain);
		d_validator.add(selector);
		ComboBoxPopupOnFocusListener.add(selector);
		builder.add(selector, cc.xyw(3, row, fullWidth-2));
		
		row += 2;
		builder.addLabel("Dose", cc.xy(1, row));
		DoseView view = new DoseView(new PresentationModel<Dose>(d_group.getDose()),
				d_validator);
		builder.add(view.buildPanel(), cc.xyw(3, row, fullWidth-2));

		row += 2;
		builder.addSeparator("Measurements", cc.xyw(1, row, fullWidth));
		
		row +=2;
		builder.addLabel("Endpoint", cc.xy(1, row));
		builder.addLabel("Measurement", cc.xyw(3, row, fullWidth-2));
		
		row +=2;		
		for (Endpoint e : d_study.getEndpoints()) {
			LayoutUtil.addRow(layout);
			builder.add(
					GUIFactory.createEndpointLabelWithIcon(d_study, e),
					cc.xy(1, row));
			int col = 3;
			BasicMeasurement m = e.buildMeasurement(d_group);
			d_measurements.put(e, m);
			JComponent[] comps = MeasurementInputHelper.getComponents(m);
			for (JComponent c : comps) {
				builder.add(c, cc.xy(col, row));
				col += 2;				
			}		
			row += 2;
		}
		
		return builder.getPanel();
	}
	
	public Map<Endpoint, Measurement> getMeasurements() {
		return d_measurements;
	}
	
	public BasicPatientGroup getPatientGroup() {
		return d_group;
	}
}
