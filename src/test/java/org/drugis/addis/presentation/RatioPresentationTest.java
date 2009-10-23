package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class RatioPresentationTest {
	
	private static final int s_sizeNum = 142;
	private static final int s_sizeDen = 144;
	private static final int s_effectNum = 73;
	private static final int s_effectDen = 63;
	
	BasicRateMeasurement d_numerator;
	BasicRateMeasurement d_denominator;
	OddsRatio d_ratio;
	RatioPresentation d_presentation;
	
	@Before
	public void setUp() {
		Endpoint e = new Endpoint("E", Type.RATE);
		PatientGroup pnum = new BasicPatientGroup(null,null,null,s_sizeNum);
		PatientGroup pden = new BasicPatientGroup(null,null,null,s_sizeDen);
		d_numerator = new BasicRateMeasurement(e, s_effectNum, pnum);		
		d_denominator = new BasicRateMeasurement(e, s_effectDen, pden);
		d_ratio = new OddsRatio(d_denominator, d_numerator);
		d_presentation = new RatioPresentation(d_ratio, new PresentationModelManager(new DomainImpl()));
	}
	
	@Test
	public void testGetLabel() {
		assertEquals("1.36 (1.07-1.72)", d_presentation.getLabel());
	}	
	
	@Test
	public void testPropertyChangeEvents() {
		d_denominator.setRate(1);
		PropertyChangeListener l = 
			JUnitUtil.mockListener(d_presentation, LabeledPresentationModel.PROPERTY_LABEL, null, "1.36 (1.07-1.72)");
		d_presentation.addPropertyChangeListener(l);
		d_denominator.setRate(s_effectDen);
		verify(l);
		d_presentation.removePropertyChangeListener(l);
		
		d_numerator.setRate(1);
		l = JUnitUtil.mockListener(d_presentation, LabeledPresentationModel.PROPERTY_LABEL, null, "1.36 (1.07-1.72)");
		d_presentation.addPropertyChangeListener(l);
		d_numerator.setRate(s_effectNum);
		verify(l);
	}
	
}
