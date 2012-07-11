package org.drugis.addis.gui.wizard;


import static org.apache.commons.collections15.CollectionUtils.forAllDo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.DefaultFormatter;

import org.apache.commons.collections15.Closure;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseRangeNode;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.RangeValidator;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.util.AffixableFormat;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DoseRangeWizardStep extends AbstractDoseTreatmentWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;
	private final Class<? extends AbstractDose> d_beanClass;
	private final String d_propertyName;
	private final ObservableList<RangeNode> d_nodes = new ArrayListModel<RangeNode>();
	private DecisionTreeNode d_parent;
	private final class AddCutOffDialog extends OkCancelDialog {
		private static final long serialVersionUID = -7519390341921875264L;
		private final int d_rangeIndex;
		private final ValueHolder<Double> d_cutOff = new ModifiableHolder<Double>(0.0d);
		private final ValueHolder<Boolean> d_upperOpen = new ModifiableHolder<Boolean>(false);
		private String d_boundName;
		private final RangeValidator d_validator;
		private final RangeNode d_node;

		public AddCutOffDialog(int rangeIndex, String boundName) {
			super(d_mainWindow, "Split range", true);
			d_rangeIndex = rangeIndex;
			d_boundName = boundName;
			d_node = d_nodes.get(rangeIndex);
			d_validator = 
					new RangeValidator(d_cutOff, d_node.getRangeLowerBound(), d_node.getRangeUpperBound());
			getUserPanel().add(buildPanel());
			pack(); 
		}
		
		protected JPanel buildPanel() { 
			Bindings.bind(d_okButton, "enabled", d_validator);

			FormLayout layout = new FormLayout(
					"pref, 3dlu, pref, 3dlu, fill:pref:grow",
					"p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			final int colSpan = builder.getColumnCount();
			int row = 1;
			
			boolean nodeIsLast = (d_rangeIndex == d_nodes.size() - 1);
			builder.addSeparator("Original range: " + d_node.getLabel(nodeIsLast), cc.xyw(1, row, colSpan));
			
			row = LayoutUtil.addRow(layout, row);
			builder.addLabel("Split range at:", cc.xy(1, row));
			final JFormattedTextField cutOffField = BasicComponentFactory.createFormattedTextField(d_cutOff, new DefaultFormatter());
			cutOffField.setColumns(5);
			cutOffField.addCaretListener(new CaretListener() {		
				public void caretUpdate(CaretEvent e) {
					try {
						cutOffField.commitEdit();
						pack();
					} catch (ParseException exp) {
						return; // we don't care
					}
				}
			});
			
			builder.add(cutOffField, cc.xy(3, row));
			String unitText = d_pm.getDoseUnitPresentation().getBean().toString();
			builder.addLabel(unitText, cc.xy(5, row));
			
			row = LayoutUtil.addRow(layout, row);
			builder.addLabel("Bound is open for:", cc.xyw(1, row, colSpan));
			
			DecimalFormat decimalFormat = new DecimalFormat();
			decimalFormat.setMaximumFractionDigits(3);
			
			AffixableFormat formatLower = new AffixableFormat();
			formatLower.setMaximumFractionDigits(3);
			StringBuilder prefix = new StringBuilder()
				.append(decimalFormat.format(d_node.getRangeLowerBound()))
				.append((d_node.isRangeLowerBoundOpen() ? " < " : " <= "))
				.append(d_boundName + " < ");
			formatLower.setPrefix(prefix.toString());
			formatLower.setSuffix(" " + unitText);
			JLabel cutOffLower = BasicComponentFactory.createLabel(d_cutOff, formatLower);
			
			row = LayoutUtil.addRow(layout, row);
			builder.add(BasicComponentFactory.createRadioButton(d_upperOpen, false, ""), cc.xy(1, row));
			builder.add(cutOffLower, cc.xy(3, row));
			
			AffixableFormat formatUpper = new AffixableFormat();
			if ( d_rangeIndex < d_pm.getBean().getDecisionTree().getChildCount(d_parent) - 1) { 
				StringBuilder suffix = new StringBuilder()
					.append(" <= " + d_boundName)
					.append((d_node.isRangeUpperBoundOpen() ? " < " : " <= "))
					.append(decimalFormat.format(d_node.getRangeUpperBound()));
				formatLower.setSuffix(" " + unitText);
				formatUpper.setSuffix(suffix.toString());
			} else {
				formatUpper.setPrefix(d_boundName + " >= ");
				formatLower.setSuffix(" " + unitText);
			}
			JLabel cutOffUpper = BasicComponentFactory.createLabel(d_cutOff, formatUpper);
			
			row = LayoutUtil.addRow(layout, row);
			builder.add(BasicComponentFactory.createRadioButton(d_upperOpen, true, ""), cc.xy(1, row));
			builder.add(cutOffUpper, cc.xy(3, row));

			return builder.getPanel();
		}
		
		@Override
		protected void commit() {
			List<RangeNode> ranges = d_pm.splitRange(d_node, d_cutOff.getValue(), !d_upperOpen.getValue());
			d_nodes.set(d_rangeIndex, ranges.get(0));
			d_nodes.add(d_rangeIndex + 1, ranges.get(1));
			setVisible(false);
		}

		@Override
		protected void cancel() {
			setVisible(false);			
		} 
		
	}
	
	public static DoseRangeWizardStep createOnBeanProperty(
			DosedDrugTreatmentPresentation pm, 
			Domain domain, 
			AddisWindow mainWindow,
			Class<? extends AbstractDose> beanClass, 
			String propertyName) {
	
		return new DoseRangeWizardStep(pm, domain, mainWindow, beanClass, propertyName);
	}
	
	private DoseRangeWizardStep(DosedDrugTreatmentPresentation presentationModel, 
			Domain domain, 
			AddisWindow mainWindow,
			Class<? extends AbstractDose> beanClass, 
			String propertyName) {
		super(presentationModel, domain, mainWindow);
		d_beanClass = beanClass;
		d_propertyName = propertyName;
		d_pm.getBean().getDecisionTree();
		d_nodes.addListDataListener((new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {}
			
			public void intervalAdded(ListDataEvent e) {
				rebuildPanel();
			}
			
			public void contentsChanged(ListDataEvent e) {}
		}));
	}

	@Override 
	public void initialize() { 
		d_parent = d_pm.getNode(d_beanClass, d_propertyName);
		if(d_nodes.isEmpty()) {
			d_nodes.add(new DoseRangeNode(d_beanClass, d_propertyName, d_pm.getDoseUnit()));
		}
		forAllDo(d_nodes, new Closure<DecisionTreeNode>() {
			public void execute(DecisionTreeNode node) {
				d_pm.setSelected(d_parent, node);
			}
		});
		rebuildPanel();
	}
	
	protected JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:pref:grow, 3dlu, pref, 3dlu",
				"p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		
		int row = 1;
		for (int i = 0; i < d_nodes.size(); ++i) {
			row = rangeRow(layout, builder, row, i);
		}
		
		return builder.getPanel();
	}

	private int rangeRow(FormLayout layout, PanelBuilder builder, int row, final int index) {
		CellConstraints cc = new CellConstraints();
		row = LayoutUtil.addRow(layout, row);
		JButton splitBtn = new JButton("Split Range");
		splitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddCutOffDialog dialog = new AddCutOffDialog(index, "bound");
				GUIHelper.centerWindow(dialog, d_mainWindow);
				dialog.setVisible(true);
			}

		});
		builder.add(splitBtn, cc.xy(1, row));
		boolean nodeIsLast = (index == d_nodes.size() - 1);
		String rangeText = d_nodes.get(index).getLabel(nodeIsLast);
		builder.add(new JLabel(rangeText), cc.xy(3, row));
		
		final JComboBox comboBox = AddDosedDrugTreatmentWizardStep.createCategoryComboBox(
				d_pm.getCategories(),
				d_pm.getSelectedCategory(d_nodes.get(index)));
		comboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				Object selected = comboBox.getSelectedItem();
				d_pm.setSelected(d_nodes.get(index), selected);
			}
		});
		builder.add(comboBox, cc.xy(5, row));
		return row;
	}
}
