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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.drugis.addis.entities.AbstractStudy;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.MutableStudy;
import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.presentation.MetaStudyPresentationModel;
import org.drugis.addis.presentation.StudyCharTableModel;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class StudyView implements ViewBuilder {
	PresentationModel<? extends Study> d_model;
	Domain d_domain;
	Main d_mainWindow;
	private ImageLoader d_loader;

	public StudyView(PresentationModel<? extends Study> model, Domain domain, Main main, ImageLoader loader) {
		d_loader = loader;
		d_model = model;
		d_mainWindow = main;
		d_domain = domain;
	}
	
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout( 
				"right:pref, 3dlu, pref:grow, 3dlu, center:pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
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
		
		int row = buildStudyPart(fullWidth, builder, cc);
		
		row = buildEndpointsPart(layout, fullWidth, builder, cc, row);
		
		if (d_model.getBean() instanceof MetaStudy) {
			row = buildStudiesPart(layout, fullWidth, builder, cc, row);
		}
		
		row = buildDataPart(layout, fullWidth, builder, cc, row);
		
		buildAnalysesPart(fullWidth, builder, cc, row);
		
		
		return builder.getPanel();
	}

	private int buildStudiesPart(FormLayout layout, int fullWidth,
			PanelBuilder builder, CellConstraints cc, int row) {
		LayoutUtil.addRow(layout);
		LayoutUtil.addRow(layout);
		LayoutUtil.addRow(layout);
		
		final MetaStudyPresentationModel pm = 
			(MetaStudyPresentationModel) d_model;
		
		builder.addSeparator("Included Studies", cc.xyw(1, row, fullWidth));
		row += 2;
		
		StudyCharTableModel model = new StudyCharTableModel(pm);
		final JTable table = new JTable(model);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setBackground(Color.WHITE);
		JScrollPane pane = new JScrollPane(table);
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				
		builder.add(pane, cc.xyw(1, row, fullWidth));
		row += 2;
		
		JButton customizeButton = new JButton("Customize Shown Characteristics");
		customizeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				JDialog dialog = new CharacteristicSelectDialog(d_mainWindow, pm);
				dialog.setVisible(true);
			}
		});
		
		builder.add(customizeButton, cc.xy(3, row));
		row += 2;
		
		return row;
	}

	private void buildAnalysesPart(int fullWidth, PanelBuilder builder,
			CellConstraints cc, int row) {
		builder.addSeparator("Analyses", cc.xyw(1, row, fullWidth));
		row += 2;
	}

	private int buildDataPart(FormLayout layout, int fullWidth,
			PanelBuilder builder, CellConstraints cc, int row) {
		builder.addSeparator("Data", cc.xyw(1, row, fullWidth));
		row += 2;
		
		builder.addLabel("Size", cc.xy(3, row, "center, center"));		
		int col = 5;
		for (Endpoint e : d_model.getBean().getEndpoints()) {
			builder.add(
					GUIFactory.createEndpointLabelWithIcon(d_loader, d_model.getBean(), e),
							cc.xy(col, row));
			col += 2;
		}
		row += 2;

		for (PatientGroup g : d_model.getBean().getPatientGroups()) {
			row = buildPatientGroup(layout, builder, cc, row, g);
		}
			
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
		return row;
	}

	private int buildPatientGroup(FormLayout layout, PanelBuilder builder,
			CellConstraints cc, int row, PatientGroup g) {
		int col;
		LayoutUtil.addRow(layout);
		builder.add(
				BasicComponentFactory.createLabel(getLabelModel(g)),
				cc.xy(1, row));
		
		builder.add(
				BasicComponentFactory.createLabel(
						new PresentationModel<PatientGroup>(g).getModel(BasicPatientGroup.PROPERTY_SIZE),
						NumberFormat.getInstance()),
						cc.xy(3, row, "center, center"));
		
		col = 5;
		for (Endpoint e : d_model.getBean().getEndpoints()) {
			Measurement m = d_model.getBean().getMeasurement(e, g);
			if (m != null) {
				builder.add(
						BasicComponentFactory.createLabel(getLabelModel(m)),
						cc.xy(col, row));
			}
			col += 2;
		}
		
		row += 2;
		return row;
	}

	protected void addPatientGroup() {
		StudyAddPatientGroupDialog dlg = new StudyAddPatientGroupDialog(d_loader, d_mainWindow, d_domain,
				(BasicStudy)d_model.getBean());
		dlg.setVisible(true);
	}

	private int buildEndpointsPart(FormLayout layout, int fullWidth, PanelBuilder builder,
			CellConstraints cc, int row) {
		builder.addSeparator("Endpoints", cc.xyw(1, row, fullWidth));
		row += 2;
		
		for (Endpoint e : d_model.getBean().getEndpoints()) {
			LayoutUtil.addRow(layout);
			builder.add(
					GUIFactory.createEndpointLabelWithIcon(d_loader, d_model.getBean(), e),
					cc.xy(1, row));
			builder.add(
					buildFindStudiesButton(e), cc.xy(3, row));
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

	private JComponent buildFindStudiesButton(final Endpoint endpoint) {
		JButton button = new JButton("Find Studies");
		button.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				d_mainWindow.endpointSelected(endpoint, d_model.getBean());
			}
		});
		
		ButtonBarBuilder2 builder = new ButtonBarBuilder2();
		builder.addButton(button);
		
		return builder.getPanel();
	}

	private int buildStudyPart(int fullWidth, PanelBuilder builder,
			CellConstraints cc) {
		String studyLabel = getStudyLabel();
		builder.addSeparator(studyLabel, cc.xyw(1,1,fullWidth));
		builder.addLabel("ID:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(d_model.getModel(AbstractStudy.PROPERTY_ID)),
				cc.xyw(3, 3, fullWidth - 2));
		
		builder.addLabel("Intended Indication:", cc.xy(1, 5));
		
		Indication indication =
			(Indication)d_model.getBean().getCharacteristics().get(StudyCharacteristic.INDICATION);
		builder.add(BasicComponentFactory.createLabel(getLabelModel(indication)),
				cc.xyw(3, 5, fullWidth - 2));
		
		return 7;
	}

	private AbstractValueModel getLabelModel(Object model) {
		return d_mainWindow.getPresentationModelManager().getLabeledModel(model).getLabelModel();
	}

	private String getStudyLabel() {
		if (d_model.getBean() instanceof MetaStudy) {
			return "Meta-study";			
		} else {
			return "Study";
		}
	}
}
