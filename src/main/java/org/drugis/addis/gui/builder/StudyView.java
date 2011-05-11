/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.components.AddisTabbedPane;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.StudyPresentation;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyView implements ViewBuilder {
	private StudyCharacteristicsView d_characteristicsView;
	private StudyOutcomeMeasuresView d_endpointView;
	private StudyOutcomeMeasuresView d_adverseEventView;	
	private StudyArmsView d_armsView;
	private StudyOutcomeMeasuresView d_pcView;
	private StudyDesignView d_designView;
	
	public StudyView(StudyPresentation model, Domain domain, JFrame parent, PresentationModelFactory pmf) {
		d_characteristicsView = new StudyCharacteristicsView(parent, model);
		d_endpointView = new StudyOutcomeMeasuresView(model, parent, pmf, Endpoint.class);
		d_adverseEventView = new StudyOutcomeMeasuresView(model, parent, pmf, AdverseEvent.class);
		d_pcView = new StudyOutcomeMeasuresView(model, parent, pmf, PopulationCharacteristic.class);
		d_armsView = new StudyArmsView(parent, model, pmf);
		d_designView = new StudyDesignView(model);
	}
	
	public StudyView(StudyPresentation model, Domain domain, AddisWindow main) {
		this(model, domain, main, main.getPresentationModelFactory());
	}
	
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout( 
				"fill:0:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		int row = 1;

		// ---------- Overview ----------
		
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Study.class).getSingularCapitalized(), cc.xy(1,row));
		row += 2;
		builder.add(d_characteristicsView.buildPanel(), cc.xy(1, row));
		row += 2;

		
		JTabbedPane tabbedPane = new AddisTabbedPane();
		tabbedPane.addTab("Overview", builder.getPanel());

		// ---------- Study design ----------
		
		builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		cc = new CellConstraints();
		row = 1;
		
		builder.addSeparator("Arms", cc.xy(1, row));
		row += 2;
		builder.add(d_armsView.buildPanel(),cc.xy(1, row));
		row += 2;
		builder.addSeparator("Study Design", cc.xy(1, row));
		row += 2;
		builder.add(d_designView.buildPanel(), cc.xy(1, row));

		tabbedPane.addTab("Design", builder.getPanel());

		// ---------- Data ----------

		layout = new FormLayout( 
				"fill:0:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		
		builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		cc = new CellConstraints();
		row = 1;
		
		builder.addSeparator("Baseline Characteristics", cc.xy(1, row));
		row += 2;
		builder.add(d_pcView.buildPanel(), cc.xy(1, row));
		row += 2;
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Endpoint.class).getPlural(), cc.xy(1, row));
		row += 2;
		builder.add(d_endpointView.buildPanel(), cc.xy(1, row));
		row += 2;
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(AdverseEvent.class).getPlural(), cc.xy(1, row));		
		row += 2;
		builder.add(d_adverseEventView.buildPanel(), cc.xy(1, row));

		tabbedPane.addTab("Data", builder.getPanel());
		
		
		
		return tabbedPane;
	}
}
