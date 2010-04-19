package org.drugis.addis.presentation;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.gui.Main;

@SuppressWarnings("serial")
public class SelectAdverseEventsPresentation
extends SelectFromFiniteListPresentationImpl<AdverseEvent> {
	
	public SelectAdverseEventsPresentation(ListHolder<AdverseEvent> options, Main main) {
		super(options, "Adverse Event", "Select Adverse Events", "Please select the appropriate adverse events.", main);
	}

	public void showAddOptionDialog(int idx) {
		d_main.showAddAdverseEventDialog(getSlot(idx));
	}
}
