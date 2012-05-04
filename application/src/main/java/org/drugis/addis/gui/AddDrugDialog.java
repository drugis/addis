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
import org.drugis.addis.entities.Drug;
import org.drugis.addis.gui.builder.AddDrugView;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class AddDrugDialog extends OkCancelDialog {
	private Domain d_domain;
	private Drug d_drug;
	private AddisWindow d_mainWindow;
	private ValueModel d_selectionModel;
	
	public AddDrugDialog(AddisWindow mainWindow, Domain domain, ValueModel selectionModel) {
		super(mainWindow, "Add Drug");
		d_mainWindow = mainWindow;
		this.setModal(true);
		d_domain = domain;
		d_drug = new Drug("", "");
		AddDrugView view = new AddDrugView(new PresentationModel<Drug>(d_drug), d_okButton);
		getUserPanel().add(view.buildPanel());
		pack();
		d_okButton.setEnabled(false);
		getRootPane().setDefaultButton(d_okButton);
		d_selectionModel = selectionModel;
	}
	
	@Override
	protected void cancel() {
		setVisible(false);
	}
	
	@Override
	protected void commit() {
		if (d_domain.getDrugs().contains(d_drug)) {
			JOptionPane.showMessageDialog(d_mainWindow,
			    "An item with the name " + d_drug.getName() + " already exists in the domain.",
			    "Couldn't add Drug", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		d_domain.getDrugs().add(d_drug);
		setVisible(false);
		d_mainWindow.leftTreeFocus(d_drug);
		if (d_selectionModel != null)
			d_selectionModel.setValue(d_drug);
	}
}
