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

package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.presentation.SelectableStudyCharTableModel;
import org.drugis.addis.presentation.wizard.AbstractMetaAnalysisWizardPM;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class SelectStudiesWizardStep extends PanelWizardStep {

	public SelectStudiesWizardStep(AbstractMetaAnalysisWizardPM<?> pm, AddisWindow mainWindow) {
		super("Select Studies","Select the studies to be used for meta analysis. At least one study must be selected to continue.");

		setLayout(new BorderLayout());
		JComponent studiesComp;			

		EnhancedTable table = new EnhancedTable(new SelectableStudyCharTableModel(pm.getStudyListModel(), mainWindow.getPresentationModelFactory()));

		JScrollPane sPane = new JScrollPane(table);
		sPane.getVerticalScrollBar().setUnitIncrement(16);			
		sPane.setPreferredSize(new Dimension(700,300));

		studiesComp = sPane;

		FormLayout layout = new FormLayout(
				"center:pref:grow",
				"p, 3dlu, p"
		);	

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();

		builder.add(BasicComponentFactory.createLabel(pm.getStudiesMeasuringLabelModel()),
				cc.xy(1, 1));
		builder.add(studiesComp, cc.xy(1, 3));
		JScrollPane sp = new JScrollPane(builder.getPanel());
		sp.getVerticalScrollBar().setUnitIncrement(16);
		add(sp);
	}
}