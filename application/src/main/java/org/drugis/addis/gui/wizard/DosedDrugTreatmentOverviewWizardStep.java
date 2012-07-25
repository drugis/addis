package org.drugis.addis.gui.wizard;


import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class DosedDrugTreatmentOverviewWizardStep extends AbstractDoseTreatmentWizardStep {
	private static final long serialVersionUID = -3991691781012756118L;

	public DosedDrugTreatmentOverviewWizardStep(DosedDrugTreatmentPresentation pm) {
		super(pm, "Overview","Overview of created treatment.", null);
	}
	
	@Override
	protected void initialize() {
		setLayout(new GridLayout(1, 1));
		rebuildPanel();
	}
	
	protected JPanel buildPanel() {
		return buildOverview(d_pm.getBean().getDecisionTree());
	}

	/**
	 * Builds a graph overview using Jung's functionality.
	 * FIXME: the layout uses static spacing so nodes with long labels still mess up the layout.
	 *        (perhaps 'ellipsize' them beyond a certain maximum length? or wrap text? or make spacing dynamic?)
	 * @param tree
	 * @return panel
	 */
	private static JPanel buildOverview(DoseDecisionTree tree) {
		// Crazy hack because sizes start at 600x600 by default. 
		Layout<DecisionTreeNode, String> layout = new TreeLayout<DecisionTreeNode, String>(new DoseDecisionTree(new EmptyNode()), 150, 50);
		layout.getSize().height = 1;
		layout.getSize().width = 1;
		layout.setGraph(tree);
		
		final VisualizationViewer<DecisionTreeNode, String> vv = new VisualizationViewer<DecisionTreeNode, String>(layout);
        
		vv.setVertexToolTipTransformer(new ToStringLabeller<DecisionTreeNode>());
        vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<DecisionTreeNode,Paint>() {
            public Paint transform(DecisionTreeNode node) {
                return (node instanceof LeafNode) ? new Color(0.55f, 0.55f, 1.0f) : Color.ORANGE;
            }
        });
        
        vv.getRenderContext().setVertexShapeTransformer(new Transformer<DecisionTreeNode, Shape>() {
			public Shape transform(DecisionTreeNode input) {
				FontMetrics fontMetrics = vv.getGraphics().getFontMetrics();
				double width = fontMetrics.stringWidth(input.toString()) + 6;
				double height = fontMetrics.getHeight() + 2;
				double arc = 5;
				return new RoundRectangle2D.Double(-width / 2, -height / 2, width, height, arc, arc);
			}
		});
        
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<DecisionTreeNode>());
		
		System.out.println(layout.getSize());
        
        GraphZoomScrollPane pane = new GraphZoomScrollPane(vv);
        return pane;
	}
}
