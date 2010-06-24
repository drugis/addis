package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.gui.AddDrugDialog;
import org.drugis.addis.gui.Main;

import com.jgoodies.binding.value.ValueModel;

public class DrugsKnowledge extends CategoryKnowledgeBase {
	public DrugsKnowledge() {
		super("Drug", FileNames.ICON_DRUG);
	}
	
	@Override
	public JDialog getAddDialog(Main main, Domain domain,
			ValueModel selectionModel) {
		return new AddDrugDialog(main, domain, selectionModel);
	}
}
