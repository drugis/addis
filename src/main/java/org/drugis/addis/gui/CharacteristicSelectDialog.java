/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package org.drugis.addis.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JSeparator;

import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.presentation.MetaStudyPresentationModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;

@SuppressWarnings("serial")
public class CharacteristicSelectDialog extends JDialog {
	private MetaStudyPresentationModel d_pm;

	public CharacteristicSelectDialog(JFrame parent,
			MetaStudyPresentationModel pm) {
		super(parent, "Select Characteristics to Show");
		this.d_pm = pm;
		setModal(true);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		initComponents();
		pack();
	}

	private void initComponents() {
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			JCheckBox b = BasicComponentFactory.createCheckBox(d_pm.getCharacteristicVisibleModel(c),
					c.getDescription());
			getContentPane().add(b);
		}
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		getContentPane().add(new JSeparator());
		getContentPane().add(okButton);
	}

}
