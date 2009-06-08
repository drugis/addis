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

package nl.rug.escher.addis.gui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.MetaAnalysis;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.RateMeasurement;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.common.CollectionUtil;
import nl.rug.escher.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class EndpointStudiesView implements ViewBuilder {
	private Endpoint d_endpoint;
	private Domain d_domain;
	private List<JCheckBox> d_studySelect;
	private Study d_selectedStudy;
	private JFrame d_frame;
	private JButton d_metaAnalyzeButton;
	
	public EndpointStudiesView(Endpoint node, Domain domain, JFrame frame) {
		d_endpoint = node;
		d_domain = domain;
		d_studySelect = new ArrayList<JCheckBox>();
	}

	public JComponent buildPanel() {
		d_studySelect.clear();
		
		PresentationModel<Endpoint> eModel = new PresentationModel<Endpoint>(d_endpoint);
		
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator("Endpoint", cc.xyw(1, 1, 3));
		builder.addLabel("Name:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(
				eModel.getModel(Endpoint.PROPERTY_NAME)), cc.xy(3, 3));
		
		builder.addLabel("Description:", cc.xy(1, 5));
		builder.add(BasicComponentFactory.createLabel(
				eModel.getModel(Endpoint.PROPERTY_DESCRIPTION)), cc.xy(3, 5));
		
		builder.addSeparator("Studies", cc.xyw(1, 7, 3));
		
		int row = 9;
		for (Study s : d_domain.getStudies(d_endpoint)) {
			layout.appendRow(RowSpec.decode("3dlu"));
			layout.appendRow(RowSpec.decode("p"));
			
			JCheckBox box = new JCheckBox();
			box.addActionListener(new CheckBoxListener());
			if (s.equals(d_selectedStudy)) {
				box.setSelected(true);
			}
			d_studySelect.add(box);
			builder.add(box, cc.xy(1, row));
			
			builder.add(BasicComponentFactory.createLabel(
					new PresentationModel<Study>(s).getModel(Study.PROPERTY_ID)), cc.xy(3, row));
			row += 2;
		}
		
		layout.appendRow(RowSpec.decode("3dlu"));
		layout.appendRow(RowSpec.decode("p"));
		builder.add(buildMetaAnalyzeButton(), cc.xy(3, row));
		updateMetaAnalyzeButtonEnabled();
		return builder.getPanel();
	}

	private JComponent buildMetaAnalyzeButton() {
		d_metaAnalyzeButton = new JButton("Meta-Analyze");
		d_metaAnalyzeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				showMetaAnalysisDialog();
			}
		});
		
		ButtonBarBuilder2 builder = new ButtonBarBuilder2();
		builder.addGlue();
		builder.addButton(d_metaAnalyzeButton);
		
		return builder.getPanel();
	}
	
	private void showMetaAnalysisDialog() {
		List<Study> studies = new ArrayList<Study>();
		for (int i = 0; i < d_studySelect.size(); ++i) {
			if (d_studySelect.get(i).isSelected()) {
				studies.add(CollectionUtil.getElementAtIndex(
						d_domain.getStudies(d_endpoint), i));
			}
		}
		
		showMetaAnalysisDialog(studies);
	}

	private void showMetaAnalysisDialog(List<Study> studies) {
		if (haveNonRateMeasurements(studies)) {
			JOptionPane.showMessageDialog(d_frame,
					"Meta-Analyze Not Implemented for non-rate measurements\n\n" + studies.toString(),
					"Meta-Analyze", JOptionPane.ERROR_MESSAGE);
			
		} else {
			MetaAnalysisDialog dialog = new MetaAnalysisDialog(d_frame, 
					d_domain, new MetaAnalysis(d_endpoint, studies));
			dialog.setVisible(true);
		}
	}

	private boolean haveNonRateMeasurements(List<Study> studies) {
		for (Study s : studies) {
			if (hasNonRateMeasurements(s)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasNonRateMeasurements(Study s) {
		for (PatientGroup g : s.getPatientGroups()) {
			if (!(g.getMeasurement(d_endpoint) instanceof RateMeasurement)) {
				return true;
			}
		}
		return false;
	}

	public void setSelectedStudy(Study selectedStudy) {
		d_selectedStudy = selectedStudy;
	}
	
	private class CheckBoxListener extends AbstractAction {
		//@Override
		public void actionPerformed(ActionEvent arg0) {
			updateMetaAnalyzeButtonEnabled();
		}
	}
	
	private void updateMetaAnalyzeButtonEnabled() {
		boolean someSelected = false;
		for (JCheckBox box : d_studySelect) {
			if (box.isSelected()) {
				someSelected = true;
				break;
			}
		}
		d_metaAnalyzeButton.setEnabled(someSelected);
	}			
}
