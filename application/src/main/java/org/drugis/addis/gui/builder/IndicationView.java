/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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
import javax.swing.JPanel;

import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.presentation.IndicationPresentation;
import org.drugis.common.gui.OneWayObjectFormat;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ConverterFactory;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class IndicationView implements ViewBuilder {
	
	private IndicationPresentation d_pm;
	private AddisWindow d_parent;

	public IndicationView(IndicationPresentation pm, AddisWindow main) {
		d_pm = pm;
		d_parent = main;
	}
	
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"fill:0:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Indication.class).getSingularCapitalized(), cc.xy(1, 1));
		builder.add(buildOverviewPart(), cc.xy(1, 3));
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Study.class).getPlural(), cc.xy(1, 5));
		builder.add(GUIFactory.buildStudyPanel(d_pm.getStudyListPresentation(), d_parent), cc.xy(1, 7));		
		
		return builder.getPanel();
	}

	private JPanel buildOverviewPart() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout("right:pref, 3dlu, fill:0:grow",
				"p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.addLabel("SNOMED Concept ID:", cc.xy(1, 1));
		ValueModel codeModel = ConverterFactory.createStringConverter(
				d_pm.getModel(Indication.PROPERTY_CODE),
				new OneWayObjectFormat());
		builder.add(BasicComponentFactory.createLabel(codeModel), cc.xy(3, 1));
		
		builder.addLabel("Fully Specified Name:", cc.xy(1, 3));
		builder.add(AuxComponentFactory.createAutoWrapLabel(d_pm.getModel(Indication.PROPERTY_NAME)), cc.xy(3, 3));
		return builder.getPanel();
	}
}
