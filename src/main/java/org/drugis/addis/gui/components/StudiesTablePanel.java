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

package org.drugis.addis.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;

import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.CharacteristicSelectDialog;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.StudyCharTableModel;
import org.drugis.addis.presentation.StudyListPresentationModel;
import org.drugis.common.gui.GUIHelper;

import com.jgoodies.forms.builder.ButtonBarBuilder2;

@SuppressWarnings("serial")
public class StudiesTablePanel extends TablePanel {
	public StudiesTablePanel(StudyListPresentationModel studyListPresentationModel, Main main) {
		super(createTable(studyListPresentationModel, main));
		
		ButtonBarBuilder2 bb = new ButtonBarBuilder2();
		bb.addButton(StudiesTablePanel.buildCustomizeButton(studyListPresentationModel, main));
		bb.addGlue();
		
		add(bb.getPanel(), BorderLayout.WEST);
	}

	public static JTable createTable(final StudyListPresentationModel studyListPM, final Main main) {
		StudyCharTableModel model = new StudyCharTableModel(studyListPM, main.getPresentationModelFactory());
		JTable table = new EnhancedTable(model);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					int row = ((EnhancedTable)e.getComponent()).rowAtPoint(e.getPoint());
					Study s = studyListPM.getIncludedStudies().getValue().get(row);
					main.leftTreeFocus(s);
				}
			}
		});
		table.addKeyListener(new EntityTableDeleteListener((Main) main));
		return table;
	}

	public static JButton buildCustomizeButton(final StudyListPresentationModel studyListPM, final Main main) {
		JButton customizeButton = new JButton("Customize Shown Characteristics");
		customizeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				JDialog dialog = new CharacteristicSelectDialog(main, studyListPM);
				GUIHelper.centerWindow(dialog, main);
				dialog.setVisible(true);
			}
		});
		return customizeButton;
	}
}
