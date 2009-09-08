package org.drugis.addis.gui;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatter;

import org.drugis.addis.entities.Indication;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class IndicationView implements ViewBuilder {
	private JFormattedTextField d_code;
	private JTextField d_name;
	private PresentationModel<Indication> d_model;
	private NotEmptyValidator d_validator;
	
	public IndicationView(PresentationModel<Indication> model, JButton okButton) {
		d_validator = new NotEmptyValidator(okButton);
		d_model = model;
	}
	
	public void initComponents() {
		d_code = new JFormattedTextField(new DefaultFormatter());
		d_code.setColumns(18);
		PropertyConnector.connectAndUpdate(d_model.getModel(Indication.PROPERTY_CODE), d_code, "value");
		d_name = BasicComponentFactory.createTextField(d_model.getModel(Indication.PROPERTY_NAME), false);
		
		d_validator.add(d_code);
		d_validator.add(d_name);
	}

	public JComponent buildPanel() {
		initComponents();
		
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Indication", cc.xyw(1, 1, 3));
		builder.addLabel("Concept ID:", cc.xy(1, 3));
		builder.add(d_code, cc.xy(3,3));
		builder.addLabel("Fully Specified Name:", cc.xy(1, 5));
		builder.add(d_name, cc.xy(3,5));
		
		return builder.getPanel();	
	}

}
