package org.drugis.addis.gui.builder;

import javax.swing.JPanel;

import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.StudiesTablePanel;
import org.drugis.addis.presentation.StudyListPresentationModel;
import org.drugis.common.gui.ViewBuilder;

public class StudyTablePanelView implements ViewBuilder {
	
	private StudyListPresentationModel d_metamodel;
	private Main d_parent;

	public StudyTablePanelView(StudyListPresentationModel metamodel, Main parent) {
		d_metamodel = metamodel;
		d_parent = parent;
	}

	public JPanel buildPanel() {
		StudiesTablePanel panel = new StudiesTablePanel(d_metamodel, d_parent);
		
		return panel;
	}

}
