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

package org.drugis.addis.gui.components;

import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.drugis.addis.gui.MeasurementInputHelper;
import org.drugis.addis.gui.renderer.MeasurementCellRenderer;
import org.drugis.addis.presentation.wizard.MissingMeasurementPresentation;

import com.jgoodies.binding.adapter.BasicComponentFactory;

@SuppressWarnings("serial")
public class MeasurementTable extends JTableWithPopupEditor {

	/**
	 * Uneditable measurement table.
	 * @param tableModel
	 */
	public MeasurementTable(TableModel tableModel) {
		this(tableModel, null);
	}

	/**
	 * Editable measurement table.
	 * @param tableModel
	 * @param parent
	 */
	public MeasurementTable(TableModel tableModel, Window parent) {
		super(tableModel, parent);

		setDefaultRenderer(MissingMeasurementPresentation.class, new MeasurementCellRenderer());

		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
		TableColumn column = null;
		for (int i = 0; i < getModel().getColumnCount(); i++) {
		    column = getColumnModel().getColumn(i);
		        column.setPreferredWidth(50);
		}
	}

	@Override
	protected JPanel createEditorPanel(int row, int col) {
		if (col < 2) {
			return null;
		}
		return createPanel((MissingMeasurementPresentation)getValueAt(row, col));
	}

	private JPanel createPanel(MissingMeasurementPresentation mmp) {
		JPanel panel = new JPanel(new FlowLayout());
		String[] h = MeasurementInputHelper.getHeaders(mmp.getMeasurement());
		JCheckBox checkBox = BasicComponentFactory.createCheckBox(mmp.getMissingModel(), "Missing");
		JComponent[] c = MeasurementInputHelper.getComponents(mmp);
		panel.add(checkBox);
		for (int i = 0; i < h.length; ++i) {
			panel.add(new JLabel(h[i]));
			panel.add(c[i]);
		}

		return panel;
	}
}