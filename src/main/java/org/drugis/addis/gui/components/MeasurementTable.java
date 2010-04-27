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

package org.drugis.addis.gui.components;

import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.gui.MeasurementInputHelper;

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

		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
		TableColumn column = null;
		for (int i = 0; i < getModel().getColumnCount(); i++) {
		    column = getColumnModel().getColumn(i);
		        column.setPreferredWidth(50);
		}
	}

	@Override
	protected JPanel createEditorPanel(int row, int col) {
		if (col < 1) {
			return null;
		}
		
		return createPanel((BasicMeasurement)getModel().getValueAt(row, col));
	}

	private JPanel createPanel(BasicMeasurement m) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		String[] h = MeasurementInputHelper.getHeaders(m);
		JComponent[] c = MeasurementInputHelper.getComponents(m);
		for (int i = 0; i < h.length; ++i) {
			panel.add(new JLabel(h[i]));
			panel.add(c[i]);
		}

		return panel;
	}
}