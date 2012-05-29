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
