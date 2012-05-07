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

package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;
import java.util.HashMap;

import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.MapBean;
import org.drugis.addis.entities.Study;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class StudyCharacteristicHolderTest {
	private Study d_study;
	private StudyCharacteristicHolder d_holder;
	private BasicStudyCharacteristic d_char;
	
	@Before
	public void setUp() {
		d_char = BasicStudyCharacteristic.BLINDING;
		d_study = new Study("Test", new Indication(0L, ""));
		d_study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.OPEN);
		d_holder = new MutableCharacteristicHolder(d_study, d_char);
	}
	
	@Test
	public void testSetValue() {
		JUnitUtil.testSetter(d_holder, "value", d_study.getCharacteristic(d_char), BasicStudyCharacteristic.Blinding.TRIPLE_BLIND);
	}
	
	@Test
	public void testSetValueTriggersCharacteristicsChanged() {
		HashMap<BasicStudyCharacteristic, Object> newVal = new HashMap<BasicStudyCharacteristic, Object>();
		Indication indication = new Indication(1L, "Indication");
		newVal.put(d_char, indication);
		PropertyChangeListener l =
			JUnitUtil.mockListener(d_study.getCharacteristics(), MapBean.PROPERTY_CONTENTS, null, null);
		d_study.getCharacteristics().addPropertyChangeListener(l);
		d_holder.setValue(indication);
		verify(l);
	}
	
	@Test
	public void testGetCharacteristic() {
		assertEquals(d_char, d_holder.getCharacteristic());
	}
}
