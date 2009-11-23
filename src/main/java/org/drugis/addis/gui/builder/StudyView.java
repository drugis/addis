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

package org.drugis.addis.gui.builder;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.MutableStudy;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.StudyAddPatientGroupDialog;
import org.drugis.addis.presentation.StudyPresentationModel;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class StudyView implements ViewBuilder {
	private PresentationModel<Study> d_model;
	private Domain d_domain;
	private Main d_mainWindow;
	private StudyCharacteristicsView d_charView;
	private StudyEndpointsView d_epView;
	private StudyDataView d_dataView;
	
	
	public StudyView(StudyPresentationModel model, Domain domain, Main main) {
		d_model = model;
		d_mainWindow = main;
		d_domain = domain;
		d_charView = new StudyCharacteristicsView(model);
		d_epView = new StudyEndpointsView(model, main);
		d_dataView = new StudyDataView(model, main.getPresentationModelManager());
	}
	
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout( 
				"pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		int row = 1;
		builder.addSeparator("Study", cc.xy(1,row));
		row += 2;
		builder.add(d_charView.buildPanel(), cc.xy(1, 3));
		row += 2;
		
		builder.addSeparator("Endpoints", cc.xy(1, row));
		row += 2;
		
		JComponent inPanel = d_epView.buildPanel();
		
		JPanel topPane = GUIFactory.createCollapsiblePanel(inPanel);
		
		builder.add(topPane, cc.xy(1, row));
		row += 2;
		
		if (d_model.getBean() instanceof BasicStudy) {
			LayoutUtil.addRow(layout);
			builder.add(buildAddEndpointButton(), cc.xy(1, row));
			row += 2;
		}		
		builder.addSeparator("Data", cc.xy(1, row));
		row += 2;
		builder.add(d_dataView.buildPanel(), cc.xy(1, row));
		row += 2;
		
		if (d_model.getBean() instanceof BasicStudy) {
			LayoutUtil.addRow(layout);
			builder.add(buildAddPatientGroupButton(), cc.xy(1, row));
		}
		return builder.getPanel();
	}

	private JComponent buildAddPatientGroupButton() {
		ButtonBarBuilder2 bb = new ButtonBarBuilder2();
		JButton addGroupButton = new JButton("Add patient group");
		addGroupButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				addPatientGroup();
			}			
		});
		bb.addButton(addGroupButton);
		bb.addGlue();
		return bb.getPanel();
	}

	private void addPatientGroup() {
		StudyAddPatientGroupDialog dlg = new StudyAddPatientGroupDialog(d_mainWindow, d_domain,
				(BasicStudy)d_model.getBean());
		GUIHelper.centerWindow(dlg, d_mainWindow);
		dlg.setVisible(true);
	}

	private JPanel buildAddEndpointButton() {
		JButton button = new JButton("Add Endpoint");
		button.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				addEndpointClicked();
			}			
		});
		ButtonBarBuilder2 bbarBuilder = new ButtonBarBuilder2();
		bbarBuilder.addButton(button);
		bbarBuilder.addGlue();		
		if (studyHasAllEndpoints()) {
			button.setEnabled(false);
		}
		JPanel panel = bbarBuilder.getPanel();
		return panel;
	}

	private boolean studyHasAllEndpoints() {
		return d_model.getBean().getEndpoints().containsAll(d_domain.getEndpoints());
	}

	private void addEndpointClicked() {
		d_mainWindow.showStudyAddEndpointDialog((MutableStudy)d_model.getBean());
	}
}
