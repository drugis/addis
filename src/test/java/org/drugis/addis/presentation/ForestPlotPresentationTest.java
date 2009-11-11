package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.MeanDifference;
import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Test;

public class ForestPlotPresentationTest {
	
	private static final double s_mean1 = 0.50;
	private static final double s_mean2 = 0.25;
	private static final double s_stdDev1 = 0.2;
	private static final double s_stdDev2 = 2.5;
	private static final int s_subjSize = 35;
	private static final int s_baslSize = 41;
	
	BasicContinuousMeasurement d_subj;
	BasicContinuousMeasurement d_basel;
	MeanDifference d_relEffect;
	MeanDifference d_relEffectInv;
	ForestPlotPresentation d_pm;
	
	@Before
	public void setUp() {
		Endpoint e = new Endpoint("E", Type.CONTINUOUS);
		PatientGroup pnum = new BasicPatientGroup(null,null,null,s_baslSize);
		PatientGroup pden = new BasicPatientGroup(null,null,null,s_subjSize);
		d_subj = new BasicContinuousMeasurement(e, s_mean1, s_stdDev1, pnum);		
		d_basel = new BasicContinuousMeasurement(e, s_mean2, s_stdDev2, pden);
		d_relEffect = new MeanDifference(d_basel, d_subj);
		d_relEffectInv = new MeanDifference(d_subj, d_basel);
		List<RelativeEffect<?>> list = new ArrayList<RelativeEffect<?>>(Collections.singleton(d_relEffect));
		list.add(d_relEffectInv);
		d_pm = new ForestPlotPresentation(list);
	}
	
	@Test
	public void testGetListedRelativeEffects() {
		assertEquals(2, d_pm.getNumRelativeEffects());
		assertEquals(d_relEffect, d_pm.getRelativeEffectAt(0));
		assertEquals(d_relEffectInv, d_pm.getRelativeEffectAt(1));
	}
	
	
	@Test
	public void testGetScale() {
		assertEquals(101, (int) d_pm.getScale().getBin(0.0).bin);
		int expectedBin = (int) Math.round( (2 - 1.09) / (4.0 / 200) ); 
		assertEquals(expectedBin + 1, (int) d_pm.getScale().getBin(-1.09).bin);
		assertTrue(!d_pm.getScale().getBin(-1.09).outOfBoundsMin);
		assertEquals(201 - expectedBin, (int) d_pm.getScale().getBin(1.09).bin);
		assertTrue(!d_pm.getScale().getBin(1.09).outOfBoundsMax);
	}
	
	@Test
	public void testGetRange() {
		// known intervals: "0.25 (-0.59, 1.09)" & "-0.25 (-1.09, 0.59)"
		Interval<Double> interval = d_pm.getRange();
		assertEquals(-2.0, interval.getLowerBound(), 0.01);
		assertEquals(2.0, interval.getUpperBound(), 0.01);	
	}
}
