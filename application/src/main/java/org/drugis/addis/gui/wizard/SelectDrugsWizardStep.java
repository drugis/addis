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

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.drugis.addis.presentation.wizard.NetworkMetaAnalysisWizardPM;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SelectDrugsWizardStep extends AbstractSelectTreatmentWizardStep {

	private static final long serialVersionUID = 5310567692551793030L;

	public SelectDrugsWizardStep(NetworkMetaAnalysisWizardPM pm) {
		super("Select Drugs",
				"Select the drugs to be used for the network meta-analysis. Click to select (green) or deselect (gray).  To continue, (1) at least two drugs must be selected, and (2) all selected drugs must be connected.",
				pm.getRawAlternativesGraph());
		d_pm = pm;
		setLayout(new BorderLayout());
		    
		FormLayout layout = new FormLayout(
				"center:pref:grow",
				"p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		builder.add(d_studyGraph, cc.xy(1, 1));
		
		JScrollPane sp = new JScrollPane(builder.getPanel());
		add(sp);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		
		Bindings.bind(this, "complete", pm.getRawSelectionCompleteModel());
	}
	
	
	@Override
	public void prepare() {
		d_pm.rebuildRawAlternativesGraph();
		d_studyGraph.layoutGraph();
	}
}