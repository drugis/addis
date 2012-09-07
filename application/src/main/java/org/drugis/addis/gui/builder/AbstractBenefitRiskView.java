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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.VariableType;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.forestplot.ForestPlot;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.ValueTreeGraph;
import org.drugis.addis.gui.components.AddisTabbedPane;
import org.drugis.addis.gui.components.ListPanel;
import org.drugis.addis.gui.renderer.BRATDifferenceRenderer;
import org.drugis.addis.gui.renderer.BRATForestCellRenderer;
import org.drugis.addis.gui.renderer.DistributionQuantileCellRenderer;
import org.drugis.addis.presentation.AbstractBenefitRiskPresentation;
import org.drugis.addis.presentation.AbstractBenefitRiskPresentation.DecisionContextField;
import org.drugis.addis.presentation.BRATTableModel;
import org.drugis.addis.presentation.BRATTableModel.BRATDifference;
import org.drugis.addis.presentation.BRATTableModel.BRATForest;
import org.drugis.addis.presentation.StudyBenefitRiskPresentation;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;
import org.drugis.common.gui.table.EnhancedTable;
import org.drugis.common.gui.table.TablePanel;

import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public abstract class AbstractBenefitRiskView<Alternative extends Entity, PresentationType extends AbstractBenefitRiskPresentation<Alternative, ?>> implements ViewBuilder {


	protected PresentationType d_pm;
	protected AddisWindow d_mainWindow;
	protected ViewBuilder d_view;

	public AbstractBenefitRiskView(PresentationType model, AddisWindow main) {
		d_pm = model;
		d_mainWindow = main;
		if (getAnalysis().getAnalysisType() == AnalysisType.SMAA) {
			d_view = new SMAAView(d_pm, d_mainWindow);
		} else {
			d_view = new LyndOBrienView(d_pm, d_mainWindow);
		}

	}

	public JComponent buildPanel() {
		JTabbedPane tabbedPane = new AddisTabbedPane();
		tabbedPane.addTab("Overview", buildOverviewPanel());
		tabbedPane.addTab("Measurements", buildMeasurementsPanel());
		tabbedPane.addTab("BRAT Framework", buildBratPanel());
		tabbedPane.addTab("Analysis", buildAnalysisPanel());
		tabbedPane.setOpaque(true);
		return tabbedPane;
	}

	protected JPanel buildBratPanel() {
		FormLayout layout = new FormLayout("fill:0:grow",
		"p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		CellConstraints cc = new CellConstraints();

		builder.addSeparator("Value Tree", cc.xy(1, 1));
		final ValueTreeGraph valueTree = new ValueTreeGraph(d_pm.getBean().getCriteria());
		builder.add(valueTree, cc.xy(1, 3));

		JButton saveBtn = new JButton("Save Image");
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				valueTree.saveImage(d_mainWindow);
			}
		});
		ButtonBarBuilder2 bbuilder = new ButtonBarBuilder2();
		bbuilder.addButton(saveBtn);
		builder.add(bbuilder.getPanel(), cc.xy(1, 5));

		int row = 5;
		for (Alternative alternative : getNonBaselineAlternatives()) {
			row = LayoutUtil.addRow(layout, row);
			builder.addSeparator("Benefit-Risk summary: " + alternative.getLabel() + " vs "  + getBaseline().getLabel(), cc.xy(1, row));
			row = LayoutUtil.addRow(layout, row);
			builder.add(new TablePanel(createSummaryTable(d_pm.createBRATTableModel(alternative))), cc.xy(1, row));
			row = LayoutUtil.addRow(layout, row);
			builder.add(createSummaryLabel(d_pm.createBRATTableModel(alternative)), cc.xy(1, row));
		}
		return builder.getPanel();
	}


	private ArrayList<Alternative> getNonBaselineAlternatives() {
		ArrayList<Alternative> alternatives = new ArrayList<Alternative>(d_pm.getBean().getAlternatives());
		alternatives.remove(getBaseline());
		return alternatives;
	}


	private Alternative getBaseline() {
		return d_pm.getBean().getBaseline();
	}


	private JComponent createSummaryLabel(BRATTableModel<Alternative, ? extends BenefitRiskAnalysis<Alternative>> model) {
		String baselineName = model.getBaseline().getLabel();
		String subjectName = model.getSubject().getLabel();
		String str =
				"<p>Key Benefit-Risk Summary table with embedded relative effect forest plot. " +
				"The color in the \"difference\" column indicates whether the point estimate favors " + baselineName + " (red) or " + subjectName + " (green). " +
				"The symbol in the forest plot indicates whether the logarithmic (square) or linear (diamond) scale is used.</p>";
		return AuxComponentFactory.createTextPane(str);
	}


	private JTable createSummaryTable(BRATTableModel<Alternative, ? extends BenefitRiskAnalysis<Alternative>> model) {
		JTable table = EnhancedTable.createBare(model);
		table.setDefaultRenderer(Variable.class, new DefaultTableCellRenderer());
		table.setDefaultRenderer(VariableType.class, new DefaultTableCellRenderer());
		table.setDefaultRenderer(Distribution.class, new DistributionQuantileCellRenderer(true));
		table.setDefaultRenderer(BRATDifference.class, new BRATDifferenceRenderer());
		table.setDefaultRenderer(BRATForest.class, new BRATForestCellRenderer<PresentationType>());
		table.setRowHeight((int) Math.max(ForestPlot.ROWHEIGHT + 2, new JLabel("<html>a<br/>b</html>").getPreferredSize().getHeight()));

		// Set minimum widths
		table.getTableHeader().getColumnModel().getColumn(BRATTableModel.COLUMN_FOREST).setMinWidth(ForestPlot.BARWIDTH + BRATForestCellRenderer.PADDING * 2);
		for (int i = 0; i < table.getColumnCount(); ++i) {
			if (table.getColumnClass(i) == Distribution.class || table.getColumnClass(i) == BRATDifference.class) {
				int minWidth = 0;
				for (int j = 0; j < table.getRowCount(); ++j) {
					minWidth = Math.max(getColumnMinWidth(table, j, i), minWidth);
				}
				table.getTableHeader().getColumnModel().getColumn(i).setMinWidth(minWidth);
			}
		}
		return table;
	}


	private int getColumnMinWidth(JTable table, int row, int column) {
		return table.getCellRenderer(row, column).getTableCellRendererComponent(table, table.getValueAt(row, column), false, false, row, column).getMinimumSize().width;
	}

	protected abstract JPanel buildOverviewPanel();

	protected JPanel buildOverviewPart() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout("right:pref, 3dlu, fill:0:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);

		builder.setOpaque(true);

		builder.addLabel("ID:", cc.xy(1, 1));
		JLabel tmp = new JLabel((String) d_pm.getModel(BenefitRiskAnalysis.PROPERTY_NAME).getValue());
		builder.add(tmp , cc.xy(3, 1));

		builder.addLabel("Analysis type:", cc.xy(1, 3));
		builder.add(AuxComponentFactory.createAutoWrapLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_ANALYSIS_TYPE)),	cc.xy(3, 3));

		builder.addLabel("Indication:", cc.xy(1, 5));
		builder.add(AuxComponentFactory.createAutoWrapLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_INDICATION)), cc.xy(3, 5));

		int row = 5;
		if (d_pm instanceof StudyBenefitRiskPresentation) {
			row += 2;
			LayoutUtil.addRow(layout);
			builder.addLabel("Study:", cc.xy(1, row));
			builder.add(AuxComponentFactory.createAutoWrapLabel(d_pm.getModel(StudyBenefitRiskAnalysis.PROPERTY_STUDY)), cc.xy(3, row));
		}

		row += 2;
		builder.addLabel("Criteria:", cc.xy(1, row));
		ListPanel criteriaList = new ListPanel(getAnalysis(), BenefitRiskAnalysis.PROPERTY_CRITERIA, OutcomeMeasure.class);
		builder.add(criteriaList,cc.xy(3, row));

		row += 2;
		builder.addLabel("Alternatives:", cc.xy(1, row));
		ListPanel alternativesList = new ListPanel(getAnalysis().getAlternatives());
		builder.add(alternativesList,cc.xy(3, row));

		row += 2;
		builder.addLabel("Baseline:", cc.xy(1, row));
		JLabel baselineLabel = new JLabel(getBaseline().getLabel());
		builder.add(baselineLabel,cc.xy(3, row));

		if (d_pm.getBean().getDecisionContext() != null) {
			row  = LayoutUtil.addRow(layout, row);
			builder.addSeparator("Decision context", cc.xyw(1, row, 3));
			for (DecisionContextField field : AbstractBenefitRiskPresentation.createDecisionContextFields(d_pm.getBean().getDecisionContext())) {
				row  = LayoutUtil.addRow(layout, row);
				builder.addLabel(field.getName() + ": ", cc.xy(1, row));
				builder.add(AuxComponentFactory.createAutoWrapLabel(field.getModel()), cc.xy(3, row));
			}
		}

		return builder.getPanel();
	}

	protected BenefitRiskAnalysis<?> getAnalysis() {
		return (BenefitRiskAnalysis<?>)d_pm.getBean();
	}

	protected JComponent buildAnalysisPanel() {
		return d_view.buildPanel();
	}

	protected abstract JPanel buildMeasurementsPanel();

}
