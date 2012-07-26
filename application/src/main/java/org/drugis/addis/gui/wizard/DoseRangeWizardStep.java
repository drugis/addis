package org.drugis.addis.gui.wizard;


import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.entities.treatment.RangeEdge;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.addis.presentation.ValueHolder;
import org.pietschy.wizard.WizardStep;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class DoseRangeWizardStep extends AbstractDoseTreatmentWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;

	private final RangeInputPresentation d_rangeInputPresentation;
	private final String d_nextPropertyName;

	public static WizardStep createOnMultipleParentRanges (
			final JDialog dialog,
			final DosedDrugTreatmentPresentation pm,
			final ObservableList<DecisionTreeEdge> parentRanges,
			final String name, final String summary) {
		return new DoseRangesWizardStep(dialog, pm, parentRanges, name, summary);
	}

	public static DoseRangeWizardStep createOnBeanProperty(
			final JDialog dialog,
			final DosedDrugTreatmentPresentation pm,
			final ChoiceNode parent,
			final String nextPropertyName,
			final String name,
			final String summary) {
		return new DoseRangeWizardStep(dialog, pm, parent, nextPropertyName, name, summary);
	}

	public static WizardStep createOnKnownDoses(
			final JDialog dialog,
			final DosedDrugTreatmentPresentation pm,
			final String name, final String summary) {
		return new DoseRangeWizardStep(dialog, pm, null, null, name, summary);
	}

	private DoseRangeWizardStep(
			final JDialog dialog,
			final DosedDrugTreatmentPresentation presentationModel,
			ChoiceNode parent,
			final String nextPropertyName,
			final String name,
			final String summary) {
		super(presentationModel, name, summary, null);
		d_nextPropertyName = nextPropertyName;
		if (parent == null) {
			parent = presentationModel.getFixedRangeNode();
		}
		d_rangeInputPresentation = new RangeInputPresentation(d_pm, parent, d_nextPropertyName);
		attachListener(d_rangeInputPresentation.getRanges());
	}

	private void attachListener(final ListModel model) {
		model.addListDataListener((new ListDataListener() {
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
		}));
	}

	@Override
	public void initialize() {
		// Handle the "ignore dose type" case
		if (!d_pm.getBean().getDecisionTree().containsVertex(d_rangeInputPresentation.getParent())) {
			d_pm.getModelForFixedDose().setValue(d_rangeInputPresentation.getParent());
		}

		// Add default ranges if necessary
		populate(d_pm, d_rangeInputPresentation.getParent());
	}

	public static void populate(final DosedDrugTreatmentPresentation pm,
			final ChoiceNode parent) {
		if (pm.getBean().getDecisionTree().getOutEdges(parent).size() == 0) {
			pm.getBean().getDecisionTree().addChild(new RangeEdge(0.0, false, Double.POSITIVE_INFINITY, true), parent, new LeafNode());
		}
	}

	public ValueHolder<Boolean> getConsiderNextProperty() {
		return d_rangeInputPresentation.getConsiderNext();
	}

	@Override
	protected JPanel buildPanel() {
		final FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:pref:grow, 3dlu, pref, 3dlu",
				"p"
				);

		final PanelBuilder builder = new PanelBuilder(layout);
		int row = 1;

		final RangeInputBuilder rangeBuilder = new RangeInputBuilder(d_dialog, d_rangeInputPresentation);
		row = rangeBuilder.addFamilyToPanel(builder, row);

		return builder.getPanel();
	}
}
