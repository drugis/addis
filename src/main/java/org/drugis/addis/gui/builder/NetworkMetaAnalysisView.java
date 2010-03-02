package org.drugis.addis.gui.builder;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.NetworkMetaAnalysisPresentation;
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
		return new JPanel();
	}
	
	public JComponent buildResultsPart() {
		JPanel jPanel = new JPanel();
		jPanel.add(new JLabel("Calculating results is not implemented yet!"));
		return jPanel;
	}
}
