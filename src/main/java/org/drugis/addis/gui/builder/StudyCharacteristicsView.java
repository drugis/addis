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

package org.drugis.addis.gui.builder;


import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.StudyCharacteristics;
import org.drugis.addis.presentation.StudyPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyCharacteristicsView implements ViewBuilder {
	
	private StudyPresentation d_model;

	public StudyCharacteristicsView(StudyPresentation model) {
		d_model = model;
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, left:pref:grow",
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		int fullWidth = 3;
		
		builder.addLabel("ID:", cc.xy(1, 1));
		JLabel idLabel = BasicComponentFactory.createLabel(d_model.getModel(Study.PROPERTY_ID));
		idLabel.setToolTipText(GUIHelper.createToolTip(d_model.getNote(Study.PROPERTY_ID)));
		builder.add(idLabel,
				cc.xyw(3, 1, fullWidth - 2));
		
		int row = 3;
		for (Characteristic c : StudyCharacteristics.values()) {
			if (isCharacteristicShown(c)) {
				LayoutUtil.addRow(layout);
				builder.addLabel(c.getDescription() + ":", cc.xy(1, row));

				JComponent charView = 
					AuxComponentFactory.createCharacteristicView(d_model.getCharacteristicModel(c));
				if (charView instanceof JScrollPane) {
					JScrollPane pane = (JScrollPane)charView;
					((JComponent)pane.getViewport().getView()).setToolTipText(
							GUIHelper.createToolTip(d_model.getNote(c)));
				} else {
					charView.setToolTipText(GUIHelper.createToolTip(d_model.getNote(c)));
				}
				builder.add(charView,
						cc.xyw(3, row, fullWidth - 2));

				row += 2;
			}
		}
		return builder.getPanel();
	}

	private boolean isCharacteristicShown(Characteristic c) {
		if (c.equals(BasicStudyCharacteristic.STUDY_END)) {
			return (d_model.isStudyFinished());
		}
		return true;
	}
}
