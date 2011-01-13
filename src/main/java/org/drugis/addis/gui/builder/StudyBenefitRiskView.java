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

import javax.swing.JPanel;

import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.presentation.StudyBenefitRiskPresentation;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyBenefitRiskView extends AbstractBenefitRiskView<StudyBenefitRiskPresentation> {

	public StudyBenefitRiskView(StudyBenefitRiskPresentation model, AddisWindow mainWindow) {
		super(model, mainWindow);
	}
	
	public JPanel buildMeasurementsPanel() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout("pref:grow:fill",
				"p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.add(AuxComponentFactory.createHtmlField("Measurements: incidence approximated with Beta-distribution, or continuous variables approximated with a Normal distribution."),
				cc.xy(1, 1));
		EnhancedTable table = new EnhancedTable(((StudyBenefitRiskPresentation) d_pm).getMeasurementTableModel());
		table.setDefaultRenderer(Distribution.class, new DistributionQuantileCellRenderer());
		builder.add(new TablePanel(table), cc.xy(1, 3));

		return builder.getPanel();
	}

	@Override
	protected JPanel buildOverviewPanel() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, " + // 1-3 
				"3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout, new JPanel());
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(BenefitRiskAnalysis.class).getSingularCapitalized(), cc.xy(1, 1));
		builder.add(buildOverviewPart(), cc.xy(1, 3));
		
		return builder.getPanel();
	}

}
