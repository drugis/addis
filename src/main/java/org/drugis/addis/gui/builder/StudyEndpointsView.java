package org.drugis.addis.gui.builder;

import javax.swing.JComponent;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyEndpointsView implements ViewBuilder {
	
	private PresentationModel<? extends Study> d_model;
	private ImageLoader d_loader;

	public StudyEndpointsView(PresentationModel<? extends Study> model, ImageLoader loader) {
		d_model = model;
		d_loader = loader;
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref",
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
	
		if (d_model.getBean().getEndpoints().isEmpty()) {
			builder.addLabel("No endpoints", cc.xy(1, 1));
		} else {
			int row = 1;
			boolean addRow = false;
			for (Endpoint e : d_model.getBean().getEndpoints()) {
				if (addRow) {
					LayoutUtil.addRow(layout);
				}
				builder.add(
						GUIFactory.createEndpointLabelWithIcon(d_loader, d_model.getBean(), e),
						cc.xy(1, row));
				row += 2;
				addRow = true;
			}
		}
		return builder.getPanel();
	}

}
