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

package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.SelectableStudyGraph;
import org.drugis.addis.gui.StudyGraph;
import org.drugis.addis.presentation.SelectableStudyGraphModel;
import org.drugis.addis.presentation.wizard.NetworkMetaAnalysisWizardPM;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class NetworkMetaAnalysisWizard extends Wizard {

	public NetworkMetaAnalysisWizard(AddisWindow mainWindow, NetworkMetaAnalysisWizardPM model) {
		super(buildModel(model, mainWindow));
		setDefaultExitMode(Wizard.EXIT_ON_FINISH);
		getTitleComponent().setPreferredSize(new Dimension(550, 100));
	}

	private static WizardModel buildModel(NetworkMetaAnalysisWizardPM pm, AddisWindow main) {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new SelectIndicationWizardStep(pm));
		wizardModel.add(new SelectEndpointWizardStep(pm));
		wizardModel.add(new SelectDrugsWizardStep(pm, main));
		SelectStudiesWizardStep selectStudiesStep = new SelectStudiesWizardStep(pm, main);
		wizardModel.add(selectStudiesStep);
		Bindings.bind(selectStudiesStep, "complete", pm.getStudySelectionCompleteModel());
		wizardModel.add(new SelectArmsWizardStep(pm));
		wizardModel.add(new OverviewWizardStep(pm, main));
		return wizardModel;
	}
	
	public static class OverviewWizardStep extends AbstractOverviewWizardStep<SelectableStudyGraphModel> {
		private StudyGraph d_studyGraph;

		public OverviewWizardStep(NetworkMetaAnalysisWizardPM pm, AddisWindow main) {
			super(pm, main);

			setLayout(new BorderLayout());
			    
			FormLayout layout = new FormLayout(
					"center:pref:grow",
					"p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			builder.add(buildStudiesGraph(), cc.xy(1, 1));
			
			JScrollPane sp = new JScrollPane(builder.getPanel());
			sp.getVerticalScrollBar().setUnitIncrement(16);
			add(sp, BorderLayout.CENTER);

			setComplete(true);
		}

		protected Component buildStudiesGraph() {
			d_studyGraph = new StudyGraph(((NetworkMetaAnalysisWizardPM)d_pm).getSelectedStudyGraphModel());
			d_studyGraph.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			return d_studyGraph;
		}
		
		@Override public void prepare() {
			d_studyGraph.layoutGraph();
		}
	}
	
	public static class SelectDrugsWizardStep extends PanelWizardStep {

		private SelectableStudyGraph d_studyGraph;

		public SelectDrugsWizardStep(NetworkMetaAnalysisWizardPM pm, AddisWindow main) {
			super("Select Drugs","Select the drugs to be used for the network meta-analysis. Click to select (green) or deselect (gray).  To continue, (1) at least two drugs must be selected, and (2) all selected drugs must be connected.");
					
			setLayout(new BorderLayout());
			    
			FormLayout layout = new FormLayout(
					"center:pref:grow",
					"p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			builder.add(buildStudiesGraph(pm), cc.xy(1, 1));
			
			JScrollPane sp = new JScrollPane(builder.getPanel());
			add(sp);
			sp.getVerticalScrollBar().setUnitIncrement(16);
			
			Bindings.bind(this, "complete", pm.getConnectedDrugsSelectedModel());
		}
		
		private Component buildStudiesGraph(NetworkMetaAnalysisWizardPM pm) {
			d_studyGraph = new SelectableStudyGraph(pm.getStudyGraphModel());
			d_studyGraph.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			return d_studyGraph;
		}
		
		@Override public void prepare() {
			d_studyGraph.layoutGraph();
		}
	}
}
