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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.drugis.addis.analyses.SMAAAdapter;
import org.drugis.addis.analyses.UnableToBuildModelException;
import org.drugis.addis.entities.AbstractStudy;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.CombinedStudy;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.Study;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;

import fi.smaa.common.ImageLoader;
import fi.smaa.jsmaa.gui.JSMAAMainFrame;
import fi.smaa.jsmaa.model.SMAAModel;

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
		
		row = buildDataPart(layout, fullWidth, builder, cc, row);
		
		buildAnalysesPart(fullWidth, builder, cc, row);
		
		return builder.getPanel();
	}

	private void buildAnalysesPart(int fullWidth, PanelBuilder builder,
			CellConstraints cc, int row) {
		builder.addSeparator("Analyses", cc.xyw(1, row, fullWidth));
		row += 2;
		JButton smaaButton = new JButton("SMAA benefit-risk");
		smaaButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				smaaAnalysis();
			}
		});
		builder.add(smaaButton, cc.xy(1, row));
	}

	private void smaaAnalysis() {
		try {
			SMAAModel model = SMAAAdapter.getModel(d_model.getBean());
			final JSMAAMainFrame app = new JSMAAMainFrame(model);
			app.setMinimalFrame();
			app.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent evt) {
					app.setVisible(false);
					app.dispose();
				}			
			});
			app.setVisible(true);
		} catch (UnableToBuildModelException e) {
			JOptionPane.showMessageDialog(d_mainWindow,
					e.getMessage(), "Cannot perform SMAA analysis",
					JOptionPane.ERROR_MESSAGE);
		}
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

		if (d_model.getBean() instanceof CombinedStudy) {
			CombinedStudy cs = (CombinedStudy) d_model.getBean();
			for (Study s : cs.getStudies()) {
				LayoutUtil.addRow(layout);
				builder.addLabel("Contained study:", cc.xy(1, row));
				builder.add(BasicComponentFactory.createLabel(
						new PresentationModel<Study>(s).getModel(Study.PROPERTY_ID)),
						cc.xyw(3, row, fullWidth-2));
				row += 2;
				for (PatientGroup g : s.getPatientGroups()) {
					row = buildPatientGroup(layout, builder, cc, row, g);
				}
			}
			
		} else {
			for (PatientGroup g : d_model.getBean().getPatientGroups()) {
				row = buildPatientGroup(layout, builder, cc, row, g);
			}
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
				BasicComponentFactory.createLabel(
						new PresentationModel<PatientGroup>(g).getModel(BasicPatientGroup.PROPERTY_LABEL)),
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
						BasicComponentFactory.createLabel(
								new PresentationModel<Measurement>(m).getModel(Measurement.PROPERTY_LABEL)),
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
		d_mainWindow.showStudyAddEndpointDialog((AbstractStudy)d_model.getBean());
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
		
		Indication indication = d_model.getBean().getIndication();
		PresentationModel<Indication> iModel = new PresentationModel<Indication>(indication);
		builder.add(BasicComponentFactory.createLabel(iModel.getModel(Indication.PROPERTY_LABEL)),
				cc.xyw(3, 5, fullWidth - 2));
		
		return 7;
	}

	private String getStudyLabel() {
		if (d_model.getBean() instanceof MetaStudy) {
			return "Meta-study";			
		} else if (d_model.getBean() instanceof CombinedStudy) {
			return "Combined study";
		} else {
			return "Study";
		}
	}
}
