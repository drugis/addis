package org.drugis.addis.gui.wizard;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.math3.util.Pair;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseRangeNode;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.WizardStep;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DoseRangeWizardStep extends AbstractDoseTreatmentWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;
	
	private Pair<Class<? extends AbstractDose>, String> d_beanProperty;
	private Family d_family;
	
	public static class RangeInputBuilder {
		private final Pair<Class<? extends AbstractDose>, String> d_beanProperty;
		private final Map<RangeNode, Object> d_selections = new HashMap<RangeNode, Object>();
		private final Family d_family;
		private JDialog d_dialog;
		private DosedDrugTreatmentPresentation d_pm;
		
		public RangeInputBuilder(JDialog dialog, DosedDrugTreatmentPresentation presentationModel,
				Pair<Class<? extends AbstractDose>, String> beanProperty, Family family) {
			d_dialog = dialog;
			d_pm = presentationModel;
			d_beanProperty = beanProperty;
			d_family = family;
		}
		
		public int addFamilyToPanel(PanelBuilder builder, int row) {
			FormLayout layout = builder.getLayout();
			CellConstraints cc = new CellConstraints();
			DecisionTreeNode parent = d_family.parent;
			if (parent instanceof RangeNode) {
				boolean nodeIsLast = Double.isInfinite(((RangeNode) parent).getRangeUpperBound());
				row = LayoutUtil.addRow(layout, row);
				builder.addSeparator(((RangeNode)parent).getLabel(nodeIsLast), cc.xyw(1, row, 6));
			}
			
			ObservableList<DecisionTreeNode> children = d_family.getChildren();
			for (int i = 0; i < children.size(); ++i) {
				row = rangeRow(layout, builder, row, d_family, i);
			}
			return row;
		}

		private int rangeRow(FormLayout layout,
				PanelBuilder builder, 
				int row, 
				final Family family,
				final int index) {
			
			if (!(family.getChildren().get(index) instanceof RangeNode)) {
				return row;
			}
			
			CellConstraints cc = new CellConstraints();
			row = LayoutUtil.addRow(layout, row);
			
			JButton splitBtn = new JButton("Split Range");
			splitBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DoseRangeCutOffDialog dialog;
					if (d_beanProperty != null) {
						dialog = new DoseRangeCutOffDialog(d_dialog, d_pm, index, family, GUIHelper.humanize(d_beanProperty.getValue()), false);
					} else {
						dialog = new DoseRangeCutOffDialog(d_dialog, d_pm, index, family, "quantity", true);
					}
					dialog.setVisible(true);
				}

			});
			builder.add(splitBtn, cc.xy(1, row));
			final RangeNode rangeNode = (RangeNode)family.getChildren().get(index);
			boolean nodeIsLast = Double.isInfinite(rangeNode.getRangeUpperBound());
			String rangeText = rangeNode.getLabel(nodeIsLast);
			builder.add(new JLabel(rangeText), cc.xy(3, row));
			
			final JComboBox comboBox = AddDosedDrugTreatmentWizardStep.createCategoryComboBox(d_pm.getCategories());
			if(d_selections.get(rangeNode) != null) { 
				comboBox.setSelectedItem(d_selections.get(rangeNode));
			}
			comboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						Object selected = comboBox.getSelectedItem();
						if(d_beanProperty == null) { 
							d_pm.setKnownDoses(rangeNode, selected);
						} else {
							d_pm.setSelected(rangeNode, selected);
						}
						d_selections.put(rangeNode, selected);
					}
				}
			});
			builder.add(comboBox, cc.xy(5, row));
			return row;
		}
	}

	public static class Family { 
		public final DecisionTreeNode parent;
		private final ObservableList<DecisionTreeNode> children;
		
		public Family(DosedDrugTreatmentPresentation pm, DecisionTreeNode parent) {
			this.parent = parent;
			this.children = pm.getChildNodes(parent);
		}
		
		/**
		 * Families equal on their parent.
		 */
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Family) { 
				Family other = (Family) obj;
				return parent.similar(other.parent);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return getChildren().hashCode() + 31 * parent.hashCode();
		}
		
		@Override
		public String toString() {
			String s = "";
			s = s + parent.toString() + "\n";
			for(DecisionTreeNode c : getChildren()) { 
				s = s + "\t\t child: " +  c + "\n";
			}
			return s;
		}

		public ObservableList<DecisionTreeNode> getChildren() {
			return children;
		}
	}
	
	public static WizardStep createOnBeanPropertyChildren (
			JDialog dialog, 
			DosedDrugTreatmentPresentation pm, 
			Class<? extends AbstractDose> beanClass,
			String propertyName,
			String childPropertyName, 
			String name, String summary) {	
		return new DoseRangesWizardStep(dialog, pm, new Pair<Class<? extends AbstractDose>, String>(beanClass, propertyName), childPropertyName, name, summary);
	}
	
	public static WizardStep createOnBeanProperty(
			JDialog dialog, 
			DosedDrugTreatmentPresentation pm, 
			Class<? extends AbstractDose> beanClass,
			String propertyName, 
			String name, String summary) {
		return new DoseRangeWizardStep(dialog, pm, new Pair<Class<? extends AbstractDose>, String>(beanClass, propertyName), name, summary);
	}
	
	public static WizardStep createOnKnownDoses(
			JDialog dialog, 
			DosedDrugTreatmentPresentation pm, 
			String name, String summary) {
		return new DoseRangeWizardStep(dialog, pm, null, name, summary);
	}

	private DoseRangeWizardStep(
			JDialog dialog, 
			DosedDrugTreatmentPresentation presentationModel,
			Pair<Class<? extends AbstractDose>, String> beanProperty,
			String name, 
			String summary) {
		super(presentationModel, name, summary, null);
		d_beanProperty = beanProperty;
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
	public void initialize() { // FIXME: move code into constructor instead of prepare().
		DecisionTreeNode typeNode; // Defaults to fixed if null, also default for all known doses
		if (d_beanProperty == null) {
			typeNode = d_pm.getType(FixedDose.class);
		} else {
			typeNode = d_pm.getType(d_beanProperty.getKey());
		}
		d_family = new Family(d_pm, typeNode);
		attachListener(d_family.getChildren());

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
	
	protected JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:pref:grow, 3dlu, pref, 3dlu",
				"p"
				);
		
		PanelBuilder builder = new PanelBuilder(layout);
		int row = 1;
		
		RangeInputBuilder rangeBuilder = new RangeInputBuilder(d_dialog, d_pm, d_beanProperty, d_family);
		row = rangeBuilder.addFamilyToPanel(builder, row);
		
		return builder.getPanel();
	}
}
