package org.drugis.addis.gui.builder;

import javax.swing.JComponent;

import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.presentation.NetworkMetaAnalysisPresentation;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class NetworkMetaAnalysisView implements ViewBuilder {
	private final NetworkMetaAnalysisPresentation d_pm;

	public NetworkMetaAnalysisView(NetworkMetaAnalysisPresentation model) {
		d_pm = model;
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();		

		builder.addSeparator("Network meta-analysis", cc.xy(1, 1));
		builder.add(GUIFactory.createCollapsiblePanel(buildOverviewPart()), cc.xy(1, 3));
		
		return builder.getPanel();
	}

	private JComponent buildOverviewPart() {
		FormLayout layout = new FormLayout(
				"pref, 3dlu, pref:grow:fill",
				"p");

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();

		builder.addLabel("ID:", cc.xy(1, 1));
		builder.add(BasicComponentFactory.createLabel(d_pm.getModel(MetaAnalysis.PROPERTY_NAME)),
				cc.xy(3, 1));
		
		return builder.getPanel();
	}

}
