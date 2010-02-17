package org.drugis.addis.presentation.wizard;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.gui.SelectableStudyGraph;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.StudyGraphModel.Vertex;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;

public class SelectedDrugsGraphListener extends MouseAdapter {
	
	private ListHolder<Drug> d_drugList;
	private JGraph d_jgraph;
	private SelectableStudyGraph d_studyGraph;

	public SelectedDrugsGraphListener(SelectableStudyGraph selectableStudyGraph, JGraph graph, ListHolder<Drug> drugsList) {
		this.d_drugList = drugsList;
		this.d_studyGraph = selectableStudyGraph;
		this.d_jgraph = graph;
	}
	
	public void mousePressed(MouseEvent e) {
		Object cell = d_jgraph.getFirstCellForLocation(e.getX(), e.getY());		
		
		if (cell instanceof DefaultGraphCell) {
			DefaultGraphCell realcell = (DefaultGraphCell) cell;
			Object obj = realcell.getUserObject();
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
		d_studyGraph.layoutGraph();
		
		
	}

}
