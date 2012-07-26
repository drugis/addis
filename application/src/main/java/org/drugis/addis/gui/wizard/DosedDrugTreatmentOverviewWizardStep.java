package org.drugis.addis.gui.wizard;


import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;
import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ConstantDirectionalEdgeValueTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class DosedDrugTreatmentOverviewWizardStep extends AbstractDoseTreatmentWizardStep {
	private static final long serialVersionUID = -3991691781012756118L;

	public DosedDrugTreatmentOverviewWizardStep(final DosedDrugTreatmentPresentation pm) {
		super(pm, "Overview","Overview of created treatment.", null);
	}

	@Override
	protected void initialize() {
		setLayout(new GridLayout(1, 1));
		rebuildPanel();
	}

	@Override
	protected JPanel buildPanel() {
		return buildOverview(d_pm.getBean().getDecisionTree());
	}

	/**
	 * Builds a graph overview using Jung's functionality.
	 * @param tree
	 * @return panel
	 */
	private static JPanel buildOverview(final DecisionTree tree) {
        return new GraphZoomScrollPane(buildDecisionTreeView(tree));
	}
	
	public static VisualizationViewer<DecisionTreeNode, DecisionTreeEdge> buildDecisionTreeView(final DecisionTree tree) { 
		// Crazy hack because sizes start at 600x600 by default.
		final Layout<DecisionTreeNode, DecisionTreeEdge> layout = new TreeLayout<DecisionTreeNode, DecisionTreeEdge>(new DecisionTree(new LeafNode()), 150, 75);
		layout.getSize().height = 1;
		layout.getSize().width = 1;
		layout.setGraph(tree);
		
		final VisualizationViewer<DecisionTreeNode, DecisionTreeEdge> vv = new VisualizationViewer<DecisionTreeNode, DecisionTreeEdge>(layout);

		vv.setVertexToolTipTransformer(new ToStringLabeller<DecisionTreeNode>());
        vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<DecisionTreeNode,Paint>() {
            @Override
			public Paint transform(final DecisionTreeNode node) {
                return (node instanceof LeafNode) ? new Color(0.55f, 0.55f, 1.0f) : Color.ORANGE;
            }
        });

        vv.getRenderContext().setVertexShapeTransformer(new Transformer<DecisionTreeNode, Shape>() {
			@Override
			public Shape transform(final DecisionTreeNode input) {
				final FontMetrics fontMetrics = vv.getGraphics().getFontMetrics();
				final double width = fontMetrics.stringWidth(input.toString()) + 6;
				final double height = fontMetrics.getHeight() + 2;
				final double arc = 5;
				return new RoundRectangle2D.Double(-width / 2, -height / 2, width, height, arc, arc);
			}
		});

        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<DecisionTreeNode>());

		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<DecisionTreeEdge>());
		vv.getRenderContext().getEdgeLabelRenderer().setRotateEdgeLabels(false);
		vv.getRenderContext().setEdgeLabelClosenessTransformer(new ConstantDirectionalEdgeValueTransformer<DecisionTreeNode, DecisionTreeEdge>(0.5, 0.4));
		return vv;
	}
}
