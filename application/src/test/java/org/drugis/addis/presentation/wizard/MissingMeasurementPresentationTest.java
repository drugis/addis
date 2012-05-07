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

package org.drugis.addis.presentation.wizard;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyOutcomeMeasure;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.ValueModel;

public class MissingMeasurementPresentationTest {

	private MissingMeasurementPresentation d_mmp;
	private Study d_s;
	private ValueModel d_missing;
	private BasicMeasurement d_defaultMeasurement;
	private Arm d_a;
	private StudyOutcomeMeasure<AdverseEvent> d_v;

	@Before
	public void setUp() {
		d_s = ExampleData.buildStudyBennie();
		d_v = d_s.getAdverseEvents().get(0);
		d_a = d_s.getArms().get(0);
		d_mmp = new MissingMeasurementPresentation(d_s, d_v, d_s.defaultMeasurementMoment(), d_a);
		d_defaultMeasurement = d_s.buildDefaultMeasurement(d_v.getValue(), d_a);
		d_missing = d_mmp.getMissingModel();
	}
	
	@Test
	public void testInitialisation() {
		assertEquals(d_defaultMeasurement, d_mmp.getMeasurement());
		assertEquals(Boolean.TRUE, d_mmp.getMissingModel().getValue());
	}
	
	@Test
	public void testChangesPropagatingToStudy() {
		assertEquals(null, d_s.getMeasurement(d_v, d_a));
		d_missing.setValue(false);
		assertEquals(Boolean.FALSE, d_mmp.getMissingModel().getValue());
		assertEquals(d_defaultMeasurement, d_s.getMeasurement(d_v, d_a));
		
		d_missing.setValue(true);
		assertEquals(null, d_s.getMeasurement(d_v, d_a));
	}
	
}
