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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.StudyAddArmDialog;
import org.drugis.addis.gui.StudyAddPopulationCharacteristicDialog;
import org.drugis.addis.presentation.StudyPresentationModel;
import org.drugis.common.gui.GUIHelper;
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
	private StudyEndpointsView d_adeView;	
	private StudyArmsView d_armsView;
	private StudyPopulationView d_popView;
	
	
	public StudyView(StudyPresentationModel model, Domain domain, Main main) {
		d_model = model;
		d_mainWindow = main;
		d_domain = domain;
		d_charView = new StudyCharacteristicsView(model);
		d_popView = new StudyPopulationView(model);
		d_epView = new StudyEndpointsView(model, main, true);
		d_adeView = new StudyEndpointsView(model, main, false);		
		d_armsView = new StudyArmsView(model, main.getPresentationModelFactory());
	}
	
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout( 
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		int row = 1;
		builder.addSeparator("Study", cc.xy(1,row));
		row += 2;
		builder.add(GUIFactory.createCollapsiblePanel(d_charView.buildPanel()),	cc.xy(1, 3));
		row += 2;
		builder.addSeparator("Baseline Characteristics", cc.xy(1, row));
		row += 2;
		builder.add(buildPopulationPart(), cc.xy(1, row));
		row += 2;
		builder.addSeparator("Outcomes - Endpoints", cc.xy(1, row));
		row += 2;
		builder.add(buildEndpointPart(), cc.xy(1, row));
		row += 2;
		builder.addSeparator("Outcomes - Adverse Drug Events", cc.xy(1, row));		
		row += 2;
		builder.add(buildAdePart(), cc.xy(1, row));
		row += 2;
		builder.addSeparator("Arms", cc.xy(1, row));
		row += 2;
		builder.add(buildArmsPart(),cc.xy(1, row));
		
		return builder.getPanel();
	}

	private Component buildPopulationPart() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(d_popView.buildPanel(), BorderLayout.CENTER);
		panel.add(buildAddCharButton(), BorderLayout.SOUTH);
		return GUIFactory.createCollapsiblePanel(panel);
	}

	private JPanel buildArmsPart() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(d_armsView.buildPanel(), BorderLayout.CENTER);
		panel.add(buildAddArmButton(), BorderLayout.SOUTH);
		return GUIFactory.createCollapsiblePanel(panel);
	}

	private JPanel buildEndpointPart() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(d_epView.buildPanel(), BorderLayout.CENTER);
		panel.add(buildAddEndpointButton(), BorderLayout.SOUTH);
		return GUIFactory.createCollapsiblePanel(panel);
	}
	
	private JPanel buildAdePart() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(d_adeView.buildPanel(), BorderLayout.CENTER);
		return GUIFactory.createCollapsiblePanel(panel);
	}
	
	
	private JComponent buildAddCharButton() {
		String text = "Input baseline characteristic";
		AbstractAction action = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				addPopulationCharacteristic();
			}
		};
		return buildOneButtonBar(text, action);
	}

	private void addPopulationCharacteristic() {
		JDialog dlg = new StudyAddPopulationCharacteristicDialog(d_mainWindow, d_domain,
				(StudyPresentationModel)d_model);
		GUIHelper.centerWindow(dlg, d_mainWindow);
		dlg.setVisible(true);
	}

	private JComponent buildAddArmButton() {
		String text = "Add study arm";
		AbstractAction action = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				addArm();
			}			
		};
		return buildOneButtonBar(text, action);
	}

	private JComponent buildOneButtonBar(String text, AbstractAction action) {
		ButtonBarBuilder2 bb = new ButtonBarBuilder2();
		JButton button = new JButton(text);
		button.addActionListener(action);
		bb.addButton(button);
		bb.addGlue();
		return bb.getPanel();
	}

	private void addArm() {
		StudyAddArmDialog dlg = new StudyAddArmDialog(d_mainWindow, d_domain,
				(Study)d_model.getBean());
		GUIHelper.centerWindow(dlg, d_mainWindow);
		dlg.setVisible(true);
	}

	private JComponent buildAddEndpointButton() {
		String text = "Add Endpoint";
		AbstractAction action = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				addEndpointClicked();
			}			
		};
		return buildOneButtonBar(text, action);
	}

	private void addEndpointClicked() {
		d_mainWindow.showStudyAddEndpointDialog((Study)d_model.getBean());
	}
}
