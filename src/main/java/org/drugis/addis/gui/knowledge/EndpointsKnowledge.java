package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.gui.AddVariableDialog;
import org.drugis.addis.gui.Main;

import com.jgoodies.binding.value.ValueModel;

public class EndpointsKnowledge extends CategoryKnowledgeBase {
	public EndpointsKnowledge() {
		super("Endpoint", FileNames.ICON_ENDPOINT);
	}
	
	@Override
	public JDialog getAddDialog(Main main, Domain domain,
			ValueModel selectionModel) {
		Variable variable = new Endpoint("", Variable.Type.RATE);
		return new AddVariableDialog(main, domain, variable, selectionModel);
	}
}
