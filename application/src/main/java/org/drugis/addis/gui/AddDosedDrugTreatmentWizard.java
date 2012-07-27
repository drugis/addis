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

import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.gui.wizard.AddDosedDrugTreatmentWizardStep;
import org.drugis.addis.gui.wizard.DoseRangeWizardStep;
import org.drugis.addis.gui.wizard.DosedDrugTreatmentOverviewWizardStep;
import org.drugis.addis.gui.wizard.SpecifyDoseTypeWizardStep;
import org.drugis.addis.presentation.UnmodifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.ValueModelWrapper;
import org.drugis.addis.presentation.wizard.DosedDrugTreatmentWizardPresentation;
import org.drugis.common.beans.AbstractObservable;
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.common.validation.BooleanNotModel;
import org.drugis.common.validation.BooleanOrModel;
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
	private static final List<Condition> s_conditions = new ArrayList<Condition>();

	private static final class DynamicMultiPathModel extends MultiPathModel {
		public DynamicMultiPathModel(final Path firstPath) {
			super(firstPath);
			for(final Condition condition : s_conditions) {
				if(condition instanceof ValueHolderCondition) {
					final ValueHolderCondition wrapped = (ValueHolderCondition) condition;
					attachListener(wrapped);
				}
			}
			setLastVisible(false);
		}

		private void attachListener(final ValueHolderCondition wrapped) {
			wrapped.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					refreshModelState();
				}
			});
		}
	}

	private static final class ValueHolderCondition extends AbstractObservable implements Condition {
		private final ValueHolder<Boolean> d_condition;

		private ValueHolderCondition(final ValueModel condition) {
			d_condition = new ValueModelWrapper<Boolean>(condition);
			d_condition.addValueChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					firePropertyChange("value", evt.getOldValue(), evt.getNewValue());
				}
			});
		}

		@Override
		public boolean evaluate(final WizardModel model) {
			return d_condition.getValue();
		}

		@Override
		public String toString() {
			return "condition[" + d_condition.getValue() + "]";
		}
	}

	public AddDosedDrugTreatmentWizard(
			final DosedDrugTreatmentWizardPresentation pm,
			final JDialog dialog) {
		super(buildModel(pm, dialog));
		final AddisWindow mainWindow = Main.getMainWindow();
		addWizardListener(new WizardListener() {
			@Override
			public void wizardClosed(final WizardEvent e) {
				mainWindow.leftTreeFocus(pm.commit());
			}

			@Override
			public void wizardCancelled(final WizardEvent e) {
			}
		});
		setDefaultExitMode(Wizard.EXIT_ON_FINISH);
	}

	private static WizardModel buildModel(final DosedDrugTreatmentWizardPresentation pm, final JDialog dialog) {
		final AddDosedDrugTreatmentWizardStep generalInfo = new AddDosedDrugTreatmentWizardStep(pm);
		final SpecifyDoseTypeWizardStep type = new SpecifyDoseTypeWizardStep(pm);
		final DosedDrugTreatmentOverviewWizardStep overview = new DosedDrugTreatmentOverviewWizardStep(pm);

		final SimplePath lastPath = new SimplePath(overview);
		final BranchingPath typePath = new BranchingPath(type);
		final BranchingPath firstStep = new BranchingPath(generalInfo);
		final BranchingPath fixedAndFlexiblePath = new BranchingPath(createFixedDose(dialog, pm));

		final ValueModel considerFixed = pm.getConsiderFixed();
		buildFlexiblePath(pm, dialog, fixedAndFlexiblePath, type, lastPath, new UnmodifiableHolder<Boolean>(true));
		buildFlexiblePath(pm, dialog, typePath, type, lastPath, new BooleanNotModel(considerFixed));

		final ValueModel anyFlexibleDose = new BooleanOrModel(Arrays.<ValueModel>asList(
				pm.getConsiderFlexibleLowerFirst(),
				pm.getConsiderFlexibleUpperFirst()));

		addBranch(typePath, createSimplePath(lastPath, createFixedDose(dialog, pm)),
				new BooleanAndModel(new BooleanNotModel(anyFlexibleDose), considerFixed));

		addBranch(typePath, lastPath,
				new BooleanAndModel(new BooleanNotModel(anyFlexibleDose), new BooleanNotModel(considerFixed)));

		addBranch(typePath, fixedAndFlexiblePath,
				new BooleanAndModel(considerFixed, anyFlexibleDose));

		addBranch(firstStep, lastPath, new BooleanNotModel(new BooleanOrModel(pm.getConsiderDoseType(), pm.getIgnoreDoseType())));

		addBranch(firstStep, typePath, pm.getConsiderDoseType());

		addBranch(firstStep, createSimplePath(lastPath, createKnownDose(dialog, pm)), pm.getIgnoreDoseType());

		return new DynamicMultiPathModel(firstStep);
	}

	private static void buildFlexiblePath(final DosedDrugTreatmentWizardPresentation pm, final JDialog dialog, final BranchingPath flexiblePath, final SpecifyDoseTypeWizardStep type, final SimplePath lastPath, final ValueModel condition) {
		final DoseRangeWizardStep lowerFirst = createFlexibleLowerDose(dialog, pm);
		final DoseRangeWizardStep upperFirst = createFlexibleUpperDose(dialog, pm);
		final BranchingPath lowerFirstPath = new BranchingPath(lowerFirst);
		final BranchingPath upperFirstPath = new BranchingPath(upperFirst);

		addBranch(flexiblePath, lowerFirstPath, new BooleanAndModel(condition, pm.getConsiderFlexibleLowerFirst()));
		addBranch(flexiblePath, upperFirstPath, new BooleanAndModel(condition, pm.getConsiderFlexibleUpperFirst()));

		addBranch(lowerFirstPath, createSimplePath(lastPath, createFlexibleUpperRanges(dialog, pm)), lowerFirst.getConsiderNextProperty());
		addBranch(upperFirstPath, createSimplePath(lastPath, createFlexibleLowerRanges(dialog, pm)), upperFirst.getConsiderNextProperty());
		addBranch(lowerFirstPath, lastPath, new BooleanNotModel(lowerFirst.getConsiderNextProperty()));
		addBranch(upperFirstPath, lastPath, new BooleanNotModel(upperFirst.getConsiderNextProperty()));
	}

	private static WizardStep createFlexibleUpperRanges(final JDialog dialog, final DosedDrugTreatmentWizardPresentation pm) {
		return DoseRangeWizardStep.createOnMultipleParentRanges(
				dialog,
				pm,
				pm.getFlexibleLowerRanges(),
				"Specify the ranges for upper bound of flexible doses", "For each of the categories, define a range in which the upper bound of the administered dose must lie. ");
	}

	private static WizardStep createFlexibleLowerRanges(final JDialog dialog, final DosedDrugTreatmentWizardPresentation pm) {
		return DoseRangeWizardStep.createOnMultipleParentRanges(
				dialog,
				pm,
				pm.getFlexibleUpperRanges(),
				"Specify the ranges for lower bound of flexible doses", "For each of the categories, define a range in which the lower bound of the administered dose must lie.");
	}

	private static DoseRangeWizardStep createFixedDose(final JDialog dialog, final DosedDrugTreatmentWizardPresentation pm) {
		return DoseRangeWizardStep.createOnBeanProperty(
				dialog,
				pm,
				pm.getFixedRangeNode(),
				null,
				"Specify ranges for fixed doses", "For each of the categories, define a range in which the administered dose must lie.");
	}

	private static DoseRangeWizardStep createFlexibleLowerDose(final JDialog dialog, final DosedDrugTreatmentWizardPresentation pm) {
		return DoseRangeWizardStep.createOnBeanProperty(
				dialog,
				pm,
				pm.getFlexibleLowerRangeNode(),
				FlexibleDose.PROPERTY_MAX_DOSE,
				"Specify the ranges for lower bound of flexible doses", "For each of the categories, define a range in which the lower bound of the administered dose must lie.");
	}

	private static DoseRangeWizardStep createFlexibleUpperDose(final JDialog dialog, final DosedDrugTreatmentWizardPresentation pm) {
		return DoseRangeWizardStep.createOnBeanProperty(
				dialog,
				pm,
				pm.getFlexibleUpperRangeNode(),
				FlexibleDose.PROPERTY_MIN_DOSE,
				"Specify the ranges for upper bound of flexible doses", "For each of the categories, define a range in which the upper bound of the administered dose must lie.");
	}

	private static WizardStep createKnownDose(final JDialog dialog, final DosedDrugTreatmentWizardPresentation pm) {
		return DoseRangeWizardStep.createOnKnownDoses(dialog,
				pm,
				"Any dose type", "For each of the categories, define a range in which the dose must lie. For flexible dose the entire administered dose range must be within the specified range to be in the category.");
	}

	private static void addBranch(final BranchingPath origin, final Path destination, final ValueModel model) {
		final Condition condition = createCondition(model);
		addBranch(origin, destination, condition);
	}

	private static void addBranch(final BranchingPath origin, final Path destination, final Condition condition) {
		s_conditions.add(condition);
		origin.addBranch(destination, condition);
	}

	private static Condition createCondition(final ValueModel condition) {
		return new ValueHolderCondition(condition);
	}

	private static SimplePath createSimplePath(final SimplePath nextPath, final WizardStep ... steps) {
		final SimplePath path = new SimplePath();
		for(final WizardStep step : steps) {
			path.addStep(step);
		}
		path.setNextPath(nextPath);
		return path;
	}
}
