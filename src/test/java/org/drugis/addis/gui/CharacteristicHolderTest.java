/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package org.drugis.addis.gui;

import static org.easymock.EasyMock.verify;

import java.beans.PropertyChangeListener;
import java.util.HashMap;

import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.entities.StudyCharacteristicsMap;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.beans.Model;

public class CharacteristicHolderTest {
	private BasicStudy d_study;
	private CharacteristicHolder d_holder;
	private StudyCharacteristic d_char;
	
	@Before
	public void setUp() {
		d_char = StudyCharacteristic.INDICATION;
		d_study = new BasicStudy("Test", new Indication(0L, ""));
		d_holder = new CharacteristicHolder(d_study, d_char);
	}
	
	@Test
	public void testSetValue() {
		JUnitUtil.testSetter(d_holder, "value", new Indication(0L, ""), new Indication(1L, "Indication"));
	}
	
	@Test
	public void testSetValueTriggersCharacteristicsChanged() {
		HashMap<StudyCharacteristic, Model> newVal = new HashMap<StudyCharacteristic, Model>();
		Indication indication = new Indication(1L, "Indication");
		newVal.put(d_char, indication);
		PropertyChangeListener l =
			JUnitUtil.mockListener(d_study.getCharacteristics(), StudyCharacteristicsMap.PROPERTY_CONTENTS, null, null);
		d_study.getCharacteristics().addPropertyChangeListener(l);
		d_holder.setValue(indication);
		verify(l);
	}
}
