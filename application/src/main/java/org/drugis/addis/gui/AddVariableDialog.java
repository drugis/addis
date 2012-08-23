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

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.gui.builder.AddVariableView;
import org.drugis.addis.presentation.VariablePresentation;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class AddVariableDialog extends OkCancelDialog {
	private Domain d_domain;
	private AddisWindow d_mainWindow;
	private ValueModel d_selectionModel;
	private PresentationModel<Variable> d_pm;

	public AddVariableDialog(AddisWindow mainWindow, Domain domain, Variable variable, ValueModel selectionModel) {
		super(mainWindow, "Add " + VariablePresentation.getEntityName(variable) );
		d_mainWindow = mainWindow;
		setModal(true);
		d_domain = domain;
		d_pm = mainWindow.getPresentationModelFactory().getModel(variable);

		AddVariableView view = new AddVariableView(this, d_pm, d_okButton);

		getUserPanel().add(view.buildPanel());
		pack();
		getRootPane().setDefaultButton(d_okButton);
		d_selectionModel = selectionModel;
	}

	@Override
	protected void cancel() {
		dispose();
	}

	@Override
	protected void commit() {
		if (d_domain.getEndpoints().contains(d_pm.getBean()) ||
				d_domain.getAdverseEvents().contains(d_pm.getBean()) ||
				d_domain.getPopulationCharacteristics().contains(d_pm.getBean())) {
			JOptionPane.showMessageDialog(d_mainWindow,
			    "An item with the name " + d_pm.getBean().getName() + " already exists in the domain.",
			    "Couldn't add Variable", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (d_pm.getBean() instanceof Endpoint) {
			d_domain.getEndpoints().add(((Endpoint) d_pm.getBean()));
		} else if (d_pm.getBean() instanceof AdverseEvent) {
			d_domain.getAdverseEvents().add(((AdverseEvent) d_pm.getBean()));
		} else if (d_pm.getBean() instanceof PopulationCharacteristic) {
			d_domain.getPopulationCharacteristics().add(((PopulationCharacteristic) d_pm.getBean()));
		} else {
			throw new IllegalArgumentException("Unknown type of OutcomeMeasure.");
		}

		dispose();
		if (d_selectionModel != null)
			d_selectionModel.setValue(d_pm.getBean());
		d_mainWindow.leftTreeFocus(d_pm.getBean());
	}
}
