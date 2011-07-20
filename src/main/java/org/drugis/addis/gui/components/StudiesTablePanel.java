/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang.StringUtils;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.CharacteristicSelectDialog;
import org.drugis.addis.presentation.StudyCharTableModel;
import org.drugis.addis.presentation.StudyListPresentation;
import org.drugis.common.gui.GUIHelper;

import com.jgoodies.forms.builder.ButtonBarBuilder2;

@SuppressWarnings("serial")
public class StudiesTablePanel extends TablePanel {
	public StudiesTablePanel(StudyListPresentation studyListPresentationModel, AddisWindow main) {
		super(createTable(studyListPresentationModel, main));
		
		ButtonBarBuilder2 bb = new ButtonBarBuilder2();
		bb.addButton(StudiesTablePanel.buildCustomizeButton(studyListPresentationModel, main));
		bb.addGlue();
		
		add(bb.getPanel(), BorderLayout.SOUTH);
	}

	public static JTable createTable(final StudyListPresentation studyListPM, final AddisWindow main) {
		StudyCharTableModel model = new StudyCharTableModel(studyListPM, main.getPresentationModelFactory());
		EnhancedTable table = EnhancedTable.createWithSorter(model);
		final TableCellRenderer defaultRenderer = table.getDefaultRenderer(Object.class);
		table.setDefaultRenderer(Object.class, new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				return defaultRenderer.getTableCellRendererComponent(table, getDescription(value, false), isSelected, hasFocus, row, column);
			}

			@SuppressWarnings("unchecked")
			private String getDescription(Object value, boolean nested) {
				if (value instanceof Entity) {
					return ((Entity)value).getDescription();
				}
				if (value instanceof Collection) {
					return getElementDescriptions((Collection<?>) value, nested);
				}
				return value.toString();
			}

			private String getElementDescriptions(Collection<?> c, boolean nested) {
				List<String> desc = new ArrayList<String>();
				for (Object o : c) {
					desc.add(getDescription(o, true));
				}
				String str = StringUtils.join(desc, ", ");
				return nested ? ("[" + str + "]") : str;
			}
		});
		table.autoSizeColumns();
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
		table.addKeyListener(new EntityTableDeleteListener(main));
		return table;
	}

	public static JButton buildCustomizeButton(final StudyListPresentation studyListPM, final AddisWindow main) {
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
