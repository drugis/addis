package nl.rug.escher.gui;

import javax.swing.JComponent;
import javax.swing.JTextField;

import nl.rug.escher.entities.Study;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyView implements ViewBuilder {
	JTextField d_id;
	PresentationModel<Study> d_model;

	public StudyView(PresentationModel<Study> presentationModel) {
		d_model = presentationModel;
	}
	
	public void initComponents() {
		d_id = BasicComponentFactory.createTextField(d_model.getModel(Study.PROPERTY_ID));
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
		
		builder.addSeparator("Study", cc.xyw(1, 1, 3));
		builder.addLabel("Identifier:", cc.xy(1, 3));
		builder.add(d_id, cc.xy(3,3));
		
		return builder.getPanel();	
	}

}
