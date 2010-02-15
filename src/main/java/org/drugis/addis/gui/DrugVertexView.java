package org.drugis.addis.gui;

import java.awt.Graphics;

import org.drugis.addis.presentation.StudyGraphPresentation;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

@SuppressWarnings("serial")
public class DrugVertexView extends VertexView {

	public class DrugVertexRenderer extends VertexRenderer {
		public void paint(Graphics g) {
			setText(d_vertex.getDrug().getName());
			super.paint(g);
		}
	}

	private StudyGraphPresentation.Vertex d_vertex;

	public DrugVertexView(StudyGraphPresentation.Vertex v, DefaultGraphCell cell) {
		super(cell);
		d_vertex = v;
	}
	
	@Override
	public CellViewRenderer getRenderer() {
		return new DrugVertexRenderer();
	}
}