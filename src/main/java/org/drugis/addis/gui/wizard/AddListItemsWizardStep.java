/**
 * 
 */
package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.TypeWithName;
import org.drugis.addis.presentation.ListOfNamedValidator;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public abstract class AddListItemsWizardStep<T extends TypeWithName> extends PanelWizardStep {
	private PanelBuilder d_builder;
	private JScrollPane d_scrollPane;
	protected ListOfNamedValidator<T> d_validator;
	protected ObservableList<T> d_list;
	private String d_typeName;

	public AddListItemsWizardStep(String name, String summary, String typeName, ObservableList<T> list, int minItems) {
		super(name, summary);
		d_typeName = typeName;
		d_list = list;
		d_validator = new ListOfNamedValidator<T>(d_list, minItems);
	}

	protected abstract void addAdditionalFields(PanelBuilder builder, CellConstraints cc, int rows, int idx);
	protected abstract T createItem();
	protected abstract List<Note> getNotes(T t);

	public void rebuild() { 
		 this.setVisible(false);
		 
		 if (d_scrollPane != null)
			 remove(d_scrollPane);
		 buildWizardStep();
		 
		 this.setVisible(true);
	}

	@Override
	public void prepare() {
		 PropertyConnector.connectAndUpdate(d_validator, this, "complete");
		 rebuild();
	 }

	private void buildWizardStep() {
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, right:pref, 3dlu, pref:grow, 7dlu, right:pref, 3dlu, pref",
				"p"
				);
		layout.setColumnGroups(new int[][]{{3, 7}, {5, 9}});
		d_builder = new PanelBuilder(layout);
		d_builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		int rows = 1;
		d_builder.addSeparator(d_typeName + "s", cc.xyw(1, 1, 9));
		
		for(int i = 0; i < d_list.size(); ++i) {
			rows = addComponents(d_builder, layout, cc, rows, i);
		}
		
		rows = addRow(layout, rows);
		JButton addBtn = new JButton("Add " + d_typeName);
		d_builder.add(addBtn, cc.xy(1, rows));
		addBtn.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				d_list.add(createItem());
				rebuild();
			}
		});
		
		JPanel panel = d_builder.getPanel();
		this.setLayout(new BorderLayout());
		d_scrollPane = new JScrollPane(panel);
		d_scrollPane.getVerticalScrollBar().setUnitIncrement(16);
	
		add(d_scrollPane, BorderLayout.CENTER);
	}

	private int addComponents(PanelBuilder builder, FormLayout layout, CellConstraints cc, int rows, int idx) {
		rows = addRow(layout, rows);
		
		// add "remove" button 
		JButton removeBtn = new JButton("Remove");
		builder.add(removeBtn, cc.xy(1, rows));
		removeBtn.addActionListener(new RemoveItemListener(idx));
		
		// name input field
		builder.addLabel("Name: ", cc.xy (3, rows));
		JTextField nameField = BasicComponentFactory.createTextField(
				getNameModel(d_list.get(idx)), false);
		builder.add(nameField, cc.xy(5, rows));
		
		// type specific input fields
		addAdditionalFields(builder, cc, rows, idx);
		
		// notes
		rows = addRow(layout, rows);
		d_builder.add(AddStudyWizard.buildNotesEditor(getNotes(d_list.get(idx))), cc.xyw(5, rows, 5));
	
		return rows;
	}

	private int addRow(FormLayout layout, int rows) {
		LayoutUtil.addRow(layout);
		return rows + 2;
	}
	
	private ValueModel getNameModel(TypeWithName item) {
		return new PresentationModel<TypeWithName>(item).getModel(TypeWithName.PROPERTY_NAME);
	}
	
	class RemoveItemListener extends AbstractAction {
		int d_index;
		
		public RemoveItemListener(int index) {
			d_index = index;
		}
		
		public void actionPerformed(ActionEvent e) {
			d_list.remove(d_index);
			rebuild();
		}	
	}
}