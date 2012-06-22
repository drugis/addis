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

package org.drugis.addis.gui;

import javax.swing.JDialog;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.gui.wizard.AddDosedDrugTreatmentWizardStep;
import org.drugis.addis.gui.wizard.AddDosedDrugTreatmentWizardStep.KnownCategorySpecifiers;
import org.drugis.addis.gui.wizard.SpecifyDoseRangeWizardStep;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardEvent;
import org.pietschy.wizard.WizardListener;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.WizardStep;
import org.pietschy.wizard.models.Condition;
import org.pietschy.wizard.models.DynamicModel;

@SuppressWarnings("serial")
public class AddDosedDrugTreatmentWizard extends Wizard {

	public AddDosedDrugTreatmentWizard(
			final DosedDrugTreatmentPresentation pm, 
			final AddisWindow mainWindow, 
			final Domain domain, 
			final JDialog dialog) {
		super(buildModel(pm, mainWindow, domain, dialog));
		addWizardListener(new WizardListener() {
			public void wizardClosed(WizardEvent e) {
				mainWindow.leftTreeFocus(pm.commit());
			}
			
			public void wizardCancelled(WizardEvent e) {
				mainWindow.leftTreeFocus(pm.getBean());
			}
		});
		setDefaultExitMode(Wizard.EXIT_ON_FINISH);
	}


	private static WizardModel buildModel(DosedDrugTreatmentPresentation pm, AddisWindow mainWindow, Domain domain, JDialog dialog) {
		final DynamicModel wizardModel = new DynamicModel();
		final AddDosedDrugTreatmentWizardStep first = new AddDosedDrugTreatmentWizardStep(pm, domain, mainWindow);
		wizardModel.add(first);
		
		final Condition considerDoseType = new Condition() {
			public boolean evaluate(WizardModel model) {
				WizardStep step = model.getActiveStep();
				if(step instanceof AddDosedDrugTreatmentWizardStep) { 	
					return (first.getKnownCategory().getValue() != null) ?
						first.getKnownCategory().getValue().toString().equals(KnownCategorySpecifiers.CONSIDER.getTitle()) : false;
				}
				return false;
			}
		};
		
		wizardModel.add(new SpecifyDoseRangeWizardStep(pm, domain, mainWindow), considerDoseType);
		wizardModel.setLastVisible(false);

		return wizardModel;
	}
	
}
