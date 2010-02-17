package org.drugis.addis.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.SelectableStudyGraphModel;
import org.drugis.addis.presentation.wizard.SelectedDrugsGraphListener;
import org.jgraph.JGraph;
import org.jgraph.graph.GraphLayoutCache;

@SuppressWarnings("serial")
public class SelectableStudyGraph extends StudyGraph {

	public SelectableStudyGraph(SelectableStudyGraphModel pm) {
		super(pm);
	}
	
	@Override
	protected JGraph createGraph(GraphLayoutCache cache) {
		final JGraph graph = super.createGraph(cache);
		ListHolder<Drug> selectedDrugs = ((SelectableStudyGraphModel)d_pm).getSelectedDrugsModel();
		SelectedDrugsGraphListener listener =
			new SelectedDrugsGraphListener(graph, selectedDrugs);
		graph.addMouseListener(listener);
		selectedDrugs.addValueChangeListener(new PropertyChangeListener() {			
			public void propertyChange(PropertyChangeEvent ev) {
				System.out.println(ev.getNewValue()); 
			}
		});
		return graph;
	}

}
