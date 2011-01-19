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

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.components.BuildViewWhenReadyComponent;
import org.drugis.addis.gui.components.ScrollableJPanel;
import org.drugis.addis.presentation.AbstractBenefitRiskPresentation;
import org.drugis.addis.presentation.MetaBenefitRiskPresentation;
import org.drugis.addis.presentation.SMAAPresentation;
import org.drugis.common.gui.FileSaveDialog;
import org.drugis.common.gui.ImageExporter;
import org.drugis.common.gui.ViewBuilder;
import org.drugis.common.gui.task.TaskProgressBar;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fi.smaa.jsmaa.gui.components.CentralWeightsCellRenderer;
import fi.smaa.jsmaa.gui.components.ResultsCellColorRenderer;
import fi.smaa.jsmaa.gui.components.ResultsTable;
import fi.smaa.jsmaa.gui.presentation.PreferencePresentationModel;
import fi.smaa.jsmaa.gui.views.PreferenceInformationView;
import fi.smaa.jsmaa.gui.views.ResultsView;

public class SMAAView implements ViewBuilder  {
	protected static final String WAITING_MESSAGE = "Please wait while the sub-analyses run";

	@SuppressWarnings("unchecked")
	private SMAAPresentation d_pm;
	@SuppressWarnings("unchecked")
	private final AbstractBenefitRiskPresentation d_BRpm;
	private final AddisWindow d_mainWindow;

	@SuppressWarnings("unchecked")
	public SMAAView(AbstractBenefitRiskPresentation pm, AddisWindow mainWindow) {
		d_pm = pm.getSMAAPresentation();
		d_mainWindow = mainWindow;
		d_BRpm = pm;
	}

	public JComponent buildPanel() {
		CellConstraints cc=  new CellConstraints();
		FormLayout layout = new FormLayout(
				"left:0:grow",
				"p, 3dlu, p, " + // 1-3 
				"3dlu, p, 3dlu, p, " + // 4-7
				"3dlu, p, 3dlu, p, " + // 8-11 
				"3dlu, p, 3dlu, p "
				);
		PanelBuilder d_builder = new PanelBuilder(layout, new ScrollableJPanel());
		d_builder.setDefaultDialogBorder();
		//d_builder.setOpaque(true);
		
		d_builder.addSeparator("Preferences", cc.xy(1, 5));
		d_builder.add(buildPreferencesPart(), cc.xy(1, 7));
		
		d_builder.addSeparator("Rank Acceptabilities", cc.xy(1, 9));
		d_builder.add(buildRankAcceptabilitiesPart(), cc.xy(1, 11));
		
		//d_builder.addSeparator("Central Weights", cc.xy(1, 13));
		//d_builder.add(buildCentralWeightsPart(), cc.xy(1, 15));
		
		return d_builder.getPanel();	
	}
		
	private JButton createExportButton() {
		JButton expButton = new JButton("Export model to JSMAA");
		expButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new FileSaveDialog(d_mainWindow, "jsmaa", "JSMAA") {
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
			FormLayout layout = new FormLayout("center:pref", "p, 3dlu, p");
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setOpaque(true);

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
			JComponent prefPanel = buildPreferenceInformationView(d_pm.getPreferencePresentationModel(), d_BRpm);
			panel.add(prefPanel, BorderLayout.CENTER);
			((JPanel)panel.getParent()).setVisible(true);
		}

	}

	@SuppressWarnings("unchecked")
	public JComponent buildPreferenceInformationView(PreferencePresentationModel preferencePresentationModel, AbstractBenefitRiskPresentation<?,?> pm) {
		if (d_BRpm instanceof MetaBenefitRiskPresentation) {
			return new PreferenceInformationView(d_pm.getPreferencePresentationModel(),
					new ClinicalScaleRenderer((MetaBenefitRiskPresentation) d_BRpm, (SMAAPresentation<Drug, BenefitRiskAnalysis<Drug>>) d_pm)).buildPanel();
		} else {
			return new PreferenceInformationView(d_pm.getPreferencePresentationModel()).buildPanel();
		}
	}
	
	public JComponent buildPreferencesPart() {
		return createWaiter(new PreferencesBuilder());
	}

	private static ChartPanel findChartPanel(JComponent viewPanel) {
		for (Component c : viewPanel.getComponents()) {
			if (c instanceof ChartPanel) {
				return (ChartPanel)c;
			}
		}
		return null;
	}
	
	public JComponent buildRankAcceptabilitiesPart() {
		return createWaiter(new RankAcceptabilitiesBuilder());
	}

	class RankAcceptabilitiesBuilder implements ViewBuilder {
		
		public JComponent buildPanel() {
			FormLayout layout = new FormLayout("pref", "p, 3dlu, p, 3dlu, p");
			CellConstraints cc = new CellConstraints();
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();
			
			ResultsTable table = new ResultsTable(d_pm.getRankAcceptabilitiesTableModel());
			table.setDefaultRenderer(Object.class, new ResultsCellColorRenderer(1.0));			
			
			final JFreeChart chart = ChartFactory.createStackedBarChart(
			        "Rank Acceptability", "Alternative", "Rank Acceptability",
			        d_pm.getRankAcceptabilityDataSet(), PlotOrientation.VERTICAL, true, true, false);
			chart.addSubtitle(new org.jfree.chart.title.ShortTextTitle("Rank 1 is best, rank N is worst."));
			
			fi.smaa.jsmaa.gui.views.ResultsView view = new fi.smaa.jsmaa.gui.views.ResultsView(d_mainWindow, table, chart, "");
			
			builder.add(getSmaaSimulationProgressBar(), cc.xy(1, 1));
			
			builder.add(view.buildPanel(), cc.xy(1, 3));
			
			
			ButtonBarBuilder2 bbuilder = new ButtonBarBuilder2();
			bbuilder.addButton(createSaveImageButton(findChartPanel(view.buildPanel())));
			
			builder.add(bbuilder.getPanel(), cc.xy(1, 5));
			
			return builder.getPanel();
		}
	}

	private Component getSmaaSimulationProgressBar() {
		return new TaskProgressBar(d_pm.getTaskProgressModel());
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

	
	class CentralWeightsBuilder implements ViewBuilder {
		public JComponent buildPanel() {
			final JFreeChart chart = ChartFactory.createLineChart(
			        "", "Criterion", "Central Weight",
			        d_pm.getCentralWeightsDataSet(), PlotOrientation.VERTICAL, true, true, false);
			LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, true);
			chart.getCategoryPlot().setRenderer(renderer);
			ResultsTable table = new ResultsTable(d_pm.getCentralWeightsTableModel());
			table.setDefaultRenderer(Object.class, new CentralWeightsCellRenderer(1.0));
			JComponent viewPanel = new ResultsView(d_mainWindow, table, chart, "").buildPanel();
			
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(viewPanel, BorderLayout.CENTER);
			
			ButtonBarBuilder2 bbuilder = new ButtonBarBuilder2();
			bbuilder.addButton(createSaveImageButton(findChartPanel(viewPanel)));
			panel.add(bbuilder.getPanel(), BorderLayout.SOUTH);
	
			return panel; 
		}
	}

	public JComponent buildCentralWeightsPart() {
		return createWaiter(new CentralWeightsBuilder());
	}
	
	@SuppressWarnings("unchecked")
	protected BuildViewWhenReadyComponent createWaiter(ViewBuilder builder) {
		return new BuildViewWhenReadyComponent(builder, d_BRpm.getSMAAPresentation().getInitializedModel(), WAITING_MESSAGE);
	}
}
