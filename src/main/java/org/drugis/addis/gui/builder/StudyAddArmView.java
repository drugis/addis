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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.drugis.addis.entities.AbstractOutcomeMeasure;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.OutcomeMeasure.Type;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.MeasurementInputHelper;
import org.drugis.addis.gui.components.AutoSelectFocusListener;
import org.drugis.addis.gui.components.ComboBoxPopupOnFocusListener;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.presentation.StudyAddArmPresentation;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyAddArmView implements ViewBuilder {
	private Domain d_domain;
	private NotEmptyValidator d_validator;
	private StudyAddArmPresentation d_pm;
	

	public StudyAddArmView(StudyAddArmPresentation pm, Domain domain, JButton okButton) {
		d_domain = domain;
		d_validator = new NotEmptyValidator(okButton);
		d_pm = pm;
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:pref:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);	
		int fullWidth = 3;
		int maxEpComponents = 0;
		for (Endpoint.Type type : AbstractOutcomeMeasure.Type.values()) {
			if (d_pm.hasEndpoints(type)) {
				maxEpComponents = Math.max(maxEpComponents, numComponents(type));
			}
		}
		for (int i=1;i<maxEpComponents;i++) {
			LayoutUtil.addColumn(layout, "fill:pref:grow");
			fullWidth += 2;
		}
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		int row = 1;
		
		builder.addSeparator("Patient Group", cc.xyw(1, 1, fullWidth));
	
		row = 3;
		builder.addLabel("Size", cc.xy(1, row));
		JTextField field = MeasurementInputHelper.buildFormatted(d_pm.getSizeModel());
		d_validator.add(field);
		AutoSelectFocusListener.add(field);
		builder.add(field, cc.xyw(3, row, fullWidth-2));
		
		row += 2;
		builder.addLabel("Drug", cc.xy(1, row));
		JComboBox selector = GUIFactory.createDrugSelector(d_pm.getDrugModel(), d_domain);
		d_validator.add(selector);
		ComboBoxPopupOnFocusListener.add(selector);
		builder.add(selector, cc.xyw(3, row, fullWidth-2));
		
		row += 2;
		builder.addLabel("Dose", cc.xy(1, row));
		DoseView view = new DoseView(d_pm.getDoseModel(), d_validator);
		builder.add(view.buildPanel(), cc.xyw(3, row, fullWidth-2));
		
		for (Type type : AbstractOutcomeMeasure.Type.values()) {
			if (d_pm.hasEndpoints(type)) {
				row = buildMeasurementsPart(type, row, layout, fullWidth, builder);
			}
		}
		
		return builder.getPanel();
	}

	private int buildMeasurementsPart(Type type, int row, FormLayout layout,
			int fullWidth, PanelBuilder builder) {
		CellConstraints cc = new CellConstraints();
		row += 2;
		LayoutUtil.addRow(layout);
		builder.addSeparator(type + " Measurements", cc.xyw(1, row, fullWidth));
		
		row +=2;
		LayoutUtil.addRow(layout);
		builder.addLabel("Endpoint", cc.xy(1, row));
		int column = 3;
		for (String header: MeasurementInputHelper.getHeaders(d_pm.getOutcomeMeasures(type).get(0))) {
			builder.addLabel(header, cc.xy(column, row));
			column += 2;
		}
		
		row +=2;
		for (OutcomeMeasure e : d_pm.getOutcomeMeasures(type)) {
			LayoutUtil.addRow(layout);
			builder.add(
					GUIFactory.createOutcomeMeasureLabelWithIcon(e),
					cc.xy(1, row));
			int col = 3;
			JComponent[] comps = MeasurementInputHelper.getComponents((BasicMeasurement)d_pm.getMeasurementModel(e).getBean());
			for (JComponent c : comps) {
				builder.add(c, cc.xy(col, row));
				col += 2;				
			}		
			row += 2;
		}
		return row;
	}

	private int numComponents(Endpoint.Type type) {
		return MeasurementInputHelper.numComponents(d_pm.getOutcomeMeasures(type).get(0));
	}
}
