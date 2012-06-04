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

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.TreatmentActivity;

public class StudyActivityRenderer extends DefaultTableCellRenderer {
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

	private String formatTreatment(DrugTreatment ta) {
		return ta.getDrug().getName() + " (" + ta.getDose().toString() + ")<br/>";
	}

}
