package org.drugis.addis.gui;

import javax.swing.JOptionPane;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Unit;
import org.drugis.addis.gui.builder.AddUnitView;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueModel;

public class AddUnitDialog extends OkCancelDialog {
	private static final long serialVersionUID = 3899454275346087991L;
	private final Domain d_domain;
	private Unit d_unit;
	private final AddisWindow d_mainWindow;
	private final ValueModel d_selectionModel;

	public AddUnitDialog(AddisWindow mainWindow, Domain domain, ValueModel selectionModel) {
		super(mainWindow, "Add Unit");
		d_mainWindow = mainWindow;
		d_domain = domain;
		d_selectionModel = selectionModel;
		this.setModal(true);
		d_unit = new Unit("", "");
		AddUnitView view = new AddUnitView(new PresentationModel<Unit>(d_unit), d_okButton);
		getUserPanel().add(view.buildPanel());
		pack();
		d_okButton.setEnabled(false);
		getRootPane().setDefaultButton(d_okButton);
	}

	@Override
	protected void cancel() {
		setVisible(false);
	}

	@Override
	protected void commit() {
		if (d_domain.getUnits().contains(d_unit)) {
			JOptionPane.showMessageDialog(d_mainWindow,
			    "An item with the name " + d_unit.getName() + " already exists in the domain.",
			    "Couldn't add Unit", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		d_domain.getUnits().add(d_unit);
		setVisible(false);
		d_mainWindow.leftTreeFocus(d_unit);
		if (d_selectionModel != null)
			d_selectionModel.setValue(d_unit);
	}

}
