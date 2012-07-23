package org.drugis.addis.gui.wizard;


import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.math3.util.Pair;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseRangeNode;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.gui.wizard.DoseRangeWizardStep.DecisionTreeNodeBuilder;
import org.drugis.addis.gui.wizard.DoseRangeWizardStep.Family;
import org.drugis.addis.gui.wizard.DoseRangeWizardStep.RangeInputBuilder;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class DoseRangesWizardStep extends AbstractDoseTreatmentWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;
	
	private List<Family> d_families = new ArrayList<Family>();
	
	private Pair<Class<? extends AbstractDose>, String> d_beanProperty;
	private String d_childPropertyName;

	public DoseRangesWizardStep(
			JDialog dialog,
			DosedDrugTreatmentPresentation presentationModel, 
			Pair<Class<? extends AbstractDose>, String> beanProperty,
			String childPropertyName,
			String name, 
			String summary) {
		super(presentationModel, name, summary, dialog);
		d_beanProperty = beanProperty;
		d_childPropertyName = childPropertyName;
	}

	private void attachListener(ListModel model) {
		model.addListDataListener((new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {}
			
			public void intervalAdded(ListDataEvent e) {
				rebuildPanel();
			}
			
			public void contentsChanged(ListDataEvent e) {}
		}));
	}

	@Override 
	public void initialize() {
		d_families.clear();
		DecisionTreeNode typeNode = d_pm.getType(d_beanProperty.getKey());
		ArrayList<Family> childrenList = new ArrayList<Family>();
		for(DecisionTreeNode parent : d_pm.getChildNodes(typeNode)) {
			Family family = new Family(d_pm, parent);
			childrenList.add(family);
			attachListener(family.getChildren());
		}
		d_families.addAll(childrenList);
		populateChildren();
	}

	private void populateChildren() {
		DecisionTreeNodeBuilder<DoseRangeNode> nodeBuilder = new DecisionTreeNodeBuilder<DoseRangeNode>() {
			public DoseRangeNode create(DecisionTreeNode parent) {
				return createRangeNode(parent);
			}
		};
		for (Family family : d_families) {
			DoseRangeWizardStep.populate(d_pm, nodeBuilder, family);
		}
	}

	private DoseRangeNode createRangeNode(final DecisionTreeNode parent) {
		RangeNode start = (RangeNode)parent;
		return new DoseRangeNode(
				d_beanProperty.getKey(),
				d_childPropertyName, 
				start.getRangeLowerBound(),
				start.isRangeLowerBoundOpen(),
				Double.POSITIVE_INFINITY,
				false,
				d_pm.getDoseUnit());
	}
	
	protected JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:pref:grow, 3dlu, pref, 3dlu",
				"p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		int row = 1;
		
		for(Family family : d_families) {
			RangeInputBuilder rangeBuilder = new RangeInputBuilder(d_dialog, d_pm, d_beanProperty, family);
			row = rangeBuilder.addFamilyToPanel(builder, row);
		}
		return builder.getPanel();
	}
}