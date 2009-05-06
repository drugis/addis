package nl.rug.escher.gui;

import java.util.List;

import javax.swing.JComponent;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.entities.Study;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class EndpointStudiesView implements ViewBuilder {
	private Endpoint d_endpoint;
	private Domain d_domain;
	
	public EndpointStudiesView(Endpoint node, Domain domain) {
		d_endpoint = node;
		d_domain = domain;
	}

	public JComponent buildPanel() {
		PresentationModel<Endpoint> eModel = new PresentationModel<Endpoint>(d_endpoint);
		
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator("Endpoint", cc.xyw(1, 1, 3));
		builder.addLabel("Name:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(
				eModel.getModel(Endpoint.PROPERTY_NAME)), cc.xy(3, 3));
		
		builder.addLabel("Description:", cc.xy(1, 5));
		builder.add(BasicComponentFactory.createLabel(
				eModel.getModel(Endpoint.PROPERTY_DESCRIPTION)), cc.xy(3, 5));
		
		builder.addSeparator("Studies", cc.xyw(1, 7, 3));
		
		int row = 9;
		for (Study s : d_domain.getStudies(d_endpoint)) {
			layout.appendRow(RowSpec.decode("3dlu"));
			layout.appendRow(RowSpec.decode("p"));
			builder.add(BasicComponentFactory.createLabel(
					new PresentationModel<Study>(s).getModel(Study.PROPERTY_ID)), cc.xy(3, row));
			row += 2;
		}
		
		return builder.getPanel();
	}
}
