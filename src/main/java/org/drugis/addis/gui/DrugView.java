package org.drugis.addis.gui;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.drugis.addis.entities.Drug;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DrugView implements ViewBuilder{
	private PresentationModel<Drug> d_model;

	public DrugView(Drug drug) {
		d_model = new PresentationModel<Drug> (drug);
	}
	
	public JComponent buildPanel() {

		
		
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Drug", cc.xyw(1, 1, 3));
		builder.addLabel("Name:", cc.xy(1, 3));
		JLabel nameComp =
			BasicComponentFactory.createLabel(d_model.getModel(Drug.PROPERTY_NAME));
		builder.add(nameComp, cc.xy(3,3));
		builder.addLabel("ATC Code:", cc.xy(1, 5));
		JLabel atcCodeComp =
			BasicComponentFactory.createLabel(d_model.getModel(Drug.PROPERTY_ATCCODE));
		builder.add(atcCodeComp, cc.xy(3, 5));
		
		return builder.getPanel();	
	}
}