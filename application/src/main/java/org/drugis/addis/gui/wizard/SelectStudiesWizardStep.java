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

package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.drugis.addis.gui.renderer.EntityCellRenderer;
import org.drugis.addis.presentation.wizard.NetworkMetaAnalysisWizardPM;
import org.drugis.common.gui.table.EnhancedTable;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.adapter.BasicComponentFactory;

@SuppressWarnings("serial")
public class SelectStudiesWizardStep extends PanelWizardStep {

	private final NetworkMetaAnalysisWizardPM d_pm;
	private EnhancedTable d_table;

	public SelectStudiesWizardStep(NetworkMetaAnalysisWizardPM pm) {
		super("Select Studies","Select the studies to be used for meta analysis. At least one study must be selected to continue.");
		d_pm = pm;
	}
	
	@Override
	public void prepare() {
		removeAll(); // Rebuild the panel
		d_pm.populateSelectableStudies();

		d_table = EnhancedTable.createWithSorter(d_pm.getSelectableStudyListPM());
		EntityCellRenderer.insertEntityRenderer(d_table);
		d_table.autoSizeColumns();
		setLayout(new BorderLayout(0, 5));
		JLabel label = BasicComponentFactory.createLabel(d_pm.getStudiesMeasuringLabelModel());
		add(label, BorderLayout.NORTH);
		add(new JScrollPane(d_table), BorderLayout.CENTER);
		
	}
}