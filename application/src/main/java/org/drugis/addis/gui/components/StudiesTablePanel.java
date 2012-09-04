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

package org.drugis.addis.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;

import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.CharacteristicSelectDialog;
import org.drugis.addis.presentation.StudyCharTableModel;
import org.drugis.addis.presentation.StudyListPresentation;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.table.TablePanel;

import com.jgoodies.forms.builder.ButtonBarBuilder2;

@SuppressWarnings("serial")
public class StudiesTablePanel extends TablePanel {
	public StudiesTablePanel(StudyListPresentation studyListPresentationModel, AddisWindow main) {
		super(EntityTablePanel.createTable(main, new StudyCharTableModel(studyListPresentationModel, main.getPresentationModelFactory())));
		
		ButtonBarBuilder2 bb = new ButtonBarBuilder2();
		bb.addButton(StudiesTablePanel.buildCustomizeButton(studyListPresentationModel, main));
		bb.addGlue();
		
		add(bb.getPanel(), BorderLayout.SOUTH);
	}

	public static JButton buildCustomizeButton(final StudyListPresentation studyListPM, final AddisWindow main) {
		JButton customizeButton = new JButton("Customize Shown Characteristics");
		customizeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent event) {
				JDialog dialog = new CharacteristicSelectDialog(main, studyListPM);
				GUIHelper.centerWindow(dialog, main);
				dialog.setVisible(true);
			}
		});
		return customizeButton;
	}
}
