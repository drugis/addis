package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.gui.AddIndicationDialog;
import org.drugis.addis.gui.Main;

import com.jgoodies.binding.value.ValueModel;

public class IndicationsKnowledge extends CategoryKnowledgeBase {
	public IndicationsKnowledge() {
		super("Indication", FileNames.ICON_INDICATION, Indication.class);
	}
	
		
	public JDialog getAddDialog(Main main, Domain domain,
			ValueModel selectionModel) {
		return new AddIndicationDialog(main, domain, selectionModel);
	}
	
	@Override
	protected String[] getShownProperties() {
		return new String[] { "name", "code" };
	}
}
