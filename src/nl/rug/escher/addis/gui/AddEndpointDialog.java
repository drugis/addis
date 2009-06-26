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

package nl.rug.escher.addis.gui;


import javax.swing.JFrame;

import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.Endpoint.Type;
import nl.rug.escher.common.gui.OkCancelDialog;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
public class AddEndpointDialog extends OkCancelDialog {
	private Domain d_domain;
	private Endpoint d_endpoint;
	
	public AddEndpointDialog(JFrame frame, Domain domain) {
		super(frame, "Add Endpoint");
		this.setModal(true);
		d_domain = domain;
		d_endpoint = new Endpoint("e", Type.CONTINUOUS);
		EndpointView view = new EndpointView(new PresentationModel<Endpoint>(d_endpoint), d_okButton);
		getUserPanel().add(view.buildPanel());
		pack();
	}

	protected void cancel() {
		setVisible(false);
	}

	protected void commit() {
		d_domain.addEndpoint(d_endpoint);
		setVisible(false);
	}
}
