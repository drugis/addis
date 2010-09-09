/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

public class RateMeasurementPresentationTest {
	private BasicRateMeasurement d_measurement;
	private Arm d_pg;
	private RateMeasurementPresentation d_presentation;
	
	@Before
	public void setUp() {
		d_pg = new Arm(null, null, 101);
		d_measurement = new BasicRateMeasurement(67, d_pg.getSize());
		d_presentation = new RateMeasurementPresentation(d_measurement);
	}

	@Test
	public void testFireLabelRateChanged() {
		AbstractValueModel lm = d_presentation.getLabelModel();
		PropertyChangeListener l = JUnitUtil.mockListener(
				lm, "value", "67/101", "68/101");
		lm.addPropertyChangeListener(l);
		d_measurement.setRate(68);
		verify(l);
	}
		
	@Test
	public void testFireLabelSizeChanged() {
		AbstractValueModel lm = d_presentation.getLabelModel();
		PropertyChangeListener l = JUnitUtil.mockListener(
				lm, "value", "67/101", "67/102");
		lm.addPropertyChangeListener(l);
		d_measurement.setSampleSize(102);
		verify(l);
	}	
	
	@Test
	public void testGetSize() {
		assertEquals(d_measurement.getSampleSize(),
				d_presentation.getModel(BasicRateMeasurement.PROPERTY_SAMPLESIZE).getValue());
		d_measurement.setSampleSize(104);
		assertEquals(d_measurement.getSampleSize(),
				d_presentation.getModel(BasicRateMeasurement.PROPERTY_SAMPLESIZE).getValue());
	}
	
	
	@Test
	public void testGetLabel() {
		assertEquals("67/101", d_presentation.getLabelModel().getValue());
	}
}
