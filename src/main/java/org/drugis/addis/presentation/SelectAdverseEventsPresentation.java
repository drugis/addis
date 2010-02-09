package org.drugis.addis.presentation;

import org.drugis.addis.entities.AdverseDrugEvent;
import org.drugis.addis.gui.Main;

@SuppressWarnings("serial")
public class SelectAdverseEventsPresentation
extends SelectFromFiniteListPresentationImpl<AdverseDrugEvent> {
	
	public SelectAdverseEventsPresentation(ListHolder<AdverseDrugEvent> options, Main main) {
		super(options, "Adverse Event", "Select Adverse Events", "Please select the appropriate adverse events.");
		d_main = main;
	}
	
	@Override
	public boolean hasAddOptionDialog() {
		return true;
	}

	public void showAddOptionDialog(int idx) {
		d_main.showAddAdeDialog(getSlot(idx));
	}
}
