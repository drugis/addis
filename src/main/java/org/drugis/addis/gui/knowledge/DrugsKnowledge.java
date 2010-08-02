package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.gui.AddDrugDialog;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.builder.DrugView;
import org.drugis.addis.presentation.DrugPresentation;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.value.ValueModel;

public class DrugsKnowledge extends CategoryKnowledgeBase {
	public DrugsKnowledge() {
		super("Drug", FileNames.ICON_DRUG, Drug.class);
	}
	
	public JDialog getAddDialog(Main main, Domain domain,
			ValueModel selectionModel) {
		return new AddDrugDialog(main, domain, selectionModel);
	}
	
	@Override
	protected String[] getShownProperties() {
		return new String[] { "name", "atcCode" };
	}

	@Override
	public ViewBuilder getEntityViewBuilder(Main main, Domain domain,
			Entity entity) {
		return new DrugView((DrugPresentation) main.getPresentationModelFactory().getModel(((Drug) entity)), main);
	}
}
