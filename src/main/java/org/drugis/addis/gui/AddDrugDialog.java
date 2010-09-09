/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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
	private Main d_main;
	private ValueModel d_selectionModel;
	
	public AddDrugDialog(Main frame, Domain domain, ValueModel selectionModel) {
		super(frame, "Add Drug");
		this.d_main = frame;
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
			JOptionPane.showMessageDialog(d_main,
			    "An item with the name " + d_drug.getName() + " already exists in the domain.",
			    "Couldn't add Drug", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		d_domain.addDrug(d_drug);
		setVisible(false);
		d_main.leftTreeFocus(d_drug);
		if (d_selectionModel != null)
			d_selectionModel.setValue(d_drug);
	}
}
