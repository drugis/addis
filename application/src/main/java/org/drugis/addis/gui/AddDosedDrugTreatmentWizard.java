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
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.gui.knowledge.DosedDrugTreatmentKnowledge.CategorySpecifiers;
import org.drugis.addis.gui.wizard.AddDosedDrugTreatmentWizardStep;
import org.drugis.addis.gui.wizard.DoseRangeWizardStep;
import org.drugis.addis.gui.wizard.DosedDrugTreatmentOverviewWizardStep;
import org.drugis.addis.gui.wizard.SpecifyDoseTypeWizardStep;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.addis.presentation.ValueHolder;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardEvent;
import org.pietschy.wizard.WizardListener;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.BranchingPath;
import org.pietschy.wizard.models.Condition;
import org.pietschy.wizard.models.MultiPathModel;
import org.pietschy.wizard.models.Path;
import org.pietschy.wizard.models.SimplePath;

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

	private static WizardModel buildModel(final DosedDrugTreatmentPresentation pm, AddisWindow mainWindow, Domain domain, JDialog dialog) {
		final AddDosedDrugTreatmentWizardStep generalInfo = new AddDosedDrugTreatmentWizardStep(pm, domain, mainWindow);
		final SpecifyDoseTypeWizardStep specifyDoseType = new SpecifyDoseTypeWizardStep(pm, domain, mainWindow);
		
		// Same for flexible upper, flexible lower and any (needs to set all)
		final DoseRangeWizardStep specifyFixedDose = DoseRangeWizardStep.createOnBeanProperty(pm, 
				domain, 
				mainWindow, 
				FixedDose.class, 
				FixedDose.PROPERTY_QUANTITY,
				"Specify ranges for fixed doses",
				"");
		
		final DoseRangeWizardStep specifyFlexibleLowerDose = DoseRangeWizardStep.createOnBeanProperty(
				pm, 
				domain, 
				mainWindow, 
				FlexibleDose.class, 
				FlexibleDose.PROPERTY_MIN_DOSE,
				"Specify the ranges for the minimum of flexible doses",
				"");
		
		final DoseRangeWizardStep specifyFlexibleUpperDose = DoseRangeWizardStep.createOnBeanProperty(
				pm, 
				domain, 
				mainWindow, 
				FlexibleDose.class, 
				FlexibleDose.PROPERTY_MAX_DOSE,
				"Specify the ranges for the maximum of flexible doses",
				"");

		final DosedDrugTreatmentOverviewWizardStep overview = new DosedDrugTreatmentOverviewWizardStep(pm, domain, mainWindow);
		
		SimplePath lastPath = new SimplePath(overview);
		BranchingPath generalPath = new BranchingPath();
		BranchingPath considerDoseTypePath = new BranchingPath();
		
		SimplePath fixedOnlyPath = createSimplePath(lastPath, specifyFixedDose);
		
		SimplePath flexibleOnlyLowerPath = createSimplePath(lastPath, specifyFlexibleLowerDose);
		SimplePath flexibleOnlyUpperPath = createSimplePath(lastPath, specifyFlexibleUpperDose);
		SimplePath flexibleBothPath = createSimplePath(lastPath, specifyFlexibleLowerDose, specifyFlexibleUpperDose);  // FIXME the upper step needs to loop over the previous rangenodes
		
		BranchingPath fixedAndFlexiblePath = new BranchingPath();
		fixedAndFlexiblePath.addStep(specifyFixedDose);
		fixedAndFlexiblePath.addBranch(flexibleOnlyLowerPath, 
				createRangeCondition(pm, CategorySpecifiers.FIXED_CONSIDER, CategorySpecifiers.FLEXIBLE_CONSIDER_LOWER));
		fixedAndFlexiblePath.addBranch(flexibleOnlyUpperPath, 
				createRangeCondition(pm, CategorySpecifiers.FIXED_CONSIDER, CategorySpecifiers.FLEXIBLE_CONSIDER_UPPER));
		fixedAndFlexiblePath.addBranch(flexibleBothPath, 
				createRangeCondition(pm, CategorySpecifiers.FIXED_CONSIDER, CategorySpecifiers.FLEXIBLE_CONSIDER_BOTH));
		
		generalPath.addStep(generalInfo);

		considerDoseTypePath.addStep(specifyDoseType);
		considerDoseTypePath.addBranch(fixedOnlyPath,
				createRangeCondition(pm, CategorySpecifiers.FIXED_CONSIDER, CategorySpecifiers.DO_NOT_CONSIDER));
		considerDoseTypePath.addBranch(flexibleOnlyLowerPath,
				createRangeCondition(pm, CategorySpecifiers.DO_NOT_CONSIDER, CategorySpecifiers.FLEXIBLE_CONSIDER_LOWER));
		considerDoseTypePath.addBranch(flexibleOnlyUpperPath,
				createRangeCondition(pm, CategorySpecifiers.DO_NOT_CONSIDER, CategorySpecifiers.FLEXIBLE_CONSIDER_UPPER));
		considerDoseTypePath.addBranch(flexibleBothPath,
				createRangeCondition(pm, CategorySpecifiers.DO_NOT_CONSIDER, CategorySpecifiers.FLEXIBLE_CONSIDER_BOTH));

		considerDoseTypePath.addBranch(fixedAndFlexiblePath,
				createRangeCondition(pm, CategorySpecifiers.FIXED_CONSIDER, null));

		generalPath.addBranch(lastPath, new Condition() {	
			public boolean evaluate(WizardModel model) {
				return generalInfo.considerDoseType() == null;
			}
		});

		generalPath.addBranch(considerDoseTypePath, new Condition() {		
			public boolean evaluate(WizardModel model) {
				return generalInfo.considerDoseType() != null && generalInfo.considerDoseType() == true;
			}
		});
		generalPath.addBranch(lastPath, new Condition() {
			public boolean evaluate(WizardModel model) {
				return generalInfo.considerDoseType() != null && generalInfo.considerDoseType() == false;
			}
		}); // TODO This is a dummy, it will be the "do not consider dose type" option

		
		MultiPathModel model = new MultiPathModel(generalPath);
		model.setLastVisible(false);

		return model;
	}

	private static Condition createRangeCondition(final DosedDrugTreatmentPresentation pm,
			final CategorySpecifiers fixedSpec, final CategorySpecifiers flexibleSpec) {
		Condition condition = new Condition() {
			public boolean evaluate(WizardModel model) {
				final ValueHolder<Object> fixed = pm.getSelectedCategory(pm.getType(FixedDose.class));
				final ValueHolder<Object> flexible = pm.getSelectedCategory(pm.getType(FlexibleDose.class));
				
				boolean fixedMatches = (fixedSpec == null)
						|| ((fixedSpec == CategorySpecifiers.DO_NOT_CONSIDER) && (fixed.getValue() instanceof DecisionTreeNode))
						|| (fixed.getValue().equals(fixedSpec));
				boolean flexibleMatches = (flexibleSpec == null)
						|| ((flexibleSpec == CategorySpecifiers.DO_NOT_CONSIDER) && (flexible.getValue() instanceof DecisionTreeNode))
						|| (flexible.getValue().equals(flexibleSpec));
				System.out.println("specs: <" + fixedSpec + "> <" + flexibleSpec + ">");
				System.out.println("  data: <" + fixed.getValue() + "> <" + flexible.getValue() + ">");
				System.out.println("  matches: " + fixedMatches + ", " + flexibleMatches);
				return fixedMatches && flexibleMatches;
				
			}
		};
		return condition;
	}

	public static SimplePath createSimplePath(Path nextPath, PanelWizardStep ... steps) { 
		SimplePath path = new SimplePath(); 
		for(PanelWizardStep step : steps) { 
			path.addStep(step);
		}
		path.setNextPath(nextPath);
		return path;
	}
}
