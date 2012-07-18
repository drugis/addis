package org.drugis.addis.gui.wizard;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseDecisionTree;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class DosedDrugTreatmentOverviewWizardStep extends AbstractDoseTreatmentWizardStep {

	private static final long serialVersionUID = -3991691781012756118L;

	public DosedDrugTreatmentOverviewWizardStep(DosedDrugTreatmentPresentation pm) {
		super(pm, "Overview","Overview of created treatment.");
	}
	
	@Override
	protected void initialize() { 
		rebuildPanel();
	}
	
	protected JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, pref:grow:fill",
				"p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		
		CellConstraints cc = new CellConstraints();
		int row = 1;
		
		builder.addLabel("Overview of" + " " + d_pm.getBean().getLabel(), cc.xy(1, row));
		row = LayoutUtil.addRow(layout, row);
		builder.add(buildOverview(d_pm.getBean().getDecisionTree()), cc.xyw(1, row, 3));
		return builder.getPanel();
	}

	/**
	 * Builds a graph overview using Jung's functionality.
	 * FIXME: the layout uses static spacing so nodes with long labels still mess up the layout.
	 *        (perhaps 'ellipsize' them beyond a certain maximum length? or wrap text? or make spacing dynamic?)
	 * @param tree
	 * @return
	 */
	private static JPanel buildOverview(DoseDecisionTree tree) {
		Layout<DecisionTreeNode, String> layout = new TreeLayout<DecisionTreeNode, String>(tree, 150, 50);
		Transformer<DecisionTreeNode,Paint> vertexPaint = new Transformer<DecisionTreeNode,Paint>() {
            public Paint transform(DecisionTreeNode node) {
                return (node instanceof LeafNode) ? new Color(0.55f, 0.55f, 1.0f) : Color.ORANGE;
            }
        };
		final VisualizationViewer<DecisionTreeNode, String> vv = new VisualizationViewer<DecisionTreeNode, String>(layout);
		vv.setVertexToolTipTransformer(new ToStringLabeller<DecisionTreeNode>());
        vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        
        vv.getRenderContext().setVertexShapeTransformer(new Transformer<DecisionTreeNode, Shape>() {
			public Shape transform(DecisionTreeNode input) {
				FontMetrics fontMetrics = vv.getGraphics().getFontMetrics();
				double width = fontMetrics.stringWidth(input.toString());
				double height = fontMetrics.getHeight();
				double arc = 5;
				return new RoundRectangle2D.Double(-width / 2, -height / 2, width, height, arc, arc);
			}
		});
        
//		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.AUTO);
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<DecisionTreeNode>());
        
		vv.setPreferredSize(new Dimension(PANEL_WIDTH, 700));
		return vv;
	}
}
