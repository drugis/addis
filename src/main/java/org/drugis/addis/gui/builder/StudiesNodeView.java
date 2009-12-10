package org.drugis.addis.gui.builder;

import javax.swing.JComponent;

import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudiesNodeView implements ViewBuilder {
	
	private JComponent d_jc;

	public StudiesNodeView(JComponent jc) {
		d_jc = jc;
	}

	public JComponent buildPanel() {

		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator("Studies", cc.xy(1, 1));
		builder.add(d_jc, cc.xy(1, 3));
		
		return builder.getPanel();
	}

}


