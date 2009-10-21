package org.drugis.addis.gui;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.presentation.PresentationModelManager;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

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
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator("Indication", cc.xyw(1, 1, 3));
		builder.addLabel("Concept ID:", cc.xy(1, 3));
		
//		builder.add(BasicComponentFactory.createLabel(
//				d_pm.getModel(Indication.PROPERTY_CODE)), cc.xy(3, 3));
		
		
		builder.addLabel("Fully Specified Name:", cc.xy(1, 5));
		builder.add(BasicComponentFactory.createLabel(
				d_pm.getModel(Indication.PROPERTY_NAME)), cc.xy(3, 5));
		
		builder.addSeparator("Studies", cc.xyw(1, 7, 3));
		
		return builder.getPanel();
	}
	

}
