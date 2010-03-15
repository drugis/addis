package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.NetworkMetaAnalysisTablePanel;
import org.drugis.addis.gui.StudyGraph;
import org.drugis.addis.presentation.NetworkMetaAnalysisPresentation;
import org.drugis.addis.presentation.NetworkMetaAnalysisTableModel;
import org.drugis.common.gui.ViewBuilder;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.ProgressEvent.EventType;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class NetworkMetaAnalysisView extends AbstractMetaAnalysisView<NetworkMetaAnalysisPresentation>
implements ViewBuilder {
	
	JPanel d_pane = new JPanel();
	private PanelBuilder d_builder;
	private CellConstraints d_cc;
	private JProgressBar d_progressBar;
	
	public NetworkMetaAnalysisView(NetworkMetaAnalysisPresentation model, Main main) {
		super(model, main);

		d_pm.getBean().getModel().addProgressListener(new ProgressListener() {
			public void update(MixedTreatmentComparison mtc, ProgressEvent event) {
				if (d_pane != null) {
					if(event.getType() == EventType.SIMULATION_PROGRESS && d_progressBar != null){
						d_progressBar.setString("Simulating: " + event.getIteration()/1000 + "%");
						d_progressBar.setValue(event.getIteration()/1000);
					} else if(event.getType() == EventType.BURNIN_PROGRESS && d_progressBar != null){
						d_progressBar.setString("Burning: " + event.getIteration()/40 + "%");
						d_progressBar.setValue(event.getIteration()/40);
					} else if(event.getType() == EventType.SIMULATION_FINISHED) {
						d_pane.setVisible(false);
						d_pane.removeAll();
						buildPanel();
						d_pane.setVisible(true);
					}
				}
			}
		});

		Thread t = new Thread(d_pm.getBean());
		t.start();
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		d_builder = new PanelBuilder(layout);
	
		d_builder.setDefaultDialogBorder();
		
		d_cc = new CellConstraints();		

		d_builder.addSeparator("Network meta-analysis", d_cc.xy(1, 1));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildOverviewPart()), d_cc.xy(1, 3));

		d_builder.addSeparator("Included studies", d_cc.xy(1, 5));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildStudiesPart()), d_cc.xy(1, 7));

		d_builder.addSeparator("Evidence network", d_cc.xy(1, 9));
		d_builder.add(GUIFactory.createCollapsiblePanel(buildStudyGraphPart()), d_cc.xy(1, 11));

		d_builder.addSeparator("Results", d_cc.xy(1, 13));
		JComponent resultsPart = buildResultsPart();
		d_builder.add(GUIFactory.createCollapsiblePanel(resultsPart), d_cc.xy(1, 15));

		d_pane.setLayout(new BorderLayout());
		d_pane.add(d_builder.getPanel(), BorderLayout.CENTER);
		
		// Update preferred size when the size of the results section has changed.
		Dimension curSize = d_pane.getPreferredSize();
		d_pane.setPreferredSize(new Dimension(curSize.width, (int) (curSize.height + resultsPart.getPreferredSize().getHeight())));

		return d_pane;
	}
	
	public JComponent buildStudyGraphPart() {
		StudyGraph panel = new StudyGraph(d_pm.getStudyGraphModel());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		return panel;
	}
	
	public JComponent buildResultsPart() {
		// make table of results (cipriani 2009, fig. 3, pp752):
		final NetworkMetaAnalysisTableModel networkAnalysisTableModel = new NetworkMetaAnalysisTableModel(
				d_pm, d_parent.getPresentationModelFactory());
		
		if(!d_pm.getBean().getModel().isReady()){
			if(d_progressBar == null)
				d_progressBar = new JProgressBar();
			
			d_progressBar.setStringPainted(true);
			return d_progressBar;
		}

		// this creates the table
		NetworkMetaAnalysisTablePanel tablePanel = new NetworkMetaAnalysisTablePanel(d_parent, networkAnalysisTableModel);
		tablePanel.setVisible(true);
		
		JScrollPane sp = new JScrollPane(tablePanel);		
		sp.setViewportBorder(BorderFactory.createEmptyBorder());
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		
		return sp;
	}	
}