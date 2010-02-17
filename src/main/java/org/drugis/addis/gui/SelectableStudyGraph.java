package org.drugis.addis.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.SelectableStudyGraphModel;
import org.drugis.addis.presentation.wizard.SelectedDrugsGraphListener;
import org.jgraph.JGraph;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphSelectionModel;

@SuppressWarnings("serial")
public class SelectableStudyGraph extends StudyGraph {

	public SelectableStudyGraph(SelectableStudyGraphModel pm) {
		super(pm);
	}
	
	@Override
	protected JGraph createGraph(GraphLayoutCache cache) {
		JGraph graph = super.createGraph(cache);
		graph.setEnabled(true);
		ListHolder<Drug> selectedDrugs = ((SelectableStudyGraphModel)d_pm).getSelectedDrugsModel();
		SelectedDrugsGraphListener listener =
			new SelectedDrugsGraphListener(selectedDrugs);
		graph.getSelectionModel().addGraphSelectionListener(listener);
		selectedDrugs.addValueChangeListener(new PropertyChangeListener() {			
			public void propertyChange(PropertyChangeEvent ev) {
				System.out.println(ev.getNewValue()); 
			}
		});
		graph.getSelectionModel().setSelectionMode(GraphSelectionModel.SINGLE_GRAPH_SELECTION);
		return graph;
	}

}
