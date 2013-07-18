/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.gui.builder;

import java.awt.Window;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.entities.Activity;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.gui.NoteViewButton;
import org.drugis.addis.presentation.BasicArmPresentation;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.DrugTreatmentPresentation;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.OneWayObjectFormat;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyArmsView implements ViewBuilder {
	
	private PresentationModel<? extends Study> d_model;
	private PresentationModelFactory d_pmf;
	private Window d_parent;

	public StudyArmsView(Window parent, PresentationModel<? extends Study> model, PresentationModelFactory pm) {
		d_parent = parent;
		d_model = model;
		d_pmf = pm;
	}

	public JPanel buildPanel() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout( 
				"left:pref, 5dlu, left:pref, 5dlu, left:pref, 5dlu, left:pref, 5dlu, right:pref",
				"p"
				);
		PanelBuilder builder = new PanelBuilder(layout);
		
		int row = 1;

		builder.addLabel("Arm", cc.xy(3, row));
		builder.addLabel("Drug", cc.xy(5, row));
		builder.addLabel("Dose", cc.xy(7, row));
		builder.addLabel("Size", cc.xy(9, row));		

		for (Arm a : d_model.getBean().getArms()) {
			row = buildArm(layout, builder, cc, row, a);
		}
		return builder.getPanel();
	}

	private int buildArm(FormLayout layout, PanelBuilder builder, CellConstraints cc, int row, Arm a) {
		BasicArmPresentation armModel = (BasicArmPresentation)d_pmf.getModel(a);
		row = LayoutUtil.addRow(layout, row);

		// non-treatment arm components
		JLabel sizeLabel = BasicComponentFactory.createLabel(armModel.getModel(Arm.PROPERTY_SIZE), NumberFormat.getInstance());
		final JLabel armLabel = BasicComponentFactory.createLabel(d_pmf.getLabeledModel(a).getLabelModel()); 
		JButton noteButton = new NoteViewButton(d_parent, "Arm: " + a.toString(), a.getNotes());

		builder.add(noteButton, cc.xy(1, row));
		builder.add(armLabel, cc.xy(3, row));
		builder.add(sizeLabel, cc.xy(9, row));
		
		Activity activity = d_model.getBean().getActivity(a);
		if (activity != null) {
			if (activity instanceof DrugTreatment) {
				DrugTreatment ta = (DrugTreatment)activity;
				DrugTreatmentPresentation activityModel = (DrugTreatmentPresentation)d_pmf.getModel(ta);
				addTreatmentActivity(activityModel, builder, cc, row);
			} else if (activity instanceof TreatmentActivity) {
				TreatmentActivity ct = (TreatmentActivity)activity;
				for(int i = 0; i < ct.getTreatments().getSize(); ++i) {
					if(i > 0) {
						row = LayoutUtil.addRow(layout, row);
					}
					DrugTreatment ta = ct.getTreatments().get(i);
					DrugTreatmentPresentation activityModel = (DrugTreatmentPresentation)d_pmf.getModel(ta);
					addTreatmentActivity(activityModel, builder, cc, row);
				}
			
			}
		}
		return row;
	}

	private void addTreatmentActivity(
			DrugTreatmentPresentation activityModel, PanelBuilder builder,
			CellConstraints cc, int row) {
		builder.add(
				BasicComponentFactory.createLabel(
						activityModel.getModel(DrugTreatment.PROPERTY_DRUG),
						new OneWayObjectFormat()),
						cc.xy(5, row));
		builder.add(
				BasicComponentFactory.createLabel(
						activityModel.getModel(DrugTreatment.PROPERTY_DOSE),
						new OneWayObjectFormat()),
						cc.xy(7, row));
	}
}
