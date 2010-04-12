package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.Variable;
import org.drugis.addis.gui.AbstractTablePanel;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.NetworkMetaAnalysisTablePanel;
import org.drugis.addis.gui.StudyGraph;
import org.drugis.addis.presentation.NetworkInconsistencyTableModel;
import org.drugis.addis.presentation.NetworkMetaAnalysisPresentation;
import org.drugis.addis.presentation.NetworkTableModel;
import org.drugis.common.gui.ViewBuilder;
import org.drugis.mtc.ConsistencyModel;
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
		
		public void update(MixedTreatmentComparison mtc, ProgressEvent event) {
			if (d_pane != null) {
				if(event.getType() == EventType.SIMULATION_PROGRESS && d_incProgressBar != null){
					d_progBar.setString("Simulating: " + event.getIteration()/1000 + "%");
					d_progBar.setValue(event.getIteration()/1000);
				} else if(event.getType() == EventType.BURNIN_PROGRESS && d_incProgressBar != null){
					d_progBar.setString("Burn in: " + event.getIteration()/40 + "%");
					d_progBar.setValue(event.getIteration()/40);
				} else if(event.getType() == EventType.SIMULATION_FINISHED) {
					d_progBar.setVisible(false);
					d_pane.validate();
				}
				
			}
		}
	}
	
	
	JPanel d_pane = new JPanel();
	private PanelBuilder d_builder;
	private CellConstraints d_cc;
	private JProgressBar d_incProgressBar;
	private JProgressBar d_conProgressBar;
	
	public NetworkMetaAnalysisView(NetworkMetaAnalysisPresentation model, Main main) {
		super(model, main);

		d_conProgressBar = new JProgressBar();
		d_incProgressBar = new JProgressBar();
		
		if( !(d_pm.getBean().getIncludedStudies().get(0).getEndpoints().get(0).getType() == Variable.Type.CONTINUOUS)){
			new AnalysisProgressListener(d_conProgressBar, d_pm.getBean().getConsistencyModel());
			new AnalysisProgressListener(d_incProgressBar, d_pm.getBean().getInconsistencyModel());
	
			d_pm.getBean().run();
		}
	}

	public JComponent buildPanel() {
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

		if( d_pm.getBean().getIncludedStudies().get(0).getEndpoints().get(0).getType() == Variable.Type.CONTINUOUS){
			d_builder.addSeparator("Results", d_cc.xy(1, 13));
			d_builder.add(new JLabel("Network meta analysis not yet possible for continuous measurements."), d_cc.xy(1, 15));
		}
		else{
			d_builder.addSeparator("Results - network inconsistency model", d_cc.xy(1, 13));
			
			JPanel inconsistencyPanel = new JPanel(new BorderLayout());
			
			if(!d_pm.getBean().getConsistencyModel().isReady())
				d_builder.add(d_incProgressBar, d_cc.xy(1, 15));
			JComponent inconsistencyResultsPart = buildResultsPart(d_pm.getBean().getInconsistencyModel(),d_incProgressBar);
			inconsistencyPanel.add(inconsistencyResultsPart,BorderLayout.NORTH);
			
			NetworkInconsistencyTableModel inconsistencyTableModel = new NetworkInconsistencyTableModel(
							d_pm, d_parent.getPresentationModelFactory());
			JPanel inconsistencyTable = new AbstractTablePanel(inconsistencyTableModel);
			inconsistencyPanel.add(inconsistencyTable,BorderLayout.SOUTH);
			
			d_builder.add(GUIFactory.createCollapsiblePanel(inconsistencyPanel), d_cc.xy(1, 19));
			
			d_builder.addSeparator("Results - network consistency model", d_cc.xy(1, 21));
			
			JPanel consistencyPanel = new JPanel(new BorderLayout());
			
			if(!d_pm.getBean().getInconsistencyModel().isReady())
				d_builder.add(d_conProgressBar, d_cc.xy(1, 23));
				
			JComponent consistencyResultsPart = buildResultsPart(d_pm.getBean().getConsistencyModel(), d_conProgressBar);
			consistencyPanel.add(consistencyResultsPart,BorderLayout.NORTH);
			
			consistencyPanel.add(createRankProbChart(d_pm.getBean().getConsistencyModel()), BorderLayout.SOUTH);
			JPanel collapsiblePanel = GUIFactory.createCollapsiblePanel(consistencyPanel);
			/* Fix: ScrollPanel Viewport doesn't have correct size when containing a CollapsiblePanel*/
			collapsiblePanel.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));  
			d_builder.add(collapsiblePanel, d_cc.xy(1, 27));
		}
		
		d_pane.setLayout(new BorderLayout());
		d_pane.add(d_builder.getPanel(), BorderLayout.CENTER);

		return d_pane;
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
		return panel;
	}
	
	public JComponent buildResultsPart(MixedTreatmentComparison networkModel, JProgressBar progBar) {
		JScrollPane scrollPane = new JScrollPane();
		
		if(!networkModel.isReady()){
			progBar.setStringPainted(true);
		}

		// make table of results (cipriani 2009, fig. 3, pp752):
		final NetworkTableModel networkAnalysisTableModel = new NetworkTableModel(
				d_pm, d_parent.getPresentationModelFactory(), networkModel);
		
		// this creates the table
		NetworkMetaAnalysisTablePanel tablePanel = new NetworkMetaAnalysisTablePanel(d_parent, networkAnalysisTableModel);
		tablePanel.setVisible(true);
		scrollPane.getViewport().add(tablePanel);
		
		scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
		return scrollPane;
	}	
	
}