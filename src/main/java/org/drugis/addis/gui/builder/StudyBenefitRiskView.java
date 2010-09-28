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

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.components.ScrollableJPanel;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.presentation.StudyBenefitRiskPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.ChildComponenentHeightPropagater;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fi.smaa.jsmaa.gui.presentation.PreferencePresentationModel;
import fi.smaa.jsmaa.gui.views.PreferenceInformationView;

public class StudyBenefitRiskView extends AbstractBenefitRiskView<StudyBenefitRiskPresentation> {

	private PanelBuilder d_builder;
	protected JPanel d_panel;
	public StudyBenefitRiskView(StudyBenefitRiskPresentation model, Main main) {
		super(model, main);
		d_pm.startSMAA();
	}
	
	public JComponent buildPanel() {
		if (d_builder != null)
			d_builder.getPanel().removeAll();
		
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p"); // 1-3 
				
		d_builder = new PanelBuilder(layout, new ScrollableJPanel());
		d_builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		String singularCapitalized = CategoryKnowledgeFactory.getCategoryKnowledge(BenefitRiskAnalysis.class).getSingularCapitalized();
		d_builder.addSeparator(singularCapitalized, cc.xy(1, 1));
		d_builder.add(buildOverviewPart(), cc.xy(1, 3));

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(singularCapitalized, d_builder.getPanel());
		
		layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, " + // 1-3 
				"3dlu, p, 3dlu, p, " + // 4-7
				"3dlu, p, 3dlu, p, " + // 8-11 
				"3dlu, p, 3dlu, p "
				);
		
		d_builder = new PanelBuilder(layout, new ScrollableJPanel());
		d_builder.setDefaultDialogBorder();
		
		cc =  new CellConstraints();
		d_builder.addSeparator("Measurements", cc.xy(1, 1));
		d_builder.add(buildMeasurementsPart(), cc.xy(1, 3));
		
		d_builder.addSeparator("Preferences", cc.xy(1, 5));
		d_builder.add(buildPreferencesPart(), cc.xy(1, 7));
		
		d_builder.addSeparator("Rank Acceptabilities", cc.xy(1, 9));
		d_builder.add(buildRankAcceptabilitiesPart(), cc.xy(1, 11));
		
		d_builder.addSeparator("Central Weights", cc.xy(1, 13));
		d_builder.add(buildCentralWeightsPart(), cc.xy(1, 15));
		
		d_panel = d_builder.getPanel();
		ChildComponenentHeightPropagater.attachToContainer(d_panel);
		
		tabbedPane.addTab("Analysis", d_builder.getPanel());
		
		return tabbedPane;
	}

	@Override
	protected JComponent buildMeasurementsPart() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout("pref:grow:fill",
				"p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		
		builder.add(AuxComponentFactory.createNoteField("Measurements: incidence approximated with Beta-distribution, or continuous variables approximated with a Normal distribution."
				),cc.xy(1, 1));
		builder.add(new TablePanel(new EnhancedTable(d_pm.getMeasurementTableModel())), cc.xy(1, 3));
	
		return builder.getPanel();
	}

	@Override
	protected JComponent buildPreferencesPart() {
		return new PreferencesBuilder().buildPanel();
	}

	@Override
	protected JComponent buildPreferenceInformationView(PreferencePresentationModel preferencePresentationModel, StudyBenefitRiskPresentation pm) {
		JComponent prefPanel = new PreferenceInformationView(d_pm.getPreferencePresentationModel()).buildPanel();
		return prefPanel;
	}

	protected JComponent buildRankAcceptabilitiesPart() {
		return new RankAcceptabilitiesBuilder().buildPanel();
	}

	protected JComponent buildCentralWeightsPart() {
		return new CentralWeightsBuilder().buildPanel();
	}
}
