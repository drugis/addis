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

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

public class RelativeEffectPresentationTest {
	
	private static final String LABELCONTENTS = "1.36 (0.85, 2.17)";
	private static final int s_sizeNum = 142;
	private static final int s_sizeDen = 144;
	private static final int s_effectNum = 73;
	private static final int s_effectDen = 63;
	
	BasicRateMeasurement d_numerator;
	BasicRateMeasurement d_denominator;
	BasicOddsRatio d_ratio;
	RelativeEffectPresentation d_presentation;
	
	@Before
	public void setUp() {
		Arm pnum = new Arm("num", s_sizeNum);
		Arm pden = new Arm("den", s_sizeDen);
		d_numerator = new BasicRateMeasurement(s_effectNum, pnum.getSize());		
		d_denominator = new BasicRateMeasurement(s_effectDen, pden.getSize());
		d_ratio = new BasicOddsRatio(d_denominator, d_numerator);
		d_presentation = new RelativeEffectPresentation(d_ratio);
	}
	
	@Test
	public void testGetLabel() {
		assertEquals(LABELCONTENTS, d_presentation.getLabelModel().getValue());
	}	
	
	@Test
	public void testPropertyChangeEvents() {
		d_denominator.setRate(1);
		AbstractValueModel labelModel = d_presentation.getLabelModel();
		PropertyChangeListener l = 
			JUnitUtil.mockListener(labelModel, "value", null, LABELCONTENTS);
		labelModel.addPropertyChangeListener(l);
		d_denominator.setRate(s_effectDen);
		verify(l);
	}
	
	@Test
	public void testLabelNumeratorChanged() {
		d_numerator.setRate(1);
		AbstractValueModel labelModel = d_presentation.getLabelModel();
		PropertyChangeListener l = 
			JUnitUtil.mockListener(labelModel, "value", null, LABELCONTENTS);
		labelModel.addPropertyChangeListener(l);
		d_numerator.setRate(s_effectNum);
		verify(l);
	}
	
}
