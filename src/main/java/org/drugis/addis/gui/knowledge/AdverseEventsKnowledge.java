package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.gui.AddVariableDialog;
import org.drugis.addis.gui.Main;

import com.jgoodies.binding.value.ValueModel;

public class AdverseEventsKnowledge extends CategoryKnowledgeBase {
	public AdverseEventsKnowledge() {
		super("Adverse event", FileNames.ICON_ADVERSE_EVENT, AdverseEvent.class);
	}
	
	public JDialog getAddDialog(Main main, Domain domain,
			ValueModel selectionModel) {
		Variable variable = new AdverseEvent("", Variable.Type.RATE);
		return new AddVariableDialog(main, domain, variable, selectionModel);
	}
	
	@Override
	protected String[] getShownProperties() {
		return new String[] { "name", "description", "unitOfMeasurement", "type", "direction" };
	}
}
