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

import javax.swing.JComponent;

import org.drugis.addis.entities.AbstractStudy;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.MetaAnalysis;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.LabeledPresentationModel;
import org.drugis.addis.presentation.PresentationModelManager;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class MetaAnalysisView implements ViewBuilder {
	MetaAnalysis d_analysis;
	private PresentationModelManager d_pmm;
	
	public MetaAnalysisView(MetaAnalysis analysis, PresentationModelManager pmm) {
		d_analysis = analysis;
		d_pmm = pmm;
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p");
		int fullWidth = 3;
		for (int i = 1; i < d_analysis.getDrugs().size(); ++i) {
			LayoutUtil.addColumn(layout);
			fullWidth += 2;
		}
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Meta Analysis", cc.xyw(1, 1, fullWidth));
		
		builder.addLabel("Endpoint: ", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(
				new PresentationModel<Endpoint>(d_analysis.getEndpoint()).getModel(Endpoint.PROPERTY_NAME)
				), cc.xy(3, 3));
		
		int col = 3;
		for (Drug d : d_analysis.getDrugs()) {
			builder.add(BasicComponentFactory.createLabel(
					new PresentationModel<Drug>(d).getModel(Drug.PROPERTY_NAME)), cc.xy(col, 5));
			col += 2;
		}
		
		int row = 7;
		for (Study s : d_analysis.getStudies()) {
			LayoutUtil.addRow(layout);
			builder.add(BasicComponentFactory.createLabel(
					new PresentationModel<Study>(s).getModel(AbstractStudy.PROPERTY_ID)), cc.xy(1, row));
			
			col = 3;
			for (Drug d : d_analysis.getDrugs()) {
				@SuppressWarnings("unchecked")
				LabeledPresentationModel pm = d_pmm.getLabeledModel(d_analysis.getMeasurement(s,d));
				
				builder.add(BasicComponentFactory.createLabel(pm.getLabelModel()),
						cc.xy(col, row));
				col += 2;
			}
			
			row += 2;
		}
		
		LayoutUtil.addRow(layout);
		builder.addLabel("Combined", cc.xy(1, row));
		col = 3;
		for (Drug d : d_analysis.getDrugs()) {
			@SuppressWarnings("unchecked")
			LabeledPresentationModel pm = d_pmm.getLabeledModel(d_analysis.getPooledMeasurement(d));
			builder.add(BasicComponentFactory.createLabel(pm.getLabelModel()),
					cc.xy(col, row));
			col += 2;
		}
	
		return builder.getPanel();
	}
}
