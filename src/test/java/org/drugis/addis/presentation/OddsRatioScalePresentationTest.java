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

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import java.beans.PropertyChangeListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.OddsRatioToClinicalConverter;
import org.drugis.common.Interval;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.ValueModel;

import fi.smaa.jsmaa.model.ScaleCriterion;

public class OddsRatioScalePresentationTest {
	private OddsRatioToClinicalConverter d_orc;
	private BenefitRiskAnalysis d_br;
	private ScaleCriterion d_criterion;
	private OddsRatioScalePresentation d_pm;

	private static Interval<Double> convert(fi.smaa.jsmaa.model.Interval interval) {
		return new Interval<Double>(interval.getStart(), interval.getEnd());
	}
	
	@Before
	public void setUp() {
		d_br = ExampleData.buildMockBenefitRiskAnalysis();
		d_orc = new OddsRatioToClinicalConverter(d_br, ExampleData.buildEndpointHamd());
		d_criterion = new ScaleCriterion("criterion", true);
		d_criterion.setScale(new fi.smaa.jsmaa.model.Interval(0.95, 1.30));
		d_pm = new OddsRatioScalePresentation(d_criterion, d_orc);
	}
	
	@Test
	public void testOddsRatioModelShouldGiveCorrectValue() {
		Object actual = d_pm.getModel(OddsRatioScalePresentation.PROPERTY_ODDS_RATIO).getValue();
		Object expected = d_orc.getOddsRatio(convert(d_criterion.getScale()));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testOddsRatioModelShouldFireOnScaleChange() {
		ValueModel model = d_pm.getModel(OddsRatioScalePresentation.PROPERTY_ODDS_RATIO);
		fi.smaa.jsmaa.model.Interval interval = new fi.smaa.jsmaa.model.Interval(0.8, 1.1);
		Object expected = d_orc.getOddsRatio(convert(interval));
		assertValueModelFires(model, interval, expected);
	}

	@Test
	public void testRiskModelShouldGiveCorrectValue() {
		Object actual = d_pm.getModel(OddsRatioScalePresentation.PROPERTY_RISK).getValue();
		Object expected = d_orc.getRisk(convert(d_criterion.getScale()));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRiskModelShouldFireOnScaleChange() {
		ValueModel model = d_pm.getModel(OddsRatioScalePresentation.PROPERTY_RISK);
		fi.smaa.jsmaa.model.Interval interval = new fi.smaa.jsmaa.model.Interval(0.8, 1.1);
		Object expected = d_orc.getRisk(convert(interval));
		assertValueModelFires(model, interval, expected);
	}

	@Test
	public void testRiskDifferenceModelShouldGiveCorrectValue() {
		Object actual = d_pm.getModel(OddsRatioScalePresentation.PROPERTY_RISK_DIFFERENCE).getValue();
		Object expected = d_orc.getRiskDifference(convert(d_criterion.getScale()));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRiskDifferenceModelShouldFireOnScaleChange() {
		ValueModel model = d_pm.getModel(OddsRatioScalePresentation.PROPERTY_RISK_DIFFERENCE);
		fi.smaa.jsmaa.model.Interval interval = new fi.smaa.jsmaa.model.Interval(0.8, 1.1);
		Object expected = d_orc.getRiskDifference(convert(interval));
		assertValueModelFires(model, interval, expected);
	}
	
	@Test
	public void testNNTModelShouldGiveCorrectValue() {
		Object actual = d_pm.getModel(OddsRatioScalePresentation.PROPERTY_NNT).getValue();
		Object expected = d_orc.getNumberNeededToTreat(convert(d_criterion.getScale()));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testNNTModelShouldFireOnScaleChange() {
		ValueModel model = d_pm.getModel(OddsRatioScalePresentation.PROPERTY_NNT);
		fi.smaa.jsmaa.model.Interval interval = new fi.smaa.jsmaa.model.Interval(0.8, 1.1);
		Object expected = d_orc.getNumberNeededToTreat(convert(interval));
		assertValueModelFires(model, interval, expected);
	}
	
	@Test
	public void testNNTLabelModelShouldGiveCorrectValue() {
		Object actual = d_pm.getModel(OddsRatioScalePresentation.PROPERTY_NNT_LABEL).getValue();
		Object expected = "NNT";
		assertEquals(expected, actual);
	}
	
	@Test
	public void testNNTLabelModelShouldFireOnScaleChange() {
		ValueModel model = d_pm.getModel(OddsRatioScalePresentation.PROPERTY_NNT_LABEL);
		Object expected = "NNH";
		PropertyChangeListener mock = JUnitUtil.mockListener(model, "value", null, expected);
		model.addValueChangeListener(mock);
		d_criterion.setAscending(false);
		assertEquals(expected, model.getValue());
		verify(mock);
	}
	
	private void assertValueModelFires(ValueModel model,
			fi.smaa.jsmaa.model.Interval interval, Object expected) {
		PropertyChangeListener mock = JUnitUtil.mockListener(model, "value", null, expected);
		model.addValueChangeListener(mock);
		d_criterion.setScale(interval);
		assertEquals(expected, model.getValue());
		verify(mock);
	}
}
