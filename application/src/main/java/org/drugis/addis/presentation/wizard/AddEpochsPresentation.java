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
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

import static org.apache.commons.collections15.CollectionUtils.exists;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Predicate;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyOutcomeMeasure;
import org.drugis.addis.entities.WhenTaken;
import org.drugis.addis.presentation.DurationPresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.util.EntityUtil;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;

public class AddEpochsPresentation extends AddListItemsPresentation<Epoch> {

	private final class EpochEnabledListener implements PropertyChangeListener {
		private final Epoch d_epoch;
		private final ValueModel d_model;

		private EpochEnabledListener(Epoch t, ValueModel model) {
			d_epoch = t;
			d_model = model;
			d_model.setValue(isEditable(d_epoch));
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			d_model.setValue(isEditable(d_epoch));
		}

		private boolean isEditable(final Epoch t) {
			boolean editable = true;
			for(StudyOutcomeMeasure<?> om : d_study.getStudyOutcomeMeasures()) {
				Predicate<WhenTaken> isUsed = new Predicate<WhenTaken>() {
					public boolean evaluate(WhenTaken object) {
						return object.getEpoch().equals(t);
					}
				};
				if(exists(om.getWhenTaken(), isUsed)) {
					editable = false;
				}
			}
			return editable;
		}
	}

	private Map<Epoch, ValueModel> d_editable = new HashMap<Epoch, ValueModel>();
	private Study d_study;

	public AddEpochsPresentation(Study study, String itemName, int minElements) {
		super(study.getEpochs(), itemName, minElements);
		d_study = study;
	}

	@Override
	public ObservableList<Note> getNotes(Epoch t) {
		return t.getNotes();
	}

	@Override
	public ValueModel getRemovable(final Epoch t) {
		ValueModel model = d_editable.get(t);
		if(model == null) {
			d_editable.put(t, buildIsEditableModel(t));
		}
		return d_editable.get(t);
	}

	private ValueModel buildIsEditableModel(final Epoch t) {
		final ValueModel model = new ModifiableHolder<Boolean>();
		for(StudyOutcomeMeasure<?> om : d_study.getStudyOutcomeMeasures()) {
			om.addPropertyChangeListener(new EpochEnabledListener(t, model));
		}
		return model;
	}

	@Override
	public Epoch createItem() {
		return new Epoch(nextItemName(), EntityUtil.createDuration("P0D"));
	}

	public DurationPresentation<Epoch> getDurationModel(int idx) {
		return new DurationPresentation<Epoch>(getList().get(idx));
	}

	@Override
	public void rename(int idx, String newName) {
		Epoch oldEpoch = d_list.get(idx);
		d_study.replaceEpoch(oldEpoch, oldEpoch.rename(newName));
	}

	public void setStudy(Study study) {
		d_study = study;
		setList(d_study.getEpochs());
	}

	public Study getStudy() {
		return d_study;
	}
}
