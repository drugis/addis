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

package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.CombinationTreatment;
import org.drugis.addis.entities.TreatmentActivity;

import com.jgoodies.binding.PresentationModel;

public class CombinationTreatmentPresentation extends PresentationModel<CombinationTreatment> {
	private static final long serialVersionUID = -3639230649100997570L;
	
	public static final String PROPERTY_NAME = "name";

	public CombinationTreatmentPresentation(final CombinationTreatment ct) {
		super(ct);

		final PropertyChangeListener nameListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(TreatmentActivity.PROPERTY_DRUG)) {
					firePropertyChange(PROPERTY_NAME, null, getName());
				}
			}
		};
		
		ct.getTreatments().addListDataListener(new ListDataListener() {
		
			@Override
			public void intervalRemoved(ListDataEvent e) {
				updateTreatmentListeners();
				firePropertyChange(PROPERTY_NAME, null, getName());
			}
			
			@Override
			public void intervalAdded(ListDataEvent e) {
				updateTreatmentListeners();
				firePropertyChange(PROPERTY_NAME, null, getName());
			}
			
			@Override
			public void contentsChanged(ListDataEvent e) {
				updateTreatmentListeners();
				firePropertyChange(PROPERTY_NAME, null, getName());
			}

			protected void updateTreatmentListeners() {
				for(TreatmentActivity ta : ct.getTreatments()) {
					ta.removePropertyChangeListener(nameListener);
					ta.addPropertyChangeListener(nameListener);
				}
			}
		});
	}

	public TreatmentActivityPresentation getTreatmentModel(TreatmentActivity ta) {
		return new TreatmentActivityPresentation(ta);
	}

	public String getName() {
		String name = "";
		for(TreatmentActivity ta : getBean().getTreatments()) {
			name += (ta.getDrug() == null ? "MISSING" : ta.getDrug()) + " + ";
		}
		return name.length() > 0 ? name.substring(0, name.length() - 3) : "";
	}
}
