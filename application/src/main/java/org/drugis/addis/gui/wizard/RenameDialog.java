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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.drugis.addis.entities.TypeWithName;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public abstract class RenameDialog extends OkCancelDialog {
	private static final long serialVersionUID = -7504316676802005798L;

	protected final List<? extends TypeWithName> d_list;
	protected final int d_idx;
	private final ValueHolder<String> d_name;
	private ValueHolder<Boolean> d_okEnabledModel = new ModifiableHolder<Boolean>(true);

	public RenameDialog(Dialog owner, String title, boolean modal, List<? extends TypeWithName> list, int idx) {
		super(owner, title, modal);
		d_list = list;
		setLocationRelativeTo(owner);
		d_idx = idx;
		d_name = new ModifiableHolder<String>(list.get(d_idx).getName());
		d_name.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				d_okEnabledModel.setValue(isCommitAllowed());
			}
		});
		initComponents();
	}

	private boolean nameIsUnique() {
		for(int i = 0; i < d_list.size(); ++i) {
			if (i != d_idx && d_name.getValue().equals(d_list.get(i).getName())) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("serial")
	private void initComponents() {
		FormLayout layout = new FormLayout("pref, 3dlu, fill:pref:grow", "p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();
		builder.addLabel("Name: ", cc.xy(1, 1));
		JTextField nameField = BasicComponentFactory.createTextField(d_name, false);
		nameField.setColumns(18);
		builder.add(nameField, cc.xy(3, 1));

		getUserPanel().setLayout(new BorderLayout());
		getUserPanel().add(builder.getPanel(), BorderLayout.CENTER);
		pack();

		getUserPanel().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "submit");
		getUserPanel().getActionMap().put("submit", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				commit();
			}
		});

		Bindings.bind(d_okButton, "enabled", d_okEnabledModel);
	}

	protected void commit() {
		if (isCommitAllowed()) {
			rename(d_name.getValue());
			dispose();
		}
	}

	protected abstract void rename(String newName);

	protected void cancel() {
		dispose();
	}

	private boolean isCommitAllowed() {
		return !d_name.getValue().isEmpty() && nameIsUnique();
	}
}