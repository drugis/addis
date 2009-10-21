package org.drugis.addis.gui;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.presentation.PresentationModelManager;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;

public class IndicationView implements ViewBuilder {
	
	private PresentationModel<Indication> d_pm;
	private Domain d_domain;
	private PresentationModelManager d_pmm;

	public IndicationView(PresentationModel<Indication> pm, Domain domain, 
			PresentationModelManager pmm) {
		d_pm = pm;
		d_domain = domain;
		d_pmm = pmm;
	}
	
	public JComponent buildPanel() {
		return new JLabel("Indications");
	}
	

}
