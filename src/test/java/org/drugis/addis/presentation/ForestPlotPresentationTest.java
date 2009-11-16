package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.MeanDifference;
import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.StandardisedMeanDifference;
import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.addis.plot.ForestPlot;
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
	
	BasicContinuousMeasurement d_subj1;
	BasicContinuousMeasurement d_basel1;
	BasicContinuousMeasurement d_subj2;
	BasicContinuousMeasurement d_basel2;
	MeanDifference d_relEffect;
	MeanDifference d_relEffectInv;
	ForestPlotPresentation d_pm;
	private PatientGroup d_pnum;
	private PatientGroup d_pden;
	private Endpoint d_e;
	
	@Before
	public void setUp() {
		d_e = new Endpoint("E", Type.CONTINUOUS);
		d_pnum = new BasicPatientGroup(ExampleData.buildDefaultStudy1(),new Drug("DrugA", "01"),null,s_baslSize);
		d_pden = new BasicPatientGroup(ExampleData.buildDefaultStudy1(),new Drug("DrugB", "02"),null,s_subjSize);
		d_subj1 = new BasicContinuousMeasurement(d_e, s_mean1, s_stdDev1, d_pnum);		
		d_basel1 = new BasicContinuousMeasurement(d_e, s_mean2, s_stdDev2, d_pden);
		d_subj2 = new BasicContinuousMeasurement(d_e, s_mean2, s_stdDev2, d_pnum);		
		d_basel2 = new BasicContinuousMeasurement(d_e, s_mean1, s_stdDev1, d_pden);
		d_relEffect = new MeanDifference(d_basel1, d_subj1);
		d_relEffectInv = new MeanDifference(d_basel2, d_subj2);
		List<RelativeEffect<?>> list = new ArrayList<RelativeEffect<?>>(Collections.singleton(d_relEffect));
		list.add(d_relEffectInv);
		d_pm = new ForestPlotPresentation(list);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDifferentMeasurementsException() {
		StandardisedMeanDifference smd = new StandardisedMeanDifference(d_basel2, d_subj2);
		List<RelativeEffect<?>> list = new ArrayList<RelativeEffect<?>>(Collections.singleton(d_relEffect));
		list.add(smd);
		new ForestPlotPresentation(list);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDifferentEndpointsException() {
		Endpoint e2 = new Endpoint("Eprime", Type.CONTINUOUS);
		MeanDifference smd = new MeanDifference(new BasicContinuousMeasurement(e2, d_pden), new BasicContinuousMeasurement(e2, d_pnum));
		List<RelativeEffect<?>> list = new ArrayList<RelativeEffect<?>>(Collections.singleton(d_relEffect));
		list.add(smd);
		new ForestPlotPresentation(list);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDifferentDrugsException() {
		PatientGroup alt = new BasicPatientGroup(ExampleData.buildDefaultStudy1(), new Drug("DrugC", "03"),null, s_baslSize);
		MeanDifference smd = new MeanDifference(new BasicContinuousMeasurement(new Endpoint("E", Type.CONTINUOUS), alt), d_subj2);
		List<RelativeEffect<?>> list = new ArrayList<RelativeEffect<?>>(Collections.singleton(d_relEffect));
		list.add(smd);
		new ForestPlotPresentation(list);
	}
	
	@Test
	public void testGetListedRelativeEffects() {
		assertEquals(2, d_pm.getNumRelativeEffects());
		assertEquals(d_relEffect, d_pm.getRelativeEffectAt(0));
		assertEquals(d_relEffectInv, d_pm.getRelativeEffectAt(1));
	}
	
	
	@Test
	public void testGetScale() {
		assertEquals( (int) Math.round(ForestPlot.BARWIDTH  / 2.0), (int) d_pm.getScale().getBin(0.0).bin);
		int expectedBin = (int) Math.round( (2 - 1.09) / (4.0 / (ForestPlot.BARWIDTH - 1)) ); 
		assertEquals(expectedBin + 1, (int) d_pm.getScale().getBin(-1.09).bin);
		assertTrue(!d_pm.getScale().getBin(-1.09).outOfBoundsMin);
		assertEquals(ForestPlot.BARWIDTH - expectedBin, (int) d_pm.getScale().getBin(1.09).bin);
		assertTrue(!d_pm.getScale().getBin(1.09).outOfBoundsMax);
	}
	
	@Test
	public void testGetRange() {
		// known intervals: "0.25 (-0.59, 1.09)" & "-0.25 (-1.03, 0.53)"
		Interval<Double> interval = d_pm.getRange();
		assertEquals(-2.0, interval.getLowerBound(), 0.01);
		assertEquals(2.0, interval.getUpperBound(), 0.01);	
	}
	
	@Test
	public void testGetDrugsLabel() {
		String baseLabel = d_pm.getBaselineDrugLabel();
		String subjLabel = d_pm.getSubjectDrugLabel();
		assertEquals("DrugB", baseLabel);
		assertEquals("DrugA", subjLabel);
	}
	
	@Test
	public void testGetStudiesLabel() {
		String studyA = d_pm.getStudyLabelAt(0);
		String studyB = d_pm.getStudyLabelAt(1);
		assertEquals("Chouinard et al, 1999", studyA);
		assertEquals("Chouinard et al, 1999", studyB);
	}
	
	@Test
	public void testGetCIlabel() {
		String interval1 = "0.25 (-0.59, 1.09)";
		String interval2 = "-0.25 (-1.03, 0.53)";
		assertEquals(interval1, d_pm.getCIlabelAt(0));
		assertEquals(interval2, d_pm.getCIlabelAt(1));
	}
	
	@Test
	public void testGetWeightAt() {
		List<RelativeEffect<?>> list = new ArrayList<RelativeEffect<?>>(Collections.singleton(d_relEffect));
		ContinuousMeasurement baseline = new BasicContinuousMeasurement(d_e, new BasicPatientGroup(ExampleData.buildDefaultStudy1(),new Drug("DrugB", "02"),null,s_baslSize * 10));
		ContinuousMeasurement subject = new BasicContinuousMeasurement(d_e, new BasicPatientGroup(ExampleData.buildDefaultStudy1(),new Drug("DrugA", "01"),null,s_subjSize * 10));
		MeanDifference md = new MeanDifference(baseline, subject);
		list.add(md);
		ForestPlotPresentation fpp = new ForestPlotPresentation(list);
		
		assertEquals(5, fpp.getDiamondSize(0));
		assertEquals(21, fpp.getDiamondSize(1));
	}
	
	@Test
	public void testLogarithmic() {
		Interval<Double> logint = d_pm.testHelper();
		System.out.println(logint.getLowerBound() + "-" + logint.getUpperBound());
	}
}
