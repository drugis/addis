package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.ContinuousPopulationCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.gui.AddVariableDialog;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.builder.VariableView;
import org.drugis.addis.presentation.VariablePresentation;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.value.ValueModel;

public class PopulationCharacteristicsKnowledge extends CategoryKnowledgeBase {
	public PopulationCharacteristicsKnowledge() {
		super("population characteristic", FileNames.ICON_POPULATION_CHAR, PopulationCharacteristic.class);
	}

	public JDialog getAddDialog(Main main, Domain domain,
			ValueModel selectionModel) {
		Variable variable = new ContinuousPopulationCharacteristic("");
		return new AddVariableDialog(main, domain, variable, selectionModel);
	}	
	
	@Override
	protected String[] getShownProperties() {
		return new String[] { "name", "description", "unitOfMeasurement", "type" };
	}

	@Override
	public ViewBuilder getEntityViewBuilder(Main main, Domain domain,
			Entity entity) {
		return new VariableView(
				(VariablePresentation) main.getPresentationModelFactory().getModel(((Variable) entity)), main);
	}
}
