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
import org.pietschy.wizard.models.Path;
import org.pietschy.wizard.models.SimplePath;

import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class AddDosedDrugTreatmentWizard extends Wizard {
	private static final List<Condition> s_conditions = new ArrayList<Condition>();

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
			final JDialog dialog) {
		super(buildModel(pm, dialog));
		final AddisWindow mainWindow = Main.getMainWindow();
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

	private static WizardModel buildModel(final DosedDrugTreatmentPresentation pm, JDialog dialog) {
		final AddDosedDrugTreatmentWizardStep generalInfo = new AddDosedDrugTreatmentWizardStep(pm);
		final SpecifyDoseTypeWizardStep type = new SpecifyDoseTypeWizardStep(pm);
		
		final DosedDrugTreatmentOverviewWizardStep overview = new DosedDrugTreatmentOverviewWizardStep(pm);
		
		final SimplePath lastPath = new SimplePath(overview);
		final BranchingPath typePath = new BranchingPath(type);
		final BranchingPath firstStep = new BranchingPath(generalInfo);
		final BranchingPath fixedAndFlexiblePath = new BranchingPath(createFixedDose(pm));

		final ValueModel anyFlexibleDose = new BooleanOrModel(Arrays.<ValueModel>asList(
				type.getConsiderFlexibleBoth(),
				type.getConsiderFlexibleLower(),
				type.getConsiderFlexibleUpper()));
		final ValueModel considerFixed = type.getConsiderFixed();
		
		addBranch(fixedAndFlexiblePath, createSimplePath(lastPath, createFlexibleLowerDose(pm)), type.getConsiderFlexibleLower());
		addBranch(fixedAndFlexiblePath, createSimplePath(lastPath, createFlexibleUpperDose(pm)), type.getConsiderFlexibleUpper());
		addBranch(fixedAndFlexiblePath, createSimplePath(lastPath, createFlexibleLowerDose(pm), createFlexibleUpperRanges(pm)),  type.getConsiderFlexibleBoth());

		addBranch(typePath, createSimplePath(lastPath, createFixedDose(pm)), 
				new BooleanAndModel(new BooleanNotModel(anyFlexibleDose), considerFixed));
		
		addBranch(typePath, lastPath, 
				new BooleanAndModel(new BooleanNotModel(anyFlexibleDose), new BooleanNotModel(considerFixed)));

		addBranch(typePath, createSimplePath(lastPath, createFlexibleLowerDose(pm)), 
				new BooleanAndModel(type.getConsiderFlexibleLower(), new BooleanNotModel(considerFixed)));
		
		addBranch(typePath, createSimplePath(lastPath, createFlexibleUpperDose(pm)), 
				new BooleanAndModel(type.getConsiderFlexibleUpper(), new BooleanNotModel(considerFixed)));
		
		addBranch(typePath, createSimplePath(lastPath, createFlexibleLowerDose(pm), createFlexibleUpperRanges(pm)), 
				new BooleanAndModel(type.getConsiderFlexibleBoth(), new BooleanNotModel(considerFixed)));
		
		addBranch(typePath, fixedAndFlexiblePath, 
				new BooleanAndModel(considerFixed, anyFlexibleDose));
		
		addBranch(firstStep, lastPath, new Condition() {	
			public boolean evaluate(WizardModel model) {
				return generalInfo.getConsiderDoseType().getValue() == null;
			}
		});

		addBranch(firstStep, typePath, new Condition() {		
			public boolean evaluate(WizardModel model) {
				return generalInfo.getConsiderDoseType().getValue() != null && generalInfo.getConsiderDoseType().getValue() == true;
			}
		});
		
		addBranch(firstStep, createSimplePath(lastPath, createKnownDose(pm)), new Condition() {
			public boolean evaluate(WizardModel model) {
				return generalInfo.getConsiderDoseType().getValue() != null && generalInfo.getConsiderDoseType().getValue() == false;
			}
		});
		
		return new DynamicMultiPathModel(firstStep);
	}

	private static DoseRangeWizardStep createFlexibleUpperRanges(final DosedDrugTreatmentPresentation pm) {
		return DoseRangeWizardStep.createOnBeanPropertyChildren(
				pm, 
				FlexibleDose.class, 
				FlexibleDose.PROPERTY_MIN_DOSE,
				FlexibleDose.PROPERTY_MAX_DOSE,
				"Specify the ranges for upper bound of flexible doses",
				"For each of the categories, define a range in which the upper bound of the administered dose must lie. ");
	}

	private static DoseRangeWizardStep createFlexibleUpperDose(final DosedDrugTreatmentPresentation pm) {
		return DoseRangeWizardStep.createOnBeanProperty(
				pm, 
				FlexibleDose.class, 
				FlexibleDose.PROPERTY_MAX_DOSE,
				"Specify the ranges for upper bound of flexible doses",
				"For each of the categories, define a range in which the upper bound of the administered dose must lie.");
	}

	private static DoseRangeWizardStep createFixedDose(final DosedDrugTreatmentPresentation pm) {
		return DoseRangeWizardStep.createOnBeanProperty(
				pm, 
				FixedDose.class, 
				FixedDose.PROPERTY_QUANTITY,
				"Specify ranges for fixed doses",
				"For each of the categories, define a range in which the administered dose must lie.");
	}

	private static DoseRangeWizardStep createFlexibleLowerDose(final DosedDrugTreatmentPresentation pm) {
		return DoseRangeWizardStep.createOnBeanProperty(
				pm, 
				FlexibleDose.class, 
				FlexibleDose.PROPERTY_MIN_DOSE,
				"Specify the ranges for lower bound of flexible doses",
				"For each of the categories, define a range in which the lower bound of the administered dose must lie.");
	}
	
	private static DoseRangeWizardStep createKnownDose(final DosedDrugTreatmentPresentation pm) {
		return DoseRangeWizardStep.createOnKnownDoses(pm,
				"Any dose type", 
				"For each of the categories, define a range in which the dose must lie. For flexible dose the entire administered dose range must be within the specified range to be in the category.");
	}
	
	private static void addBranch(BranchingPath origin, Path destination, ValueModel model) { 
		Condition condition = createCondition(model);
		addBranch(origin, destination, condition);
	}
	
	private static void addBranch(BranchingPath origin, Path destination, Condition condition) { 
		s_conditions.add(condition);
		origin.addBranch(destination, condition);
	}

	private static Condition createCondition(final ValueModel condition) {
		return new ValueHolderCondition(condition);
	}

	private static SimplePath createSimplePath(SimplePath nextPath, PanelWizardStep ... steps) { 
		SimplePath path = new SimplePath(); 
		for(PanelWizardStep step : steps) { 
			path.addStep(step);
		}
		path.setNextPath(nextPath);
		return path;
	}
}
