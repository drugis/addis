package org.drugis.addis.gui.wizard;


import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.math3.util.Pair;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.addis.presentation.ValueHolder;
import org.pietschy.wizard.WizardStep;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class DoseRangeWizardStep extends AbstractDoseTreatmentWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;
	
	private Pair<Class<? extends AbstractDose>, String> d_beanProperty;
	private Family d_family;
	private String d_nextPropertyName;

	private RangeInputPresentation d_rangeInputPresentation;
	
	public static WizardStep createOnMultipleParentRanges (
			JDialog dialog, 
			DosedDrugTreatmentPresentation pm, 
			Class<? extends AbstractDose> beanClass,
			String parentPropertyName,
			String childPropertyName, 
			String name, String summary) {	
		return new DoseRangesWizardStep(dialog, pm, new Pair<Class<? extends AbstractDose>, String>(beanClass, parentPropertyName), childPropertyName, name, summary);
	}
	
	public static DoseRangeWizardStep createOnBeanProperty(
			JDialog dialog, 
			DosedDrugTreatmentPresentation pm, 
			Class<? extends AbstractDose> beanClass,
			String propertyName, 
			String nextPropertyName, 
			String name, 
			String summary) {
		return new DoseRangeWizardStep(dialog, pm, new Pair<Class<? extends AbstractDose>, String>(beanClass, propertyName), nextPropertyName, name, summary);
	}
	
	public static WizardStep createOnKnownDoses(
			JDialog dialog, 
			DosedDrugTreatmentPresentation pm, 
			String name, String summary) {
		return new DoseRangeWizardStep(dialog, pm, null, null, name, summary);
	}

	private DoseRangeWizardStep(
			JDialog dialog, 
			DosedDrugTreatmentPresentation presentationModel,
			Pair<Class<? extends AbstractDose>, String> beanProperty,
			String nextPropertyName,
			String name, 
			String summary) {
		super(presentationModel, name, summary, null);
		d_beanProperty = beanProperty;
		d_nextPropertyName = nextPropertyName;
		DecisionTreeNode typeNode; // Defaults to fixed if null, also default for all known doses
		if (d_beanProperty == null) {
			typeNode = d_pm.getType(FixedDose.class);
		} else {
			typeNode = d_pm.getType(d_beanProperty.getKey());
		}
		d_family = new Family(d_pm, typeNode);
		attachListener(d_family.getChildren());
		d_rangeInputPresentation = new RangeInputPresentation(d_pm, d_beanProperty, d_nextPropertyName, d_family);
	}

	private void attachListener(ListModel model) {
		model.addListDataListener((new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				rebuildPanel();
			}
			
			public void intervalAdded(ListDataEvent e) {
				rebuildPanel();
			}
			
			public void contentsChanged(ListDataEvent e) {}
		}));
	}

	@Override 
	public void initialize() {
		populateFamily(d_family);
	}

	private void populateFamily(Family family) {
		DecisionTreeNodeBuilder<DoseRangeNode> nodeBuilder = new DecisionTreeNodeBuilder<DoseRangeNode>() {
			public DoseRangeNode create(DecisionTreeNode parent) {
				return createRangeNode(parent);
			}
		};
		populate(d_pm, nodeBuilder, family);
	}

	public static void populate(final DosedDrugTreatmentPresentation pm,
			DecisionTreeNodeBuilder<DoseRangeNode> nodeBuilder,
			Family family) {
		boolean shouldClear = false;
		for (DecisionTreeNode node : new ArrayList<DecisionTreeNode>(family.getChildren())) {
			if (!(node instanceof RangeNode)) {
				shouldClear = true;
			}
		}
		if (shouldClear || family.getChildren().isEmpty()) {
			pm.setSelected(family.parent, nodeBuilder.create(family.parent));
		}
	}
	
	public interface DecisionTreeNodeBuilder<ChildType extends DecisionTreeNode> {
		public ChildType create(DecisionTreeNode parent);
	}

	private DoseRangeNode createRangeNode(final DecisionTreeNode parent) {
		if (d_beanProperty == null) {
			return (DoseRangeNode)d_pm.setKnownDoses(new DoseRangeNode(null, null, d_pm.getDoseUnit()));
		} else {
			return new DoseRangeNode(d_beanProperty.getKey(), d_beanProperty.getValue(), d_pm.getDoseUnit());
		}
	}
	
	public ValueHolder<Boolean> getConsiderNextProperty() {
		return d_rangeInputPresentation.getConsiderNext();
	}
	
	protected JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:pref:grow, 3dlu, pref, 3dlu",
				"p"
				);
		
		PanelBuilder builder = new PanelBuilder(layout);
		int row = 1;
		
		RangeInputBuilder rangeBuilder = new RangeInputBuilder(d_dialog, d_rangeInputPresentation);
		row = rangeBuilder.addFamilyToPanel(builder, row);
		
		return builder.getPanel();
	}
}
