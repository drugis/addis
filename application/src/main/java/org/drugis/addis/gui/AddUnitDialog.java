/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen,
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi,
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal,
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid,
 * Joël Kuiper, Wouter Reckman.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
		dispose();
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
		dispose();
		d_mainWindow.leftTreeFocus(d_unit);
		if (d_selectionModel != null)
			d_selectionModel.setValue(d_unit);
	}

}
