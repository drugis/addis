package org.drugis.addis.gui.wizard;


import static org.apache.commons.collections15.CollectionUtils.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.collections15.Closure;
import org.apache.commons.math3.util.Pair;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseRangeNode;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DoseRangeWizardStep extends AbstractDoseTreatmentWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;
	
	private List<Family> d_families = new ArrayList<Family>();
	
	private Pair<Class<? extends AbstractDose>, String> d_beanProperty;
	private String d_childPropertyName;
	
	static class Family { 
		public final DecisionTreeNode parent;
		public final ObservableList<RangeNode> children;

		public Family(DecisionTreeNode parent, ObservableList<RangeNode> children) {
			this.parent = parent;
			this.children = children;	
		}
		
		/**
		 * Families equal on their parent.
		 */
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Family) { 
				Family other = (Family) obj;
				return parent.toString().equals(other.parent.toString());
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return children.hashCode() + 31 * parent.hashCode();
		}
		
		@Override
		public String toString() {
			String s = "";
			s = s + parent.toString() + "\n";
			for(RangeNode c : children) { 
				s = s + "\t\t child: " +  c + "\n";
			}
			return s;
		}
	}
	
	public static DoseRangeWizardStep createOnBeanPropertyChildren (
			DosedDrugTreatmentPresentation pm, 
			Class<? extends AbstractDose> beanClass, 
			String propertyName,
			String childPropertyName,
			String name, 
			String summary) {	
		return new DoseRangeWizardStep(pm, new Pair<Class<? extends AbstractDose>, String>(beanClass, propertyName), childPropertyName, name, summary);
	}
	
	public static DoseRangeWizardStep createOnBeanProperty(
			DosedDrugTreatmentPresentation pm, 
			Class<? extends AbstractDose> beanClass, 
			String propertyName,
			String name, 
			String summary) {
		return new DoseRangeWizardStep(pm, new Pair<Class<? extends AbstractDose>, String>(beanClass, propertyName), null, name, summary);
	}
	
	public static DoseRangeWizardStep createOnKnownDoses(
			DosedDrugTreatmentPresentation pm, 
			String name, 
			String summary) {
		return new DoseRangeWizardStep(pm, null, null, name, summary);
	}

	private DoseRangeWizardStep(
			DosedDrugTreatmentPresentation presentationModel, 
			Pair<Class<? extends AbstractDose>, String> beanProperty,
			String childPropertyName,
			String name, 
			String summary) {
		super(presentationModel, name, summary);
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
		DecisionTreeNode typeNode = (d_beanProperty == null) ? d_pm.getType(FixedDose.class) : d_pm.getType(d_beanProperty.getKey());
		if(d_childPropertyName != null) {
			ArrayList<Family> childrenList = new ArrayList<Family>();
			for(DecisionTreeNode parent : d_pm.getChildNodes(typeNode)) {
				ArrayListModel<RangeNode> children = new ArrayListModel<RangeNode>();
				attachListener(children);
				childrenList.add(new Family(parent, children));
			}
			d_families.addAll(childrenList);
		} else { 
			ArrayListModel<RangeNode> children = new ArrayListModel<RangeNode>();
			attachListener(children);
			DecisionTreeNode parent = typeNode;
			d_families.add(new Family(parent, children));
		}
		populateChildren();
	}

	private void populateChildren() {
		for (Family family : d_families) {
			populateFamily(family);
		}
	}

	private void populateFamily(Family family) {
		ObservableList<RangeNode> nodes = family.children;
		final DecisionTreeNode parent = family.parent;
		nodes.clear();
		for(DecisionTreeNode node : d_pm.getChildNodes(parent)) {
			if(node instanceof RangeNode) { 
				nodes.add((RangeNode)node);
			}
		}
		if(nodes.isEmpty()) {
			DoseRangeNode rangeNode = createRangeNode(parent, d_beanProperty);
			nodes.add(rangeNode);
		}
		forAllDo(nodes, new Closure<DecisionTreeNode>() {
			public void execute(DecisionTreeNode node) {
				d_pm.setSelected(parent, node);
			}
		});
	}

	private DoseRangeNode createRangeNode(
			final DecisionTreeNode parent,
			final Pair<Class<? extends AbstractDose>, 
			String> beanProperty) {
		if (d_childPropertyName != null) {
			RangeNode start = (RangeNode)parent;
			return new DoseRangeNode(
					beanProperty.getKey(),
					d_childPropertyName, 
					start.getRangeLowerBound(),
					start.isRangeLowerBoundOpen(),
					Double.POSITIVE_INFINITY,
					false,
					d_pm.getDoseUnit());
		} else {
			if (d_beanProperty == null) {
				return (DoseRangeNode)d_pm.setKnownDoses(new DoseRangeNode(null, null, d_pm.getDoseUnit()));
			} else {
				return new DoseRangeNode(beanProperty.getKey(), beanProperty.getValue(), d_pm.getDoseUnit());
			}
		}
	}
	
	protected JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:pref:grow, 3dlu, pref, 3dlu",
				"p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		int row = 1;
		
		for(Family family : d_families) {
			CellConstraints cc = new CellConstraints();
			DecisionTreeNode parent = family.parent;
			if (parent instanceof RangeNode) {
				boolean nodeIsLast = parent.equals(d_families.get(d_families.size() - 1).parent);
				row = LayoutUtil.addRow(layout, row);
				builder.addSeparator(((RangeNode)parent).getLabel(nodeIsLast), cc.xyw(1, row, 6));
			}
			
			ObservableList<RangeNode> children = family.children;
			for (int i = 0; i < children.size(); ++i) {
				row = rangeRow(layout, builder, row, family, i);
			}
		}
		return builder.getPanel();
	}

	private int rangeRow(FormLayout layout,
			PanelBuilder builder, 
			int row, 
			final Family family,
			final int index) {
		CellConstraints cc = new CellConstraints();
		row = LayoutUtil.addRow(layout, row);
		
		JButton splitBtn = new JButton("Split Range");
		splitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DoseRangeCutOffDialog dialog;
				if (d_beanProperty != null) {
					dialog = new DoseRangeCutOffDialog(d_pm, index, family, GUIHelper.humanize(d_beanProperty.getValue()), false);
				} else {
					dialog = new DoseRangeCutOffDialog(d_pm, index, family, "known doses", true);
				}
				GUIHelper.centerWindow(dialog, d_mainWindow);
				dialog.setVisible(true);
			}

		});
		builder.add(splitBtn, cc.xy(1, row));
		boolean nodeIsLast = (index == family.children.size() - 1);
		String rangeText = family.children.get(index).getLabel(nodeIsLast);
		builder.add(new JLabel(rangeText), cc.xy(3, row));
		
		final JComboBox comboBox = AddDosedDrugTreatmentWizardStep.createCategoryComboBox(d_pm.getCategories());
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object selected = comboBox.getSelectedItem();
				if(d_beanProperty == null) { 
					d_pm.setKnownDoses(family.children.get(index), selected);
				} else {
					d_pm.setSelected(family.children.get(index), selected);
				}
			}
		});
		builder.add(comboBox, cc.xy(5, row));
		return row;
	}
}
