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

package org.drugis.addis.gui.builder;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import org.apache.commons.lang.StringUtils;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.AddisTabbedPane;
import org.drugis.addis.gui.wizard.TreatmentCategorizationOverviewWizardStep;
import org.drugis.addis.presentation.TreatmentCategorizationPresentation;
import org.drugis.addis.presentation.UnmodifiableHolder;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.SingleColumnPanelBuilder;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class TreatmentCategorizationView implements ViewBuilder {

	private final TreatmentCategorizationPresentation d_model;

	public TreatmentCategorizationView(final TreatmentCategorizationPresentation pm, final AddisWindow parent) {
		d_model = pm;
	}

	@Override
	public JComponent buildPanel() {
		SingleColumnPanelBuilder builder = new SingleColumnPanelBuilder();

		// ---------- Overview ----------
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(TreatmentCategorization.class).getSingularCapitalized());
		builder.add(buildOverviewPanel());
		final JTabbedPane tabbedPane = new AddisTabbedPane();
		tabbedPane.addTab("Overview", builder.getPanel());

		// ---------- Tree visualization ----------
		builder = new SingleColumnPanelBuilder();
		FormLayout layout = new FormLayout("fill:pref:grow", "p, 3dlu, p, 3dlu, fill:pref:grow");
		final PanelBuilder tree = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		tree.addSeparator("Dose Decision Tree", cc.xy(1, 1));
		tree.add(new JLabel("Dose range values are in: " + d_model.getModel(TreatmentCategorization.PROPERTY_DOSE_UNIT).getValue().toString()), cc.xy(1, 3));

		tree.add(TreatmentCategorizationOverviewWizardStep.buildOverview(d_model.getBean().getDecisionTree()), cc.xy(1, 5));
		builder.add(tree.getPanel());
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
			String criterionLabel = category.getCriterionLabel();
			String[] criteria = StringUtils.splitByWholeSeparator(criterionLabel, " OR ");
			criterionLabel = StringUtils.join(criteria, " OR\n");
			builder.add(AuxComponentFactory.createAutoWrapLabel(new UnmodifiableHolder<String>("Inclusion criteria: " + criterionLabel)), cc.xy(1, row));
			row = LayoutUtil.addRow(layout, row);

			builder.add(DrugView.buildStudyListComponent(d_model.getCategorizedStudyList(category), Main.getMainWindow()), cc.xy(1, row));
			layout.appendRow(RowSpec.decode("10dlu"));
			row += 1;
		}
		return builder.getPanel();
	}
}