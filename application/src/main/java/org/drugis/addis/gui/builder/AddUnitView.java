package org.drugis.addis.gui.builder;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.drugis.addis.entities.Unit;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddUnitView implements ViewBuilder{

	private final PresentationModel<Unit> d_model;
	private JTextField d_name;
	private NotEmptyValidator d_validator;
	private JTextField d_symbol;
	private JPanel d_panel;

	public AddUnitView(PresentationModel<Unit> presentationModel, JButton okButton) {
		d_validator = new NotEmptyValidator();
		Bindings.bind(okButton, "enabled", d_validator);
		d_model = presentationModel;
	}
	
	public void initComponents() {
		d_name = BasicComponentFactory.createTextField(d_model.getModel(Unit.PROPERTY_NAME), false);
		d_name.setColumns(15);
		d_symbol = BasicComponentFactory.createTextField(d_model.getModel(Unit.PROPERTY_SYMBOL), false);
		d_validator.add(d_name);
		d_validator.add(d_symbol);
	}

	@Override
	public JComponent buildPanel() {
		initComponents();
		
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Drug", cc.xyw(1, 1, 5));
		builder.addLabel("Name:", cc.xy(1, 3));
		builder.add(d_name, cc.xy(3, 3));
		builder.addLabel("Symbol:", cc.xy(1, 5));
		builder.add(d_symbol, cc.xyw(3, 5, 3));
		
		d_panel = builder.getPanel();
		return d_panel;	
	}

}
