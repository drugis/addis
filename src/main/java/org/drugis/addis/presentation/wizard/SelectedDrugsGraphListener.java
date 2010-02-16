package org.drugis.addis.presentation.wizard;

import java.util.ArrayList;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.StudyGraphPresentation.Vertex;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultEdge;

public class SelectedDrugsGraphListener implements GraphSelectionListener {
	
	private ListHolder<Drug> d_drugList;

	public SelectedDrugsGraphListener(ListHolder<Drug> drugsList) {
		this.d_drugList = drugsList;
	}

	public void valueChanged(GraphSelectionEvent e) {
		if (!(e.getCell() instanceof DefaultEdge)) {
			Vertex vert = (Vertex) e.getCell();
			selectUnselectDrug(vert.getDrug());
		}
		
	}

	private void selectUnselectDrug(Drug drug) {
		ArrayList<Drug> drugs = new ArrayList<Drug>(d_drugList.getValue());
		if (drugs.contains(drug)) {
			drugs.remove(drug);
		} else {
			drugs.add(drug);
		}
		d_drugList.setValue(drugs);
	}

}
