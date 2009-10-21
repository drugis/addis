/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
public class AddDrugDialog extends OkCancelDialog {
	private Domain d_domain;
	private Drug d_drug;
	private Main d_main;
	
	public AddDrugDialog(Main frame, Domain domain) {
		super(frame, "Add Drug");
		this.d_main = frame;
		this.setModal(true);
		d_domain = domain;
		d_drug = new Drug("");
		DrugView view = new DrugView(new PresentationModel<Drug>(d_drug), d_okButton);
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
		d_domain.addDrug(d_drug);
		setVisible(false);
		d_main.leftTreeFocusOnDrug(d_drug);
	}
}
