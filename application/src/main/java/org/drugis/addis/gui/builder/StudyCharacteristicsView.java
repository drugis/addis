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

package org.drugis.addis.gui.builder;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.DerivedStudyCharacteristic;
import org.drugis.addis.entities.ObjectWithNotes;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristics;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.NoteViewButton;
import org.drugis.addis.presentation.StudyPresentation;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyCharacteristicsView implements ViewBuilder {
	
	private StudyPresentation d_model;
	private Window d_parent;
	public StudyCharacteristicsView(Window parent, StudyPresentation model) {
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
		JLabel idLabel = AuxComponentFactory.createAutoWrapLabel(d_model.getModel(Study.PROPERTY_NAME));
		builder.add(new NoteViewButton(d_parent, "Study ID", d_model.getBean().getNotes()), cc.xy(3, 1));
		builder.add(idLabel,
				cc.xyw(5, 1, fullWidth - 4));
		
		int row = 1;
		for (Characteristic c : StudyCharacteristics.values()) {
			row = LayoutUtil.addRow(layout, row);

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
			
			JComponent charView = AuxComponentFactory.createCharacteristicView(d_model.getCharacteristicModel(c));
			builder.add(charView, cc.xyw(5, row, fullWidth - 4));
		}
		
		row = LayoutUtil.addRow(layout, row);
		builder.addSeparator("", cc.xyw(1, row, fullWidth));
		
		JButton d80Button = new JButton("Summary of Efficacy Table", ImageLoader.getIcon(FileNames.ICON_FILE_NEW));
		d80Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				D80ReportView view = new D80ReportView(d_parent, d_model.getBean());
				view.setVisible(true);
			}
		});
		
		row = LayoutUtil.addRow(layout, row);
		builder.add(d80Button, cc.xy(5, row));
		row = LayoutUtil.addRow(layout, row);
		String str = "<html>Display the Summary of Efficacy Table according to the <a href='http://www.ema.europa.eu/ema/index.jsp?curl=pages/regulation/general/general_content_000121.jsp'>EMA D80</a> Clinical report template</html>";
		JTextPane jep = AuxComponentFactory.createTextPaneWithHyperlinks(str);
		// ScrollPane because otherwise the caret in the textpane causing the tab to scroll down to the JTextPane.
		Component sp = new JScrollPane(jep, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		builder.add(sp, cc.xy(5, row));
		
		return builder.getPanel();
	}
}
