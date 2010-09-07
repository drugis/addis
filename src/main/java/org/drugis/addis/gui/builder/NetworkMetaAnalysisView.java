/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.NetworkMetaAnalysisTablePanel;
import org.drugis.addis.gui.StudyGraph;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.components.ScrollableJPanel;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.presentation.NetworkInconsistencyFactorsTableModel;
import org.drugis.addis.presentation.NetworkMetaAnalysisPresentation;
import org.drugis.addis.presentation.NetworkTableModel;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.FileSaveDialog;
import org.drugis.common.gui.ImageExporter;
import org.drugis.common.gui.ViewBuilder;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.ProgressEvent.EventType;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;

import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class NetworkMetaAnalysisView extends AbstractMetaAnalysisView<NetworkMetaAnalysisPresentation>
implements ViewBuilder {
	private class AnalysisProgressListener implements ProgressListener {
		private JProgressBar d_progBar;
		
		public AnalysisProgressListener(JProgressBar progBar) {
			d_progBar = progBar;
		}
		
		public void update(MCMCModel mtc, ProgressEvent event) {
			if(event.getType() == EventType.SIMULATION_PROGRESS && d_progBar != null){
				d_progBar.setString("Simulating: " + event.getIteration()/(event.getTotalIterations()/100) + "%");
				d_progBar.setValue(event.getIteration()/(event.getTotalIterations()/100));
			} else if(event.getType() == EventType.BURNIN_PROGRESS && d_progBar != null){
				d_progBar.setString("Burn in: " + event.getIteration()/(event.getTotalIterations()/100) + "%");
				d_progBar.setValue(event.getIteration()/(event.getTotalIterations()/100));
			}
		}
	}
	
	private static class AnalysisFinishedListener implements ProgressListener {
		private final JProgressBar d_progressBar;
		private final TablePanel[] d_tablePanels;

		public AnalysisFinishedListener(JProgressBar progressBar, TablePanel[] tablePanels) {
			d_progressBar = progressBar;
			d_tablePanels = tablePanels;
		}

		public void update(MCMCModel mtc, ProgressEvent event) {
			if(event.getType() == EventType.SIMULATION_FINISHED) {
				for (TablePanel tablePanel : d_tablePanels) {
					((AbstractTableModel) tablePanel.getTable().getModel()).fireTableDataChanged();
					tablePanel.doLayout();
				}
				d_progressBar.setVisible(false);
			}
		}
		
	}

	private PanelBuilder d_builder;
	private CellConstraints d_cc;
	private final Main d_main;
	
	public NetworkMetaAnalysisView(NetworkMetaAnalysisPresentation model, Main main) {
		super(model, main);
		d_main = main;

		d_pm.getBean().run();
	}

	public JComponent buildPanel() {
		if (d_builder != null) {
			d_builder.getPanel().removeAll();
			d_builder.getPanel().setVisible(false);
		}
		
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		d_builder = new PanelBuilder(layout, new ScrollableJPanel());
		d_builder.setDefaultDialogBorder();
		
		d_cc = new CellConstraints();		

		d_builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(NetworkMetaAnalysis.class).getSingularCapitalized(), d_cc.xy(1, 1));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildOverviewPart()), d_cc.xy(1, 3));

		d_builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Study.class).getPlural(), d_cc.xy(1, 5));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildStudiesPart()), d_cc.xy(1, 7));

		d_builder.addSeparator("Evidence network", d_cc.xy(1, 9));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildStudyGraphPart()), d_cc.xy(1, 11));

		d_builder.addSeparator("Results - network inconsistency model", d_cc.xy(1, 13));
		buildInconsistencyPart();

		d_builder.addSeparator("Results - network consistency model", d_cc.xy(1, 21));
		buildConsistencyPart();

		JPanel panel = d_builder.getPanel();
		return panel;
	}

	private void buildConsistencyPart() {
		
		
		JPanel consistencyPanel = new JPanel(new BorderLayout());
		ConsistencyModel consistencyModel = d_pm.getBean().getConsistencyModel();
		JProgressBar d_conProgressBar = new JProgressBar();
		consistencyModel.addProgressListener(new AnalysisProgressListener(d_conProgressBar));
		if(!consistencyModel.isReady()) {
			d_builder.add(d_conProgressBar, d_cc.xy(1, 23));
			d_conProgressBar.setStringPainted(true);
		}

		String consistencyText = "<html>If there is no relevant inconsistency in the evidence, a consistency model can be used to draw conclusions about the relative effect of the included treatments. Using normal meta-analysis, we could only get a subset of the confidence intervals for relative effects we derive using network meta-analysis. Network meta-analysis gives a consistent, integrated picture of the relative effects. However, given such a consistent set of relative effect estimates, it may still be difficult to draw conclusions on a potentially large set of treatments. Luckily, the Bayesian approach allows us to do even more with the data, and can be used to estimate the probability that, given the priors and the data, each of the treatments is the best, the second best, etc. This is given below in the rank probability plot. Rank probabilities sum to one, both within a rank over treatments and within a treatment over ranks.</html>";
		JComponent consistencyNote = AuxComponentFactory.createNoteField(consistencyText);
		consistencyPanel.add(consistencyNote, BorderLayout.NORTH);
		
		TablePanel consistencyTablePanel = createNetworkTablePanel(consistencyModel);
		consistencyModel.addProgressListener(
				new AnalysisFinishedListener(d_conProgressBar, new TablePanel[] {consistencyTablePanel}));
		
		consistencyPanel.add(consistencyTablePanel,BorderLayout.CENTER);

		consistencyPanel.add(createRankProbChart(), BorderLayout.SOUTH);
		JPanel collapsiblePanel = GUIFactory.createCollapsiblePanel(consistencyPanel);
		/* Fix: ScrollPanel Viewport doesn't have correct size when containing a CollapsiblePanel*/
		collapsiblePanel.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));  
		
		d_builder.add(collapsiblePanel, d_cc.xy(1, 27));
	}

	private void buildInconsistencyPart() {
		JPanel inconsistencyPanel = new JPanel();
		inconsistencyPanel.setLayout(new BorderLayout());
		
		InconsistencyModel inconsistencyModel = d_pm.getBean().getInconsistencyModel();
		JProgressBar d_incProgressBar = new JProgressBar();
		inconsistencyModel.addProgressListener(new AnalysisProgressListener(d_incProgressBar));
		if(!inconsistencyModel.isReady()) {
			d_builder.add(d_incProgressBar, d_cc.xy(1, 15));
			d_incProgressBar.setStringPainted(true);
		}
		
		String inconsistencyText = "<html>In network meta-analysis, because of the more complex evidence structure, we can assess <em>inconsistency</em> of evidence, in addition to <em>heterogeneity</em> within a comparison. Whereas heterogeneity represents between-study variation in the measured relative effect of a pair of treatments, inconsistency can only occur when a treatment C has a different effect when it is compared with A or B (i.e., studies comparing A and C are systematically different from studies comparing B and C). Thus, inconsistency may even occur with normal meta-analysis, but can only be detected using a network meta-analysis, and then only when there are closed loops in the evidence structure. For more information about assessing inconsistency, see G. Lu and A. E. Ades (2006), <em>Assessing evidence inconsistency in mixed treatment comparisons</em>, Journal of the American Statistical Association, 101(474): 447-459. <a href=\"http://dx.doi.org/10.1198/016214505000001302\">doi:10.1198/016214505000001302</a>.</html>";
		JComponent inconsistencyNote = AuxComponentFactory.createNoteField(inconsistencyText);
		
		inconsistencyPanel.add(inconsistencyNote, BorderLayout.NORTH);
		
		TablePanel inconsistencyTablePanel = createNetworkTablePanel(inconsistencyModel);
		inconsistencyPanel.add(inconsistencyTablePanel, BorderLayout.CENTER);
		
		NetworkInconsistencyFactorsTableModel inconsistencyFactorsTableModel = new NetworkInconsistencyFactorsTableModel(
				d_pm, d_parent.getPresentationModelFactory());
		EnhancedTable table = new EnhancedTable(inconsistencyFactorsTableModel, 300);
		final TablePanel inconsistencyFactorsTablePanel = new TablePanel(table);
		
		inconsistencyModel.addProgressListener(new ProgressListener() {
			public void update(MCMCModel mtc, ProgressEvent event) {
				if (event.getType() == EventType.MODEL_CONSTRUCTION_FINISHED) {
					if (inconsistencyFactorsTablePanel != null) {
						((AbstractTableModel) inconsistencyFactorsTablePanel.getTable().getModel()).fireTableStructureChanged();
						inconsistencyFactorsTablePanel.doLayout();
						d_parent.repaintRightPanel();
					}
				}
			}
		});
		
		inconsistencyPanel.add(inconsistencyFactorsTablePanel, BorderLayout.SOUTH);
		inconsistencyPanel.revalidate();
		
		inconsistencyModel.addProgressListener(
				new AnalysisFinishedListener(d_incProgressBar, new TablePanel[] {
						inconsistencyTablePanel, inconsistencyFactorsTablePanel
				})
			);
		
		d_builder.add(GUIFactory.createCollapsiblePanel(inconsistencyPanel), d_cc.xy(1, 19));
	}

	private JComponent createRankProbChart() {
		CategoryDataset dataset = d_pm.getRankProbabilityDataset();
		JFreeChart chart = ChartFactory.createBarChart("Rank Probability", "Treatment", "Probability", 
						dataset, PlotOrientation.VERTICAL, true, false, false);	
		chart.addSubtitle(new org.jfree.chart.title.ShortTextTitle(d_pm.getRankProbabilityRankChartNote()));

		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
		
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setSize(chartPanel.getPreferredSize().width, chartPanel.getPreferredSize().height+1);
		chartPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
	
		builder.add(chartPanel, cc.xy(1, 1));
		
		ButtonBarBuilder2 bbuilder = new ButtonBarBuilder2();
		bbuilder.addButton(createSaveImageButton(chartPanel));
		builder.add(bbuilder.getPanel(), cc.xy(1, 3));

		return builder.getPanel();
	}

	private JButton createSaveImageButton(final JComponent comp) {
		JButton button = new JButton("Save Image");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ImageExporter.writeImage(d_main, comp, (int) comp.getSize().getWidth(), (int) comp.getSize().getHeight());
			}
		});
		return button;
	}
	
	private JButton createSaveDataButton() {
		JButton button = new JButton("Save MTC Data Set");
		button.setToolTipText("Save data set for analysis using drugis.org MTC");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new FileSaveDialog(d_main, "xml", "XML files") {
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
				panel.saveImage(d_main);
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
	private NetworkMetaAnalysisTablePanel createNetworkTablePanel(
			MixedTreatmentComparison networkModel) {
		NetworkTableModel networkAnalysisTableModel = new NetworkTableModel(
				d_pm, d_parent.getPresentationModelFactory(), networkModel);
		
		return new NetworkMetaAnalysisTablePanel(d_parent, networkAnalysisTableModel);
	}	
	
}