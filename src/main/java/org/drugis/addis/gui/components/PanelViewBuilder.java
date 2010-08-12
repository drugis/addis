package org.drugis.addis.gui.components;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.drugis.common.gui.ViewBuilder;

public class PanelViewBuilder implements ViewBuilder {

	private final JPanel d_tablePanel;

	public PanelViewBuilder(JPanel tablePanel) {
		d_tablePanel = tablePanel;
	}
	
	public JComponent buildPanel() {
		return d_tablePanel;
	}
}
