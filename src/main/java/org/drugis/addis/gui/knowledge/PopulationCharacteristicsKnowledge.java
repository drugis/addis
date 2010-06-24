package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.ContinuousPopulationCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.gui.AddVariableDialog;
import org.drugis.addis.gui.Main;

import com.jgoodies.binding.value.ValueModel;

public class PopulationCharacteristicsKnowledge extends CategoryKnowledgeBase {
	public PopulationCharacteristicsKnowledge() {
		super("Population characteristic", FileNames.ICON_POPULATION_CHAR);
	}

	@Override
	public JDialog getAddDialog(Main main, Domain domain,
			ValueModel selectionModel) {
		Variable variable = new ContinuousPopulationCharacteristic("");
		return new AddVariableDialog(main, domain, variable, selectionModel);
	}	
}
