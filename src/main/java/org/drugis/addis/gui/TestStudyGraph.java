package org.drugis.addis.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.StudyGraphPresentation;
import org.drugis.addis.presentation.UnmodifiableHolder;
import org.drugis.addis.presentation.StudyGraphPresentation.Vertex;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.VertexView;
import org.jgrapht.ext.JGraphModelAdapter;

import com.jgraph.components.labels.CellConstants;
import com.jgraph.components.labels.MultiLineVertexRenderer;
import com.jgraph.components.labels.MultiLineVertexView;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.graph.JGraphSimpleLayout;

@SuppressWarnings("serial")
public class TestStudyGraph extends JFrame {
	private StudyGraphPresentation d_pm;
	@SuppressWarnings("unchecked")
	private JGraphModelAdapter d_model;
	@SuppressWarnings("unchecked")
	public TestStudyGraph(StudyGraphPresentation pm) {
		super("Test for Drug/Study graph");
		d_pm = pm;
		
		d_model = new JGraphModelAdapter(d_pm);
		AttributeMap vertexAttributes = new AttributeMap();
		CellConstants.setVertexShape(vertexAttributes, MultiLineVertexRenderer.SHAPE_CIRCLE);
		d_model.setDefaultVertexAttributes(vertexAttributes);
		GraphLayoutCache layoutCache = new GraphLayoutCache(d_model, new MyFactory());
		JGraph jgraph = new JGraph(d_model, layoutCache);
		jgraph.setEditable(false);
		jgraph.setAntiAliased(true);
		jgraph.setAutoResizeGraph(false);
		getContentPane().add(jgraph);
		
		
		JGraphSimpleLayout layout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_CIRCLE);
		JGraphFacade facade = new JGraphFacade(jgraph);
		layout.run(facade);
		
		// Obtain a map of the resulting attribute changes from the facade
		Map nested = facade.createNestedMap(true, true);
		// Apply the results to the actual graph
		jgraph.getGraphLayoutCache().edit(nested);
		jgraph.repaint();
	}

	public static void main(String[] args) {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		final ArrayList<Drug> d_drugs = new ArrayList<Drug>();
		d_drugs.add(ExampleData.buildDrugFluoxetine());
		d_drugs.add(ExampleData.buildDrugParoxetine());
		d_drugs.add(ExampleData.buildDrugSertraline());
		StudyGraphPresentation pm =
			new StudyGraphPresentation(new UnmodifiableHolder<Indication>(ExampleData.buildIndicationDepression()),
				new UnmodifiableHolder<Endpoint>(ExampleData.buildEndpointHamd()),
				new AbstractListHolder<Drug>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<Drug> getValue() {
						return d_drugs;
					}}, domain);
		
		JFrame frame = new TestStudyGraph(pm);
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public class MyFactory extends DefaultCellViewFactory {
		protected VertexView createVertexView(Object cell) {
			if (cell instanceof DefaultGraphCell) {
				MultiLineVertexView multiLineVertexView = new MultiLineVertexView(cell);
				multiLineVertexView.setAttributes(d_model.getDefaultVertexAttributes());
				return multiLineVertexView;
			}
			return super.createVertexView(cell);
		}
	}
}
