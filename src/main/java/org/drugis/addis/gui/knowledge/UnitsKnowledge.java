package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Unit;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.value.ValueModel;

public class UnitsKnowledge extends CategoryKnowledgeBase {

	public UnitsKnowledge() {
		super("Unit", FileNames.ICON_INDICATION, Unit.class);
	}
	
	@Override
	public JDialog getAddDialog(AddisWindow mainWindow, Domain domain, ValueModel selectionModel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ViewBuilder getEntityViewBuilder(AddisWindow main, Domain domain, Entity entity) {
		// TODO Auto-generated method stub
		return null;
	}

}
