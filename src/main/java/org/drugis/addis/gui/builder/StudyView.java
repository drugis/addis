/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.MeasurementTable;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.StudyPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyView implements ViewBuilder {
	private StudyPresentation d_model;
	private StudyCharacteristicsView d_charView;
	private StudyOutcomeMeasuresView d_epView;
	private StudyOutcomeMeasuresView d_adeView;	
	private StudyArmsView d_armsView;
	
	public StudyView(StudyPresentation model, Domain domain, JFrame parent, PresentationModelFactory pmf) {
		d_model = model;
		d_charView = new StudyCharacteristicsView(model);
		d_epView = new StudyOutcomeMeasuresView(model, parent, pmf, true);
		d_adeView = new StudyOutcomeMeasuresView(model, parent, pmf, false);		
		d_armsView = new StudyArmsView(model, pmf);			
	}
	
	public StudyView(StudyPresentation model, Domain domain, Main main) {
		this(model, domain, main, main.getPresentationModelFactory());
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
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Study.class).getSingularCapitalized(), cc.xy(1,row));
		row += 2;
		builder.add(GUIFactory.createCollapsiblePanel(d_charView.buildPanel()),	cc.xy(1, row));
		row += 2;
		builder.addSeparator("Arms", cc.xy(1, row));
		row += 2;
		builder.add(buildArmsPart(),cc.xy(1, row));
		row += 2;
		builder.addSeparator("Baseline Characteristics", cc.xy(1, row));
		row += 2;
		builder.add(buildPopulationPart(), cc.xy(1, row));
		row += 2;
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Endpoint.class).getPlural(), cc.xy(1, row));
		row += 2;
		builder.add(buildEndpointPart(), cc.xy(1, row));
		row += 2;
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(AdverseEvent.class).getPlural(), cc.xy(1, row));		
		row += 2;
		builder.add(buildAdverseEventPart(), cc.xy(1, row));
		
		return builder.getPanel();
	}

	private JComponent buildPopulationPart() {
		return GUIFactory.createCollapsiblePanel(createPopulationPanel());
	}

	private JComponent createPopulationPanel() {
		if (d_model.getPopulationCharacteristicCount() < 1) {
			return new JLabel("No Population Characteristics");
		}
		MeasurementTable measurementTable = new MeasurementTable(d_model.getPopulationCharTableModel());
		return AuxComponentFactory.createUnscrollableTablePanel(measurementTable);
	}

	private JPanel buildArmsPart() {
		return GUIFactory.createCollapsiblePanel(d_armsView.buildPanel());
	}

	private JComponent buildEndpointPart() {
		return GUIFactory.createCollapsiblePanel(d_epView.buildPanel());
	}
	
	private JComponent buildAdverseEventPart() {
		return GUIFactory.createCollapsiblePanel(d_adeView.buildPanel());
	}
}
