package org.drugis.addis.gui.builder;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.drugis.addis.entities.Indication;
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
				"right:pref, 3dlu, left:pref:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator("Indication", cc.xyw(1, 1, 3));
		builder.addLabel("Concept ID:", cc.xy(1, 3));
		
		ValueModel codeModel = ConverterFactory.createStringConverter(
				d_pm.getModel(Indication.PROPERTY_CODE),
				new OneWayObjectFormat());
		builder.add(BasicComponentFactory.createLabel(codeModel), cc.xy(3, 3));
		
		builder.addLabel("Fully Specified Name:", cc.xy(1, 5));
		builder.add(BasicComponentFactory.createLabel(
				d_pm.getModel(Indication.PROPERTY_NAME)), cc.xy(3, 5));
		
		builder.addSeparator("Studies", cc.xyw(1, 7, 3));
		
		int row = 9;
		row = buildStudiesPart(builder, cc, row);
		
		return builder.getPanel();
	}

	private int buildStudiesPart(PanelBuilder builder, CellConstraints cc,
			int row) {

		JComponent studiesComp = null;
		if(d_pm.getIncludedStudies().isEmpty()) {
			studiesComp = new JLabel("No studies found.");
		} else {
			studiesComp = new StudyTablePanelView(d_pm, d_parent).buildPanel();
		}
		builder.add(studiesComp, cc.xyw(1, row, 3));
		return row + 2;
	}
}
