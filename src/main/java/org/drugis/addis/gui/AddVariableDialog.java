/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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
import org.drugis.addis.presentation.VariablePresentationModel;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class AddVariableDialog extends OkCancelDialog {
	private Domain d_domain;
	private Main d_main;
	private ValueModel d_selectionModel;
	private PresentationModel<Variable> d_pm;
	
	public AddVariableDialog(Main frame, Domain domain, Variable variable, ValueModel selectionModel) {
		super(frame, "Add " + VariablePresentationModel.getEntityName(variable) );
		this.d_main = frame;
		this.setModal(true);
		d_domain = domain;
		d_pm = frame.getPresentationModelFactory().getCreationModel(variable);
		
		AddVariableView view = new AddVariableView(d_pm, d_okButton);
		
		getUserPanel().add(view.buildPanel());
		pack();
		getRootPane().setDefaultButton(d_okButton);
		d_selectionModel = selectionModel;
	}

	@Override
	protected void cancel() {
		setVisible(false);
	}

	@Override
	protected void commit() {
		
		if ( 	d_domain.getEndpoints().contains(d_pm.getBean())     ||
				d_domain.getAdverseEvents().contains(d_pm.getBean()) ||
				d_domain.getPopulationCharacteristics().contains(d_pm.getBean())       ) {
			
			JOptionPane.showMessageDialog(d_main,
			    "An item with the name " + d_pm.getBean().getName() + " already exists in the domain.",
			    "Couldn't add Variable", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (d_pm.getBean() instanceof Endpoint)
				d_domain.addEndpoint((Endpoint) d_pm.getBean());
		else if (d_pm.getBean() instanceof AdverseEvent)
			d_domain.addAdverseEvent((AdverseEvent) d_pm.getBean());
		else if (d_pm.getBean() instanceof PopulationCharacteristic) {
			d_domain.addPopulationCharacteristic((PopulationCharacteristic) d_pm.getBean());
		}
		else 
			throw new IllegalArgumentException("Unknown type of OutcomeMeasure.");
		
		setVisible(false);
		if (d_selectionModel != null)
			d_selectionModel.setValue(d_pm.getBean());
		d_main.leftTreeFocus(d_pm.getBean());
	}
}
