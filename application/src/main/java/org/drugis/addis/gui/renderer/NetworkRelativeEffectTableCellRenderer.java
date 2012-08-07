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

package org.drugis.addis.gui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.mtc.presentation.results.NetworkRelativeEffectTableModel;

public class NetworkRelativeEffectTableCellRenderer extends SummaryCellRenderer implements TableCellRenderer {
	public NetworkRelativeEffectTableCellRenderer(boolean applyExpTransform) {
		super(applyExpTransform);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table,
			Object cellContents, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component component =  super.getTableCellRendererComponent(
				table, cellContents, isSelected, hasFocus, row, column);

		if (cellContents instanceof TreatmentDefinition) {
			String text = ((TreatmentDefinition) cellContents).getLabel();
			component = (new DefaultTableCellRenderer()).getTableCellRendererComponent(
					table, text, isSelected, hasFocus, row, column);
			component.setBackground(Color.LIGHT_GRAY);
		}
		
		@SuppressWarnings("unchecked")
		NetworkRelativeEffectTableModel<TreatmentDefinition> networkTableModel = 
			(NetworkRelativeEffectTableModel<TreatmentDefinition>)table.getModel();
		((JComponent) component).setToolTipText(networkTableModel.getDescriptionAt(row, column));
		return component;
	}
}