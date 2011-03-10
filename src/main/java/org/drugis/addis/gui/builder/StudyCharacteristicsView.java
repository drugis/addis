/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.DerivedStudyCharacteristic;
import org.drugis.addis.entities.ObjectWithNotes;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristics;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.NoteViewButton;
import org.drugis.addis.presentation.StudyPresentation;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyCharacteristicsView implements ViewBuilder {
	
	private StudyPresentation d_model;
	private JFrame d_parent;
	public StudyCharacteristicsView(JFrame parent, StudyPresentation model) {
		d_parent = parent;
		d_model = model;
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, left:pref, 3dlu, fill:0:grow",
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		int fullWidth = 5;

		builder.addLabel("ID:", cc.xy(1, 1));
		JLabel idLabel = AuxComponentFactory.createAutoWrapLabel(d_model.getModel(Study.PROPERTY_ID));
		builder.add(new NoteViewButton(d_parent, "Study ID", d_model.getBean().getStudyIdWithNotes().getNotes()), cc.xy(3, 1));
		builder.add(idLabel,
				cc.xyw(5, 1, fullWidth - 4));
		
		int row = 3;
		for (Characteristic c : StudyCharacteristics.values()) {
			LayoutUtil.addRow(layout);
			builder.addLabel(c.getDescription() + ":", cc.xy(1, row, "right, top"));
			
			if (c instanceof BasicStudyCharacteristic || c == DerivedStudyCharacteristic.INDICATION) {
				ObjectWithNotes<?> characteristicWithNotes = null;
				if (c instanceof BasicStudyCharacteristic) {
					characteristicWithNotes = d_model.getBean().getCharacteristicWithNotes(c);
				} else {
					characteristicWithNotes = d_model.getBean().getIndicationWithNotes();
				}
				builder.add(new NoteViewButton(d_parent, c.getDescription(), characteristicWithNotes == null ? null : characteristicWithNotes.getNotes()),
						cc.xy(3, row, "left, top"));
			}
			
			JComponent charView = 
				AuxComponentFactory.createCharacteristicView(d_model.getCharacteristicModel(c));
			builder.add(charView, cc.xyw(5, row, fullWidth - 4));

			row += 2;
		}
		return builder.getPanel();
	}
}
