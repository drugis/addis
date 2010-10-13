package org.drugis.addis.gui.builder;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.drugis.addis.gui.LyndOBrienChartFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.AbstractBenefitRiskPresentation;
import org.drugis.addis.presentation.LyndOBrienPresentation;
import org.drugis.common.gui.ViewBuilder;
import org.jfree.chart.ChartPanel;

public class LyndOBrienView implements ViewBuilder {
	LyndOBrienPresentation<?,?> d_pm;
	
	public LyndOBrienView(AbstractBenefitRiskPresentation<?,?> pm, Main main) {
		d_pm = pm.getLyndOBrienPresentation();
		
		d_pm.startLyndOBrien();
	}

	public JComponent buildPanel() {
		JPanel panel = new JPanel();
		
		panel.add(new ChartPanel(LyndOBrienChartFactory.buildScatterPlot(d_pm.getModel())));
		
		return panel;
	}

}
