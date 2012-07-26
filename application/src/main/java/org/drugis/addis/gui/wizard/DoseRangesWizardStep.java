package org.drugis.addis.gui.wizard;


import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class DoseRangesWizardStep extends AbstractDoseTreatmentWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;

	private final ObservableList<DecisionTreeEdge> d_parents;

	private final ListDataListener d_listener = new ListDataListener() { // FIXME
		@Override
		public void intervalRemoved(final ListDataEvent e) {
			rebuildPanel();
		}

		@Override
		public void intervalAdded(final ListDataEvent e) {
			rebuildPanel();
		}

		@Override
		public void contentsChanged(final ListDataEvent e) {}
	};

	public DoseRangesWizardStep(
			final JDialog dialog,
			final DosedDrugTreatmentPresentation presentationModel,
			final ObservableList<DecisionTreeEdge> parentRanges,
			final String name,
			final String summary) {
		super(presentationModel, name, summary, dialog);
		d_parents = parentRanges;
	}

	@Override
	public void initialize() {
		final DecisionTree tree = d_pm.getBean().getDecisionTree();
		for (final DecisionTreeEdge edge : d_parents) {
			final DecisionTreeNode node = tree.getEdgeTarget(edge);
			if (node  instanceof ChoiceNode) {
				DoseRangeWizardStep.populate(d_pm, (ChoiceNode)node); // FIXME: take into account parent ranges
			}
		}
	}

//	private DoseRangeNode createRangeNode(final DecisionTreeNode parent) {
//		final RangeNode start = (RangeNode)parent;
//		return new DoseRangeNode(
//				d_beanProperty.getKey(),
//				d_childPropertyName,
//				start.getRangeLowerBound(),
//				start.isRangeLowerBoundOpen(),
//				Double.POSITIVE_INFINITY,
//				false,
//				d_pm.getDoseUnit());
//	}

	@Override
	protected JPanel buildPanel() {
		final FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:pref:grow, 3dlu, pref, 3dlu",
				"p"
				);

		final PanelBuilder builder = new PanelBuilder(layout);
		int row = 1;

		final DecisionTree tree = d_pm.getBean().getDecisionTree();
		for (final DecisionTreeEdge edge : d_parents) { // FIXME: separator
			final DecisionTreeNode node = tree.getEdgeTarget(edge);
			if (node instanceof ChoiceNode) {
				final RangeInputBuilder rangeBuilder = new RangeInputBuilder(d_dialog, new RangeInputPresentation(d_pm, (ChoiceNode) node, null));
				row = rangeBuilder.addFamilyToPanel(builder, row);
			} else {

			}
		}
		return builder.getPanel();
	}
}
