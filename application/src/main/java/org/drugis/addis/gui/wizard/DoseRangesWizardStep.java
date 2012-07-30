package org.drugis.addis.gui.wizard;


import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.entities.treatment.RangeEdge;
import org.drugis.addis.presentation.wizard.DosedDrugTreatmentWizardPresentation;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DoseRangesWizardStep extends AbstractDoseTreatmentWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;

	private final ObservableList<DecisionTreeEdge> d_parents;
	private List<ObservableList<DecisionTreeEdge>> d_observed = new ArrayList<ObservableList<DecisionTreeEdge>>();

	public DoseRangesWizardStep(
			final JDialog dialog,
			final DosedDrugTreatmentWizardPresentation presentationModel,
			final ObservableList<DecisionTreeEdge> parentRanges,
			final String name,
			final String summary) {
		super(presentationModel, name, summary, dialog);
		d_parents = parentRanges;
	}

	@Override
	public void initialize() {
		// Remove old listeners
		for (ObservableList<DecisionTreeEdge> list : d_observed) {
			list.removeListDataListener(d_rebuildListener);
		}
		d_observed.clear();
		
		// Initialize ranges
		final DecisionTree tree = d_pm.getBean().getDecisionTree();
		for (final DecisionTreeEdge edge : d_parents) {
			final DecisionTreeNode node = tree.getEdgeTarget(edge);
			if (node  instanceof ChoiceNode) {
				DoseRangeWizardStep.populate(d_pm, (ChoiceNode)node);
			}
		}
		
		// Listen to range lists
		for (DecisionTreeEdge edge : d_parents) {
			DecisionTreeNode node = tree.getEdgeTarget(edge);
			if (node instanceof ChoiceNode) {
				ObservableList<DecisionTreeEdge> ranges = d_pm.getOutEdges(node);
				ranges.addListDataListener(d_rebuildListener);
				d_observed.add(ranges);
			}
		}
		
		rebuildPanel();
	}

	@Override
	protected JPanel buildPanel() {
		final FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:pref:grow, 3dlu, pref",
				"p"
				);
		final int fullWidth = layout.getColumnCount();
		final CellConstraints cc = new CellConstraints();
		final PanelBuilder builder = new PanelBuilder(layout);
		int row = 1;

		final DecisionTree tree = d_pm.getBean().getDecisionTree();
	
		for (final DecisionTreeEdge edge : d_parents) {
			final ChoiceNode parent = (ChoiceNode) tree.getEdgeSource(edge);
			
			row = LayoutUtil.addRow(layout, row);
			builder.addSeparator(RangeEdge.format(GUIHelper.humanize(parent.getPropertyName()), (RangeEdge) edge), cc.xyw(1, row, fullWidth));
			
			final DecisionTreeNode node = tree.getEdgeTarget(edge);
			if (node instanceof ChoiceNode) {
				final RangeInputBuilder rangeBuilder = new RangeInputBuilder(d_dialog, new RangeInputPresentation(d_pm, (ChoiceNode) node, null));
				row = rangeBuilder.addFamilyToPanel(builder, row);
			} else {
				LeafNode leaf = (LeafNode) node;
				row = LayoutUtil.addRow(layout, row);
				if (leaf.getCategory() == null) {
					builder.addLabel("Range excluded", cc.xy(3, row));
				} else {
					builder.addLabel("Range assigned to " + node.getName(), cc.xy(3, row));
				}
			}
		}
		return builder.getPanel();
	}
}
