/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.Arm;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

public class ContinuousMeasurementPresentationTest {
	private BasicContinuousMeasurement d_basicContinuousMeasurement;
	private Arm d_pg;
	private ContinuousMeasurementPresentation<BasicContinuousMeasurement> d_pres;
	
	@Before
	public void setUp() {
		d_pg = new Arm(null, null, 1);
		d_basicContinuousMeasurement = new BasicContinuousMeasurement(0.0, 0.0, d_pg.getSize());
		d_pres = new ContinuousMeasurementPresentation<BasicContinuousMeasurement>(d_basicContinuousMeasurement);
	}
	
	@Test
	public void testGetLabel() {
		assertEquals("0.0 \u00B1 0.0 (" + d_pg.getSize() +")", d_pres.getLabelModel().getValue());
	}
	
	@Test
	public void testFireStdDevChanged() {
		getMeasurement().setMean(25.5);
		AbstractValueModel lm = d_pres.getLabelModel();
		PropertyChangeListener l = JUnitUtil.mockStrictListener(
				lm, "value", null, "25.5 \u00B1 1.1 (1)");
		lm.addPropertyChangeListener(l);
		getMeasurement().setStdDev(1.1);
		verify(l);
	}

	@Test
	public void testFireMeanChanged() {
		getMeasurement().setMean(25.5);
		getMeasurement().setStdDev(1.1);
		AbstractValueModel lm = d_pres.getLabelModel();
		PropertyChangeListener l = JUnitUtil.mockStrictListener(
				lm, "value", null, "27.5 \u00B1 1.1 (1)");
		lm.addPropertyChangeListener(l);
		getMeasurement().setMean(27.5);
		verify(l);
	}
	
	private BasicContinuousMeasurement getMeasurement() {
		return d_basicContinuousMeasurement;
	}
}
