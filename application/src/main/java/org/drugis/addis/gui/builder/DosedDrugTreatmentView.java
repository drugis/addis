/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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
import javax.swing.JTabbedPane;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.AddisTabbedPane;
import org.drugis.addis.gui.wizard.DosedDrugTreatmentOverviewWizardStep;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.SingleColumnPanelBuilder;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import edu.uci.ics.jung.visualization.VisualizationViewer;

public class DosedDrugTreatmentView implements ViewBuilder {

	private final DosedDrugTreatmentPresentation d_model;

	public DosedDrugTreatmentView(final DosedDrugTreatmentPresentation dosedDrugTreatmentPresentation, final AddisWindow parent) {
		d_model = dosedDrugTreatmentPresentation;
	}

	@Override
	public JComponent buildPanel() {
		SingleColumnPanelBuilder builder = new SingleColumnPanelBuilder();

		// ---------- Overview ----------
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(DosedDrugTreatment.class).getSingularCapitalized());
		builder.add(buildOverviewPanel());
		final JTabbedPane tabbedPane = new AddisTabbedPane();
		tabbedPane.addTab("Overview", builder.getPanel());

		// ---------- Tree visualization ----------
		builder = new SingleColumnPanelBuilder();
		builder.addSeparator("Dose Decision Tree");
		final VisualizationViewer<DecisionTreeNode, DecisionTreeEdge> treeView = DosedDrugTreatmentOverviewWizardStep.buildDecisionTreeView(d_model.getBean().getDecisionTree());
		builder.add(treeView);
		tabbedPane.addTab("Decision tree", builder.getPanel());

		return tabbedPane;
	}

	private JComponent buildOverviewPanel() {
		final FormLayout layout = new FormLayout("fill:pref:grow", "p");

		final PanelBuilder builder = new PanelBuilder(layout);
		final CellConstraints cc = new CellConstraints();
		int row = 1;

		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Drug.class).getSingularCapitalized(), cc.xy(1, row));
		row = LayoutUtil.addRow(layout, row);
		builder.add(DrugView.createDrugOverviewPanel(d_model.getDrugPresentation()), cc.xy(1, row));
		layout.appendRow(RowSpec.decode("10dlu"));
		row += 1;

		row = LayoutUtil.addRow(layout, row);
		builder.addSeparator("Dose categories", cc.xy(1, row));
		for(final Category category : d_model.getBean().getCategories()) {
			row = LayoutUtil.addRow(layout, row);
			builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Study.class).getPlural()
					+ " measuring this "
					+  CategoryKnowledgeFactory.getCategoryKnowledge(Drug.class).getSingular()
					+ " categorized as '" + category.getName() + "'", cc.xy(1, row));
			row = LayoutUtil.addRow(layout, row);
			builder.add(DrugView.buildStudyListComponent(d_model.getCategorizedStudyList(category), Main.getMainWindow()), cc.xy(1, row));
			layout.appendRow(RowSpec.decode("10dlu"));
			row += 1;
		}
		return builder.getPanel();
	}
}