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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.StudyGraph;
import org.drugis.addis.gui.components.AddisTabbedPane;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.components.ScrollableJPanel;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.gui.renderer.NetworkRelativeEffectTableCellRenderer;
import org.drugis.addis.gui.renderer.SummaryCellRenderer;
import org.drugis.addis.gui.util.TableCopyHandler;
import org.drugis.addis.presentation.NetworkMetaAnalysisPresentation;
import org.drugis.addis.presentation.mcmc.MCMCResultsAvailableModel;
import org.drugis.addis.util.EmpiricalDensityDataset;
import org.drugis.addis.util.EmpiricalDensityDataset.PlotParameter;
import org.drugis.addis.util.MCMCResultsMemoryUsageModel;
import org.drugis.common.gui.FileSaveDialog;
import org.drugis.common.gui.ImageExporter;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.threading.status.TaskTerminatedModel;
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.MCMCResultsEvent;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.gui.MainWindow;
import org.drugis.mtc.gui.SimulationComponentFactory;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.presentation.ConsistencyWrapper;
import org.drugis.mtc.presentation.InconsistencyWrapper;
import org.drugis.mtc.presentation.MTCModelWrapper;
import org.drugis.mtc.presentation.NodeSplitWrapper;
import org.drugis.mtc.presentation.SimulationConsistencyWrapper;
import org.drugis.mtc.presentation.SimulationNodeSplitWrapper;
import org.drugis.mtc.presentation.results.NetworkInconsistencyFactorsTableModel;
import org.drugis.mtc.presentation.results.NetworkRelativeEffectTableModel;
import org.drugis.mtc.presentation.results.NetworkVarianceTableModel;
import org.drugis.mtc.presentation.results.NodeSplitResultsTableModel;
import org.drugis.mtc.summary.NodeSplitPValueSummary;
import org.drugis.mtc.summary.QuantileSummary;
import org.drugis.mtc.summary.Summary;
import org.drugis.mtc.util.MCMCResultsWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class NetworkMetaAnalysisView extends AbstractMetaAnalysisView<NetworkMetaAnalysisPresentation>
implements ViewBuilder {
	private static final String MEMORY_USAGE_TAB_TITLE = "Memory Usage";
	private static final String NODE_SPLIT_TAB_TITLE = "Node Split";
	private static final String INCONSISTENCY_TAB_TITLE = "Inconsistency";
	private static final String CONSISTENCY_TAB_TITLE = "Consistency";
	private static final String OVERVIEW_TAB_TITLE = "Overview";

	private final AddisWindow d_mainWindow;

	public NetworkMetaAnalysisView(final NetworkMetaAnalysisPresentation model, final AddisWindow mainWindow) {
		super(model, mainWindow);
		d_mainWindow = mainWindow;
	}

	public JComponent buildOverviewTab() {
		final FormLayout layout = new FormLayout(
				"fill:0:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p" +
				", 3dlu, p"); // Memory usage part
		final PanelBuilder builder = new PanelBuilder(layout, new ScrollableJPanel());
		builder.setDefaultDialogBorder();

		final CellConstraints cc = new CellConstraints();

		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(NetworkMetaAnalysis.class).getSingularCapitalized(), cc.xy(1, 1));
		builder.add(buildPropertiesPart(), cc.xy(1, 3));

		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Study.class).getPlural(), cc.xy(1, 5));
		builder.add(buildStudiesPart(), cc.xy(1, 7));

		builder.addSeparator("Evidence network", cc.xy(1, 9));
		builder.add(buildStudyGraphPart(), cc.xy(1, 11));

		return builder.getPanel();
	}

	private JComponent buildMemoryUsageTab() {
		final CellConstraints cc = new CellConstraints();

		final FormLayout header = new FormLayout("fill:0:grow", "p");
		final PanelBuilder builderheader = new PanelBuilder(header);

		final FormLayout layout = new FormLayout(
				"left:0:grow, 3dlu, left:pref, 3dlu, pref, 3dlu, pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		final int colSpan = layout.getColumnCount();
		final PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		int row = 1;

		builder.addSeparator("Memory usage", cc.xyw(1, row, colSpan));
		row += 2;

		builderheader.add(AuxComponentFactory.createHtmlField("Network meta-analysis results can use quite a bit of memory. Here, the results of " +
				"analyses may be discarded to save memory. The aggregate-level results will be maintained. However, after " +
		"discarding the results, it will no longer be possible to display the convergence plots."), cc.xy(1,1));

		builder.add(builderheader.getPanel(), cc.xyw(1, row, colSpan));
		row += 2;

		row = buildMemoryUsage(d_pm.getConsistencyModel(), "Consistency model", builder, layout, row);
		row = buildMemoryUsage(d_pm.getInconsistencyModel(), "Inconsistency model", builder, layout, row);
		builder.addSeparator("", cc.xyw(1, row, 3));
		row += 2;
		for(final BasicParameter p : d_pm.getSplitParameters()) {
			row = buildMemoryUsage(d_pm.getNodeSplitModel(p), "<html>Node Split model:<br />&nbsp;&nbsp;&nbsp; Parameter " + p.getName() + "</html>", builder, layout, row);
			LayoutUtil.addRow(layout);
			builder.addSeparator("", cc.xyw(1, row, 3));
			row += 2;
		}

		return builder.getPanel();
	}

	private int buildMemoryUsage(final MTCModelWrapper<TreatmentDefinition> model, final String name, final PanelBuilder builder, final FormLayout layout, final int row) {
		final CellConstraints cc = new CellConstraints();
		if(model.isSaved()) {
			LayoutUtil.addRow(layout);
			builder.add(new JLabel(name), cc.xy(1, row));
			builder.add(new JLabel("N/A"), cc.xyw(3, row, 7));
			return row + 2;
		} else {
			final MixedTreatmentComparison mtc = model.getModel();

			final MCMCResultsMemoryUsageModel memoryModel = new MCMCResultsMemoryUsageModel(mtc.getResults());
			final JLabel memory = AuxComponentFactory.createAutoWrapLabel(memoryModel);

			final MCMCResultsAvailableModel resultsAvailableModel = new MCMCResultsAvailableModel(mtc.getResults());
			final TaskTerminatedModel modelTerminated = new TaskTerminatedModel(mtc.getActivityTask());

			final JButton clearButton = new JButton(Main.IMAGELOADER.getIcon(FileNames.ICON_DELETE));
			clearButton.setToolTipText("Clear results");
			final BooleanAndModel modelFinishedAndResults = new BooleanAndModel(Arrays.<ValueModel>asList(modelTerminated, resultsAvailableModel));
			Bindings.bind(clearButton, "enabled",  modelFinishedAndResults);


			final JButton saveButton = new JButton(Main.IMAGELOADER.getIcon(FileNames.ICON_SAVEFILE));
			saveButton.setToolTipText("Save to R-file");
			Bindings.bind(saveButton, "enabled", modelFinishedAndResults);
			saveButton.addActionListener(buildRButtonActionListener(mtc));

			clearButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					mtc.getResults().clear();
					// FIXME: change MCMC contract so clear fires a MCMCResultsClearedEvent
					memoryModel.resultsEvent(new MCMCResultsEvent(mtc.getResults()));
					resultsAvailableModel.resultsEvent(new MCMCResultsEvent(mtc.getResults()));
				}
			});

			LayoutUtil.addRow(layout);
			builder.add(new JLabel(name), cc.xy(1, row));
			builder.add(memory, cc.xy(3, row));
			builder.add(clearButton, cc.xy(5, row));
			builder.add(saveButton, cc.xy(7, row));
			return row + 2;
		}
	}

	private ActionListener buildRButtonActionListener(final MCMCModel model) {
		return new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final FileSaveDialog dialog = new FileSaveDialog(d_mainWindow, "R", "R files") {
					@Override
					public void doAction(final String path, final String extension) {
						try {
							final MCMCResultsWriter writer = new MCMCResultsWriter(model.getResults());
							writer.write(new FileOutputStream(path));
						} catch (final FileNotFoundException e) {
							throw new RuntimeException(e);
						} catch (final IOException e) {
							throw new RuntimeException(e);
						}
					}
				};
				dialog.saveActions();
			}
		};
	}

	private JComponent buildInconsistencyTab() {
		final FormLayout layout = new FormLayout("pref, 3dlu, pref, 3dlu, fill:0:grow",
		"p, 3dlu, p, 3dlu, p, 5dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		final PanelBuilder builder = new PanelBuilder(layout, new ScrollableJPanel());
		builder.setDefaultDialogBorder();
		final CellConstraints cc = new CellConstraints();

		int row = 1;
		final int colSpan = 5;
		builder.addSeparator("Results - network inconsistency model", cc.xyw(1, row, colSpan));

		row += 2;

		final InconsistencyWrapper<TreatmentDefinition> inconsistencyModel = d_pm.getInconsistencyModel();

		final JPanel simulationControls = SimulationComponentFactory.createSimulationControls(d_pm.getWrappedModel(inconsistencyModel), d_mainWindow, false, AuxComponentFactory.COLOR_NOTE, d_mainWindow.getReloadRightPanelAction(INCONSISTENCY_TAB_TITLE));
		builder.add(simulationControls, cc.xyw(3, row, 3));

		row += 2;

		final String inconsistencyText = "In network meta-analysis, because of the more complex evidence structure, we can assess <em>inconsistency</em> of evidence, " +
				"in addition to <em>heterogeneity</em> within a comparison. Whereas heterogeneity represents between-study variation in the measured relative effect" +
				"of a pair of treatments, inconsistency can only occur when a treatment C has a different effect when it is compared with A or B (i.e., studies comparing " +
				"A and C are systematically different from studies comparing B and C). Thus, inconsistency may even occur with normal meta-analysis, but can only be detected " +
				"using a network meta-analysis, and then only when there are closed loops in the evidence structure. For more information about assessing inconsistency, see " +
				" G. Lu and A. E. Ades (2006), <em>Assessing evidence inconsistency in mixed treatment comparisons</em>, Journal of the American Statistical Association, " +
				"101(474): 447-459. <a href=\"http://dx.doi.org/10.1198/016214505000001302\">doi:10.1198/016214505000001302</a>.";
		final JComponent inconsistencyNote = AuxComponentFactory.createHtmlField(inconsistencyText);

		builder.add(inconsistencyNote, cc.xyw(1, row, colSpan));
		row += 2;

		final TablePanel relativeEffectsTablePanel = createNetworkTablePanel(inconsistencyModel);
		builder.addSeparator("Network Meta-Analysis (Inconsistency Model)", cc.xyw(1, row, colSpan));
		row += 2;
		builder.add(relativeEffectsTablePanel, cc.xyw(1, row, colSpan));
		row += 2;

		final NetworkInconsistencyFactorsTableModel inconsistencyFactorsTableModel = new NetworkInconsistencyFactorsTableModel(d_pm.getInconsistencyModel(), d_pm.getWrappedModel(d_pm.getInconsistencyModel()).isModelConstructed());
		final EnhancedTable table = new EnhancedTable(inconsistencyFactorsTableModel, 300);
		table.setDefaultRenderer(Summary.class, new SummaryCellRenderer(false));
		final TablePanel inconsistencyFactorsTablePanel = new TablePanel(table);

		d_pm.getWrappedModel(inconsistencyModel).isModelConstructed().addValueChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				if (event.getNewValue().equals(true)) {
					final Runnable r = new Runnable() {
						@Override
						public void run() {
							inconsistencyFactorsTablePanel.doLayout();
						}
					};
					SwingUtilities.invokeLater(r);
				}
			}
		});

		builder.addSeparator("Inconsistency Factors", cc.xyw(1, row, colSpan));
		row += 2;
		builder.add(inconsistencyFactorsTablePanel, cc.xyw(1, row, colSpan));
		row += 2;

		final NetworkVarianceTableModel varianceTableModel = new NetworkVarianceTableModel(inconsistencyModel);
		final EnhancedTable varianceTable = new EnhancedTable(varianceTableModel, 300);
		varianceTable.setDefaultRenderer(QuantileSummary.class, new SummaryCellRenderer());
		final TablePanel varianceTablePanel = new TablePanel(varianceTable);

		builder.addSeparator("Variance Calculation", cc.xyw(1, row, colSpan));
		row += 2;
		builder.add(varianceTablePanel, cc.xyw(1, row, colSpan));
		row += 2;

		return builder.getPanel();
	}


	private JComponent buildConsistencyTab() {
		final FormLayout layout = new FormLayout("pref, 3dlu, fill:0:grow",
		"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		final PanelBuilder builder = new PanelBuilder(layout, new ScrollableJPanel());
		builder.setDefaultDialogBorder();
		final CellConstraints cc =  new CellConstraints();

		int row = 1;
		final int colSpan = 3;
		builder.addSeparator("Results - network consistency model", cc.xyw(1, row, colSpan));

		row += 2;
		final ConsistencyWrapper<TreatmentDefinition> consistencyModel = d_pm.getConsistencyModel();
		final JPanel simulationControls = SimulationComponentFactory.createSimulationControls(
				d_pm.getWrappedModel(consistencyModel), d_mainWindow, false,
				AuxComponentFactory.COLOR_NOTE, d_mainWindow.getReloadRightPanelAction(CONSISTENCY_TAB_TITLE));
		builder.add(simulationControls, cc.xyw(1, row, 3));

		row += 2;
		final String consistencyText = "If there is no relevant inconsistency in the evidence, a consistency model can be used to draw " +
				"conclusions about the relative effect of the included treatments. Using normal meta-analysis, we could only get a " +
				"subset of the confidence intervals for relative effects we derive using network meta-analysis. " +
				"Network meta-analysis gives a consistent, integrated picture of the relative effects. " +
				"However, given such a consistent set of relative effect estimates, it may still be difficult to draw " +
				"conclusions on a potentially large set of treatments. Luckily, the Bayesian approach allows us to do " +
				"even more with the data, and can be used to estimate the probability that, given the priors and the data, " +
				"each of the treatments is the best, the second best, etc. This is given below in the rank probability plot. " +
				"Rank probabilities sum to one, both within a rank over treatments and within a treatment over ranks.";
		final JComponent consistencyNote = AuxComponentFactory.createHtmlField(consistencyText);
		builder.add(consistencyNote, cc.xyw(1, row, colSpan));

		final TablePanel relativeEffectsTablePanel = createNetworkTablePanel(consistencyModel);

		row += 2;
		builder.addSeparator("Network Meta-Analysis (Consistency Model)", cc.xyw(1, row, colSpan));
		row += 2;
		builder.add(relativeEffectsTablePanel, cc.xyw(1, row, colSpan));
		row += 2;

		builder.add(createRankProbChart(), cc.xyw(1, row, colSpan));
		row += 2;

		builder.add(createRankProbTable(), cc.xyw(1, row, colSpan));
		row += 2;

		builder.addSeparator("Variance Parameters", cc.xyw(1, row, colSpan));
		row += 2;
		final EnhancedTable varianceTable = new EnhancedTable(new NetworkVarianceTableModel(consistencyModel), 300);
		varianceTable.setDefaultRenderer(QuantileSummary.class, new SummaryCellRenderer());
		builder.add(new TablePanel(varianceTable), cc.xyw(1, row, colSpan));

		return builder.getPanel();
	}

	private JComponent buildNodeSplitTab() {
		final FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:0:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p");
		final CellConstraints cc = new CellConstraints();
		final PanelBuilder builder = new PanelBuilder(layout, new ScrollableJPanel());
		builder.setDefaultDialogBorder();
		final int colSpan = 3;

		int row = 1;
		builder.addSeparator("Results - node-splitting analysis of inconsistency", cc.xyw(1, row, colSpan));
		row += 2;

		builder.add(
				AuxComponentFactory.createHtmlField("<p>Node-splitting analysis is an alternative method to assess inconsistency in network meta-analysis. " +
						"It assesses whether direct and indirect evidence on a specific node (the split node) are in agreement. " +
						"While the results are easier to interpret, it requires a separate model to be run for each node to be split. " +
						"</p><p>The table below allows you to compare the estimated quantiles for the direct and indirect evidence as well " +
						"as the combined evidence. In addition a P-value is shown; a large value indicates no significant inconsistency was found. " +
						"See S. Dias et al. (2010), <em>Checking consistency in mixed treatment comparison meta-analysis</em>, " +
						"Statistics in Medicine, 29(7-8, Sp. Iss. SI): 932-944. <a href=\"http://dx.doi.org/10.1002/sim.3767\">doi:10.1002/sim.3767</a>.</p>"),
				cc.xyw(1, row, colSpan));
		row += 2;

		builder.add(buildNodeSplitControls(), cc.xyw(1, row, colSpan));
		row += 2;

		builder.add(buildNodeSplitResultsTable(), cc.xyw(1, row, colSpan));

		for (final BasicParameter p : d_pm.getSplitParameters()) {


			LayoutUtil.addRow(layout);
			row += 2;
			final NodeSplitWrapper<TreatmentDefinition> model = d_pm.getNodeSplitModel(p);

			final JPanel simulationControls = SimulationComponentFactory.createSimulationControls(d_pm.getWrappedModel(model), d_mainWindow, true, AuxComponentFactory.COLOR_NOTE, d_mainWindow.getReloadRightPanelAction(NODE_SPLIT_TAB_TITLE));
			builder.add(simulationControls, cc.xyw(1, row, 3));


			LayoutUtil.addRow(layout);
			row += 2;
			builder.add(makeNodeSplitDensityChart(p), cc.xyw(1, row, colSpan));

			LayoutUtil.addRow(layout);
			row += 2;
		}

		return builder.getPanel();
	}

	private JComponent buildNodeSplitControls() {
		final FormLayout layout = new FormLayout(
				"fill:0:grow, 3dlu, pref",
				"p");
		final CellConstraints cc = new CellConstraints();
		final PanelBuilder panelBuilder = new PanelBuilder(layout);

		final JButton resetAll = new JButton(MainWindow.IMAGELOADER.getIcon(org.drugis.mtc.gui.FileNames.ICON_REDO));
		resetAll.setToolTipText("Reset all simulations");
		resetAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				d_pm.getBean().resetNodeSplitModels();
				if(d_mainWindow instanceof AddisWindow) {
					d_mainWindow.reloadRightPanel(NODE_SPLIT_TAB_TITLE);
				}
			}
		});

		final JButton runAll = new JButton(MainWindow.IMAGELOADER.getIcon(org.drugis.mtc.gui.FileNames.ICON_RUN));
		runAll.setText("Run all node-split models");
		runAll.setToolTipText("Run all simulations");
		final List<Task> tasks = new ArrayList<Task>();
		for (final BasicParameter p : d_pm.getSplitParameters()) {
			final NodeSplitWrapper<TreatmentDefinition> wrapper = d_pm.getNodeSplitModel(p);
			if (!wrapper.isSaved()) {
				tasks.add(wrapper.getModel().getActivityTask());
			}
		}
		runAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				ThreadHandler.getInstance().scheduleTasks(tasks);
			}
		});

		panelBuilder.add(resetAll, cc.xy(3, 1));
		panelBuilder.add(runAll,  cc.xy(1, 1));

		return panelBuilder.getPanel();
	}

	private JComponent buildNodeSplitResultsTable() {
		final NodeSplitResultsTableModel tableModel = d_pm.createNodeSplitResultsTableModel();

		final EnhancedTable table = EnhancedTable.createWithSorter(tableModel);
		table.setDefaultRenderer(QuantileSummary.class, new SummaryCellRenderer());
		table.setDefaultRenderer(NodeSplitPValueSummary.class, new SummaryCellRenderer());
		table.autoSizeColumns();
		return new TablePanel(table);
	}


	@Override
	public JComponent buildPanel() {
		final JTabbedPane tabbedPane = new AddisTabbedPane();
		tabbedPane.addTab(OVERVIEW_TAB_TITLE, buildOverviewTab());
		tabbedPane.addTab(CONSISTENCY_TAB_TITLE, buildConsistencyTab());
		tabbedPane.addTab(INCONSISTENCY_TAB_TITLE, buildInconsistencyTab());
		tabbedPane.addTab(NODE_SPLIT_TAB_TITLE, buildNodeSplitTab());
		tabbedPane.addTab(MEMORY_USAGE_TAB_TITLE, buildMemoryUsageTab());
		return tabbedPane;
	}

	private JComponent makeNodeSplitDensityChart(final BasicParameter p) {
		if (!(d_pm.getNodeSplitModel(p) instanceof SimulationNodeSplitWrapper)) {
			return new JLabel("Can not build density plot based on saved results.");
		}
		final SimulationNodeSplitWrapper<TreatmentDefinition> splitWrapper = (SimulationNodeSplitWrapper<TreatmentDefinition>) d_pm.getNodeSplitModel(p);
		XYDataset dataset;
		final MCMCResults splitResults = splitWrapper.getModel().getResults();
		if(d_pm.getConsistencyModel() instanceof SimulationConsistencyWrapper) {
			final SimulationConsistencyWrapper<TreatmentDefinition> consistencyWrapper = (SimulationConsistencyWrapper<TreatmentDefinition>) d_pm.getConsistencyModel();
			dataset = new EmpiricalDensityDataset(50, new PlotParameter(splitResults, splitWrapper.getDirectEffect()),
					new PlotParameter(splitResults, splitWrapper.getIndirectEffect()),
					new PlotParameter(consistencyWrapper.getModel().getResults(), p));
		} else {
			dataset = new EmpiricalDensityDataset(50, new PlotParameter(splitResults, splitWrapper.getDirectEffect()),
					new PlotParameter(splitResults, splitWrapper.getIndirectEffect()));
		}
		final JFreeChart chart = ChartFactory.createXYLineChart(
	            p.getName() + " density plot", "Relative Effect", "Density",
	            dataset, PlotOrientation.VERTICAL,
	            true, true, false
	        );

        return new ChartPanel(chart);
	}

	private JComponent createRankProbChart() {
		final CategoryDataset dataset = d_pm.getRankProbabilityDataset();
		final JFreeChart chart = ChartFactory.createBarChart("Rank Probability", "Treatment", "Probability",
						dataset, PlotOrientation.VERTICAL, true, true, false);
		chart.addSubtitle(new org.jfree.chart.title.ShortTextTitle(d_pm.getRankProbabilityRankChartNote()));

		final FormLayout layout = new FormLayout(
				"fill:0:grow",
				"p, 3dlu, p");
		final PanelBuilder builder = new PanelBuilder(layout);
		final CellConstraints cc =  new CellConstraints();

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setSize(chartPanel.getPreferredSize().width, chartPanel.getPreferredSize().height+1);
		chartPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

		builder.add(chartPanel, cc.xy(1, 1));

		final ButtonBarBuilder2 bbuilder = new ButtonBarBuilder2();
		bbuilder.addButton(createSaveImageButton(chart));
		builder.add(bbuilder.getPanel(), cc.xy(1, 3));

		return builder.getPanel();
	}

	private JComponent createRankProbTable() {
		final EnhancedTable table = EnhancedTable.createBare(d_pm.getRankProbabilityTableModel());
		table.setDefaultRenderer(Double.class, new SummaryCellRenderer());
		table.autoSizeColumns();
		return new TablePanel(table);
	}

	private JButton createSaveImageButton(final JFreeChart chart) {
		final JButton button = new JButton("Save Image");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				ImageExporter.writeImage(d_mainWindow, chart, 600, 400);
			}
		});
		return button;
	}

	private JButton createSaveDataButton() {
		final JButton button = new JButton("Open in GeMTC");
		button.setToolTipText("Open for analysis in drugis.org GeMTC");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				openGeMTC();
			}
		});
		return button;
	}

	private void openGeMTC() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final MainWindow geMTC = new MainWindow(d_pm.getNetwork());
				geMTC.setVisible(true);
			}
		});
	}

	@SuppressWarnings("serial")
	public JComponent buildStudyGraphPart() {
		final FormLayout layout = new FormLayout(
				"pref",
				"p, 3dlu, p");
		final PanelBuilder builder = new PanelBuilder(layout);
		final CellConstraints cc =  new CellConstraints();

		final StudyGraph panel = new StudyGraph(d_pm.getStudyGraphModel());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.layoutGraph();
		builder.add(panel, cc.xy(1, 1));

		final JButton saveBtn = new JButton("Save Image");
		saveBtn.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				panel.saveImage(d_mainWindow);
			}
		});
		final ButtonBarBuilder2 bbuilder = new ButtonBarBuilder2();
		bbuilder.addButton(saveBtn);
		bbuilder.addButton(createSaveDataButton());
		builder.add(bbuilder.getPanel(), cc.xy(1, 3));

		return builder.getPanel();
	}




	/**
	 * Make table of results (Cipriani et al., Lancet(2009), fig. 3, pp752).
	 * @param mtc Model for which to display results.
	 * @return A TablePanel
	 */
	private TablePanel createNetworkTablePanel(final MTCModelWrapper<TreatmentDefinition> mtc) {
		final JTable table = new JTable(new NetworkRelativeEffectTableModel<TreatmentDefinition>(d_pm.getIncludedDrugs(), mtc));
		table.setDefaultRenderer(Object.class, new NetworkRelativeEffectTableCellRenderer(!d_pm.isContinuous()));
		table.setTableHeader(null);
		setColumnWidths(table);
		TableCopyHandler.registerCopyAction(table);
		return new TablePanel(table);
	}

	private void setColumnWidths(final JTable table) {
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (final TableColumn c : Collections.list(table.getColumnModel().getColumns())) {
			c.setMinWidth(170);
			c.setPreferredWidth(170);
		}
	}
}
