/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.drugis.addis.entities.Activity;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.components.EnhancedTableHeader;
import org.drugis.addis.presentation.StudyActivitiesTableModel;
import org.drugis.addis.presentation.StudyPresentation;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyDesignView implements ViewBuilder {
	
	int d_maxNDrugsInCombination = 1;

	private class StudyActivityRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -3963454510182436593L;
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			TableColumnModel colModel = table.getColumnModel();
			setSize(colModel.getColumn(column).getWidth(), 0);
			String text = "";
			if (value instanceof StudyActivity) {
				StudyActivity sa = (StudyActivity) value;
				if (sa.getActivity() instanceof TreatmentActivity) {
					text += "<html>";
					TreatmentActivity ct = (TreatmentActivity) sa.getActivity();
					for (DrugTreatment ta : ct.getTreatments()) {
						text += formatTreatment(ta);
					}
					text += "</html>";
				} else if (sa.getActivity() instanceof DrugTreatment) {
					DrugTreatment ta = (DrugTreatment) sa.getActivity();
					text = "<html>" + formatTreatment(ta) + "</html>";
				} else if (sa.getActivity() instanceof TreatmentActivity) {
					TreatmentActivity ct = (TreatmentActivity) sa.getActivity();
					for(DrugTreatment ta : ct.getTreatments()) {
						text += formatTreatment(ta) + "<br/>";
					}
					text = "<html>" + text + "</html>";
				} else {
					text = "<html>" + sa.getActivity().toString() + "</html>";
				}
			} else {
				text = value == null ? "" : value.toString();
			}
			return super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
		}
		
	}
	
	private TableModel d_tableModel;

	public StudyDesignView(StudyPresentation spm) {

		d_tableModel = new StudyActivitiesTableModel(spm.getBean());
		
		for(StudyActivity sa : spm.getBean().getStudyActivities()) {
			Activity activity = sa.getActivity();
			if(activity instanceof TreatmentActivity) {
				d_maxNDrugsInCombination = Math.max(d_maxNDrugsInCombination, ((TreatmentActivity) activity).getTreatments().getSize());
			}
		}
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"fill:0:grow", 
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		// We can't use the EnhancedTable because it doesn't play nicely with the cell renderer.
		EnhancedTable armsEpochsTable = EnhancedTable.createBare(d_tableModel);
		
		// Set our own row height and cell renderer
		armsEpochsTable.setDefaultRenderer(StudyActivity.class, new StudyActivityRenderer());
		armsEpochsTable.setRowHeight(calculateHeight());
		
		// use our own column resizer
		EnhancedTableHeader tableHeader = armsEpochsTable.getTableHeader();
		tableHeader.setMaxColWidth(1000);
		armsEpochsTable.autoSizeColumns();
		
		// disable reordering and resizing of columns
		tableHeader.setReorderingAllowed(false);
		tableHeader.setResizingAllowed(false);

		// create a scrollpane that only scrolls horizontally
		JScrollPane tableScrollPane = new JScrollPane(armsEpochsTable);
		tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		// fit the viewport to the contents
		armsEpochsTable.setPreferredScrollableViewportSize(armsEpochsTable.getPreferredSize());
		
		builder.add(tableScrollPane, cc.xy(1,1));
		return builder.getPanel();
	}

	private int calculateHeight() {
		String labelText = "<html>";
		for (int i = 1; i < d_maxNDrugsInCombination; ++i) {
			labelText += "text<br>";
		}
		labelText += "text</html>";
		JLabel jLabel = new JLabel(labelText);
		return (int) jLabel.getPreferredSize().getHeight();
	}

	private String formatTreatment(DrugTreatment ta) {
		return ta.getDrug().getName() + " (" + ta.getDose().toString() + ")<br/>";
	}

}
