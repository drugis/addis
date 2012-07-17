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
import org.pietschy.wizard.WizardStep;
import org.pietschy.wizard.models.BranchingPath;
import org.pietschy.wizard.models.Condition;
import org.pietschy.wizard.models.MultiPathModel;
import org.pietschy.wizard.models.Path;
import org.pietschy.wizard.models.SimplePath;

import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class AddDosedDrugTreatmentWizard extends Wizard {
	private static List<Condition> s_conditions;

	private static final class DynamicMultiPathModel extends MultiPathModel {
		public DynamicMultiPathModel(Path firstPath) {
			super(firstPath);
			for(Condition condition : s_conditions) {
				if(condition instanceof ValueHolderCondition) {
					final ValueHolderCondition wrapped = (ValueHolderCondition) condition; 
					attachListener(wrapped);
				}
			}
			setLastVisible(false);
		}

		private void attachListener(final ValueHolderCondition wrapped) {
			wrapped.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					refreshModelState();
					WizardStep activeStep = getActiveStep();
					reset();
					setActiveStep(activeStep);
				}
			});
		}
		
	}
	private static final class ValueHolderCondition extends AbstractObservable implements Condition {
		private final ValueHolder<Boolean> d_condition;
		
		private ValueHolderCondition(ValueModel condition) {
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
			return "condition[" + d_condition.getValue() + "]";
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
		BranchingPath startPath = new BranchingPath(generalInfo);
		BranchingPath typePath = new BranchingPath(type);
		
		s_conditions = new ArrayList<Condition>();
		final ValueModel anyFlexibleDose = new BooleanOrModel(Arrays.<ValueModel>asList(
				type.getConsiderFlexibleBoth(),
				type.getConsiderFlexibleLower(),
				type.getConsiderFlexibleUpper()));
		final ValueModel considerFixed = type.getConsiderFixed();

		
		SimplePath lowerOnlyPath = createSimplePath(lastPath, flexibleLowerDose);
		SimplePath upperOnlyPath = createSimplePath(lastPath, flexibleUpperDose);
		SimplePath bothPath = createSimplePath(lastPath, flexibleLowerDose, flexibleUpperDose);

		BranchingPath fixedAndFlexiblePath = new BranchingPath(fixedDose);
		addBranch(fixedAndFlexiblePath, lowerOnlyPath, new BooleanAndModel(anyFlexibleDose, type.getConsiderFlexibleLower()));
		addBranch(fixedAndFlexiblePath, upperOnlyPath, new BooleanAndModel(anyFlexibleDose, type.getConsiderFlexibleUpper()));
		addBranch(fixedAndFlexiblePath, bothPath, new BooleanAndModel(anyFlexibleDose, type.getConsiderFlexibleBoth()));
		addBranch(fixedAndFlexiblePath, lastPath,  new BooleanNotModel(anyFlexibleDose)); // Failsafe
		
		addBranch(typePath, 
				createSimplePath(lastPath, fixedDose), 
				new BooleanAndModel(new BooleanNotModel(anyFlexibleDose), considerFixed));
		
		addBranch(typePath, 
				lastPath, 
				new BooleanAndModel(new BooleanNotModel(anyFlexibleDose), new BooleanNotModel(considerFixed)));

			addBranch(typePath, 
					lowerOnlyPath, 
					new BooleanAndModel(type.getConsiderFlexibleLower(), new BooleanNotModel(considerFixed)));
			
			addBranch(typePath, 
					upperOnlyPath, 
					new BooleanAndModel(type.getConsiderFlexibleUpper(), new BooleanNotModel(considerFixed)));
			
			addBranch(typePath, 
					bothPath, 
					new BooleanAndModel(type.getConsiderFlexibleBoth(), new BooleanNotModel(considerFixed)));
		
		addBranch(typePath, 
				fixedAndFlexiblePath, 
				new BooleanAndModel(considerFixed, anyFlexibleDose));
		
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
		
		final MultiPathModel model = new DynamicMultiPathModel(startPath);
		return model;
	}

	private static void addBranch(BranchingPath origin, Path destination, ValueModel model) { 
		Condition condition = createCondition(model);
		s_conditions.add(condition);
		origin.addBranch(destination, condition);
	}
	
	private static Condition createCondition(final ValueModel condition) {
		return new ValueHolderCondition(condition);
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
