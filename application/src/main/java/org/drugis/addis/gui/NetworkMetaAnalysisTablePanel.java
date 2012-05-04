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

package org.drugis.addis.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.presentation.LabeledPresentation;
import org.drugis.addis.presentation.NetworkTableModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;

@SuppressWarnings("serial")
public class NetworkMetaAnalysisTablePanel extends TablePanel {
	
	public NetworkMetaAnalysisTablePanel(JFrame parent, NetworkTableModel networkAnalysisTableModel) {
		super(new JTable(networkAnalysisTableModel));
		d_table.setDefaultRenderer(Object.class, new NetworkTableCellRenderer());
		d_table.setTableHeader(null);
		setColumnWidths();
	}
	
	private void setColumnWidths() {
		d_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (TableColumn c : Collections.list(d_table.getColumnModel().getColumns())) {
			c.setMinWidth(170);
			c.setPreferredWidth(170);
		}
	}

	static class NetworkTableCellRenderer implements TableCellRenderer {
		@SuppressWarnings("unchecked")
		public Component getTableCellRendererComponent(JTable table, Object val, boolean isSelected, boolean hasFocus, int row, int col) {
			JLabel label =  (val instanceof LabeledPresentation) ? 			// FIXME: Is this general enough?
					BasicComponentFactory.createLabel(((LabeledPresentation)val).getLabelModel()) :
				new JLabel(((PresentationModel<DrugSet>) val).getBean().getLabel()); 

			label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	
			if (((PresentationModel<?>)val).getBean() instanceof Drug) {
				label.setBackground(Color.lightGray);
			} else {
				if (!isSelected)
					label.setBackground(Color.WHITE);
			}
			label.setOpaque(true);
			
			TableModel model = table.getModel();
			if (model instanceof NetworkTableModel) {
				NetworkTableModel networkTableModel = (NetworkTableModel) model;
				if (networkTableModel.getDescriptionAt(row, col) != null) {
					label.setToolTipText(networkTableModel.getDescriptionAt(row, col));
				}
			}
			
			return label;
		}
	}
}
