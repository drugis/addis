/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.TypeWithName;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.ListOfNamedValidator;
import org.drugis.addis.presentation.wizard.AddListItemsPresentation;
import org.drugis.common.event.IndifferentListDataListener;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
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
	private ListOfNamedValidator<T> d_validator;
	protected AddListItemsPresentation<T> d_pm;
	private final JDialog d_parent;

	public AddListItemsWizardStep(String name, String summary, AddListItemsPresentation<T> pm, JDialog dialog) {
		super(name, summary);
		d_pm = pm;
		d_parent = dialog;
		resetUnderlyingList();
		d_pm.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				resetUnderlyingList();
				rebuild();
			}
		});
	}

	private void resetUnderlyingList() {
		d_validator = new ListOfNamedValidator<T>(d_pm.getList(), d_pm.getMinElements());
		PropertyConnector.connectAndUpdate(d_validator, this, "complete");
		d_pm.getList().addListDataListener(new IndifferentListDataListener() {
			public void update() {
				rebuild();
			}
		});
	}

	protected abstract void addAdditionalFields(PanelBuilder builder, CellConstraints cc, int rows, int idx);

	protected T createItem() {
		return d_pm.createItem();
	}
	protected ObservableList<Note> getNotes(T t) {
		return d_pm.getNotes(t);
	}

	public void rebuild() {
		this.setVisible(false);

		if (d_scrollPane != null) remove(d_scrollPane);
		buildWizardStep();

		this.setVisible(true);
	}

	@Override
	public void prepare() {
		rebuild();
	 }

	private void buildWizardStep() {
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, right:pref, 3dlu, fill:pref:grow, 7dlu, right:pref, 3dlu, pref",
				"p"
				);
		d_builder = new PanelBuilder(layout);
		d_builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();

		int rows = 1;
		d_builder.addSeparator(d_pm.getItemName() + "s", cc.xyw(1, 1, 9));

		for(int i = 0; i < d_pm.getList().size(); ++i) {
			rows = addComponents(d_builder, layout, cc, rows, i);
		}

		rows = LayoutUtil.addRow(layout, rows);
		JButton addBtn = new JButton("Add " + d_pm.getItemName());
		d_builder.add(addBtn, cc.xy(1, rows));
		addBtn.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				d_pm.getList().add(createItem());
			}
		});

		JPanel panel = d_builder.getPanel();
		this.setLayout(new BorderLayout());
		d_scrollPane = new JScrollPane(panel);
		d_scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		add(d_scrollPane, BorderLayout.CENTER);
	}

	private int addComponents(PanelBuilder builder, FormLayout layout, CellConstraints cc, int rows, final int idx) {
		rows = LayoutUtil.addRow(layout, rows);

		// add "remove" button
		JButton removeBtn = new JButton("Remove");
		Bindings.bind(removeBtn, "enabled", d_pm.getRemovable(d_pm.getList().get(idx)));
		builder.add(removeBtn, cc.xy(1, rows));
		removeBtn.addActionListener(new RemoveItemListener(idx));

		// name input field
		builder.addLabel("Name: ", cc.xy (3, rows));
		JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel nameField = BasicComponentFactory.createLabel(getNameModel(idx));
		namePanel.add(nameField);
		JButton editNameButton = new JButton(Main.IMAGELOADER.getIcon(FileNames.ICON_EDIT));
		editNameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showRenameDialog(idx);
			}
		});
		namePanel.add(editNameButton);
		builder.add(namePanel, cc.xy(5, rows));

		// type specific input fields
		addAdditionalFields(builder, cc, rows, idx);

		// notes
		rows = LayoutUtil.addRow(layout, rows);
		d_builder.add(AddStudyWizard.buildNotesEditor(getNotes(d_pm.getList().get(idx))), cc.xyw(5, rows, 5));

		return rows;
	}

	private void showRenameDialog(final int idx) {
		JDialog renameDialog = new AddListItemsRenameDialog(d_parent, "Rename " + d_pm.getItemName(), true, idx);
		renameDialog.setVisible(true);
	}

	private ValueModel getNameModel(final int idx) {
		return new PresentationModel<TypeWithName>(d_pm.getList().get(idx)).getModel(TypeWithName.PROPERTY_NAME);
	}

	private class AddListItemsRenameDialog extends RenameDialog {
		private AddListItemsRenameDialog(Dialog owner, String title, boolean modal, int idx) {
			super(owner, title, modal, d_pm.getList(), idx);
		}

		@Override
		protected void rename(String newName) {
			d_pm.rename(d_idx, newName);
		}
	}

	private class RemoveItemListener extends AbstractAction {
		int d_index;

		public RemoveItemListener(int index) {
			d_index = index;
		}

		public void actionPerformed(ActionEvent e) {
			d_pm.remove(d_index);
		}
	}
}