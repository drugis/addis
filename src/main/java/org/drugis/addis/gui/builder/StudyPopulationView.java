package org.drugis.addis.gui.builder;


import javax.swing.JComponent;

import org.drugis.addis.gui.components.MeasurementTable;
import org.drugis.addis.presentation.StudyPresentationModel;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.ViewBuilder;

public class StudyPopulationView implements ViewBuilder {
	private StudyPresentationModel d_pm;

	public StudyPopulationView(StudyPresentationModel model) {
		d_pm = model;
	}

	public JComponent buildPanel() {
		MeasurementTable measurementTable = new MeasurementTable(d_pm.getPopulationCharTableModel());
		return AuxComponentFactory.createUnscrollableTablePanel(measurementTable);
	}
}
