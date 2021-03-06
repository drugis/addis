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

package org.drugis.addis.gui;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.gui.components.StudiesTablePanel;
import org.drugis.addis.presentation.StudyListPresentation;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LinkLabel;
import org.drugis.mtc.gui.MainWindow;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.renderer.category.StandardBarPainter;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.AbstractValueModel;

public class GUIFactory {
	private static PrintStream s_errorStream =  System.err;

	public static JButton createPlusButton(final String toolTipText) {
		return createIconButton(FileNames.ICON_PLUS, toolTipText);
	}

	public static JButton createIconButton(final String iconName, final String toolTipText) {
		suppressErrors(true);
		Icon icon = Main.IMAGELOADER.getIcon(iconName);
		if(icon == null) {
			suppressErrors(false);
			icon = MainWindow.IMAGELOADER.getIcon(iconName); // Fallback to MTC ImageLoader
		}
		JButton button = new JButton(icon);
		button.setToolTipText(toolTipText);
		return button;
	}

	public static void suppressErrors(boolean suppress) {
		if(suppress) {
	 		System.setErr(new PrintStream(new OutputStream() { // suppress system.err output
			    public void write(int b) {
			    }
			}));
		} else {
			System.setErr(s_errorStream); // reset system.err output
		}
	}

	public static JButton createLabeledIconButton(final String label, final String iconName) {
		JButton btn = createIconButton(iconName, label);
		btn.setText(label);
		return btn;
	}

	public static JComponent createOutcomeMeasureLabelWithIcon(final Variable e, final boolean isPrimary) {
		String fname = FileNames.ICON_STUDY;
		String primary = "";
		if (e instanceof Endpoint) {
			fname = FileNames.ICON_ENDPOINT;
			primary = isPrimary ? " (primary)" : " (secondary)";
		} else if (e instanceof AdverseEvent) {
			fname = FileNames.ICON_ADVERSE_EVENT;
		} else if (e instanceof PopulationCharacteristic) {
			fname = FileNames.ICON_POPULATION_CHAR;
		}
		Icon icon = Main.IMAGELOADER.getIcon(fname);
		return new JLabel(e.getName() + primary, icon, JLabel.CENTER);
	}

	public static JComponent buildStudyPanel(final StudyListPresentation studies, final AddisWindow parent) {
		JComponent studiesComp = null;
		if(studies.getIncludedStudies().isEmpty()) {
			studiesComp = new JLabel("No studies found.");
		} else {
			studiesComp = new StudiesTablePanel(studies, parent);
		}
		return studiesComp;
	}

	public static JLabel buildSiteLink() {
		return new LinkLabel("www.drugis.org", "http://drugis.org/");
	}

	public static JComboBox createDrugSelector(final AbstractValueModel drugModel,
			final Domain domain) {
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

	public static String createToolTip(final Note note) {
		if (note == null) {
			return null;
		}
		return "<html><b>From " + note.getSource().toString() + "</b><br>\n" +
			GUIHelper.wordWrap(note.getText(), false) + "</html>";
	}
}
