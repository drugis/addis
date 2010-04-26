package org.drugis.addis.gui.builder;

import java.util.List;

import javax.swing.JComponent;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.EntitiesTablePanel;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class EntitiesNodeView<T extends Entity> implements ViewBuilder {
	
	private List<String> d_formatter;
	private List<PresentationModel<T>> d_dpms;
	private Main d_main;
	private String d_title;

	public EntitiesNodeView(List<String> formatter, List<PresentationModel<T>> dpms, Main main, String title) {
		d_formatter = formatter;
		d_dpms = dpms;
		d_main = main;
		d_title = title;
	}

	public JComponent buildPanel() {		
		FormLayout layout = new FormLayout(
				"pref",
				"p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator(d_title, cc.xy(1, 1));
		builder.add(new EntitiesTablePanel<T>(d_formatter, d_dpms, d_main), cc.xy(1, 3));
		
		return builder.getPanel();
	}

}
