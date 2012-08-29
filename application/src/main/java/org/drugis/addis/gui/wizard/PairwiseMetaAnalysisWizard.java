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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.builder.PairWiseMetaAnalysisView;
import org.drugis.addis.presentation.PairWiseMetaAnalysisPresentation;
import org.drugis.addis.presentation.wizard.PairWiseMetaAnalysisWizardPresentation;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;

@SuppressWarnings("serial")
public class PairwiseMetaAnalysisWizard extends Wizard {
	
	public PairwiseMetaAnalysisWizard(AddisWindow mainWindow, PairWiseMetaAnalysisWizardPresentation pm) {
		super(buildModel(pm, mainWindow));
		setDefaultExitMode(Wizard.EXIT_ON_FINISH);
	}
	
	private static WizardModel buildModel(final PairWiseMetaAnalysisWizardPresentation pm, AddisWindow mainWindow) {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new SelectIndicationAndNameWizardStep(pm, mainWindow));
		wizardModel.add(new SelectTwoDrugsWizardStep(pm, 
				pm.getRawAlternativesGraph(), 
				pm.getRawFirstDefinitionModel(), 
				pm.getRawSecondDefinitionModel(), 
				new Runnable() {
					public void run() {
						pm.rebuildRawAlternativesGraph();
						
					}
				}, "Select Drugs", pm.getRawDescription()));
		wizardModel.add(new RefineDrugSelectionWizardStep(pm));
		wizardModel.add(new SelectTwoDrugsWizardStep(pm, 
				pm.getRefinedAlternativesGraph(), 
				pm.getRefinedFirstDefinitionModel(), 
				pm.getRefinedSecondDefinitionModel(), 
				new Runnable() {
					public void run() {
						pm.rebuildRefinedAlternativesGraph();
						
					}
				}, "Select Definitions", pm.getRefinedDescription()));
		
		SelectStudiesWizardStep selectStudiesStep = new SelectStudiesWizardStep(pm);
		wizardModel.add(selectStudiesStep);
		Bindings.bind(selectStudiesStep, "complete", pm.getMetaAnalysisCompleteModel());
		wizardModel.add(new SelectArmsWizardStep(pm));
		wizardModel.add(new OverviewWizardStep(pm, mainWindow));
		return wizardModel;
	}

	public static class OverviewWizardStep extends AbstractOverviewWizardStep {
		public OverviewWizardStep(PairWiseMetaAnalysisWizardPresentation pm, AddisWindow mainWindow) {
			super(pm, mainWindow);
		}
		
		@Override
		public void prepare() {
			removeAll();
			d_pm.rebuildOverviewGraph();
			
			setLayout(new BorderLayout()); // needed for placement
			
			PairWiseMetaAnalysisPresentation pm = ((PairWiseMetaAnalysisWizardPresentation)d_pm).getMetaAnalysisModel();
			final PairWiseMetaAnalysisView mav = new PairWiseMetaAnalysisView(
					(PairWiseMetaAnalysisPresentation)pm, d_mainWindow);
			final JComponent panel = mav.getPlotsPanel(true);

			if(d_pm.getOutcomeMeasureModel().getValue().getVariableType() instanceof RateVariableType){
				JCheckBox checkBox = BasicComponentFactory.createCheckBox(pm.getModel(RandomEffectsMetaAnalysis.PROPERTY_CORRECTED), "Correct for zeroes");
				add(checkBox, BorderLayout.NORTH);
			}
			
			final JScrollPane sp = new JScrollPane(panel);
			sp.getVerticalScrollBar().setUnitIncrement(16);
			add(sp, BorderLayout.CENTER);
			
			pm.getModel(RandomEffectsMetaAnalysis.PROPERTY_CORRECTED).addValueChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					sp.setVisible(false);
					sp.setViewportView(mav.getPlotsPanel(true));
					sp.setVisible(true);
				}
			});
			
			setComplete(true);
		}
	}	
}
