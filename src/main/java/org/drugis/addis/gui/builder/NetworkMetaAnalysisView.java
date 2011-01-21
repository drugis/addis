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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.ConvergencePlotsDialog;
import org.drugis.addis.gui.NetworkMetaAnalysisTablePanel;
import org.drugis.addis.gui.StudyGraph;
import org.drugis.addis.gui.components.AddisTabbedPane;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.components.ScrollableJPanel;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.presentation.ConvergenceDiagnosticTableModel;
import org.drugis.addis.presentation.NetworkInconsistencyFactorsTableModel;
import org.drugis.addis.presentation.NetworkMetaAnalysisPresentation;
import org.drugis.addis.presentation.NetworkTableModel;
import org.drugis.addis.presentation.NetworkVarianceTableModel;
import org.drugis.addis.presentation.NodeSplitResultsTableModel;
import org.drugis.addis.presentation.SummaryCellRenderer;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.mcmc.MCMCResultsAvailableModel;
import org.drugis.addis.util.EmpiricalDensityDataset;
import org.drugis.addis.util.MCMCResultsMemoryUsageModel;
import org.drugis.addis.util.EmpiricalDensityDataset.PlotParameter;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.FileSaveDialog;
import org.drugis.common.gui.ImageExporter;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;
import org.drugis.common.gui.task.TaskProgressBar;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.drugis.mtc.BasicParameter;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MCMCResultsEvent;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.NodeSplitModel;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.NodeSplitPValueSummary;
import org.drugis.mtc.summary.QuantileSummary;
import org.drugis.mtc.util.MCMCResultsWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class NetworkMetaAnalysisView extends AbstractMetaAnalysisView<NetworkMetaAnalysisPresentation>
implements ViewBuilder {
	private static final String CONVERGENCE_TEXT = "<p>Convergence is assessed using the Brooks-Gelman-Rubin method. " +
			"This method compares within-chain and between-chain variance to calculate the <em>Potential Scale Reduction Factor</em> " +
			"(PSRF). A PSRF close to one indicates approximate convergence has been reached. See S.P. Brooks and A. Gelman (1998), " +
			"<em>General methods for monitoring convergence of iterative simulations</em>, Journal of Computational and Graphical " +
			"Statistics, 7(4): 434-455. <a href=\"http://www.jstor.org/stable/1390675\">JSTOR 1390675</a>." +
			"</p><p>Double click a parameter in the table below to see the convergence plots.</p>";

	private static class AnalysisFinishedListener implements TaskListener {
		private final TablePanel[] d_tablePanels;

		public AnalysisFinishedListener(TablePanel[] tablePanels) {
			d_tablePanels = tablePanels;
		}

		public void taskEvent(TaskEvent event) {
			if (event.getType() == EventType.TASK_FINISHED) {
				Runnable r = new Runnable() {
					public void run() {
						for (TablePanel tablePanel : d_tablePanels) {
							tablePanel.doLayout();
						}
					}
				};
				SwingUtilities.invokeLater(r);
			}
		}
	}
	
	private final AddisWindow d_mainWindow;
	
	public NetworkMetaAnalysisView(NetworkMetaAnalysisPresentation model, AddisWindow mainWindow) {
		super(model, mainWindow);
		d_mainWindow = mainWindow;
		d_pm.startModels();
	}
	
	public JComponent buildOverviewTab() {
		final FormLayout layout = new FormLayout(
				"fill:0:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p" +
				", 3dlu, p"); // Memory usage part
		PanelBuilder builder = new PanelBuilder(layout, new ScrollableJPanel());
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();

		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(NetworkMetaAnalysis.class).getSingularCapitalized(), cc.xy(1, 1));
		builder.add(buildPropertiesPart(), cc.xy(1, 3));

		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Study.class).getPlural(), cc.xy(1, 5));
		builder.add(buildStudiesPart(), cc.xy(1, 7));

		builder.addSeparator("Evidence network", cc.xy(1, 9));
		builder.add(buildStudyGraphPart(), cc.xy(1, 11));
		
		return builder.getPanel();
	}
	
	private JComponent buildMemoryUsageTab() {
		CellConstraints cc = new CellConstraints();
		
		FormLayout header = new FormLayout("fill:0:grow", "p");
		PanelBuilder builderheader = new PanelBuilder(header);
		
		FormLayout layout = new FormLayout(
				"3dlu, left:0:grow, 3dlu, left:pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu",
				"3dlu, p, 3dlu, p"
				);
		PanelBuilder builder = new PanelBuilder(layout);

		int row = 2;
		builder.addSeparator("Memory usage", cc.xyw(2, row, 7));
		row += 2;
		
		builderheader.add(AuxComponentFactory.createHtmlField("Network meta-analysis results can use quite a bit of memory. Here, the results of " +
				"analyses may be discarded to save memory. The aggregate-level results will be maintained. However, after " +
		"discarding the results, it will no longer be possible to display the convergence plots."), cc.xy(1,1));
		
		builder.add(builderheader.getPanel(), cc.xyw(2, row, 7));
		
		LayoutUtil.addRow(builder.getLayout());
		row += 2;

		row = buildMemoryUsage(d_pm.getConsistencyModel(), "Consistency model", builder, layout, row);
		row = buildMemoryUsage(d_pm.getInconsistencyModel(), "Inconsistency model", builder, layout, row);
		builder.addSeparator("", cc.xyw(2, row-1, 3));
		for(BasicParameter p : d_pm.getSplitParameters()) {
			row = buildMemoryUsage(d_pm.getNodeSplitModel(p), "<html>Node Split model:<br />&nbsp;&nbsp;&nbsp; Parameter " + p.getName() + "</html>", builder, layout, row);
			builder.addSeparator("", cc.xyw(2, row-1, 3));
		}
		
		return builder.getPanel();
	}

	private int buildMemoryUsage(final MCMCModel model, String name, PanelBuilder builder, FormLayout layout, int row) {
		LayoutUtil.addRow(layout);
		row += 2;
		CellConstraints cc = new CellConstraints();
		
		final MCMCResultsMemoryUsageModel memoryModel = new MCMCResultsMemoryUsageModel(model.getResults());
		JLabel memory = AuxComponentFactory.createAutoWrapLabel(memoryModel);
		builder.add(new JLabel(name), cc.xy(2, row));
		
		final MCMCResultsAvailableModel resultsAvailableModel = new MCMCResultsAvailableModel(model.getResults());
		
		builder.add(memory, cc.xy(4, row));
		JButton clearButton = new JButton(ImageLoader.getIcon(FileNames.ICON_DELETE));
		clearButton.setToolTipText("Clear results");
		Bindings.bind(clearButton, "enabled", resultsAvailableModel);
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.getResults().clear();
				// FIXME: change MCMC contract so clear fires a MCMCResultsClearedEvent
				memoryModel.resultsEvent(new MCMCResultsEvent(model.getResults()));
				resultsAvailableModel.resultsEvent(new MCMCResultsEvent(model.getResults()));
			}
		});
		builder.add(clearButton, cc.xy(6, row));
		final JButton saveButton = new JButton(ImageLoader.getIcon(FileNames.ICON_SAVEFILE));
		saveButton.setToolTipText("Save to R-file");
		Bindings.bind(saveButton, "enabled", resultsAvailableModel);
		saveButton.addActionListener(buildRButtonActionListener(model));
		builder.add(saveButton, cc.xy(8, row));
		return row;
	}

	private ActionListener buildRButtonActionListener(final MCMCModel model) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new FileSaveDialog(d_mainWindow, "R", "R files") {
					@Override
					public void doAction(String path, String extension) {
						try {
							MCMCResultsWriter writer = new MCMCResultsWriter(model.getResults());
							writer.write(new FileOutputStream(path));
						} catch (FileNotFoundException e) {
							throw new RuntimeException(e);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				};
				
			}
		};
	}
	
	private JComponent buildInconsistencyTab() {
		FormLayout layout = new FormLayout("3dlu, fill:0:grow, 3dlu",
		"3dlu, p, 3dlu, p, 3dlu, p, 5dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu");
		PanelBuilder builder = new PanelBuilder(layout, new ScrollableJPanel());
		
		CellConstraints cc = new CellConstraints();
		int row = 2;
		builder.addSeparator("Results - network inconsistency model", cc.xy(2, row));
		row += 2;
		
		final InconsistencyModel inconsistencyModel = (InconsistencyModel) d_pm.getInconsistencyModel();
		JProgressBar incProgressBar = new TaskProgressBar(d_pm.getProgressModel(inconsistencyModel));
		builder.add(incProgressBar, cc.xy(2, row));
		row += 2;
		
		String inconsistencyText = "In network meta-analysis, because of the more complex evidence structure, we can assess <em>inconsistency</em> of evidence, " +
				"in addition to <em>heterogeneity</em> within a comparison. Whereas heterogeneity represents between-study variation in the measured relative effect" + 
				"of a pair of treatments, inconsistency can only occur when a treatment C has a different effect when it is compared with A or B (i.e., studies comparing " + 
				"A and C are systematically different from studies comparing B and C). Thus, inconsistency may even occur with normal meta-analysis, but can only be detected " + 
				"using a network meta-analysis, and then only when there are closed loops in the evidence structure. For more information about assessing inconsistency, see " + 
				" G. Lu and A. E. Ades (2006), <em>Assessing evidence inconsistency in mixed treatment comparisons</em>, Journal of the American Statistical Association, " + 
				"101(474): 447-459. <a href=\"http://dx.doi.org/10.1198/016214505000001302\">doi:10.1198/016214505000001302</a>.";
		JComponent inconsistencyNote = AuxComponentFactory.createHtmlField(inconsistencyText);
		
		builder.add(inconsistencyNote, cc.xy(2, row));
		row += 2;
		
		TablePanel inconsistencyTablePanel = createNetworkTablePanel(inconsistencyModel);
		builder.addSeparator("Network Meta-Analysis (Inconsistency Model)", cc.xy(2, row));
		row += 2;
		builder.add(inconsistencyTablePanel, cc.xy(2,row));
		row += 2;
		
		NetworkInconsistencyFactorsTableModel inconsistencyFactorsTableModel = new NetworkInconsistencyFactorsTableModel(
				d_pm, d_mainWindow.getPresentationModelFactory());
		EnhancedTable table = new EnhancedTable(inconsistencyFactorsTableModel, 300);
		final TablePanel inconsistencyFactorsTablePanel = new TablePanel(table);
		
		d_pm.getInconsistencyModelConstructedModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getNewValue().equals(true)) {
					Runnable r = new Runnable() {
						public void run() {
							inconsistencyFactorsTablePanel.doLayout();
							d_mainWindow.reloadRightPanel();
						}
					};
					SwingUtilities.invokeLater(r);
				}
			}
		});
		
		builder.addSeparator("Inconsistency Factors", cc.xy(2, row));
		row += 2;
		builder.add(inconsistencyFactorsTablePanel, cc.xy(2, row));
		row += 2;
		
		NetworkVarianceTableModel mixedComparisonTableModel = new NetworkVarianceTableModel(d_pm, inconsistencyModel);
		EnhancedTable mixedComparisontable = new EnhancedTable(mixedComparisonTableModel, 300);
		mixedComparisontable.setDefaultRenderer(QuantileSummary.class, new SummaryCellRenderer());
		final TablePanel mixedComparisonTablePanel = new TablePanel(mixedComparisontable);
		
		builder.addSeparator("Variance Calculation", cc.xy(2, row));
		row += 2;
		builder.add(mixedComparisonTablePanel, cc.xy(2, row));
		row += 2;
		
		inconsistencyModel.getActivityTask().addTaskListener(
				new AnalysisFinishedListener(new TablePanel[] {
						inconsistencyTablePanel, inconsistencyFactorsTablePanel
				})
			);
		
		builder.addSeparator("Convergence", cc.xy(2, row));
		row += 2;
		builder.add(AuxComponentFactory.createHtmlField(CONVERGENCE_TEXT), cc.xy(2, row));
		row += 2;
		builder.add(buildConvergenceTable(inconsistencyModel, d_pm.getInconsistencyModelConstructedModel()), cc.xy(2, row));
		row += 2;
		
		return builder.getPanel();
	}
	
	private JComponent buildConsistencyTab() {
		FormLayout layout = new FormLayout(	"3dlu, fill:0:grow, 3dlu",
		"3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu" );
		PanelBuilder builder = new PanelBuilder(layout, new ScrollableJPanel());
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator("Results - network consistency model", cc.xy(2, 2));
		
		final ConsistencyModel consistencyModel = d_pm.getConsistencyModel();
		JProgressBar conProgressBar = new TaskProgressBar(d_pm.getProgressModel(consistencyModel));
		builder.add(conProgressBar, cc.xy(2, 4));
		
		String consistencyText = "If there is no relevant inconsistency in the evidence, a consistency model can be used to draw " +
				"conclusions about the relative effect of the included treatments. Using normal meta-analysis, we could only get a " +
				"subset of the confidence intervals for relative effects we derive using network meta-analysis. " +
				"Network meta-analysis gives a consistent, integrated picture of the relative effects. " +
				"However, given such a consistent set of relative effect estimates, it may still be difficult to draw " +
				"conclusions on a potentially large set of treatments. Luckily, the Bayesian approach allows us to do " +
				"even more with the data, and can be used to estimate the probability that, given the priors and the data, " +
				"each of the treatments is the best, the second best, etc. This is given below in the rank probability plot. " +
				"Rank probabilities sum to one, both within a rank over treatments and within a treatment over ranks.";
		JComponent consistencyNote = AuxComponentFactory.createHtmlField(consistencyText);
		
		builder.add(consistencyNote, cc.xy(2, 6));
		
		TablePanel consistencyTablePanel = createNetworkTablePanel(consistencyModel);
		consistencyModel.getActivityTask().addTaskListener(
				new AnalysisFinishedListener(new TablePanel[] {consistencyTablePanel}));
		
		int row = 8;
		builder.addSeparator("Network Meta-Analysis (Consistency Model)", cc.xy(2, row));
		row += 2;
		builder.add(consistencyTablePanel, cc.xy(2, row));
		row += 2;
		
		builder.add(createRankProbChart(), cc.xy(2, row));
		row += 2;
		
		EnhancedTable varianceTable = new EnhancedTable(new NetworkVarianceTableModel(d_pm, consistencyModel), 300);
		varianceTable.setDefaultRenderer(QuantileSummary.class, new SummaryCellRenderer());
		
		builder.addSeparator("Variance Parameters", cc.xy(2, row));
		row += 2;
		builder.add(new TablePanel(varianceTable), cc.xy(2, row));
		row += 2;
		
		builder.addSeparator("Convergence", cc.xy(2, row));
		row += 2;
		
		builder.add(AuxComponentFactory.createHtmlField(CONVERGENCE_TEXT), cc.xy(2, row));
		row += 2;
		builder.add(buildConvergenceTable(consistencyModel, d_pm.getConsistencyModelConstructedModel()), cc.xy(2, row));
		row += 2;
		
		return builder.getPanel();
	}
	
	private JComponent buildNodeSplitTab() {
		final FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:0:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p");
		CellConstraints cc = new CellConstraints();
		PanelBuilder builder = new PanelBuilder(layout, new ScrollableJPanel());
		builder.setDefaultDialogBorder();
		final int width = 3;
		
		int row = 1;
		builder.addSeparator("Results - node-splitting analysis of inconsistency", cc.xyw(1, row, width));
		row += 2;
		
		builder.add(
				AuxComponentFactory.createHtmlField("<p>Node-splitting analysis is an alternative method to assess inconsistency in network meta-analysis. " +
						"It assesses whether direct and indirect evidence on a specific node (the split node) are in agreement. " +
						"While the results are easier to interpret, it requires a separate model to be run for each node to be split. " +
						"</p><p>The table below allows you to compare the estimated quantiles for the direct and indirect evidence as well " +
						"as the combined evidence. In addition a P-value is shown; a large value indicates no significant inconsistency was found. " +
						"See S. Dias et al. (2010), <em>Checking consistency in mixed treatment comparison meta-analysis</em>, " +
						"Statistics in Medicine, 29(7-8, Sp. Iss. SI): 932-944. <a href=\"http://dx.doi.org/10.1002/sim.3767\">doi:10.1002/sim.3767</a>.</p>"),
				cc.xyw(1, row, width));
		row += 2;
		
		builder.add(buildNodeSplitRunAllButton(), cc.xyw(1, row, width));
		row += 2;
		
		builder.add(buildNodeSplitResultsTable(), cc.xyw(1, row, width));

		for (BasicParameter p : d_pm.getSplitParameters()) {
			LayoutUtil.addRow(layout);
			row += 2;
			builder.addSeparator(p.getName(), cc.xyw(1, row, width));
			
			LayoutUtil.addRow(layout);
			row += 2;
			NodeSplitModel model = d_pm.getNodeSplitModel(p);
			builder.add(createStartButton(model), cc.xy(1, row));
			builder.add(new TaskProgressBar(d_pm.getProgressModel(model)), cc.xy(3, row));

			LayoutUtil.addRow(layout);
			row += 2;
			builder.add(makeNodeSplitDensityChart(p), cc.xyw(1, row, width));
			
			LayoutUtil.addRow(layout);
			row += 2;
			builder.addSeparator("Convergence", cc.xyw(1, row, width));
			LayoutUtil.addRow(layout);
			row += 2;
			builder.add(AuxComponentFactory.createHtmlField(CONVERGENCE_TEXT), cc.xyw(1, row, width));
			LayoutUtil.addRow(layout);
			row += 2;
			builder.add(buildConvergenceTable(model, d_pm.getNodesplitModelConstructedModel(p)), cc.xyw(1, row, width));
			
		}
		
		return builder.getPanel();
	}

	private Component buildNodeSplitRunAllButton() {
		JButton button = new JButton(ImageLoader.getIcon(FileNames.ICON_RUN));
		button.setText("Run all node-split models");
		button.setToolTipText("Run all simulations");
		final List<Task> tasks = new ArrayList<Task>();
		for (BasicParameter p : d_pm.getSplitParameters()) {
			tasks.add(d_pm.getNodeSplitModel(p).getActivityTask());
		}
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ThreadHandler.getInstance().scheduleTasks(tasks);
			}
		});
		return button;
	}

	private JComponent buildNodeSplitResultsTable() {
		NodeSplitResultsTableModel tableModel = new NodeSplitResultsTableModel(d_pm);
		
		EnhancedTable table = new EnhancedTable(tableModel);
		table.setDefaultRenderer(QuantileSummary.class, new SummaryCellRenderer());
		table.setDefaultRenderer(NodeSplitPValueSummary.class, new SummaryCellRenderer());
		return new TablePanel(table);
	}

	private JButton createStartButton(final NodeSplitModel model) {
		JButton button = new JButton(ImageLoader.getIcon(FileNames.ICON_RUN));
		button.setToolTipText("Run simulation");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ThreadHandler.getInstance().scheduleTask(model.getActivityTask());
			}
		});
		return button;
	}

	public JComponent buildPanel() {
		JTabbedPane tabbedPane = new AddisTabbedPane();
		tabbedPane.addTab("Overview", buildOverviewTab());
		tabbedPane.addTab("Consistency", buildConsistencyTab());
		tabbedPane.addTab("Inconsistency", buildInconsistencyTab());
		tabbedPane.addTab("Node Split", buildNodeSplitTab());
		tabbedPane.addTab("Memory Usage", buildMemoryUsageTab());
		return tabbedPane;
	}

	private JComponent makeNodeSplitDensityChart(BasicParameter p) {
		NodeSplitModel splitModel = d_pm.getNodeSplitModel(p);
		ConsistencyModel consModel = d_pm.getConsistencyModel();
		XYDataset dataset = new EmpiricalDensityDataset(50, new PlotParameter(splitModel.getResults(), splitModel.getDirectEffect()), 
				new PlotParameter(splitModel.getResults(), splitModel.getIndirectEffect()), 
				new PlotParameter(consModel.getResults(), p));
		
		JFreeChart chart = ChartFactory.createXYLineChart(
	            p.getName() + " density plot", "Relative Effect", "Density",                      
	            dataset, PlotOrientation.VERTICAL,
	            true, true, false                    
	        );

        return new ChartPanel(chart);	
	}

	private JComponent buildConvergenceTable(final MixedTreatmentComparison mtc, ValueHolder<Boolean> modelConstructed) {
		ConvergenceDiagnosticTableModel tableModel = new ConvergenceDiagnosticTableModel(mtc, modelConstructed);
		EnhancedTable convergenceTable = new EnhancedTable(tableModel);
		TablePanel pane = new TablePanel(convergenceTable);
	
		convergenceTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					int row = ((EnhancedTable)e.getComponent()).rowAtPoint(e.getPoint());
					Parameter p = mtc.getResults().getParameters()[row];
					showConvergencePlots(mtc, p);
				}
			}
		});
		return pane;
	}

	protected void showConvergencePlots(MixedTreatmentComparison mtc, Parameter p) {
		if(mtc.getResults().getNumberOfSamples() > 0) {
			JDialog dialog = new ConvergencePlotsDialog(d_mainWindow, mtc, p);
			dialog.setPreferredSize(new Dimension(d_mainWindow.getWidth() / 5 * 4, d_mainWindow.getHeight() / 5 * 4));
			dialog.setMinimumSize(new Dimension(d_mainWindow.getMinimumSize().width - 100, d_mainWindow.getMinimumSize().height - 100));
			dialog.setModal(true);
			dialog.setLocationRelativeTo(d_mainWindow);
			dialog.setLocationByPlatform(true);
			dialog.pack();
			dialog.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(d_mainWindow, "Convergence plots cannot be shown because the results of " +
					"this analysis has been discarded to save memory.", "No results available", JOptionPane.WARNING_MESSAGE);
		}
	}

	private JComponent createRankProbChart() {
		CategoryDataset dataset = d_pm.getRankProbabilityDataset();
		JFreeChart chart = ChartFactory.createBarChart("Rank Probability", "Treatment", "Probability", 
						dataset, PlotOrientation.VERTICAL, true, false, false);	
		chart.addSubtitle(new org.jfree.chart.title.ShortTextTitle(d_pm.getRankProbabilityRankChartNote()));

		FormLayout layout = new FormLayout(
				"fill:0:grow",
				"p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
		
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setSize(chartPanel.getPreferredSize().width, chartPanel.getPreferredSize().height+1);
		chartPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
	
		builder.add(chartPanel, cc.xy(1, 1));
				
		ButtonBarBuilder2 bbuilder = new ButtonBarBuilder2();
		bbuilder.addButton(createSaveImageButton(chart));
		builder.add(bbuilder.getPanel(), cc.xy(1, 3));

		return builder.getPanel();
	}

	private JButton createSaveImageButton(final JFreeChart chart) {
		JButton button = new JButton("Save Image");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ImageExporter.writeImage(d_mainWindow, chart, 600, 400);
			}
		});
		return button;
	}
	
	private JButton createSaveDataButton() {
		JButton button = new JButton("Save MTC Data Set");
		button.setToolTipText("Save data set for analysis using drugis.org MTC");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new FileSaveDialog(d_mainWindow, "xml", "XML files") {
					@Override
					public void doAction(String path, String extension) {
						writeXML(path, d_pm.getNetworkXML());
					}
				};
			}
		});
		return button;
	}
	
	private void writeXML(String path, String networkXML) {
		try {
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(path));
			out.write(networkXML);
			out.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("serial")
	public JComponent buildStudyGraphPart() {
		FormLayout layout = new FormLayout(
				"pref",
				"p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
	
		final StudyGraph panel = new StudyGraph(d_pm.getStudyGraphModel());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.layoutGraph();
		builder.add(panel, cc.xy(1, 1));
		
		JButton saveBtn = new JButton("Save Image");
		saveBtn.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				panel.saveImage(d_mainWindow);
			}
		});
		ButtonBarBuilder2 bbuilder = new ButtonBarBuilder2();
		bbuilder.addButton(saveBtn);
		bbuilder.addButton(createSaveDataButton());
		builder.add(bbuilder.getPanel(), cc.xy(1, 3));
		
		return builder.getPanel();
	}

	/**
	 * Make table of results (Cipriani et al., Lancet(2009), fig. 3, pp752).
	 * @param networkModel Model for which to display results.
	 * @return A TablePanel
	 */
	private NetworkMetaAnalysisTablePanel createNetworkTablePanel( MixedTreatmentComparison networkModel ) {
			NetworkTableModel networkAnalysisTableModel = new NetworkTableModel(d_pm, d_mainWindow.getPresentationModelFactory(), networkModel);
		return new NetworkMetaAnalysisTablePanel(d_mainWindow, networkAnalysisTableModel);
	}
}
