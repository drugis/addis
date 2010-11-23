package org.drugis.addis.gui;

import java.awt.Dialog;

import javax.swing.JDialog;

import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.Parameter;

@SuppressWarnings("serial")
public class ConvergencePlotsDialog extends JDialog {

	public ConvergencePlotsDialog(Dialog main, MixedTreatmentComparison mtc, Parameter p) {
		super(main, p + " convergence diagnostics", false);
		setSize(400, 150);
	}

}
