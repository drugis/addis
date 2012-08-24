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

package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.TreatmentActivity;

import com.jgoodies.binding.PresentationModel;

public class TreatmentActivityPresentation extends PresentationModel<TreatmentActivity> {
	private static final long serialVersionUID = -3639230649100997570L;
	
	public static final String PROPERTY_NAME = "name";

	private PropertyChangeListener d_nameListener;

	public TreatmentActivityPresentation(final TreatmentActivity ta) {
		super(ta);

		d_nameListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(DrugTreatment.PROPERTY_DRUG)) {
					firePropertyChange(PROPERTY_NAME, null, getName());
				}
			}
		};
		
		ta.getTreatments().addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				updateTreatmentListeners();
				firePropertyChange(PROPERTY_NAME, null, getName());
			}
			public void intervalAdded(ListDataEvent e) {
				updateTreatmentListeners();
				firePropertyChange(PROPERTY_NAME, null, getName());
			}
			public void contentsChanged(ListDataEvent e) {
				updateTreatmentListeners();
				firePropertyChange(PROPERTY_NAME, null, getName());
			}

		});
		updateTreatmentListeners();
	}

	public DrugTreatmentPresentation getTreatmentModel(DrugTreatment ta) {
		return new DrugTreatmentPresentation(ta);
	}

	public List<DrugTreatmentPresentation> getTreatmentModels() {
		ArrayList<DrugTreatmentPresentation> arrayList = new ArrayList<DrugTreatmentPresentation>();
		for (DrugTreatment dt : getBean().getTreatments()) {
			arrayList.add(getTreatmentModel(dt));
		}
		return arrayList; 
	}
	
	public String getName() {
		String name = "";
		for(DrugTreatment ta : getBean().getTreatments()) {
			name += (ta.getDrug() == null ? "MISSING" : ta.getDrug()) + " + ";
		}
		return name.length() > 0 ? name.substring(0, name.length() - 3) : "";
	}

	private void updateTreatmentListeners() {
		for(DrugTreatment ta : getBean().getTreatments()) {
			ta.removePropertyChangeListener(d_nameListener);
			ta.addPropertyChangeListener(d_nameListener);
		}
	}

}
