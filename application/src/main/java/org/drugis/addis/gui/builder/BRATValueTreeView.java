package org.drugis.addis.gui.builder;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.presentation.AbstractBenefitRiskPresentation;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class BRATValueTreeView extends JDialog {
	private static final long serialVersionUID = 1352621112589840579L;
	private final SelectableOutcomeGraphModel d_pm;
	@SuppressWarnings("unchecked")
	public BRATValueTreeView(JFrame mainWindow, AbstractBenefitRiskPresentation<?,?> pm) {
		super(mainWindow, "Summary of Efficacy Table");
		d_pm = new SelectableOutcomeGraphModel((ObservableList<OutcomeMeasure>) pm.getBean().getAlternatives());
		setModal(false);
		setMinimumSize(new Dimension(mainWindow.getWidth()/4*3, mainWindow.getHeight()/4*3));
		setLocationRelativeTo(mainWindow);
		
		buildPanel();
	}
	
	private void buildPanel() {
		FormLayout layout = new FormLayout("center:pref:grow",
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		
		builder.add(new SelectableOutcomeMeasureGraph(d_pm));
		
		
	}


}
