package org.drugis.addis.gui.wizard;


import static org.apache.commons.collections15.CollectionUtils.forAllDo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.drugis.addis.entities.FlexibleDose;
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
	
	private Map<Pair<Class<? extends AbstractDose>, String>, List<Family>> d_families = 
			new HashMap<Pair<Class<? extends AbstractDose>,String>, List<Family>>();
	
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
		Pair<Class<? extends AbstractDose>, String> beanProperty = new Pair<Class<? extends AbstractDose>, String>(beanClass, propertyName);
		return new DoseRangeWizardStep(pm, Collections.singleton(beanProperty), childPropertyName, name, summary);
	}
	
	public static DoseRangeWizardStep createOnBeanProperty(
			DosedDrugTreatmentPresentation pm, 
			Class<? extends AbstractDose> beanClass, 
			String propertyName,
			String name, 
			String summary) {
		Pair<Class<? extends AbstractDose>, String> beanProperty = new Pair<Class<? extends AbstractDose>, String>(beanClass, propertyName);
		return new DoseRangeWizardStep(pm, Collections.singleton(beanProperty), null, name, summary);
	}
	
	public static DoseRangeWizardStep createOnKnownDose(
			DosedDrugTreatmentPresentation pm, 
			String name, 
			String summary) {
		Set<Pair<Class<? extends AbstractDose>, String>> set = new HashSet<Pair<Class<? extends AbstractDose>, String>>();
		set.add(new Pair<Class<? extends AbstractDose>, String>(FixedDose.class, FixedDose.PROPERTY_QUANTITY)); 
		set.add(new Pair<Class<? extends AbstractDose>, String>(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE));
		set.add(new Pair<Class<? extends AbstractDose>, String>(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE));
		return new DoseRangeWizardStep(pm, set, null, name, summary);
	}

	private DoseRangeWizardStep(
			DosedDrugTreatmentPresentation presentationModel, 
			Set<Pair<Class<? extends AbstractDose>, String>> beanProperties,
			String childPropertyName,
			String name, 
			String summary) {
		super(presentationModel, name, summary);
		for(Pair<Class<? extends AbstractDose>, String> pair : beanProperties) { 
			d_families.put(pair, null);
		}
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
		for (Pair<Class<? extends AbstractDose>, String> pair : d_families.keySet()) {
			if(d_childPropertyName != null) {
				ArrayList<Family> childrenList = new ArrayList<Family>();
				for(DecisionTreeNode parent : d_pm.getChildNodes(d_pm.getType(pair.getKey()))) {
					ArrayListModel<RangeNode> children = new ArrayListModel<RangeNode>();
					attachListener(children);
					
					childrenList.add(new Family(parent, children));
				}
				d_families.put(pair, childrenList);
			} else { 
				ArrayListModel<RangeNode> children = new ArrayListModel<RangeNode>();
				attachListener(children);
				DecisionTreeNode parent = d_pm.getType(pair.getKey());
				d_families.put(pair, Collections.singletonList(new Family(parent, children)));
			}
		}
		populateChildren();
	}

	private void populateChildren() {
		for(final Entry<Pair<Class<? extends AbstractDose>, String>, List<Family>> entry : d_families.entrySet()) {
			for (Family family : entry.getValue()) {
				populateFamily(entry.getKey(), family);
			}
		}
	}

	private void populateFamily(
			final Pair<Class<? extends AbstractDose>, String> entry,
			Family family) {
		ObservableList<RangeNode> nodes = family.children;
		final DecisionTreeNode parent = family.parent;
		nodes.clear();
		for(DecisionTreeNode node : d_pm.getChildNodes(parent)) {
			if(node instanceof RangeNode) { 
				nodes.add((RangeNode)node);
			}
		}
		if(nodes.isEmpty()) {
			DoseRangeNode rangeNode = createRangeNode(parent, entry);
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
			return new DoseRangeNode(beanProperty.getKey(), beanProperty.getValue(), d_pm.getDoseUnit());
		}
	}
	
	protected JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:pref:grow, 3dlu, pref, 3dlu",
				"p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		int row = 1;
		
		final Collection<List<Family>> families = d_families.values();
		
		final List<Family> firstNode = new ArrayList<List<Family>>(families).get(0);
		for(Family family : firstNode) {
			CellConstraints cc = new CellConstraints();
			DecisionTreeNode parent = family.parent;
			if (parent instanceof RangeNode) {
				boolean nodeIsLast = parent.equals(firstNode.get(firstNode.size() - 1).parent);
				row = LayoutUtil.addRow(layout, row);
				builder.addSeparator(((RangeNode)parent).getLabel(nodeIsLast), cc.xyw(1, row, 6));
			}
			
			ObservableList<RangeNode> children = family.children;
			for (int i = 0; i < children.size(); ++i) {
				row = rangeRow(layout, builder, row, family, families, i);
			}
		}
		return builder.getPanel();
	}

	private int rangeRow(FormLayout layout,
			PanelBuilder builder, 
			int row, 
			final Family family,
			final Collection<List<Family>> families, 
			final int index) {
		CellConstraints cc = new CellConstraints();
		row = LayoutUtil.addRow(layout, row);
		
		JButton splitBtn = new JButton("Split Range");
		splitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DoseRangeCutOffDialog dialog = new DoseRangeCutOffDialog(d_pm, index, family, families, "bound");
				GUIHelper.centerWindow(dialog, d_mainWindow);
				dialog.setVisible(true);
			}

		});
		builder.add(splitBtn, cc.xy(1, row));
		boolean nodeIsLast = (index == family.children.size() - 1);
		String rangeText = family.children.get(index).getLabel(nodeIsLast);
		builder.add(new JLabel(rangeText), cc.xy(3, row));
		
		final JComboBox comboBox = AddDosedDrugTreatmentWizardStep.createCategoryComboBox(
				d_pm.getCategories());
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object selected = comboBox.getSelectedItem();
				d_pm.setSelected(family.children.get(index), selected);
			}
		});
		builder.add(comboBox, cc.xy(5, row));
		return row;
	}
}
