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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

import nl.rug.escher.addis.entities.BasicMeasurement;
import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.MutablePatientGroup;
import nl.rug.escher.addis.entities.SIUnit;
import nl.rug.escher.common.gui.OkCancelDialog;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.ButtonBarBuilder2;

@SuppressWarnings("serial")
public class AddStudyDialog extends OkCancelDialog {
	private Domain d_domain;
	private BasicStudy d_study;
	private EndpointHolder d_primaryEndpoint;
	private AddStudyView d_view;
	
	public AddStudyDialog(JFrame frame, Domain domain) {
		super(frame, "Add Study");
		this.setModal(true);
		d_domain = domain;
		d_study = new BasicStudy("new study");
		d_primaryEndpoint = new EndpointHolder();
		d_primaryEndpoint.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				setEndpoint();
				buildMeasurements();
				initUserPanel();
			}
		});
		d_view = new AddStudyView(new PresentationModel<BasicStudy>(d_study),
				new PresentationModel<EndpointHolder>(d_primaryEndpoint), domain,
				d_okButton);
		initUserPanel();
	}

	protected void setEndpoint() {
		d_study.setEndpoints(Collections.singletonList(d_primaryEndpoint.getEndpoint()));
	}

	protected void buildMeasurements() {
		for (MutablePatientGroup g : d_study.getPatientGroups()) {
			Endpoint endpoint = d_primaryEndpoint.getEndpoint();
			d_study.setMeasurement(endpoint, g, endpoint.buildMeasurement());
		}
	}

	private void initUserPanel() {
		getUserPanel().removeAll();
		getUserPanel().setLayout(new BorderLayout());
		getUserPanel().add(d_view.buildPanel(), BorderLayout.CENTER);
		getUserPanel().add(buildButtonBar(), BorderLayout.SOUTH);
		pack();
	}

	private JComponent buildButtonBar() {
		ButtonBarBuilder2 builder = new ButtonBarBuilder2();
		builder.addButton(createAddPatientGroupButton());
		return builder.getPanel();
	}

	private JComponent createAddPatientGroupButton() {
		JButton button = new JButton("Add Patient Group");
		button.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				addPatientGroup();
			}
		});
		return button;
	}

	protected void addPatientGroup() {
		addNewPatientGroup();
		initUserPanel();
	}

	private void addNewPatientGroup() {
		BasicPatientGroup group = new BasicPatientGroup(d_study, null,
				new Dose(0.0, SIUnit.MILLIGRAMS_A_DAY), 0);
		d_study.addPatientGroup(group);
		if (d_primaryEndpoint.getEndpoint() != null) {
			BasicMeasurement m = d_primaryEndpoint.getEndpoint().buildMeasurement();
			d_study.setMeasurement(d_primaryEndpoint.getEndpoint(), group, m);
		}
	}

	@Override
	protected void cancel() {
		setVisible(false);
	}

	@Override
	protected void commit() {
		bindEndpoint();
		d_domain.addStudy(d_study);
		setVisible(false);
	}

	private void bindEndpoint() {
		d_study.setEndpoints(d_primaryEndpoint.asList());
		for (BasicPatientGroup g : d_study.getPatientGroups()) {
			((BasicMeasurement)d_study.getMeasurement(d_primaryEndpoint.getEndpoint(), g))
				.setEndpoint(d_primaryEndpoint.getEndpoint());
		}
	}
}