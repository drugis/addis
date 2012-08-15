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

package org.drugis.addis.gui.wizard;

import javax.swing.BorderFactory;
import javax.swing.Icon;

import org.drugis.addis.gui.SelectableTreatmentDefinitionsGraph;
import org.drugis.addis.presentation.SelectableTreatmentDefinitionsGraphModel;
import org.drugis.addis.presentation.wizard.NetworkMetaAnalysisWizardPM;
import org.pietschy.wizard.PanelWizardStep;

public class AbstractSelectTreatmentWizardStep extends PanelWizardStep {

	private static final long serialVersionUID = 1746696286888281689L;
	protected SelectableTreatmentDefinitionsGraph d_studyGraph;
	protected NetworkMetaAnalysisWizardPM d_pm;

	public AbstractSelectTreatmentWizardStep(String name, String summary, SelectableTreatmentDefinitionsGraphModel selectableStudyGraphModel) {
		super(name, summary);
		d_studyGraph = buildStudiesGraph(selectableStudyGraphModel);
	}

	public AbstractSelectTreatmentWizardStep(String name, String summary, Icon icon, SelectableTreatmentDefinitionsGraphModel selectableStudyGraphModel) {
		super(name, summary, icon);
		d_studyGraph = buildStudiesGraph(selectableStudyGraphModel);
	}

	private SelectableTreatmentDefinitionsGraph buildStudiesGraph(SelectableTreatmentDefinitionsGraphModel selectableStudyGraphModel) {
		SelectableTreatmentDefinitionsGraph studyGraph = new SelectableTreatmentDefinitionsGraph(selectableStudyGraphModel);
		studyGraph.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		return studyGraph;
	}
}