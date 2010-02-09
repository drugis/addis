package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drugis.addis.gui.components.MeasurementTable;
import org.drugis.addis.presentation.StudyPresentationModel;
import org.drugis.common.gui.ViewBuilder;

public class StudyPopulationView implements ViewBuilder {
	private StudyPresentationModel d_pm;

	public StudyPopulationView(StudyPresentationModel model) {
		d_pm = model;
	}

	public JComponent buildPanel() {
		MeasurementTable measurementTable = new MeasurementTable(d_pm.getPopulationCharTableModel());
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(measurementTable, BorderLayout.CENTER);
		panel.add(measurementTable.getTableHeader(), BorderLayout.PAGE_START);
		
		measurementTable.setBackground(Color.WHITE);
		measurementTable.setBorder(new JScrollPane().getBorder());
		return panel;
	}
}
