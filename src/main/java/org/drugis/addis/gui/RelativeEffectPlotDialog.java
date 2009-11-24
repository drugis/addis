package org.drugis.addis.gui;

import javax.swing.JDialog;

import org.drugis.addis.gui.components.RelativeEffectCanvas;
import org.drugis.addis.presentation.ForestPlotPresentation;

@SuppressWarnings("serial")
public class RelativeEffectPlotDialog extends JDialog {
	public RelativeEffectPlotDialog(JDialog parent, ForestPlotPresentation pres, String title) { 
		super(parent, title);
		add(new RelativeEffectCanvas(pres));
		pack();
	}	
}
