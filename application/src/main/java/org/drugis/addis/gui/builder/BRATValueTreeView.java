package org.drugis.addis.gui.builder;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.drugis.addis.gui.OutcomeMeasureGraph;
import org.drugis.addis.presentation.AbstractBenefitRiskPresentation;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class BRATValueTreeView extends JDialog {
	private static final long serialVersionUID = 1352621112589840579L;
	private final AbstractBenefitRiskPresentation<?, ?> d_pm;
	public BRATValueTreeView(JFrame mainWindow, AbstractBenefitRiskPresentation<?,?> pm) {
		super(mainWindow, "Value tree");
		d_pm = pm;
		setModal(false);
		setMinimumSize(new Dimension(mainWindow.getWidth()/4*3, mainWindow.getHeight()/4*3));
		setLocationRelativeTo(mainWindow);
		
		buildPanel();
	}
	
	private void buildPanel() {
		FormLayout layout = new FormLayout(
				"center:pref:grow",
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		OutcomeMeasureGraph panel = new OutcomeMeasureGraph(d_pm.getBean().getCriteria());
		builder.add(panel);
		panel.doLayout();
	}


}
