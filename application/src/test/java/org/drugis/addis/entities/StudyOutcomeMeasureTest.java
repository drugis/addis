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

package org.drugis.addis.entities;

import org.drugis.addis.entities.WhenTaken.RelativeTo;
import org.drugis.addis.util.EntityUtil;
import org.junit.Test;

public class StudyOutcomeMeasureTest {

//	@Test
//	public void testWhenTakenChangePropagatesToStudyOutcomeMeasure() {
//		WhenTaken wt = new WhenTaken(EntityUtil.createDuration("P08D"), RelativeTo.FROM_EPOCH_START, new Epoch("Epoch", null));
//		StudyOutcomeMeasure<Endpoint> som = new StudyOutcomeMeasure<Endpoint>(new Endpoint("Death", new RateVariableType()), wt );
//		PropertyChangeListener l = JUnitUtil.mockStrictListener(som, StudyOutcomeMeasure.PROPERTY_WHEN_TAKEN_EDITED, false, true);
//		som.addPropertyChangeListener(l);
//		wt.setRelativeTo(RelativeTo.BEFORE_EPOCH_END);
//		verify(l);
//		som.removePropertyChangeListener(l);
//	}
//	
//	@Test
//	public void testWhenTakenChangePropagatesToStudyOutcomeMeasureOtherConstructor() {
//		StudyOutcomeMeasure<Endpoint> som = new StudyOutcomeMeasure<Endpoint>(new Endpoint("Death", new RateVariableType()));
//		PropertyChangeListener l = JUnitUtil.mockStrictListener(som, StudyOutcomeMeasure.PROPERTY_WHEN_TAKEN_EDITED, false, true);
//		som.addPropertyChangeListener(l);
//		WhenTaken wt = new WhenTaken(EntityUtil.createDuration("P08D"), RelativeTo.FROM_EPOCH_START, new Epoch("Epoch", null));
//		som.getWhenTaken().add(wt);
//		wt.setRelativeTo(RelativeTo.BEFORE_EPOCH_END);
//		verify(l);
//		som.removePropertyChangeListener(l);
//	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddWhenTakenMustBeCommitted() {
		StudyOutcomeMeasure<Endpoint> som = new StudyOutcomeMeasure<Endpoint>(new Endpoint("Death", new RateVariableType()));
		WhenTaken wt = new WhenTaken(EntityUtil.createDuration("P08D"), RelativeTo.FROM_EPOCH_START, new Epoch("Epoch", null));
		som.getWhenTaken().add(wt);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSetWhenTakenMustBeCommitted() {
		StudyOutcomeMeasure<Endpoint> som = new StudyOutcomeMeasure<Endpoint>(new Endpoint("Death", new RateVariableType()));
		WhenTaken wt1 = new WhenTaken(EntityUtil.createDuration("P08D"), RelativeTo.FROM_EPOCH_START, new Epoch("Epoch", null));
		wt1.commit();
		som.getWhenTaken().add(wt1);
		WhenTaken wt2 = new WhenTaken(EntityUtil.createDuration("P08D"), RelativeTo.FROM_EPOCH_START, new Epoch("Epoch", null));
		som.getWhenTaken().set(0, wt2);
	}
}
