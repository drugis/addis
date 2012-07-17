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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JDialog;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.gui.wizard.AddDosedDrugTreatmentWizardStep;
import org.drugis.addis.gui.wizard.DoseRangeWizardStep;
import org.drugis.addis.gui.wizard.DosedDrugTreatmentOverviewWizardStep;
import org.drugis.addis.gui.wizard.SpecifyDoseTypeWizardStep;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.ValueModelWrapper;
import org.drugis.common.beans.AbstractObservable;
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.common.validation.BooleanNotModel;
import org.drugis.common.validation.BooleanOrModel;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardEvent;
import org.pietschy.wizard.WizardListener;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.BranchingPath;
import org.pietschy.wizard.models.Condition;
import org.pietschy.wizard.models.MultiPathModel;
import org.pietschy.wizard.models.SimplePath;

import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class AddDosedDrugTreatmentWizard extends Wizard {


	private static final class ValueHolderCondition extends AbstractObservable implements Condition {
		private final ValueHolder<Boolean> d_condition;
		private String d_name;
		
		private ValueHolderCondition(String name, ValueModel condition) {
			d_name = name;
			d_condition = new ValueModelWrapper<Boolean>(condition);
			d_condition.addValueChangeListener(new PropertyChangeListener() {	
				public void propertyChange(PropertyChangeEvent evt) {
					firePropertyChange("value", evt.getOldValue(), evt.getNewValue());
				}
			});
		}

		public boolean evaluate(WizardModel model) {
			return d_condition.getValue();
		}
		
		public String toString() {
			return d_name + " condition[" + d_condition.getValue() + "]";
		}

	}

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
		final SpecifyDoseTypeWizardStep type = new SpecifyDoseTypeWizardStep(pm, domain, mainWindow);
		
		// Same for flexible upper, flexible lower and any (needs to set all)
		final DoseRangeWizardStep fixedDose = DoseRangeWizardStep.createOnBeanProperty(pm, 
				domain, 
				mainWindow, 
				FixedDose.class, 
				FixedDose.PROPERTY_QUANTITY,
				"Specify ranges for fixed doses",
				"");
		
		final DoseRangeWizardStep flexibleLowerDose = DoseRangeWizardStep.createOnBeanProperty(
				pm, 
				domain, 
				mainWindow, 
				FlexibleDose.class, 
				FlexibleDose.PROPERTY_MIN_DOSE,
				"Specify the ranges for the minimum of flexible doses",
				"");
		
		final DoseRangeWizardStep flexibleUpperDose = DoseRangeWizardStep.createOnBeanProperty(
				pm, 
				domain, 
				mainWindow, 
				FlexibleDose.class, 
				FlexibleDose.PROPERTY_MAX_DOSE,
				"Specify the ranges for the maximum of flexible doses",
				"");

		final DosedDrugTreatmentOverviewWizardStep overview = new DosedDrugTreatmentOverviewWizardStep(pm, domain, mainWindow);
		
		SimplePath lastPath = new SimplePath(overview);
		BranchingPath startPath = new BranchingPath();
		BranchingPath typePath = new BranchingPath();
			
		startPath.addStep(generalInfo);
		startPath.addBranch(lastPath, new Condition() {	
			public boolean evaluate(WizardModel model) {
				return generalInfo.getConsiderDoseType().getValue() == null;
			}
		});

		startPath.addBranch(typePath, new Condition() {		
			public boolean evaluate(WizardModel model) {
				return generalInfo.getConsiderDoseType().getValue() != null && generalInfo.getConsiderDoseType().getValue() == true;
			}
		});
		startPath.addBranch(lastPath, new Condition() {
			public boolean evaluate(WizardModel model) {
				return generalInfo.getConsiderDoseType().getValue() != null && generalInfo.getConsiderDoseType().getValue() == false;
			}
		}); // TODO This is a dummy, it will be the "do not consider dose type" option

		typePath.addStep(type);
		
		final ValueModel anyFlexibleDose = new BooleanOrModel(Arrays.<ValueModel>asList(
				type.getConsiderFlexibleBoth(),
				type.getConsiderFlexibleLower(),
				type.getConsiderFlexibleUpper()));
		final ValueModel considerFixed = type.getConsiderFixed();

		final List<Condition> conditions = new ArrayList<Condition>();
		/** Only fixed **/
		Condition fixed = createCondition("Fixed", new BooleanAndModel(considerFixed, new BooleanNotModel(anyFlexibleDose)));
		conditions.add(fixed);
		addBranch(typePath, 
				createSimplePath(lastPath, fixedDose), 
				fixed);
		
		/** Only flexible **/
		Condition lower = createCondition("Flexible lower", new BooleanAndModel(new BooleanNotModel(considerFixed), type.getConsiderFlexibleLower()));
		conditions.add(lower);
		addBranch(typePath, 
				createSimplePath(lastPath, flexibleLowerDose), 
				lower);

		Condition upper = createCondition("Flexible upper", new BooleanAndModel(new BooleanNotModel(considerFixed), type.getConsiderFlexibleUpper()));
		conditions.add(upper);
		addBranch(typePath, 
				createSimplePath(lastPath, flexibleUpperDose), 
				upper);
		
		Condition both = createCondition("Flexible both", new BooleanAndModel(new BooleanNotModel(considerFixed), type.getConsiderFlexibleBoth()));
		conditions.add(both);
		addBranch(typePath,
				createSimplePath(lastPath, flexibleLowerDose, flexibleUpperDose), 
				both);
		
		/** Fixed and flexible **/
		Condition fixedAndLower = createCondition("Fixed and flexible lower", 
				new BooleanAndModel(considerFixed, type.getConsiderFlexibleLower()));
		conditions.add(fixedAndLower);
		addBranch(typePath, 
				createSimplePath(lastPath, fixedDose, flexibleLowerDose), 
				fixedAndLower);

		Condition fixedAndUpper = createCondition("Fixed and flexible upper", 
				new BooleanAndModel(considerFixed, type.getConsiderFlexibleUpper()));
		conditions.add(fixedAndUpper);
		addBranch(typePath,
				createSimplePath(lastPath,fixedDose, flexibleUpperDose), 
				fixedAndUpper);
		
		Condition fixedAndBoth = createCondition("Fixed and flexible both", 
				new BooleanAndModel(considerFixed, type.getConsiderFlexibleBoth()));
		conditions.add(fixedAndBoth);
		addBranch(typePath, 
				createSimplePath(lastPath, fixedDose, flexibleLowerDose, flexibleUpperDose), 
				fixedAndBoth);
		
		/** Neither **/
		Condition neither = createCondition("Neither", 
				new BooleanAndModel(new BooleanNotModel(considerFixed), new BooleanNotModel(anyFlexibleDose)));
		conditions.add(neither);
		addBranch(typePath, lastPath, neither);
		
		final MultiPathModel model = new MultiPathModel(startPath);
		
		for(Condition condition : conditions) {
			if(condition instanceof ValueHolderCondition) {
				final ValueHolderCondition wrapped = (ValueHolderCondition) condition; 
				wrapped.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						model.refreshModelState();
					}
				});
			}
		}
		model.setLastVisible(false);
		return model;
	}

	private static void addBranch(BranchingPath origin, SimplePath destination, Condition condition) { 
		origin.addBranch(destination, condition);
	}
	
	private static Condition createCondition(final String name, final ValueModel condition) {
		return new ValueHolderCondition(name, condition);
	}

	public static SimplePath createSimplePath(SimplePath nextPath, PanelWizardStep ... steps) { 
		SimplePath path = new SimplePath(); 
		for(PanelWizardStep step : steps) { 
			path.addStep(step);
		}
		path.setNextPath(nextPath);
		return path;
	}
}
