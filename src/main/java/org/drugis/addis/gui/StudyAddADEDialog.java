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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.builder.StudyAddADEView;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
public class StudyAddADEDialog extends OkCancelDialog {
	private Main d_main;
	private Domain d_domain;
	private Study d_study;
	private OutcomeMeasureHolder d_newOutcome;
	private Map<Arm, BasicMeasurement> d_measurements;
	
	public StudyAddADEDialog(Main frame, Domain domain, Study study) {
		super(frame, "Add Adverse Event to Study");
		this.setModal(true);
		d_main = frame;
		d_domain = domain;
		d_study = study;
		d_measurements = new HashMap<Arm, BasicMeasurement>();
		d_newOutcome = new OutcomeMeasureHolder();
		final StudyAddADEView view = new StudyAddADEView(d_domain, d_study,
				new PresentationModel<OutcomeMeasureHolder>(d_newOutcome), d_measurements,
				d_okButton, frame.getPresentationModelFactory());
		
		
		d_newOutcome.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				initPanel(view);
			}
		});
		initPanel(view);
	}

	private void initPanel(StudyAddADEView view) {
		d_measurements.clear();
		getUserPanel().removeAll();
		getUserPanel().add(view.buildPanel());
		pack();
	}

	protected void cancel() {
		setVisible(false);
	}

	protected void commit() {
		setVisible(false);
		
		addOutcomeToStudy();
		addMeasurementsToStudy();
		d_main.leftTreeFocus(d_study);
	}

	private void addMeasurementsToStudy() {
		for (Entry<Arm, BasicMeasurement> entry : d_measurements.entrySet()) {
			d_study.setMeasurement(d_newOutcome.getOutcomeMeasure(), entry.getKey(), entry.getValue());
		}
	}

	private void addOutcomeToStudy() {
		d_study.addOutcomeMeasure(d_newOutcome.getOutcomeMeasure());
	}
}
