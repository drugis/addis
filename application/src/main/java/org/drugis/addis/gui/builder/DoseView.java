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

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatter;

import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.ScaleModifier;
import org.drugis.addis.entities.Unit;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.components.ComboBoxPopupOnFocusListener;
import org.drugis.addis.presentation.DosePresentation;
import org.drugis.addis.presentation.DoseUnitPresentation;
import org.drugis.addis.presentation.DurationPresentation;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DoseView implements ViewBuilder {
	private DosePresentation d_model;
	private JFormattedTextField d_quantityMin;
	private JFormattedTextField d_quantityMax;
	private final List<Unit> d_unitOptions;
	
	public DoseView(DosePresentation dosePresentation, List<Unit> unitOptions) {
		d_model = dosePresentation;
		d_unitOptions = unitOptions;
	}
	
	public void initComponents() {
		d_quantityMin = new JFormattedTextField(new DefaultFormatter());
		d_quantityMax = new JFormattedTextField(new DefaultFormatter());
		PropertyConnector.connectAndUpdate(d_model.getMinModel(), d_quantityMin, "value");
		PropertyConnector.connectAndUpdate(d_model.getMaxModel(), d_quantityMax, "value");
		d_quantityMin.setColumns(8);
		d_quantityMax.setColumns(8);
		
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout("p, 3dlu, p, 3dlu, p, fill:pref:grow",
				"p");
		initComponents();
		PanelBuilder builder = new PanelBuilder(layout);
		
		CellConstraints cc = new CellConstraints();
		builder.add(d_quantityMin, cc.xy(1, 1));
		builder.add(new JLabel(" up to "), cc.xy(3, 1));
		builder.add(d_quantityMax, cc.xy(5, 1));
		
		int row = LayoutUtil.addRow(layout, 1);
		
		DosePresentation model = d_model;

		JPanel doseUnitRow = createDoseUnitRow(model.getDoseUnitPresentation(), d_unitOptions);
		builder.add(doseUnitRow, cc.xyw(1, row, 6));
		JPanel panel = builder.getPanel();
		return panel;
	}

	public static JPanel createDoseUnitRow(DoseUnitPresentation model, List<Unit> unitOptions) {
		FormLayout layout = new FormLayout("p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p",
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		SelectionInList<ScaleModifier> scaleModifierSelectionInList = new SelectionInList<ScaleModifier>(
				ScaleModifier.values(),
				model.getModel(DoseUnit.PROPERTY_SCALE_MODIFIER));
		
		SelectionInList<Unit> unitSelectionInList = new SelectionInList<Unit>(
				unitOptions,
				model.getModel(DoseUnit.PROPERTY_UNIT));
		
		ComboBoxPopupOnFocusListener.add(BasicComponentFactory.createComboBox(unitSelectionInList));
		ComboBoxPopupOnFocusListener.add(BasicComponentFactory.createComboBox(scaleModifierSelectionInList));
		
		int row = 1;
		builder.add(BasicComponentFactory.createComboBox(scaleModifierSelectionInList), cc.xy(1, row));
		builder.add(BasicComponentFactory.createComboBox(unitSelectionInList), cc.xy(3, row));
		builder.add(new JLabel(" per "), cc.xy(5, row));
		DurationPresentation<DoseUnit> durationModel = model.getDurationPresentation();
		
		// duration quantity input
		final JTextField quantityField = BasicComponentFactory.createFormattedTextField(
				new PropertyAdapter<DurationPresentation<DoseUnit>>(durationModel, DurationPresentation.PROPERTY_DURATION_QUANTITY, true),
				new DefaultFormatter());
		quantityField.setColumns(4);
		builder.add(quantityField, cc.xy(7, row));

		// duration units input
		final JComboBox unitsField = AuxComponentFactory.createBoundComboBox(
				DurationPresentation.DateUnits.values(), 
				new PropertyAdapter<DurationPresentation<DoseUnit>>(durationModel, DurationPresentation.PROPERTY_DURATION_UNITS, true));
		builder.add(unitsField, cc.xy(9, row));
		return builder.getPanel();
	}
}
