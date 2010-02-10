package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.presentation.SelectFromFiniteListPresentationModel;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class SelectFromFiniteListWizardStep<T> extends PanelWizardStep {
	private SelectFromFiniteListPresentationModel<T> d_pm;
	
	private class RemoveSlotListener extends AbstractAction {
		int d_index;
		
		public RemoveSlotListener(int index) {
			d_index = index;
		}
		
		public void actionPerformed(ActionEvent e) {
			d_pm.removeSlot(d_index);
			prepare();
		}	
	} 
		
	private class AddOptionButtonListener extends AbstractAction {
		int d_index;

		public AddOptionButtonListener(int index) {
			d_index = index;
		}
		
		public void actionPerformed(ActionEvent e) {
			d_pm.showAddOptionDialog(d_index);
		}
	}
		
	private PanelBuilder d_builder;
	private JScrollPane d_scrollPane;
		
	public SelectFromFiniteListWizardStep(SelectFromFiniteListPresentationModel<T> pm) {
		super(pm.getTitle(), pm.getDescription());
		this.setLayout(new BorderLayout());
		d_pm = pm;
		setComplete((Boolean)d_pm.getInputCompleteModel().getValue());
		d_pm.getInputCompleteModel().addValueChangeListener(new CompleteListener(this));
	}
		
	 public void prepare() {
		 this.setVisible(false);
		 
		 if (d_scrollPane != null)
			 remove(d_scrollPane);
		 
		 buildWizardStep();
		 this.setVisible(true);
		 repaint();
	 }
		 
	private void buildWizardStep() {
		FormLayout layout = new FormLayout(
				"center:pref, 3dlu, right:pref, 3dlu, fill:pref:grow, 3dlu, left:pref",
				"p, 3dlu, p"
				);	
		d_builder = new PanelBuilder(layout);
		d_builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		int row = buildSlotsPart(7, cc, 1, layout);
		
		// Add slot button
		row += 2;
		JButton btn = new JButton("Add " + d_pm.getTypeName());
		d_builder.add(btn, cc.xy(1, row));
		Bindings.bind(btn, "enabled", d_pm.getAddSlotsEnabledModel());
		btn.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				d_pm.addSlot();
				prepare();
			}
		});
	
		JPanel panel = d_builder.getPanel();
		d_scrollPane = new JScrollPane(panel);
		d_scrollPane.getVerticalScrollBar().setUnitIncrement(16);
	
		add(d_scrollPane, BorderLayout.CENTER);
	}

	private int buildSlotsPart(int fullWidth, CellConstraints cc, int row, FormLayout layout) {
		for(int i = 0; i < d_pm.countSlots(); ++i){
			LayoutUtil.addRow(layout);
			row+=2;
			
			// add 'remove' button
			JButton btn = new JButton("Remove " + d_pm.getTypeName());
			d_builder.add(btn, cc.xy(1, row));
			btn.addActionListener(new RemoveSlotListener(i));
			
			// add label
			d_builder.addLabel(d_pm.getTypeName() + ": ", cc.xy(3, row));
			
			// dropdown
			JComboBox endpoints = AuxComponentFactory.createBoundComboBox(d_pm.getOptions(), d_pm.getSlot(i));
			d_builder.add(endpoints, cc.xy(5, row));
			
			// possibly add "new X" button
			if (d_pm.hasAddOptionDialog()) {
				JButton addOptionButton = GUIFactory.createPlusButton("Add new " + d_pm.getTypeName());
				addOptionButton.addActionListener(new AddOptionButtonListener(i));
				d_builder.add(addOptionButton, cc.xy(7, row));
			}
		}
		return row;	
	}
}
