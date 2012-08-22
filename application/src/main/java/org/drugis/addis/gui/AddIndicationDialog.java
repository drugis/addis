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
import org.drugis.addis.entities.Indication;
import org.drugis.addis.gui.builder.AddIndicationView;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class AddIndicationDialog extends OkCancelDialog {
	private Domain d_domain;
	private Indication d_indication;
	private AddisWindow d_mainWindow;
	private ValueModel d_selectionModel;
	
	public AddIndicationDialog(AddisWindow mainWindow, Domain domain, ValueModel selection) {
		super(mainWindow, "Add Indication");
		setModal(true);
		d_mainWindow = mainWindow;
		d_domain = domain;
		d_indication = new Indication(0L, "");
		AddIndicationView view = new AddIndicationView(new PresentationModel<Indication>(d_indication), d_okButton);
		getUserPanel().add(view.buildPanel());
		pack();
		d_okButton.setEnabled(false);
		getRootPane().setDefaultButton(d_okButton);
		d_selectionModel = selection;
	}

	@Override
	protected void cancel() {
		setVisible(false);
		dispose();
	}

	@Override
	protected void commit() {
		if (d_domain.getIndications().contains(d_indication)) {
			JOptionPane.showMessageDialog(d_mainWindow,
			    "An item with the code " + d_indication.getCode() + " already exists in the domain.",
			    "Couldn't add Indication", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		d_domain.getIndications().add(d_indication);
		setVisible(false);
		dispose();

		d_mainWindow.leftTreeFocus(d_indication);
		if (d_selectionModel != null)
			d_selectionModel.setValue(d_indication);
	}
}
