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

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.NetworkMetaAnalysisTablePanel;
import org.drugis.addis.gui.StudyGraph;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.presentation.NetworkInconsistencyFactorsTableModel;
import org.drugis.addis.presentation.NetworkMetaAnalysisPresentation;
import org.drugis.addis.presentation.NetworkTableModel;
import org.drugis.addis.util.HtmlWordWrapper;
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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class NetworkMetaAnalysisView extends AbstractMetaAnalysisView<NetworkMetaAnalysisPresentation>
implements ViewBuilder {

	private class AnalysisProgressListener implements ProgressListener {
		private JProgressBar d_progBar;
		
		public AnalysisProgressListener(JProgressBar progBar, MixedTreatmentComparison networkModel) {
			d_progBar = progBar;
			networkModel.addProgressListener(this);
		}
		
		public void update(MCMCModel mtc, ProgressEvent event) {
			if (event.getType() == EventType.MODEL_CONSTRUCTION_FINISHED) {
				if (d_inconsistencyFactorsTablePanel != null) {
					d_inconsistencyFactorsTablePanel.doLayout();
					d_parent.repaintRightPanel();
				}
			}
			if(event.getType() == EventType.SIMULATION_PROGRESS && d_incProgressBar != null){
				d_progBar.setString("Simulating: " + event.getIteration()/(event.getTotalIterations()/100) + "%");
				d_progBar.setValue(event.getIteration()/(event.getTotalIterations()/100));
			} else if(event.getType() == EventType.BURNIN_PROGRESS && d_incProgressBar != null){
				d_progBar.setString("Burn in: " + event.getIteration()/(event.getTotalIterations()/100) + "%");
				d_progBar.setValue(event.getIteration()/(event.getTotalIterations()/100));
			} else if(event.getType() == EventType.SIMULATION_FINISHED) {
				d_progBar.setVisible(false);
				d_inconsistencyTablePanel.doLayout();
				d_consistencyTablePanel.doLayout();
				d_inconsistencyFactorsTablePanel.doLayout();
				d_parent.repaintRightPanel();
				
			}
		}
	}
	
	//JPanel d_pane = new JPanel();
	private PanelBuilder d_builder;
	private CellConstraints d_cc;
	private JProgressBar d_incProgressBar;
	private JProgressBar d_conProgressBar;
	private NetworkMetaAnalysisTablePanel d_inconsistencyTablePanel;
	private NetworkMetaAnalysisTablePanel d_consistencyTablePanel;
	private JPanel d_inconsistencyFactorsTablePanel;
	
	public NetworkMetaAnalysisView(NetworkMetaAnalysisPresentation model, Main main) {
		super(model, main);

		// Get progress-bars.
		d_conProgressBar = new JProgressBar();
		d_incProgressBar = new JProgressBar();
		new AnalysisProgressListener(d_conProgressBar, d_pm.getBean().getConsistencyModel());
		new AnalysisProgressListener(d_incProgressBar, d_pm.getBean().getInconsistencyModel());
		
		//System.out.println(d_pm.getNetworkXML());
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
		d_builder = new PanelBuilder(layout);
	
		d_builder.setDefaultDialogBorder();
		
		d_cc = new CellConstraints();		

		d_builder.addSeparator("Network meta-analysis", d_cc.xy(1, 1));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildOverviewPart()), d_cc.xy(1, 3));

		d_builder.addSeparator("Included studies", d_cc.xy(1, 5));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildStudiesPart()), d_cc.xy(1, 7));

		d_builder.addSeparator("Evidence network", d_cc.xy(1, 9));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildStudyGraphPart()), d_cc.xy(1, 11));

		d_builder.addSeparator("Results - network inconsistency model", d_cc.xy(1, 13));
		buildInconsistencyPart();

		d_builder.addSeparator("Results - network consistency model", d_cc.xy(1, 21));
		buildConsistencyPart();

		return d_builder.getPanel();
	}

	private void buildConsistencyPart() {
		
		
		JPanel consistencyPanel = new JPanel(new BorderLayout());
		if(!d_pm.getBean().getConsistencyModel().isReady())
			d_builder.add(d_conProgressBar, d_cc.xy(1, 23));

		String consistencyText = "<html>If there is no relevant inconsistency in the evidence, a consistency model can be used to draw conclusions <br>about the relative effect of the included treatments. Using normal meta-analysis, we could only get a subset <br>of the confidence intervals for relative effects we derive using network meta-analysis. <br>Network meta-analysis gives a consistent, integrated picture of the relative effects. However, given such a <br>consistent set of relative effect estimates, it may still be difficult to draw conclusions on a potentially large <br>set of treatments. Luckily, the Bayesian approach allows us to do even more with the data, and can be used <br>to estimate the probability that, given the priors and the data, each of the treatments is the best, the <br>second best, etc. This is given below in the rank probability plot. Rank probabilities sum to one, both within a <br>rank over treatments and within a treatment over ranks.</html>";
		JComponent consistencyPane = HtmlWordWrapper.createHtmlPane(consistencyText);
		consistencyPanel.add(consistencyPane, BorderLayout.NORTH);
		
		JComponent consistencyResultsPart = buildResultsPart(d_pm.getBean().getConsistencyModel(), d_conProgressBar);
		
		consistencyPanel.add(consistencyResultsPart,BorderLayout.CENTER);

		consistencyPanel.add(createRankProbChart(d_pm.getBean().getConsistencyModel()), BorderLayout.SOUTH);
		JPanel collapsiblePanel = GUIFactory.createCollapsiblePanel(consistencyPanel);
		/* Fix: ScrollPanel Viewport doesn't have correct size when containing a CollapsiblePanel*/
		collapsiblePanel.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));  
		
		d_builder.add(collapsiblePanel, d_cc.xy(1, 27));
	}

	private void buildInconsistencyPart() {
		JPanel inconsistencyPanel = new JPanel(new BorderLayout());

		if(!d_pm.getBean().getInconsistencyModel().isReady())
			d_builder.add(d_incProgressBar, d_cc.xy(1, 15));
		
		String inconsistencyText = "<html>In network meta-analysis, because of the more complex evidence structure, we can assess <em>inconsistency</em><br> of evidence, in addition to <em>heterogeneity</em> within a comparison. Whereas heterogeneity represents <br>between-study variation in the measured relative effect of a pair of treatments, inconsistency can only <br> occur when a treatment C has a different effect when it is compared with A or B (i.e., studies comparing <br>A and C are systematically different from studies comparing B and C). Thus, inconsistency may even occur <br> with normal meta-analysis, but can only be detected using a network meta-analysis, and then only when <br>there are closed loops in the evidence structure. For more information about assessing inconsistency, <br>see G. Lu and A. E. Ades (2006), <em>Assessing evidence inconsistency in mixed treatment comparisons</em>, <br>Journal of the American Statistical Association, 101(474): 447-459. <a href=\"http://dx.doi.org/10.1198/016214505000001302\">doi:10.1198/016214505000001302</a>.</html>";
		JComponent inconsistencyPane = HtmlWordWrapper.createHtmlPane(inconsistencyText);
		inconsistencyPanel.add(inconsistencyPane, BorderLayout.NORTH);
		
		JComponent inconsistencyResultsPart = buildResultsPart(d_pm.getBean().getInconsistencyModel(),d_incProgressBar);
		inconsistencyPanel.add(inconsistencyResultsPart,BorderLayout.CENTER);

		
		NetworkInconsistencyFactorsTableModel inconsistencyFactorsTableModel = new NetworkInconsistencyFactorsTableModel(
				d_pm, d_parent.getPresentationModelFactory());
		EnhancedTable table = new EnhancedTable(inconsistencyFactorsTableModel, 300);
		d_inconsistencyFactorsTablePanel = new TablePanel(table);
		inconsistencyPanel.add(d_inconsistencyFactorsTablePanel,BorderLayout.SOUTH);
		
		
		d_builder.add(GUIFactory.createCollapsiblePanel(inconsistencyPanel), d_cc.xy(1, 19));
	}

	private JComponent createRankProbChart(ConsistencyModel networkModel) {
		CategoryDataset dataset = d_pm.getRankProbabilityDataset();
		
		JFreeChart chart = ChartFactory.createBarChart("Rank Probability", "Rank", "Probability", 
						dataset, PlotOrientation.VERTICAL, true, false, false);	
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setSize(chartPanel.getPreferredSize().width, chartPanel.getPreferredSize().height+1);
		chartPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
	
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		panel.add(chartPanel, BorderLayout.CENTER);

		return panel;
	}

	public JComponent buildStudyGraphPart() {
		StudyGraph panel = new StudyGraph(d_pm.getStudyGraphModel());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.layoutGraph();
		return panel;
	}
	
	public JComponent buildResultsPart(MixedTreatmentComparison networkModel, JProgressBar progBar) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		if(!networkModel.isReady()){
			progBar.setStringPainted(true);
		}

		// make table of results (cipriani 2009, fig. 3, pp752):
		NetworkTableModel networkAnalysisTableModel = new NetworkTableModel(
				d_pm, d_parent.getPresentationModelFactory(), networkModel);
		
		// this creates the table
		NetworkMetaAnalysisTablePanel tablePanel = new NetworkMetaAnalysisTablePanel(d_parent, networkAnalysisTableModel);
		if(networkModel instanceof InconsistencyModel)
			d_inconsistencyTablePanel = tablePanel;
		else
			d_consistencyTablePanel = tablePanel;
			
		tablePanel.setVisible(true);
		panel.add(tablePanel, BorderLayout.NORTH);
		
		return panel;
	}	
	
}