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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.MetaAnalysis;
import org.drugis.addis.entities.MetaStudy;
import org.drugis.common.gui.ViewBuilder;


import com.jgoodies.forms.builder.ButtonBarBuilder2;

@SuppressWarnings("serial")
public class MetaAnalysisDialog extends JDialog {
	
	private MetaAnalysis d_analysis;
	private Domain d_domain;
	private Main d_main;

	public MetaAnalysisDialog(Main parent, Domain domain, MetaAnalysis analysis) {
		super(parent, "Meta-Analysis");
		this.setModal(true);
		this.d_main = parent;
		d_domain = domain;
		d_analysis = analysis;
		
		initComponents();
		pack();
	}

	private void initComponents() {
		ViewBuilder view = new MetaAnalysisView(d_analysis,d_main.getPresentationModelManager());
		add(view.buildPanel(), BorderLayout.CENTER);
		JButton closeButton = new JButton("Close");
		closeButton.setMnemonic('c');
		closeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		JButton saveButton = new JButton("Save");
		saveButton.setMnemonic('s');
		saveButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				saveAsStudy();
			}			
		});
		
		ButtonBarBuilder2 bbuilder = new ButtonBarBuilder2();
		bbuilder.addButton(closeButton);
		bbuilder.addGlue();
		bbuilder.addButton(saveButton);
		
		add(bbuilder.getPanel(), BorderLayout.SOUTH);
	}

	protected void saveAsStudy() {
		String res = JOptionPane.showInputDialog(this, "Input name for new analysis", 
				"Save meta-analysis", JOptionPane.QUESTION_MESSAGE);
		if (res != null) {
			MetaStudy study = new MetaStudy(res, d_analysis);
			d_domain.addStudy(study);
			setVisible(false);
			d_main.leftTreeFocusOnMetaStudy(study);			
		}
	}

}
