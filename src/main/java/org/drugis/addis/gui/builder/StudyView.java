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

import org.drugis.addis.entities.AbstractStudy;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.MutableStudy;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.gui.CharacteristicHolder;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.StudyAddPatientGroupDialog;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.OneWayObjectFormat;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class StudyView implements ViewBuilder {
	PresentationModel<Study> d_model;
	Domain d_domain;
	Main d_mainWindow;
	private ImageLoader d_loader;

	public StudyView(PresentationModel<Study> model, Domain domain, Main main, ImageLoader loader) {
		d_loader = loader;
		d_model = model;
		d_mainWindow = main;
		d_domain = domain;
	}
	
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout( 
				"left:pref, 3dlu, pref:grow, 3dlu, center:pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		int fullWidth = 5;
		int[] colGroup = new int[d_model.getBean().getEndpoints().size()];
		colGroup[0] = 5;	
		for (int i = 1; i < d_model.getBean().getEndpoints().size(); ++i) {			
			colGroup[i] = 5 + (i*2);
			layout.appendColumn(ColumnSpec.decode("3dlu"));
			layout.appendColumn(ColumnSpec.decode("center:pref"));			
			fullWidth += 2;
		}
		
		layout.setColumnGroups(new int[][]{new int[]{3}, colGroup});
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		int row = buildStudyPart(fullWidth, builder, cc, layout);
		
		row = buildEndpointsPart(layout, fullWidth, builder, cc, row);

		builder.addSeparator("Data", cc.xyw(1, row, fullWidth));
		row += 2;
		
		builder.add(new StudyDataView(d_model, d_loader, d_mainWindow.getPresentationModelManager()).buildPanel(), cc.xyw(1, row, fullWidth));
		row += 2;
		
		if (d_model.getBean() instanceof BasicStudy) {
			LayoutUtil.addRow(layout);
			JButton addGroupButton = new JButton("Add patient group");
			addGroupButton.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					addPatientGroup();
				}			
			});
			builder.add(addGroupButton, cc.xy(1, row));
			row += 2;			
		}
		
		row += 2;
		
		return builder.getPanel();
	}

	private void addPatientGroup() {
		StudyAddPatientGroupDialog dlg = new StudyAddPatientGroupDialog(d_loader, d_mainWindow, d_domain,
				(BasicStudy)d_model.getBean());
		dlg.setVisible(true);
	}

	private int buildEndpointsPart(FormLayout layout, int fullWidth, PanelBuilder builder,
			CellConstraints cc, int row) {
		row += 2;
		builder.addSeparator("Endpoints", cc.xyw(1, row, fullWidth));
		row += 4;
		
		for (Endpoint e : d_model.getBean().getEndpoints()) {
			LayoutUtil.addRow(layout);
			builder.add(
					GUIFactory.createEndpointLabelWithIcon(d_loader, d_model.getBean(), e),
					cc.xy(1, row));
			row += 2;
		}
		if (d_model.getBean() instanceof BasicStudy) {
			LayoutUtil.addRow(layout);
			builder.add(buildAddEndpointButton(), cc.xy(1, row));
			
			row += 2;
		}

		return row;
	}

	private JPanel buildAddEndpointButton() {
		JButton button = new JButton("Add Endpoint");
		button.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				addEndpointClicked();
			}			
		});
		ButtonBarBuilder2 bbarBuilder = new ButtonBarBuilder2();
		bbarBuilder.addGlue();
		bbarBuilder.addButton(button);
		
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

	private int buildStudyPart(int fullWidth, PanelBuilder builder,
			CellConstraints cc, FormLayout layout) {
		String studyLabel = getStudyLabel();
		builder.addSeparator(studyLabel, cc.xyw(1,1,fullWidth));
		builder.addLabel("ID:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(d_model.getModel(AbstractStudy.PROPERTY_ID)),
				cc.xyw(3, 3, fullWidth - 2));
		
		int row = 5;
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			LayoutUtil.addRow(layout);
			builder.addLabel(c.getDescription() + ":", cc.xy(1, row));
			
			ValueModel model = new CharacteristicHolder(d_model.getBean(), c);
			builder.add(BasicComponentFactory.createLabel(model, new OneWayObjectFormat()),
					cc.xyw(3, row, fullWidth - 2));
			
			row += 2;
		}
		return row;
	}

	private String getStudyLabel() {
		return "Study";
	}
}
