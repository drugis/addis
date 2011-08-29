package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Unit;
import org.drugis.addis.gui.AddUnitDialog;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.builder.UnitView;
import org.drugis.addis.presentation.UnitPresentation;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.value.ValueModel;

public class UnitsKnowledge extends CategoryKnowledgeBase {

	public UnitsKnowledge() {
		super("unit", FileNames.ICON_UNIT, Unit.class);
	}
	
	@Override
	public JDialog getAddDialog(AddisWindow mainWindow, Domain domain, ValueModel selectionModel) {
		return new AddUnitDialog(mainWindow, domain, selectionModel);
	}

	@Override
	public ViewBuilder getEntityViewBuilder(AddisWindow main, Domain domain, Entity entity) {
		return new UnitView((UnitPresentation) main.getPresentationModelFactory().getModel(((Unit) entity)));
	}

}
