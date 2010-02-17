package org.drugis.addis.presentation.wizard;

import java.util.ArrayList;
import java.util.Arrays;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.StudyGraphModel.Vertex;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;

public class SelectedDrugsGraphListener implements GraphSelectionListener {
	
	private ListHolder<Drug> d_drugList;

	public SelectedDrugsGraphListener(ListHolder<Drug> drugsList) {
		this.d_drugList = drugsList;
	}

	public void valueChanged(GraphSelectionEvent e) {
		if (!(e.getCell() instanceof DefaultEdge)) {
			DefaultGraphCell cell = (DefaultGraphCell) e.getCell();
			Object obj = cell.getUserObject();
			if (obj instanceof Vertex) {
				selectUnselectDrug(((Vertex) obj).getDrug());
			}
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
