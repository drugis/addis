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

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.WhenTaken;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ObservableList;

public class WhenTakenPresentation extends PresentationModel<WhenTaken> {
	private static final long serialVersionUID = 3332262569759324845L;
	private final ObservableList<Epoch> d_epochs;

	public WhenTakenPresentation(WhenTaken bean, ObservableList<Epoch> epochs) {
		super(bean); // To the rescue!
		d_epochs = epochs;
		
		d_epochs.addListDataListener(new ListDataListener() {
			public void contentsChanged(ListDataEvent e) {
				update();
			}

			public void intervalAdded(ListDataEvent e) {
			}
			
			public void intervalRemoved(ListDataEvent e) {
				update();
			}

			private void update() {
				if (!d_epochs.contains(getBean().getEpoch())) {
					getBean().setEpoch(null);
				}
			}
			
		});
	}

	public DurationPresentation<WhenTaken> getOffsetPresentation() {
		return new DurationPresentation<WhenTaken>(getBean());
	}
}
