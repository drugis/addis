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

package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Activity;
import org.drugis.addis.entities.CombinationTreatment;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.PredefinedActivity;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.CombinationTreatmentPresentation;
import org.drugis.addis.presentation.TreatmentActivityPresentation;
import org.drugis.addis.presentation.ValueHolder;

import scala.actors.threadpool.Arrays;

import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class StudyActivityPresentation {
	public class ValidModel extends AbstractValueModel implements ValueHolder<Boolean> {
		private static final long serialVersionUID = -9106397706220712681L;

		ValidModel() {
			PropertyChangeListener listener = new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					firePropertyChange("value", null, getValue());
				}
			};
			d_treatmentModel.getBean().addPropertyChangeListener(listener);
			d_newActivity.addPropertyChangeListener(listener);
		}
		@Override
		public Boolean getValue() {
			return isProperlyFilledIn() && isNameUnique();
		}

		@Override
		public void setValue(Object newValue) {
		}
	}

	private final ObservableList<StudyActivity> d_activityList;
	private final StudyActivity d_oldActivity;
	private StudyActivity d_newActivity;
	private ValueModel d_nameHolder;
	private ValueModel d_activityHolder;
	private TreatmentActivityPresentation d_treatmentModel;
	private List<Activity> d_activityOptions;
	private ValueHolder<Boolean> d_valid;
	private AbstractListHolder<Drug> d_drugOptions;
	private CombinationTreatmentPresentation d_combinationModel;

	public StudyActivityPresentation(ObservableList<StudyActivity> activityList, AbstractListHolder<Drug> drugOptions) {
		this (activityList, drugOptions, null);
	}
	
	@SuppressWarnings("unchecked")
	public StudyActivityPresentation(ObservableList<StudyActivity> activityList, AbstractListHolder<Drug> drugOptions, StudyActivity activity) {
		d_activityList = activityList;
		d_drugOptions = drugOptions;
		d_oldActivity = activity;
		d_newActivity = activity == null ? new StudyActivity(null, null) : activity.clone();
		d_nameHolder = new PropertyAdapter<StudyActivity>(d_newActivity, StudyActivity.PROPERTY_NAME, true);
		d_activityHolder = new PropertyAdapter<StudyActivity>(d_newActivity, StudyActivity.PROPERTY_ACTIVITY, true);
		d_activityOptions = new ArrayList<Activity>(Arrays.asList(PredefinedActivity.values()));
		TreatmentActivity initialTreatment = getInitialTreatment();
		d_activityOptions.add(initialTreatment);
	    CombinationTreatment initialCombination = getInitialCombination();
	    d_activityOptions.add(initialCombination);
		d_treatmentModel = new TreatmentActivityPresentation(initialTreatment);
		d_combinationModel = new CombinationTreatmentPresentation(initialCombination);
		d_valid = new ValidModel();
		PropertyChangeListener listener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateName();
			}
		};
		if (!isEditing()) {
			d_activityHolder.addValueChangeListener(listener);
			d_treatmentModel.addBeanPropertyChangeListener(listener);
			d_combinationModel.addPropertyChangeListener(listener);
		}
	}

	private boolean isProperlyFilledIn() {
		if (d_newActivity.getName() == null || d_newActivity.getName().equals("")) {
			return false;
		}
		return d_newActivity.isComplete();
	}
	
	private boolean isNameUnique() {
		for (StudyActivity act : d_activityList) {
			if(act.getName().equals(d_newActivity.getName()) && !act.equals(d_oldActivity)) {
				return false;
			}
		}
		return true;
	}
	
	private TreatmentActivity getInitialTreatment() {
		if (d_newActivity.getActivity() instanceof TreatmentActivity) {
			return (TreatmentActivity) d_newActivity.getActivity();
		} else {
			return new TreatmentActivity(null, null);
		}
	}
	
	private CombinationTreatment getInitialCombination() {
		if (d_newActivity.getActivity() instanceof CombinationTreatment) {
			return (CombinationTreatment) d_newActivity.getActivity();
		} else {
			return new CombinationTreatment();
		}
	}
	
	public boolean isEditing() {
		return d_oldActivity != null;
	}
	
	/**
	 * The value model containing the activity name. Class of value: String.
	 */
	public ValueModel getNameModel() {
		return d_nameHolder;
	}
	
	/**
	 * Get the activity notes.
	 */
	public ObservableList<Note> getNotesModel() {
		return d_newActivity.getNotes();
	}
	
	/**
	 * The value model containing the activity type. Class of value: Activity.
	 */
	public ValueModel getActivityModel() {
		return d_activityHolder;
	}
	
	/**
	 * The list of selectable activity types.
	 */
	public List<Activity> getActivityOptions() {
		return d_activityOptions;
	}
	
	/**
	 * The presentation model for the treatment activity.
	 */
	public TreatmentActivityPresentation getTreatmentModel() {
		return d_treatmentModel;
	}
	
	public CombinationTreatmentPresentation getCombinationTreatmentModel() {
		return d_combinationModel;
	}
	
	
	/**
	 * A value model indicating whether the input is complete and valid.
	 */
	public ValueHolder<Boolean> getValidModel() {
		return d_valid;
	}
	
	/**
	 * Commit the activity to the activityList.
	 */
	public void commit() {
		if(isEditing()) {
			d_activityList.remove(d_oldActivity);
		}
		d_activityList.add(d_newActivity);
	}

	public AbstractListHolder<Drug> getDrugOptions() {
		return d_drugOptions;
	}
	
	private void updateName() {
		if (d_activityHolder.getValue() instanceof TreatmentActivity) {
			Object drug = d_treatmentModel.getModel(TreatmentActivity.PROPERTY_DRUG).getValue();
			if (drug != null) {
				d_nameHolder.setValue(drug.toString());
			}
		} else if(d_activityHolder.getValue() instanceof CombinationTreatment) {
			d_newActivity.setName(d_combinationModel.getName());
		} else if (d_activityHolder.getValue() != null) {
			d_newActivity.setName(d_activityHolder.getValue().toString());
		}
	}
}
