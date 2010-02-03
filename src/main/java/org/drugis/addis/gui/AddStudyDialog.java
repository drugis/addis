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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainEvent;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.builder.AddStudyView;
import org.drugis.common.Interval;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.ButtonBarBuilder2;

@SuppressWarnings("serial")
public class AddStudyDialog extends OkCancelDialog {
	private Domain d_domain;
	private Study d_study;
	private OutcomeMeasureHolder d_primaryOutcomeMeasure;
	private AddStudyView d_view;
	private JButton d_addArmButton;
	private Main d_main;
	
	public AddStudyDialog(Main mainWindow, Domain domain) {
		super(mainWindow, "Add Study");
		this.d_main = mainWindow;
		this.setModal(true);
		d_domain = domain;
		d_study = new Study("", new Indication(0L, ""));
		d_primaryOutcomeMeasure = new OutcomeMeasureHolder();
		d_primaryOutcomeMeasure.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				setEndpoint();
				buildMeasurements();
				initUserPanel();
			}
		});
		d_domain.addListener(new DomainListener() {
			public void domainChanged(DomainEvent evt) {
				initUserPanel();	
			}
		});
		d_view = new AddStudyView(new PresentationModel<Study>(d_study),
				new PresentationModel<OutcomeMeasureHolder>(d_primaryOutcomeMeasure), domain,
				d_okButton, mainWindow);
		initUserPanel();
	}

	protected void setEndpoint() {
		d_study.setOutcomeMeasures(Collections.singletonList(d_primaryOutcomeMeasure.getOutcomeMeasure()));
		if (d_primaryOutcomeMeasure.getOutcomeMeasure() != null) {			
			d_addArmButton.setEnabled(true);
		}
	}

	protected void buildMeasurements() {
		for (Arm g : d_study.getArms()) {
			OutcomeMeasure endpoint = d_primaryOutcomeMeasure.getOutcomeMeasure();
			d_study.setMeasurement(endpoint, g, endpoint.buildMeasurement(g));
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
		builder.addButton(createAddArmButton());
		return builder.getPanel();
	}

	private JComponent createAddArmButton() {
		if (d_addArmButton == null) {
			d_addArmButton = new JButton("Add Arm");
			d_addArmButton.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					addArm();
				}
			});
			d_addArmButton.setEnabled(false);
		}
		return d_addArmButton;
	}

	protected void addArm() {
		addNewArm();
		initUserPanel();
	}

	private void addNewArm() {
		Arm group = new Arm(null, new FlexibleDose(new Interval<Double>(0.0,0.0), SIUnit.MILLIGRAMS_A_DAY),
				0);
		d_study.addArm(group);
		if (d_primaryOutcomeMeasure.getOutcomeMeasure() != null) {
			BasicMeasurement m = d_primaryOutcomeMeasure.getOutcomeMeasure().buildMeasurement(group);
			d_study.setMeasurement(d_primaryOutcomeMeasure.getOutcomeMeasure(), group, m);
		}
	}

	@Override
	protected void cancel() {
		setVisible(false);
	}

	@Override
	protected void commit() {
		bindOutcomeMeasure();
		d_domain.addStudy(d_study);
		setVisible(false);
		d_main.leftTreeFocus(d_study);
	}

	private void bindOutcomeMeasure() {
		d_study.setOutcomeMeasures(new ArrayList<OutcomeMeasure>(d_primaryOutcomeMeasure.asList()));
	}
}