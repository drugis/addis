package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.NetworkMetaAnalysisTablePanel;
import org.drugis.addis.gui.StudyGraph;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.NetworkMetaAnalysisPresentation;
import org.drugis.addis.presentation.NetworkMetaAnalysisTableModel;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class NetworkMetaAnalysisView extends AbstractMetaAnalysisView<NetworkMetaAnalysisPresentation>
implements ViewBuilder {
	public NetworkMetaAnalysisView(NetworkMetaAnalysisPresentation model, Main main) {
		super(model, main);
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();		

		builder.addSeparator("Network meta-analysis", cc.xy(1, 1));
		builder.add(GUIFactory.createCollapsiblePanel(buildOverviewPart()), cc.xy(1, 3));

		builder.addSeparator("Included studies", cc.xy(1, 5));
		builder.add(GUIFactory.createCollapsiblePanel(buildStudiesPart()), cc.xy(1, 7));

		builder.addSeparator("Evidence network", cc.xy(1, 9));
		builder.add(GUIFactory.createCollapsiblePanel(buildStudyGraphPart()), cc.xy(1, 11));

		builder.addSeparator("Results", cc.xy(1, 13));
		builder.add(GUIFactory.createCollapsiblePanel(buildResultsPart()), cc.xy(1, 15));

		return builder.getPanel();
	}
	
	public JComponent buildStudyGraphPart() {
		StudyGraph panel = new StudyGraph(d_pm.getStudyGraphModel());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		return panel;
	}
	
	public JComponent buildResultsPart() {
		JPanel jPanel = new JPanel();
		
		// make table of results (cipriani 2009, fig. 3, pp752):
		final NetworkMetaAnalysisTableModel networkAnalysisTableModel = new NetworkMetaAnalysisTableModel(
				d_pm.getBean().getIncludedDrugs(), d_pm.getBean().getOutcomeMeasure(), d_parent.getPresentationModelFactory(), 
				d_pm.getBean().getModel(), d_pm.getBean().getBuilder());

		Thread thread = new Thread(networkAnalysisTableModel);
		thread.start();
		
		ModifiableHolder<Boolean> readyModel = new ModifiableHolder<Boolean> (networkAnalysisTableModel.isReady());
		readyModel.addValueChangeListener(new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println("value changed!");
				
			}
		});

		
		if(!networkAnalysisTableModel.isReady())
			return new JLabel("calculating.....");
		

		// this creates the table
		NetworkMetaAnalysisTablePanel tablePanel = new NetworkMetaAnalysisTablePanel(d_parent, networkAnalysisTableModel);
		tablePanel.setVisible(true);
		jPanel.add(tablePanel, BorderLayout.CENTER);

		return jPanel;
	}	
	

}
