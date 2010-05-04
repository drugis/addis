package org.drugis.addis.presentation.wizard;

import org.drugis.addis.entities.Indication;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.ValueHolder;

public interface WizardWithSelectableIndicationPresentation {

	public abstract ValueHolder<Indication> getIndicationModel();
	public abstract ListHolder<Indication> getIndicationListModel();

}