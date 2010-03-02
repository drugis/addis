package org.drugis.addis.gui.builder;

import javax.swing.JComponent;

import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.AbstractMetaAnalysisPresentation;
import org.drugis.common.gui.OneWayObjectFormat;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AbstractMetaAnalysisView<T extends AbstractMetaAnalysisPresentation<?>> {

	protected T d_pm;
	protected Main d_parent;

	public AbstractMetaAnalysisView(T model, Main main) {
		d_pm = model;
		d_parent = main;
	}

	protected JComponent buildStudiesPart() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
				
		builder.add(GUIFactory.buildStudyPanel(d_pm, d_parent), cc.xy(1, 1));
		
		return builder.getPanel();
	}

	protected JComponent buildOverviewPart() {
		FormLayout layout = new FormLayout(
				"pref, 3dlu, pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
		
		builder.addLabel("ID:", cc.xy(1, 1));
		builder.add(BasicComponentFactory.createLabel(d_pm.getModel(RandomEffectsMetaAnalysis.PROPERTY_NAME)),
				cc.xy(3, 1));
		
		builder.addLabel("Type:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(d_pm.getModel(MetaAnalysis.PROPERTY_TYPE), new OneWayObjectFormat()), cc.xy(3, 3));
				
		builder.addLabel("Indication:", cc.xy(1, 5));
		builder.add(BasicComponentFactory.createLabel(d_pm.getIndicationModel().getLabelModel()),
				cc.xy(3, 5));
	
		builder.addLabel("Endpoint:", cc.xy(1, 7));
		builder.add(BasicComponentFactory.createLabel(d_pm.getOutcomeMeasureModel().getLabelModel()),
				cc.xy(3, 7));
		
		builder.addLabel("Included drugs:", cc.xy(1, 9));
		builder.add(BasicComponentFactory.createLabel(d_pm.getModel(MetaAnalysis.PROPERTY_INCLUDED_DRUGS), new OneWayObjectFormat()),
				cc.xy(3, 9));
		
		return builder.getPanel();
	}

}
