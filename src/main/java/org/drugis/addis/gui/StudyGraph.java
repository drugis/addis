package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.StudyGraphModel.Edge;
import org.drugis.addis.presentation.StudyGraphModel.Vertex;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphLayoutCache;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.ext.JGraphModelAdapter;

import com.jgraph.components.labels.CellConstants;
import com.jgraph.components.labels.MultiLineVertexRenderer;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

@SuppressWarnings("serial")
public class StudyGraph extends JPanel {
	protected StudyGraphModel d_pm;
	
	@SuppressWarnings("unchecked")
	protected JGraphModelAdapter d_model;
	private AttributeMap d_vertexAttributes;
	
	public StudyGraph(StudyGraphModel pm) {
		super(new BorderLayout());
		d_pm = pm;

		d_vertexAttributes = new AttributeMap();
		CellConstants.setVertexShape(d_vertexAttributes, MultiLineVertexRenderer.SHAPE_RECTANGLE);
		CellConstants.setBackground(d_vertexAttributes, Color.WHITE);
		CellConstants.setForeground(d_vertexAttributes, Color.BLACK);
		CellConstants.setBorder(d_vertexAttributes, BorderFactory.createLineBorder(Color.BLACK, 2));
		
		layoutGraph();

		d_pm.addGraphListener(new GraphListener<Vertex, Edge>() {
			public void vertexRemoved(GraphVertexChangeEvent<Vertex> e) {
				// CANNOT call layoutGraph() here, because of bug in JGraph/JGraphT
				// HOWEVER, it is neither needed due to order of adding/removing in the PM.
				// Hanno + Tommi, 16.2.2010
			}
			public void vertexAdded(GraphVertexChangeEvent<Vertex> e) {
				layoutGraph();
			}
			public void edgeRemoved(GraphEdgeChangeEvent<Vertex, Edge> e) {
				layoutGraph();				
			}
			public void edgeAdded(GraphEdgeChangeEvent<Vertex, Edge> e) {
				layoutGraph();
			}
		}); 
		
	}

	@SuppressWarnings("unchecked")
	public void layoutGraph() {
		// in the JGraphModelAdapter, the Vertex size is set. Therefore, this must be done every time the graph is redrawn
		d_model = new JGraphModelAdapter<Vertex, Edge>(d_pm);
		
		// set out vertex (layout) attributes
		d_model.setDefaultVertexAttributes(d_vertexAttributes);
		
		// the graph layout cache 
		GraphLayoutCache layoutCache = new GraphLayoutCache(d_model, getCellFactory());
		
		// create graph
		removeAll();
		JGraph jgraph = createGraph(layoutCache);
		add(jgraph, BorderLayout.CENTER);
		
		// Layout the graph
		final JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
		final JGraphFacade facade = new JGraphFacade(jgraph);
		layout.run(facade);
		Map nested = facade.createNestedMap(true, true);
		jgraph.getGraphLayoutCache().edit(nested);
	
		jgraph.repaint();
	}

	protected MyDefaultCellViewFactory getCellFactory() {
		return new MyDefaultCellViewFactory(d_model);
	}

	protected JGraph createGraph(GraphLayoutCache layoutCache) {
		JGraph jgraph = new JGraph(d_model, layoutCache);
		jgraph.setAntiAliased(true);
		jgraph.setEditable(false);
		jgraph.setEnabled(false);
		
		return jgraph;
	}
	
	public static class MutableDrugListHolder extends AbstractListHolder<Drug> {
		private List<Drug> d_drugs;
		
		public MutableDrugListHolder(List<Drug> drugs) {
			d_drugs = new ArrayList<Drug>(drugs);
		}

		@Override
		public List<Drug> getValue() {
			return d_drugs;
		}
		
		@SuppressWarnings("unchecked")
		@Override 
		public void setValue(Object o) {
			setValue((List<Drug>)o);
		}
		
		public void setValue(List<Drug> drugs) {
			List<Drug> oldValue = d_drugs;
			d_drugs = new ArrayList<Drug>(drugs);
			fireValueChange(oldValue, d_drugs);
		}
		
	}
}
