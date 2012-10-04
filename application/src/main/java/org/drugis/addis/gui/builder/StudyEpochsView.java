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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.entities.Epoch;
import org.drugis.addis.gui.NoteViewButton;
import org.drugis.addis.presentation.DurationPresentation;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.StudyPresentation;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyEpochsView {

	private final StudyPresentation d_pm;
	private final PresentationModelFactory d_pmf;
	private final Window d_parent;

	public StudyEpochsView(Window parent, StudyPresentation spm, PresentationModelFactory pmf) {
		d_parent = parent;
		d_pm = spm;
		d_pmf = pmf;
	}

	public JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref, 5dlu, left:pref, 5dlu, left:pref", 
				"p");
		PanelBuilder builder = new PanelBuilder(layout );
		
		CellConstraints cc = new CellConstraints();
		
		int row = 1;

		builder.addLabel("Epoch", cc.xy(3, row));
		builder.addLabel("Duration", cc.xy(5, row));

		for (Epoch e : d_pm.getBean().getEpochs()) {
			row = buildEpoch(layout, builder, cc, row, e);
		}
		return builder.getPanel();

	}

	private int buildEpoch(FormLayout layout, PanelBuilder builder,	CellConstraints cc, int row, Epoch e) {
		PresentationModel<Epoch> epochModel = d_pmf.getModel(e);
		DurationPresentation<Epoch> edpm = new DurationPresentation<Epoch>(e);
		
		LayoutUtil.addRow(layout);
		row += 2;
		
		final JLabel epochLabel = BasicComponentFactory.createLabel(epochModel.getModel(Epoch.PROPERTY_NAME));
		final JLabel epochDurationLabel = BasicComponentFactory.createLabel(
				new PropertyAdapter<DurationPresentation<Epoch>>(edpm, DurationPresentation.PROPERTY_LABEL, true));
		JButton button = new NoteViewButton(d_parent, "Epoch: " + e.toString(), e.getNotes());
		builder.add(button, cc.xy(1, row));
		builder.add(epochLabel, cc.xy(3, row));
		builder.add(epochDurationLabel, cc.xy(5, row));
		
		return row;
	}
}
