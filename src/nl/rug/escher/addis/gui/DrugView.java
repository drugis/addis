package nl.rug.escher.addis.gui;

import javax.swing.JComponent;
import javax.swing.JTextField;

import nl.rug.escher.addis.entities.Drug;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DrugView implements ViewBuilder {
	JTextField d_id;
	PresentationModel<Drug> d_model;

	public DrugView(PresentationModel<Drug> presentationModel) {
		d_model = presentationModel;
	}
	
	public void initComponents() {
		d_id = BasicComponentFactory.createTextField(d_model.getModel(Drug.PROPERTY_NAME));
		d_id.setColumns(15);
	}

	public JComponent buildPanel() {
		initComponents();
		
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref",
				"p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Drug", cc.xyw(1, 1, 3));
		builder.addLabel("Name:", cc.xy(1, 3));
		builder.add(d_id, cc.xy(3,3));
		
		return builder.getPanel();	
	}
}