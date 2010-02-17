/**
 * 
 */
package org.drugis.addis.gui;

import org.drugis.addis.presentation.StudyGraphModel.Vertex;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.VertexView;
import org.jgrapht.ext.JGraphModelAdapter;

import com.jgraph.components.labels.MultiLineVertexView;

@SuppressWarnings({ "unchecked", "serial" })	
public class MyDefaultCellViewFactory extends DefaultCellViewFactory {
	
	private JGraphModelAdapter d_model;

	public MyDefaultCellViewFactory(JGraphModelAdapter model) {
		this.d_model = model;
	}
	
	protected VertexView createVertexView(Object cell) {
		if (cell instanceof DefaultGraphCell) {
			MultiLineVertexView multiLineVertexView = new MultiLineVertexView(cell);
			AttributeMap map = new AttributeMap(d_model.getDefaultVertexAttributes());
			multiLineVertexView.setAttributes(map);
			Object obj = ((DefaultGraphCell)cell).getUserObject();
			if (obj instanceof Vertex) {
				addVertexAttributes(map, (Vertex) obj);
			}
			return multiLineVertexView;
		}
		return super.createVertexView(cell);
	}
	
	protected void addVertexAttributes(AttributeMap map, Vertex v) {
	}
}