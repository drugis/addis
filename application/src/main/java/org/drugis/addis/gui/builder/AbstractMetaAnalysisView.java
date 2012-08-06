/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.components.ListPanel;
import org.drugis.addis.presentation.AbstractMetaAnalysisPresentation;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public abstract class AbstractMetaAnalysisView<T extends AbstractMetaAnalysisPresentation<?>> {

	protected T d_pm;
	protected AddisWindow d_parent;

	public AbstractMetaAnalysisView(T model, AddisWindow mainWindow) {
		d_pm = model;
		d_parent = mainWindow;
	}

	protected JComponent buildStudiesPart() {
		FormLayout layout = new FormLayout("fill:0:grow","p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
				
		builder.add(GUIFactory.buildStudyPanel(d_pm, d_parent), cc.xy(1, 1));
		
		return builder.getPanel();
	}

	protected JComponent buildPropertiesPart() {
		FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:0:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
		
		builder.addLabel("ID:", cc.xy(1, 1));
		builder.add(AuxComponentFactory.createAutoWrapLabel(d_pm.getModel(RandomEffectsMetaAnalysis.PROPERTY_NAME)), cc.xy(3, 1));
		
		builder.addLabel("Type:", cc.xy(1, 3));
		builder.add(AuxComponentFactory.createAutoWrapLabel(d_pm.getModel(MetaAnalysis.PROPERTY_TYPE)), cc.xy(3, 3));
				
		builder.addLabel("Indication:", cc.xy(1, 5));
		builder.add(AuxComponentFactory.createAutoWrapLabel(d_pm.getIndicationModel().getLabelModel()),
				cc.xy(3, 5));
	
		builder.addLabel("Endpoint:", cc.xy(1, 7));
		builder.add(AuxComponentFactory.createAutoWrapLabel(d_pm.getOutcomeMeasureModel().getLabelModel()),
				cc.xy(3, 7));
		
		builder.addLabel("Included drugs:", cc.xy(1, 9));
		
		ListPanel drugList = new ListPanel(d_pm.getBean(), MetaAnalysis.PROPERTY_ALTERNATIVES, Drug.class);
		builder.add(drugList, cc.xy(3, 9));

		if(d_pm.getBean() instanceof NetworkMetaAnalysis){
			String paneText =  "Network Meta-Analysis (or Mixed Treatment Comparison, MTC) is a technique to meta-analyze more than two drugs at the same time. Using a full Bayesian evidence network, all indirect comparisons are taken into account to arrive at a single, integrated, estimate of the effect of all included treatments based on all included studies.";
			JComponent generalPane = AuxComponentFactory.createHtmlField(paneText);
			builder.add(generalPane, cc.xyw(1, 11, 3));
		}
		
		return builder.getPanel();
	}

}
