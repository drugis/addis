/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.ListPanel;
import org.drugis.addis.presentation.AbstractBenefitRiskPresentation;
import org.drugis.addis.presentation.StudyBenefitRiskPresentation;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.OneWayObjectFormat;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public abstract class AbstractBenefitRiskView<PresentationType extends AbstractBenefitRiskPresentation<?, ?>> implements ViewBuilder {


	protected PresentationType d_pm;
	protected Main d_main;
	protected ViewBuilder d_view;

	public AbstractBenefitRiskView(PresentationType model, Main main) {
		d_pm = model;
		d_main = main;
		if (getAnalysis().getAnalysisType() == AnalysisType.SMAA) {
			d_view = new SMAAView(d_pm, d_main);
		} else {
			d_view = new LyndOBrienView(d_pm, d_main);
		}
		d_pm.startAllSimulations();
	}
	

	public JComponent buildPanel() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Overview", buildOverviewPanel());
		tabbedPane.addTab("Measurements", buildMeasurementsPanel());
		tabbedPane.addTab("Analysis", buildAnalysisPanel());
		return tabbedPane;
	}

	
	protected abstract JPanel buildOverviewPanel();

	protected JPanel buildOverviewPart() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout("right:pref, 3dlu, left:pref:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		
		builder.addLabel("ID:", cc.xy(1, 1));
		String value = (String) d_pm.getModel(BenefitRiskAnalysis.PROPERTY_NAME).getValue();
		System.out.println(value);
		JLabel tmp = new JLabel(value);
		builder.add(tmp , cc.xy(3, 1));
		
		builder.addLabel("Analysis type:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_ANALYSIS_TYPE), new OneWayObjectFormat()),
				cc.xy(3, 3));

		builder.addLabel("Indication:", cc.xy(1, 5));
		builder.add(BasicComponentFactory.createLabel(d_pm.getModel(BenefitRiskAnalysis.PROPERTY_INDICATION), new OneWayObjectFormat()), 
				cc.xy(3, 5));

		int row = 5;
		if (d_pm instanceof StudyBenefitRiskPresentation) {
			row += 2;
			LayoutUtil.addRow(layout);
			builder.addLabel("Study:", cc.xy(1, row));
			builder.add(BasicComponentFactory.createLabel(d_pm.getModel(StudyBenefitRiskAnalysis.PROPERTY_STUDY), new OneWayObjectFormat()), 
					cc.xy(3, row));
		}
		
		row += 2;
		builder.addLabel("Criteria:", cc.xy(1, row));
		ListPanel criteriaList = new ListPanel(getAnalysis(), BenefitRiskAnalysis.PROPERTY_OUTCOMEMEASURES, OutcomeMeasure.class);
		builder.add(criteriaList,cc.xy(3, row));
		
		row += 2;
		builder.addLabel("Alternatives:", cc.xy(1, row));
		//ListPanel alternativesList = new ListPanel(d_pm.getBean(), BenefitRiskAnalysis.PROPERTY_ALTERNATIVES, Alternative.class);
		ListPanel alternativesList = new ListPanel(getAnalysis().getAlternatives());
		builder.add(alternativesList,cc.xy(3, row));

		
		return builder.getPanel();	
	}
	
	protected BenefitRiskAnalysis<?> getAnalysis() {
		return (BenefitRiskAnalysis<?>)d_pm.getBean();
	}

	protected abstract JPanel buildMeasurementsPanel();

	protected JComponent buildAnalysisPanel() {
		return d_view.buildPanel();
	}

}
