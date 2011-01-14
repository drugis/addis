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

package org.drugis.addis.gui;

import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.gui.components.LinkLabel;
import org.drugis.addis.gui.components.StudiesTablePanel;
import org.drugis.addis.presentation.StudyListPresentation;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.GUIHelper;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.renderer.category.StandardBarPainter;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.AbstractValueModel;

public class GUIFactory {
	public static JButton createPlusButton(String toolTipText) {
		return createIconButton(FileNames.ICON_PLUS, toolTipText);
	}

	public static JButton createIconButton(String iconName, String toolTipText) {
		Icon icon = ImageLoader.getIcon(iconName);
		JButton button = new JButton(icon);
		button.setToolTipText(toolTipText);
		return button;
	}

	public static JComponent createOutcomeMeasureLabelWithIcon(OutcomeMeasure e) {
		String fname = FileNames.ICON_STUDY;
		if (e instanceof Endpoint) {
			fname = FileNames.ICON_ENDPOINT;
		} if (e instanceof AdverseEvent) {
			fname = FileNames.ICON_ADVERSE_EVENT;
		}
		Icon icon = ImageLoader.getIcon(fname);
		JLabel textLabel = new JLabel(e.getName(), icon, JLabel.CENTER);
		
		Bindings.bind(textLabel, "text", new HTMLWrappingModel(new PresentationModel<OutcomeMeasure>(e).getModel(OutcomeMeasure.PROPERTY_NAME)));
		return textLabel;
	}

	public static JComponent buildStudyPanel(StudyListPresentation studies, AddisWindow parent) {
		JComponent studiesComp = null;
		if(studies.getIncludedStudies().getValue().isEmpty()) {
			studiesComp = new JLabel("No studies found.");
		} else {
			studiesComp = new StudiesTablePanel(studies, parent); 
		}
		return studiesComp;
	}	
	
	public static JLabel buildSiteLink() {
		return new LinkLabel("www.drugis.org", "http://drugis.org/");
	}

	public static JComboBox createDrugSelector(AbstractValueModel drugModel,
			Domain domain) {
		SelectionInList<Drug> drugSelectionInList =
			new SelectionInList<Drug>(
					new ArrayList<Drug>(domain.getDrugs()),
					drugModel);
		return BasicComponentFactory.createComboBox(drugSelectionInList);
	}
	
	public static void configureJFreeChartLookAndFeel() {
		StandardChartTheme chartTheme = new StandardChartTheme("ADDIS");
		chartTheme.setBarPainter(new StandardBarPainter());
		chartTheme.setShadowVisible(false);
		ChartFactory.setChartTheme(chartTheme);
	}
	
	public static String createToolTip(Note note) {
		if (note == null) {
			return null;
		}
		return "<html><b>From " + note.getSource().toString() + "</b><br>\n" + 
			GUIHelper.wordWrap(note.getText(), false) + "</html>";
	}

}
