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


import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.gui.builder.AddOutcomeMeasureView;
import org.drugis.addis.presentation.VariablePresentationModel;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class AddOutcomeDialog extends OkCancelDialog {
	private Domain d_domain;
	private Variable d_om;
	private Main d_main;
	private ValueModel d_selectionModel;
	
	public AddOutcomeDialog(Main frame, Domain domain, Variable om, ValueModel selectionModel) {
		super(frame, "Add " + VariablePresentationModel.getCategoryName(om) );
		this.d_main = frame;
		this.setModal(true);
		d_domain = domain;
		d_om = om;
		
		AddOutcomeMeasureView view = new AddOutcomeMeasureView(
				frame.getPresentationModelFactory().getCreationModel(d_om), d_okButton);
		getUserPanel().add(view.buildPanel());
		pack();
		getRootPane().setDefaultButton(d_okButton);
		d_selectionModel = selectionModel;
	}

	protected void cancel() {
		setVisible(false);
	}

	protected void commit() {
		
		if (d_om instanceof Endpoint)
			d_domain.addEndpoint((Endpoint) d_om);
		else if (d_om instanceof AdverseEvent)
			d_domain.addAdverseEvent((AdverseEvent) d_om);
		else if (d_om instanceof PopulationCharacteristic) {
			d_domain.addVariable((PopulationCharacteristic) d_om);
		}
		else 
			throw new IllegalArgumentException("Unknown type of OutcomeMeasure.");
		
		setVisible(false);
		if (d_selectionModel != null)
			d_selectionModel.setValue(d_om);
		d_main.leftTreeFocus(d_om);
	}
}
