package org.drugis.addis.gui;

import java.awt.Color;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.StudyGraphModel.Vertex;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;

@SuppressWarnings("serial")
public class SelectableCellViewFactory extends MyDefaultCellViewFactory {

	private ListHolder<Drug> d_selectedDrugs;

	@SuppressWarnings("unchecked")
	public SelectableCellViewFactory(JGraphModelAdapter model, ListHolder<Drug> selectedDrugs) {
		super(model);
		
		this.d_selectedDrugs = selectedDrugs;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addVertexAttributes(AttributeMap map, Vertex v) {
		Color col = null;
		if (d_selectedDrugs.getValue().contains(v.getDrug())) {
			col = Color.green;
		} else {
			col = Color.DARK_GRAY;
		}
		map.put(GraphConstants.BACKGROUND, col);
	}

}
