package org.drugis.addis.presentation.wizard;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;

public class AbstractWizardWithSelectableIndicationPM implements WizardWithSelectableIndicationPresentation {

	protected Domain d_domain;
	protected ModifiableHolder<Indication> d_indicationHolder;

	public AbstractWizardWithSelectableIndicationPM(Domain d) {
		d_domain = d;
		d_indicationHolder = new ModifiableHolder<Indication>();
	}

	public ValueHolder<Indication> getIndicationModel() {
		return d_indicationHolder; 
	}

	@SuppressWarnings("serial")
	public ListHolder<Indication> getIndicationListModel() {
		return new AbstractListHolder<Indication>() {
			@Override
			public List<Indication> getValue() {
				return new ArrayList<Indication>(d_domain.getIndications());
			}
		};
	}

}