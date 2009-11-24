package org.drugis.addis.gui.builder;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.drugis.addis.entities.Indication;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.presentation.IndicationPresentation;
import org.drugis.common.gui.OneWayObjectFormat;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ConverterFactory;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class IndicationView implements ViewBuilder {
	
	private IndicationPresentation d_pm;
	private JFrame d_parent;

	public IndicationView(IndicationPresentation pm, JFrame parent) {
		d_pm = pm;
		this.d_parent = parent;
	}
	
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator("Indication", cc.xy(1, 1));
		builder.add(GUIFactory.createCollapsiblePanel(buildOverviewPart()), cc.xy(1, 3));
		builder.addSeparator("Studies", cc.xy(1, 5));
		builder.add(GUIFactory.createCollapsiblePanel(GUIFactory.buildStudyPanel(d_pm, d_parent)), cc.xy(1, 7));		
		
		return builder.getPanel();
	}

	private JPanel buildOverviewPart() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout("right:pref, 3dlu, left:pref:grow",
				"p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.addLabel("Concept ID:", cc.xy(1, 1));
		ValueModel codeModel = ConverterFactory.createStringConverter(
				d_pm.getModel(Indication.PROPERTY_CODE),
				new OneWayObjectFormat());
		builder.add(BasicComponentFactory.createLabel(codeModel), cc.xy(3, 1));
		
		builder.addLabel("Fully Specified Name:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(
				d_pm.getModel(Indication.PROPERTY_NAME)), cc.xy(3, 3));
		return builder.getPanel();
	}
}
