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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.analysis.AbstractMetaAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AnalysisComponentFactory;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.MCMCWrapper;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.components.EntityTablePanel;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.presentation.MetaBenefitRiskPresentation;
import org.drugis.addis.presentation.SummaryCellRenderer;
import org.drugis.common.gui.ImageExporter;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class MetaBenefitRiskView extends AbstractBenefitRiskView<DrugSet, MetaBenefitRiskPresentation> {
	
	@SuppressWarnings("serial")
	private final class DistributionParameterCellRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, 
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (value instanceof GaussianBase) {
				GaussianBase d = (GaussianBase)value;
				String str = SummaryCellRenderer.format(d.getMu()) + " \u00B1 " + SummaryCellRenderer.format(d.getSigma());
				return super.getTableCellRendererComponent(table, str, isSelected, hasFocus, row, column);
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}

	public MetaBenefitRiskView(MetaBenefitRiskPresentation pm, AddisWindow mainWindow) {
		super(pm, mainWindow);
	}

	@Override
	protected JPanel buildOverviewPanel() {
		FormLayout layout = new FormLayout(
				"fill:0:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout, new JPanel());
		builder.setDefaultDialogBorder();
		builder.setOpaque(true);
		
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(BenefitRiskAnalysis.class).getSingularCapitalized(), cc.xy(1, 1));
		builder.add(buildOverviewPart(), cc.xy(1, 3));

		builder.addSeparator("Included Analyses", cc.xy(1, 5));
		builder.add(buildAnalysesPart(), cc.xy(1, 7));
		
		final JComponent progressBars = buildProgressBars();
		builder.add(progressBars, cc.xy(1, 9));
		
		return builder.getPanel();
	}
	
	private JComponent buildProgressBars() {
		FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:0:grow",
				"p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();

		builder.addSeparator("Sub-analyses are required. Please run them.", cc.xyw(1, 1, 3));
		builder.add(createRunAllButton(), cc.xyw(1, 3, 3));
		int row = 3;

		for (MCMCWrapper mw : d_pm.getWrappedModels()) {
			LayoutUtil.addRow(layout);
			row += 2;
			builder.add(AnalysisComponentFactory.createSimulationControls(mw, row, d_mainWindow, true), cc.xyw(1, row, 3));
		}
		
		return builder.getPanel();
	}
	
	private JButton createRunAllButton() {
		JButton button = new JButton(Main.IMAGELOADER.getIcon(FileNames.ICON_RUN));
		button.setText("Run all required sub-analyses");
		button.setToolTipText("Run all simulations");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				d_pm.startAllSimulations();
			}
		});
		return button;
	}

	protected JButton createSaveImageButton(final JComponent chart) {
		JButton button = new JButton("Save Image");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ImageExporter.writeImage(d_mainWindow, chart, (int) chart.getSize().getWidth(), (int) chart.getSize().getHeight());
			}
		});
		return button;
	}
	protected JComponent buildAnalysesPart() {	
		String[] formatter = {"name","type","indication","outcomeMeasure","includedDrugs","includedStudies","sampleSize"};
		return new EntityTablePanel(AbstractMetaAnalysis.class, d_pm.getAnalysesModel(), Arrays.asList(formatter), d_mainWindow, d_pm.getFactory());
	}

	@Override
	protected JPanel buildMeasurementsPanel() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout("fill:0:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.setOpaque(true);
		
		int row = 1;
		int width = 1;
		
		builder.addSeparator("Relative effect distributions", cc.xyw(1, row, width));
		row += 2;
		builder.add(AuxComponentFactory.createHtmlField("Relative measurements: log odds-ratio or mean difference, with "
				+ d_pm.getBaseline().getLabel() +" as the common comparator."),cc.xy(1, row));
		row += 2;
		EnhancedTable table = EnhancedTable.createWithSorter(d_pm.getRelativeMeasurementTableModel());
		table.setDefaultRenderer(Distribution.class, new DistributionParameterCellRenderer());
		table.autoSizeColumns();
		builder.add(new TablePanel(table), cc.xy(1, row));
		row += 2;

		builder.addSeparator("Baseline effect distributions", cc.xyw(1, row, width));
		row += 2;
		builder.add(AuxComponentFactory.createHtmlField("Baseline measurements: log odds or mean for " + 
				d_pm.getBaseline().getLabel() + ". The method used to derive the assumed odds or mean are heuristic, "
				+ "and these values should be interpreted with care."), cc.xy(1, row));
		row += 2;
		EnhancedTable table2 = EnhancedTable.createWithSorter(d_pm.getBaselineMeasurementTableModel());
		table2.setDefaultRenderer(Distribution.class, new DistributionParameterCellRenderer());
		table2.autoSizeColumns();
		builder.add(new TablePanel(table2), cc.xy(1, row));
		row += 2;
		
		builder.addSeparator("Measurements", cc.xyw(1, row, width));
		row += 2;
		builder.add(AuxComponentFactory.createHtmlField("Measurements: incidence approximated with logit-Normal distribution, or continuous variables approximated with a Normal distribution."),
				cc.xy(1, row));
		row += 2;
		EnhancedTable table3 = EnhancedTable.createWithSorter(d_pm.getMeasurementTableModel());
		table3.setDefaultRenderer(Distribution.class, new DistributionQuantileCellRenderer());
		table3.autoSizeColumns();
		builder.add(new TablePanel(table3), cc.xy(1, row));
		row += 2;
		
		return builder.getPanel();
	}
}
