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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.BenefitRiskPresentation;
import org.drugis.addis.presentation.StudyBenefitRiskPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.FileSaveDialog;
import org.drugis.common.gui.ImageExporter;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.OneWayObjectFormat;
import org.drugis.common.gui.ViewBuilder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ConverterFactory;
import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fi.smaa.jsmaa.gui.components.CentralWeightsCellRenderer;
import fi.smaa.jsmaa.gui.components.ResultsCellColorRenderer;
import fi.smaa.jsmaa.gui.components.ResultsTable;
import fi.smaa.jsmaa.gui.presentation.PreferencePresentationModel;
import fi.smaa.jsmaa.gui.views.ResultsView;

public abstract class AbstractBenefitRiskView<PresentationType extends BenefitRiskPresentation<?, ?>> implements ViewBuilder {

	protected static final String WAITING_MESSAGE = "Please wait while the sub-analyses run";
	private static ChartPanel findChartPanel(JComponent viewPanel) {
		for (Component c : viewPanel.getComponents()) {
			if (c instanceof ChartPanel) {
				return (ChartPanel)c;
			}
		}
		return null;
	}

	protected PresentationType d_pm;
	protected Main d_main;

	public AbstractBenefitRiskView(PresentationType model, Main main) {
		d_pm = model;
		d_main = main;
	}

	protected abstract JComponent buildMeasurementsPart();

	protected JPanel buildOverviewPart() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout("right:pref, 3dlu, left:pref:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		
		builder.addLabel("ID:", cc.xy(1, 1));
		builder.add(BasicComponentFactory.createLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_NAME)), cc.xy(3, 1));
		
		builder.addLabel("Indication:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_INDICATION), new OneWayObjectFormat()), 
				cc.xy(3, 3));
		
		int row = 3;
		if (d_pm instanceof StudyBenefitRiskPresentation) {
			row += 2;
			LayoutUtil.addRow(layout);
			builder.addLabel("Study:", cc.xy(1, row));
			builder.add(BasicComponentFactory.createLabel(d_pm.getModel(StudyBenefitRiskAnalysis.PROPERTY_STUDY), new OneWayObjectFormat()), 
					cc.xy(3, row));
			
		}
		
		row += 2;
		builder.addLabel("Criteria:", cc.xy(1, row));
		builder.add(BasicComponentFactory.createLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_OUTCOMEMEASURES), new OneWayObjectFormat()), 
				cc.xy(3, row));
		
		row += 2;
		builder.addLabel("Alternatives:", cc.xy(1, row));
		builder.add(AuxComponentFactory.createTextArea(ConverterFactory.createStringConverter(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_ALTERNATIVES), new OneWayObjectFormat()), false), 
				cc.xy(3, row));
		
		return builder.getPanel();	
	}

	protected abstract JComponent buildPreferencesPart();

	protected JButton createSaveImageButton(final JComponent chart) {
		JButton button = new JButton("Save Image");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ImageExporter.writeImage(d_main, chart, (int) chart.getSize().getWidth(), (int) chart.getSize().getHeight());
			}
		});
		return button;
	}

	private JButton createExportButton() {
		JButton expButton = new JButton("Export model to JSMAA");
		expButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new FileSaveDialog(d_main, "jsmaa", "JSMAA") {
					@Override
					public void doAction(String path, String extension) {
						d_pm.saveSmaa(path);
					}
				};
			}
		});
		return expButton;
	}

	final class PreferencesBuilder implements ViewBuilder {

		public JComponent buildPanel() {
			FormLayout layout = new FormLayout("pref:grow:fill", "p, 3dlu, p");
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();

			final JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			builder.add(panel, cc.xy(1, 1));

			ButtonBarBuilder2 bbuilder = new ButtonBarBuilder2();
			bbuilder.addButton(createExportButton());
			JPanel buttonBar = bbuilder.getPanel();
			builder.add(buttonBar, cc.xy(1, 3));

			d_pm.getPreferencePresentationModel().addPropertyChangeListener(
					PreferencePresentationModel.PREFERENCE_TYPE,
					new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent arg0) {
							rebuildPanel(panel);
						}			
					});
			rebuildPanel(panel);

			return builder.getPanel();
		}

		private void rebuildPanel(final JPanel panel) {
			((JPanel)panel.getParent()).setVisible(false);
			panel.removeAll();
			JComponent prefPanel = buildPreferenceInformationView(d_pm.getPreferencePresentationModel(), d_pm);
			panel.add(prefPanel, BorderLayout.CENTER);
			((JPanel)panel.getParent()).setVisible(true);
		}

	}

	class RankAcceptabilitiesBuilder implements ViewBuilder {
	
		public JComponent buildPanel() {
			ResultsTable table = new ResultsTable(d_pm.getRankAcceptabilitiesTableModel());
			table.setDefaultRenderer(Object.class, new ResultsCellColorRenderer(1.0));			
			
			final JFreeChart chart = ChartFactory.createStackedBarChart(
			        "Rank Acceptability", "Alternative", "Rank Acceptability",
			        d_pm.getRankAcceptabilityDataSet(), PlotOrientation.VERTICAL, true, true, false);
			chart.addSubtitle(new org.jfree.chart.title.ShortTextTitle("Rank 1 is best, rank N is worst."));
	
			JPanel panel = new JPanel(new BorderLayout());
			fi.smaa.jsmaa.gui.views.ResultsView view = new fi.smaa.jsmaa.gui.views.ResultsView(d_main, table, chart, "");
			panel.add(d_pm.getSmaaSimulationProgressBar(), BorderLayout.NORTH);
			JComponent viewPanel = view.buildPanel();
			panel.add(viewPanel, BorderLayout.CENTER);
			
			ButtonBarBuilder2 bbuilder = new ButtonBarBuilder2();
			bbuilder.addButton(createSaveImageButton(findChartPanel(viewPanel)));
			panel.add(bbuilder.getPanel(), BorderLayout.SOUTH);
	
			return panel;
		}
	
	}

	class CentralWeightsBuilder implements ViewBuilder {
		public JComponent buildPanel() {
			final JFreeChart chart = ChartFactory.createLineChart(
			        "", "Criterion", "Central Weight",
			        d_pm.getCentralWeightsDataSet(), PlotOrientation.VERTICAL, true, true, false);
			LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, true);
			chart.getCategoryPlot().setRenderer(renderer);
			ResultsTable table = new ResultsTable(d_pm.getCentralWeightsTableModel());
			table.setDefaultRenderer(Object.class, new CentralWeightsCellRenderer(1.0));
			JComponent viewPanel = new ResultsView(d_main, table, chart, "").buildPanel();
			
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(viewPanel, BorderLayout.CENTER);
			
			ButtonBarBuilder2 bbuilder = new ButtonBarBuilder2();
			bbuilder.addButton(createSaveImageButton(findChartPanel(viewPanel)));
			panel.add(bbuilder.getPanel(), BorderLayout.SOUTH);
	
			return panel; 
		}
	}

	protected abstract JComponent buildPreferenceInformationView(
			PreferencePresentationModel preferencePresentationModel,
			PresentationType pm);

	protected abstract JComponent buildRankAcceptabilitiesPart();

	protected abstract JComponent buildCentralWeightsPart();
}
